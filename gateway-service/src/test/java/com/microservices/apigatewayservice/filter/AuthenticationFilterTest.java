package com.microservices.apigatewayservice.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;

import com.microservices.apigatewayservice.utils.JwtUtil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

/**
 * Unit tests for {@link AuthenticationFilter}: no Spring context, no network, no Eureka.
 *
 * <p>The filter is built by hand with a real {@link RouteValidator} and {@link JwtUtil}. Instead of
 * a downstream service, a fake {@link GatewayFilterChain} records the request it receives in
 * {@link #forwarded}, so each test can check what would have been sent on, or that nothing was.
 */
class AuthenticationFilterTest {

    /** Raw HMAC key (39 bytes, above the 32-byte minimum) used to sign valid test tokens. */
    private static final byte[] KEY = "test-secret-test-secret-test-secret-123".getBytes();
    /** {@link #KEY} in Base64, the format {@code JWT_SECRET} uses. */
    private static final String SECRET = Base64.getEncoder().encodeToString(KEY);
    /** Value the filter must put in {@code X-Gateway-Secret}. */
    private static final String GATEWAY_SECRET = "gw-test-secret";

    /** The filter under test, as {@link AuthenticationFilter#apply} builds it for a route. */
    private GatewayFilter filter;
    /** The request the filter forwarded, or {@code null} if it rejected the request. */
    private final AtomicReference<ServerHttpRequest> forwarded = new AtomicReference<>();
    /** Stand-in for the rest of the gateway: records the forwarded request and completes. */
    private final GatewayFilterChain chain = exchange -> {
        forwarded.set(exchange.getRequest());
        return Mono.empty();
    };

    /**
     * Creates the filter and injects its {@code @Autowired}/{@code @Value} fields with
     * {@link ReflectionTestUtils}, since there is no Spring context to do it.
     */
    @BeforeEach
    void setUp() {
        AuthenticationFilter factory = new AuthenticationFilter();
        ReflectionTestUtils.setField(factory, "validator", new RouteValidator());
        ReflectionTestUtils.setField(factory, "jwtUtil", new JwtUtil(SECRET));
        ReflectionTestUtils.setField(factory, "gatewaySecret", GATEWAY_SECRET);
        filter = factory.apply(new AuthenticationFilter.Config());
    }

    /**
     * Builds a signed HS256 token with the same claims authentication-service issues:
     * {@code userId=42}, email, username, roles {@code [SALES_REP]} and privileges
     * {@code [LEAD_READ, LEAD_WRITE]}.
     *
     * @param key         signing key; pass a different key to get a token with a bad signature
     * @param expiresInMs lifetime from now; a negative value gives an already expired token
     * @return the compact JWT, without the "Bearer " prefix
     */
    private static String token(byte[] key, long expiresInMs) {
        return Jwts.builder()
                .setSubject("42")
                .claim("userId", 42L)
                .claim("email", "rep@example.com")
                .claim("username", "rep")
                .claim("roles", List.of("SALES_REP"))
                .claim("privileges", List.of("LEAD_READ", "LEAD_WRITE"))
                .setExpiration(new Date(System.currentTimeMillis() + expiresInMs))
                .signWith(Keys.hmacShaKeyFor(key), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Runs the filter on a mock request and waits for it to finish.
     *
     * @param request the request to send through the filter
     * @return the exchange, so the test can inspect the response status
     */
    private MockServerWebExchange run(MockServerHttpRequest request) {
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        filter.filter(exchange, chain).block();
        return exchange;
    }

    /**
     * Asserts the request was rejected: status 401 and nothing forwarded downstream.
     *
     * @param exchange the exchange returned by {@link #run}
     */
    private void assertRejected(MockServerWebExchange exchange) {
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        assertNull(forwarded.get(), "request must not be forwarded");
    }

    /** A secured path with no Authorization header gets 401. */
    @Test
    void rejectsMissingAuthorizationHeader() {
        assertRejected(run(MockServerHttpRequest.get("/employee/list").build()));
    }

    /** A valid token sent without the "Bearer " prefix gets 401. */
    @Test
    void rejectsNonBearerHeader() {
        assertRejected(run(MockServerHttpRequest.get("/employee/list")
                .header(HttpHeaders.AUTHORIZATION, token(KEY, 60_000)).build()));
    }

    /** A token signed with a different key (bad signature) gets 401. */
    @Test
    void rejectsTokenSignedWithOtherKey() {
        byte[] otherKey = "another-secret-another-secret-another-1".getBytes();
        assertRejected(run(MockServerHttpRequest.get("/employee/list")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token(otherKey, 60_000)).build()));
    }

    /** A correctly signed but expired token gets 401. */
    @Test
    void rejectsExpiredToken() {
        assertRejected(run(MockServerHttpRequest.get("/employee/list")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token(KEY, -60_000)).build()));
    }

    /** A path that merely contains a public endpoint ({@code /auth/signin}) is still secured. */
    @Test
    void rejectsPathThatOnlyContainsAnOpenEndpoint() {
        assertRejected(run(MockServerHttpRequest.get("/employee/x/auth/signin").build()));
    }

    /**
     * With a valid token, identity headers come from the token's claims, and client-sent
     * {@code id}, {@code X-User-*} (any casing) and {@code X-Gateway-Secret} values are replaced.
     */
    @Test
    void validTokenForwardsIdentityFromClaimsAndIgnoresSpoofedHeaders() {
        run(MockServerHttpRequest.get("/account/create-account")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token(KEY, 60_000))
                .header("id", "1")                       // spoof attempt: legacy header
                .header("X-User-Id", "1")                // spoof attempt
                .header("x-user-privileges", "ADMIN_ALL") // spoof attempt, other casing
                .header("X-Gateway-Secret", "guessed")    // spoof attempt
                .build());

        ServerHttpRequest out = forwarded.get();
        assertNotNull(out, "request should be forwarded");
        HttpHeaders h = out.getHeaders();
        assertEquals(List.of("42"), h.get("id"));
        assertEquals(List.of("42"), h.get(AuthenticationFilter.HEADER_USER_ID));
        assertEquals("rep@example.com", h.getFirst(AuthenticationFilter.HEADER_USER_EMAIL));
        assertEquals("rep", h.getFirst(AuthenticationFilter.HEADER_USER_NAME));
        assertEquals("SALES_REP", h.getFirst(AuthenticationFilter.HEADER_USER_ROLES));
        assertEquals(List.of("LEAD_READ,LEAD_WRITE"), h.get(AuthenticationFilter.HEADER_USER_PRIVILEGES));
        assertEquals(List.of(GATEWAY_SECRET), h.get(AuthenticationFilter.GATEWAY_SECRET_HEADER));
    }

    /**
     * A public exact path is forwarded without a token, with spoofed identity headers removed
     * and the real gateway secret added.
     */
    @Test
    void openEndpointIsForwardedWithoutTokenButSpoofedHeadersAreStripped() {
        run(MockServerHttpRequest.post("/auth/signin")
                .header("id", "1")
                .header("X-User-Roles", "ADMIN")
                .header("X-Gateway-Secret", "guessed")
                .build());

        ServerHttpRequest out = forwarded.get();
        assertNotNull(out, "open endpoint should be forwarded");
        assertFalse(out.getHeaders().containsKey("id"));
        assertFalse(out.getHeaders().containsKey("X-User-Roles"));
        assertEquals(List.of(GATEWAY_SECRET), out.getHeaders().get(AuthenticationFilter.GATEWAY_SECRET_HEADER));
    }

    /** A path under a public prefix ({@code /auth/signupcheckEmailId/}) is forwarded without a token. */
    @Test
    void openPrefixEndpointIsForwardedWithoutToken() {
        run(MockServerHttpRequest.get("/auth/signupcheckEmailId/a@b.com").build());
        assertNotNull(forwarded.get());
    }
}

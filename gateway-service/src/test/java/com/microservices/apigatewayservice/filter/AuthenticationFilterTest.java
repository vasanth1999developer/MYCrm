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

class AuthenticationFilterTest {

    private static final byte[] KEY = "test-secret-test-secret-test-secret-123".getBytes();
    private static final String SECRET = Base64.getEncoder().encodeToString(KEY);
    private static final String GATEWAY_SECRET = "gw-test-secret";

    private GatewayFilter filter;
    private final AtomicReference<ServerHttpRequest> forwarded = new AtomicReference<>();
    private final GatewayFilterChain chain = exchange -> {
        forwarded.set(exchange.getRequest());
        return Mono.empty();
    };

    @BeforeEach
    void setUp() {
        AuthenticationFilter factory = new AuthenticationFilter();
        ReflectionTestUtils.setField(factory, "validator", new RouteValidator());
        ReflectionTestUtils.setField(factory, "jwtUtil", new JwtUtil(SECRET));
        ReflectionTestUtils.setField(factory, "gatewaySecret", GATEWAY_SECRET);
        filter = factory.apply(new AuthenticationFilter.Config());
    }

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

    private MockServerWebExchange run(MockServerHttpRequest request) {
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        filter.filter(exchange, chain).block();
        return exchange;
    }

    private void assertRejected(MockServerWebExchange exchange) {
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        assertNull(forwarded.get(), "request must not be forwarded");
    }

    @Test
    void rejectsMissingAuthorizationHeader() {
        assertRejected(run(MockServerHttpRequest.get("/employee/list").build()));
    }

    @Test
    void rejectsNonBearerHeader() {
        assertRejected(run(MockServerHttpRequest.get("/employee/list")
                .header(HttpHeaders.AUTHORIZATION, token(KEY, 60_000)).build()));
    }

    @Test
    void rejectsTokenSignedWithOtherKey() {
        byte[] otherKey = "another-secret-another-secret-another-1".getBytes();
        assertRejected(run(MockServerHttpRequest.get("/employee/list")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token(otherKey, 60_000)).build()));
    }

    @Test
    void rejectsExpiredToken() {
        assertRejected(run(MockServerHttpRequest.get("/employee/list")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token(KEY, -60_000)).build()));
    }

    @Test
    void rejectsPathThatOnlyContainsAnOpenEndpoint() {
        assertRejected(run(MockServerHttpRequest.get("/employee/x/auth/signin").build()));
    }

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

    @Test
    void openPrefixEndpointIsForwardedWithoutToken() {
        run(MockServerHttpRequest.get("/auth/signupcheckEmailId/a@b.com").build());
        assertNotNull(forwarded.get());
    }
}

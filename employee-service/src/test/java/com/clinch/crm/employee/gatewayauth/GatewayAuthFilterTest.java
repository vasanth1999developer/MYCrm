package com.clinch.crm.employee.gatewayauth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import feign.RequestTemplate;

/**
 * Unit tests for {@link GatewayAuthFilter} and {@link GatewaySecretFeignInterceptor}, using
 * Spring's servlet mocks; no application context or network needed.
 */
class GatewayAuthFilterTest {

    /** Filter under test, expecting the secret "s3cret". */
    private final GatewayAuthFilter filter = new GatewayAuthFilter("s3cret");

    /**
     * Sends {@code GET /employee/list} through the filter.
     *
     * @param headerValue value for {@code X-Gateway-Secret}, or {@code null} to omit the header
     * @param response    the response to inspect afterwards
     * @return the chain; {@code chain.getRequest()} is non-null only if the filter let the request through
     * @throws Exception if the filter throws
     */
    private MockFilterChain run(String headerValue, MockHttpServletResponse response) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/employee/list");
        if (headerValue != null) {
            request.addHeader(GatewayAuthFilter.HEADER, headerValue);
        }
        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(request, response, chain);
        return chain;
    }

    /**
     * No header → 403 and the request stops.
     *
     * @throws Exception if the filter throws
     */
    @Test
    void rejectsRequestWithoutSecret() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = run(null, response);
        assertEquals(403, response.getStatus());
        assertNull(chain.getRequest(), "must not reach the controller");
    }

    /**
     * A secret that is one character short → 403.
     *
     * @throws Exception if the filter throws
     */
    @Test
    void rejectsWrongSecret() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = run("s3cre", response);
        assertEquals(403, response.getStatus());
        assertNull(chain.getRequest());
    }

    /**
     * The right secret → the request continues (200 from the mock chain).
     *
     * @throws Exception if the filter throws
     */
    @Test
    void allowsCorrectSecret() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = run("s3cret", response);
        assertEquals(200, response.getStatus());
        assertNotNull(chain.getRequest());
    }

    /** A blank secret makes the constructor throw, so a misconfigured service fails at startup. */
    @Test
    void failsFastWhenSecretMissing() {
        assertThrows(IllegalStateException.class, () -> new GatewayAuthFilter(" "));
    }

    /** The Feign interceptor puts the secret in {@code X-Gateway-Secret} of outgoing calls. */
    @Test
    void feignInterceptorSendsTheSecret() {
        RequestTemplate template = new RequestTemplate();
        new GatewaySecretFeignInterceptor("s3cret").apply(template);
        assertEquals("s3cret", template.headers().get(GatewayAuthFilter.HEADER).iterator().next());
    }
}

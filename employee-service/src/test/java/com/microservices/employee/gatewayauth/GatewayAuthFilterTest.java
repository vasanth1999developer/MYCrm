package com.microservices.employee.gatewayauth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import feign.RequestTemplate;

class GatewayAuthFilterTest {

    private final GatewayAuthFilter filter = new GatewayAuthFilter("s3cret");

    private MockFilterChain run(String headerValue, MockHttpServletResponse response) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/employee/list");
        if (headerValue != null) {
            request.addHeader(GatewayAuthFilter.HEADER, headerValue);
        }
        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(request, response, chain);
        return chain;
    }

    @Test
    void rejectsRequestWithoutSecret() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = run(null, response);
        assertEquals(403, response.getStatus());
        assertNull(chain.getRequest(), "must not reach the controller");
    }

    @Test
    void rejectsWrongSecret() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = run("s3cre", response);
        assertEquals(403, response.getStatus());
        assertNull(chain.getRequest());
    }

    @Test
    void allowsCorrectSecret() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = run("s3cret", response);
        assertEquals(200, response.getStatus());
        assertNotNull(chain.getRequest());
    }

    @Test
    void failsFastWhenSecretMissing() {
        assertThrows(IllegalStateException.class, () -> new GatewayAuthFilter(" "));
    }

    @Test
    void feignInterceptorSendsTheSecret() {
        RequestTemplate template = new RequestTemplate();
        new GatewaySecretFeignInterceptor("s3cret").apply(template);
        assertEquals("s3cret", template.headers().get(GatewayAuthFilter.HEADER).iterator().next());
    }
}

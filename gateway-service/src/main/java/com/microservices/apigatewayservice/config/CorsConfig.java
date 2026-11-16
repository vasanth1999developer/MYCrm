package com.microservices.apigatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Cross-Origin Resource Sharing (CORS) settings for browser clients.
 *
 * <p>Browsers block a page served from one origin (e.g. the frontend on
 * {@code http://localhost:5173}) from calling another (the gateway on {@code :8556}) unless the
 * server allows it. CORS is enforced only by browsers; curl and Postman ignore it.
 *
 * <p>The filter is a {@code WebFilter}, so it runs before route matching and before
 * {@link com.microservices.apigatewayservice.filter.AuthenticationFilter}. That is why preflight
 * {@code OPTIONS} requests succeed without a JWT.
 */
@Configuration
public class CorsConfig {

	/**
	 * Builds the CORS filter applied to every path ({@code /**}).
	 *
	 * <ul>
	 *   <li>Allowed origins: the Vite ({@code 5173}) and React ({@code 3000}) dev servers, plus a
	 *       placeholder production domain.</li>
	 *   <li>Allowed methods: GET, POST, PUT, DELETE, OPTIONS. {@code PATCH} is missing, so a browser
	 *       preflight for a PATCH request is rejected with 403.</li>
	 *   <li>Any request header is allowed (e.g. {@code Authorization}, {@code Content-Type}).</li>
	 *   <li>Credentials are allowed, which is why origins are listed explicitly ({@code *} is not
	 *       permitted together with credentials).</li>
	 *   <li>Browsers cache a preflight answer for one hour.</li>
	 * </ul>
	 *
	 * @return the reactive CORS filter registered in the WebFlux filter chain
	 */
	@Bean
	public CorsWebFilter corsWebFilter() {

		CorsConfiguration config = new CorsConfiguration();


		config.setAllowedOrigins(Arrays.asList(
				"http://localhost:5173",
				"http://localhost:3000",
				"https://yourdomain.com"
		));


		config.setAllowedMethods(Arrays.asList(
				"GET", "POST", "PUT", "DELETE", "OPTIONS"
		));

		config.setAllowedHeaders(Arrays.asList("*"));


		config.setAllowCredentials(true);

		config.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return new CorsWebFilter(source);
	}
}

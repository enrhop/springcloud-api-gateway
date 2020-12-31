package de.enrhop.examples.springcloud.gateway;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;

import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableConfigurationProperties(UriConfiguration.class)
@RestController
public class APIGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(APIGatewayApplication.class, args);
	}

	@Autowired
	private TokenRelayGatewayFilterFactory filterFactory;
	
	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder, UriConfiguration uriConfiguration) {
		
		return builder.routes()
			.route(p -> p
					.path("/api/service1/**")
					.filters(f -> f.removeRequestHeader("Cookie")
							.rewritePath("/api/(?<remaining>.*)", "/${remaining}")
							.filter(filterFactory.apply())
							)
					.uri(uriConfiguration.getApiService1()))
			.build();
	}

	@GetMapping(path = "/tokens", produces = "application/json")
	public Mono<Map<String, Object>> getAuthenticationDetails(WebSession session, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient oAuth2AuthorizedClient) {
		
		Map<String, Object> m = new HashMap<>();
		m.put("session", session);
		m.put("access-token", oAuth2AuthorizedClient.getAccessToken().getTokenValue());
		m.put("refresh-token", oAuth2AuthorizedClient.getRefreshToken().getTokenValue());
		return Mono.just(m);
	}	
}

@ConfigurationProperties
class UriConfiguration {

	private String apiService1 = "http://localhost:7070";

	public String getApiService1() {
		return apiService1;
	}

	public void setApiService1(String apiService1) {
		this.apiService1 = apiService1;
	}
}

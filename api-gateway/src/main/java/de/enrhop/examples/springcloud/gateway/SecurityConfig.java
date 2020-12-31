package de.enrhop.examples.springcloud.gateway;

import static org.springframework.security.config.Customizer.withDefaults;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.session.MapSession;
import org.springframework.session.ReactiveMapSessionRepository;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

@EnableWebFluxSecurity
@EnableSpringWebSession
public class SecurityConfig {

	@Autowired
	private SessionProperties sessionProperties;

//	@Bean
//	public WebClient defaultWebClient() {
//
//		return WebClient.builder().clientConnector(new ReactorClientHttpConnector(HttpClient.create()
//				.wiretap("ehopf.examples", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)))
//				.build();
//	}
	
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		return http.authorizeExchange()
				.pathMatchers("/actuator").permitAll()
				.pathMatchers("/api/service1/read").hasAuthority("SCOPE_service1.read")
				.pathMatchers("/api/service1/write").hasAuthority("SCOPE_service1.write")
				.anyExchange().authenticated()
				.and().oauth2Login(withDefaults())
				.securityContextRepository(new WebSessionServerSecurityContextRepository()).build();
	}
		
	@Bean
	public ReactiveSessionRepository<MapSession> sessionRepository() {
		ReactiveMapSessionRepository repo = new ReactiveMapSessionRepository(new ConcurrentHashMap<>());
		repo.setDefaultMaxInactiveInterval((int) sessionProperties.determineTimeout(() -> Duration.ofSeconds(1*60)).getSeconds());
		return repo;
	}

	@Bean
	public WebSessionIdResolver webSessionIdResolver() {
		CookieWebSessionIdResolver customResolver = new CookieWebSessionIdResolver();
		customResolver.setCookieName("JSESSIONID");
		customResolver.setCookieMaxAge(Duration.ofHours(10));
//		customResolver.addCookieInitializer(builder -> builder.secure(true));
		customResolver.addCookieInitializer(builder -> builder.sameSite("Strict"));
		return customResolver;
	}

}

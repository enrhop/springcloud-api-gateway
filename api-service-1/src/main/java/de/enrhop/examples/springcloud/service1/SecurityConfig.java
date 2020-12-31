package de.enrhop.examples.springcloud.service1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests(authorize -> authorize.anyRequest().authenticated())
				.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
	}

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		return http.authorizeExchange()
				.pathMatchers("/actuator").permitAll()
				.pathMatchers("/service1/read").hasAuthority("SCOPE_service1.read")
				.pathMatchers("/service1/write").hasAuthority("SCOPE_service1.write")
				.anyExchange().authenticated()
				.and().oauth2ResourceServer().jwt()
				.and().and()
				.build();
	}
}

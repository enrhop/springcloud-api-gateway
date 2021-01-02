package de.enrhop.examples.springcloud.service1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenReactiveAuthenticationManager;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.oauth2.server.resource.introspection.NimbusReactiveOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	private OAuth2ResourceServerProperties auth2ResourceServerProperties;
	
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
				.and().oauth2ResourceServer().authenticationManagerResolver(serverWebExchange -> {
					boolean highRiskOp = serverWebExchange.getRequest().getURI().getPath().contains("write");
					return highRiskOp ? Mono.just(getOpaqueTokenAuthenticationManager()) : Mono.just(getJwtAuthenticationManager());
				})
				.and()
				.build();
	}
	
	
	private ReactiveAuthenticationManager getJwtAuthenticationManager() {
		String jwkSetUri = auth2ResourceServerProperties.getJwt().getJwkSetUri();
		ReactiveJwtDecoder jwtDecoder = new NimbusReactiveJwtDecoder(jwkSetUri);
		Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverterAdapter(
				new JwtAuthenticationConverter());
		JwtReactiveAuthenticationManager authenticationManager = new JwtReactiveAuthenticationManager(jwtDecoder);
		authenticationManager.setJwtAuthenticationConverter(jwtAuthenticationConverter);
		return authenticationManager;
	}

	private ReactiveAuthenticationManager getOpaqueTokenAuthenticationManager() {
		String introspectionUri = auth2ResourceServerProperties.getOpaquetoken().getIntrospectionUri();
		String clientId = auth2ResourceServerProperties.getOpaquetoken().getClientId();
		String clientSecret = auth2ResourceServerProperties.getOpaquetoken().getClientSecret();
		ReactiveOpaqueTokenIntrospector introspector = new NimbusReactiveOpaqueTokenIntrospector(introspectionUri,
				clientId, clientSecret);
		return new OpaqueTokenReactiveAuthenticationManager(introspector);
	}
	
}

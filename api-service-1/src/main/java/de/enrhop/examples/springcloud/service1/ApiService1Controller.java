package de.enrhop.examples.springcloud.service1;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("service1")
public class ApiService1Controller {

	@GetMapping("/read")
	public Mono<String> read(Authentication authentication) {
		return Mono.just(String.format("Access granted for %s", getName(authentication)));
	}

	@GetMapping("/write")
	public Mono<String> write(Authentication authentication) {
		return Mono.just(String.format("Access granted for %s", getName(authentication)));
	}

	private String getName(Authentication authentication) {
		return authentication.getName();
	}
}

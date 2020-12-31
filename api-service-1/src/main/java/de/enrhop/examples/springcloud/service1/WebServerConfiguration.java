package de.enrhop.examples.springcloud.service1;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Configuration
public class WebServerConfiguration {

	@Component
	public class ApiService1WebServerCustomizer implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {
		@Override
		public void customize(NettyReactiveWebServerFactory factory) {
			factory.addServerCustomizers(httpServer -> httpServer.wiretap(WebServerConfiguration.class.getPackageName(),
					io.netty.handler.logging.LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL));
		}
	}
}

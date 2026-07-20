package com.hotel.cosumoweb.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class webClientConfig {
	@Bean
	WebClient webClient(WebClient.Builder builder) {
		return builder.baseUrl("http://localhost:8079/api").build(); 
	}

}

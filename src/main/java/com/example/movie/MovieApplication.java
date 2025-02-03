package com.example.movie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.function.Function;

@SpringBootApplication
public class MovieApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieApplication.class, args);
	}

	@Bean
	public Function<String, String> enrichLogMessage() {
		return value -> "[Movie] - " + value; // Thay đổi theo nhu cầu của bạn
	}

//	@Bean
//	public Function<String, Message<String>> processLogs() {
//		return log -> {
//			boolean shouldBeEnriched = log.length() > 10;
//			String destination = shouldBeEnriched ? "enrichLogMessage-in-0" : "queue.pretty.log.messages";
//
//			return MessageBuilder.withPayload(log)
//					.setHeader("spring.cloud.stream.sendto.destination", destination)
//					.build();
//		};
//	}
}

package com.example.movie;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("default")
class MovieApplicationTests {

	@Value("${spring.application.name}")
	private String appName;

	@Test
	void contextLoads() {
		assertThat(appName).isEqualTo("movie");
	}

}

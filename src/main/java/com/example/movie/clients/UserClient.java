package com.example.movie.clients;

import com.example.movie.dtos.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UserClient {
    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${api.user.uri}")
    private String userApiUri;

    public UserResponse getUser(String token, String username) {
        UserResponse user = webClientBuilder.build()
                .get()
                .uri(userApiUri, username)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block();

        return user;
    }
}

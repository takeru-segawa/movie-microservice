package com.example.movie.services;

import com.example.movie.dtos.MovieDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MovieEventProducer {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public MovieEventProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishMovieCreatedEvent(MovieDTO movieDTO) {
        try {
            String json = objectMapper.writeValueAsString(movieDTO);
            rabbitTemplate.convertAndSend("movie-exchange", "movie.created", json);
            System.out.println("Sent message: " + json);
        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }
    }
}

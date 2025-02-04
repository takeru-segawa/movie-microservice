package com.example.movie.services;

import com.example.movie.dtos.MovieDTO;
import com.example.movie.models.Movie;
import com.example.movie.repositories.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovieEventConsumer {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieRepository movieRepository;

    @RabbitListener(queues = "movie-queue")
    public void handleMovieCreatedEvent(String json) {
        try {
            System.out.println("Received message: " + json);

            json = json.replace("\\\"", "\"");
            json = json.replace("\\", "");
            json = json.substring(1, json.length() - 1);

            System.out.println("Decoded message: " + json);
            MovieDTO movieDTO = objectMapper.readValue(json, MovieDTO.class);

            System.out.println(movieDTO.toString());

            ModelMapper modelMapper = new ModelMapper();
            Movie movie = modelMapper.map(movieDTO, Movie.class);
            movieRepository.save(movie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

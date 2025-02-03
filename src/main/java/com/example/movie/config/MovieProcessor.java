package com.example.movie.config;

import com.example.movie.dtos.MovieDTO;
import com.example.movie.models.Movie;
import com.example.movie.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class MovieProcessor {

    @Autowired
    private MovieService movieService; // Dịch vụ để lưu vào DB

    @Bean
    public Function<Message<MovieDTO>, String> processLogs() {
        return message -> {
            MovieDTO movieDTO = message.getPayload();

            Movie movie = new Movie();
            movie.setTitle(movieDTO.getTitle());
            movie.setId(movieDTO.getId());
            movie.setMovieId(movieDTO.getMovieId());
            movie.setGenres(movieDTO.getGenres());

            movieService.saveMovie(movie);

            return "Saved: " + movie.getTitle();
        };
    }
}

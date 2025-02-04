package com.example.movie.events;

import com.example.movie.dtos.MovieDTO;
import com.example.movie.models.Movie;
import com.example.movie.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class MovieProcessor {

    @Autowired
    private MovieService movieService;

    @Bean
    public Function<Message<MovieDTO>, String> processMovies() {
        return message -> {
            MovieDTO movieDTO = message.getPayload();
            System.out.println("Received movie: " + movieDTO.getTitle());
            Movie movie = new Movie();
            movie.setId(movieDTO.getId());
            movie.setTitle(movieDTO.getTitle());
            movie.setMovieId(movieDTO.getMovieId());
            movie.setGenres(movieDTO.getGenres());

            movieService.saveMovie(movie);
            return "Processed: " + movieDTO.getTitle();
        };
    }
}

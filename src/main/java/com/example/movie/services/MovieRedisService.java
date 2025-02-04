package com.example.movie.services;

import com.example.movie.dtos.MovieDTO;
import com.example.movie.models.Movie;
import com.example.movie.models.MovieRedis;
import com.example.movie.repositories.MovieRedisRepository;
import com.example.movie.repositories.MovieRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MovieRedisService {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieRedisRepository movieRedisRepository;

    private ModelMapper modelMapper = new ModelMapper();

    public MovieRedis createMovie(MovieDTO movieDTO) {
        Movie movieMongo = modelMapper.map(movieDTO, Movie.class);
        movieRepository.save(movieMongo);

        MovieRedis movieRedis = modelMapper.map(movieDTO, MovieRedis.class);
        movieRedisRepository.save(movieRedis);

        return movieRedis;
    }

    public MovieRedis getMovieById(String id) {
        Optional<MovieRedis> movieRedis = movieRedisRepository.findById(id);
        if (movieRedis.isPresent()) {
            return movieRedis.get();
        }

        System.out.println("Cache miss");
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isPresent()) {
            Movie tmpMovie = movie.get();

            MovieRedis movieRedis1 = modelMapper.map(tmpMovie, MovieRedis.class);

            movieRedisRepository.save(movieRedis1);

            return movieRedis1;
        }

        return null;
    }
}

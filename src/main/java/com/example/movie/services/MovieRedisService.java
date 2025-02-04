package com.example.movie.services;

import com.example.movie.dtos.MovieDTO;
import com.example.movie.models.Movie;
import com.example.movie.models.MovieRedis;
import com.example.movie.repositories.MovieRedisRepository;
import com.example.movie.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MovieRedisService {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieRedisRepository movieRedisRepository;

    public MovieRedis createMovie(MovieDTO movie) {
        Movie movieMongo = new Movie();
        movieMongo.setId(movie.getId());
        movieMongo.setMovieId(movie.getMovieId());
        movieMongo.setTitle(movie.getTitle());
        movieMongo.setGenres(movie.getGenres());
        movieMongo.setOwner(movie.getOwner());

        movieRepository.save(movieMongo);

        MovieRedis movieRedis = new MovieRedis();
        movieRedis.setId(movie.getId());
        movieRedis.setMovieId(movie.getMovieId());
        movieRedis.setTitle(movie.getTitle());
        movieRedis.setGenres(movie.getGenres());
        movieRedis.setOwner(movie.getOwner());

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

            MovieRedis movieRedis1 = new MovieRedis();
            movieRedis1.setId(tmpMovie.getId());
            movieRedis1.setMovieId(tmpMovie.getMovieId());
            movieRedis1.setTitle(tmpMovie.getTitle());
            movieRedis1.setGenres(tmpMovie.getGenres());

            movieRedisRepository.save(movieRedis1);

            return movieRedis1;
        }

        return null;
    }
}

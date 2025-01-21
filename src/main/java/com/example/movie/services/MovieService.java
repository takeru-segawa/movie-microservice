package com.example.movie.services;

import com.example.movie.models.Movie;
import com.example.movie.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Optional<Movie> getMovieById(String id) {
        Optional<Movie> movieOptional = movieRepository.findById(id);
        if (movieOptional.isPresent()) {
            return movieOptional;
        }

        return Optional.empty();
    }

    public Movie createMovie(Movie movie) {
        if (movieRepository.findByMovieId(movie.getMovieId()).isPresent()) {
            throw new RuntimeException("Movie with movieId " + movie.getMovieId() + " already exists.");
        }

        return movieRepository.save(movie);
    }

    public Movie updateMovie(String id, Movie movie) {
        Optional<Movie> movieOptional = movieRepository.findById(id);

        if (movieOptional.isPresent()) {
            Movie existingMovie = movieOptional.get();

            if (movie.getMovieId() != null) {
                existingMovie.setMovieId(movie.getMovieId());
            }
            if (movie.getTitle() != null) {
                existingMovie.setTitle(movie.getTitle());
            }
            if (movie.getGenres() != null) {
                existingMovie.setGenres(movie.getGenres());
            }

            return movieRepository.save(existingMovie);
        } else {
            throw new RuntimeException("Movie not found with id: " + id);
        }
    }

    public void deleteMovie(String id) {
        movieRepository.deleteById(id);
    }
}

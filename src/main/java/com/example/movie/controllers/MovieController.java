package com.example.movie.controllers;

import com.example.movie.dtos.MovieDTO;
import com.example.movie.dtos.MovieResponse;
import com.example.movie.models.Movie;
import com.example.movie.services.MovieEventProducer;
import com.example.movie.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/movies")
@CrossOrigin(origins = "http://localhost:4200")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieEventProducer eventProducer;

    @PostMapping("/create")
    public void createMovies(@RequestBody List<MovieDTO> movies) {
        for (MovieDTO movie : movies) {
            eventProducer.publishMovieCreatedEvent(movie);
        }
    }

    @GetMapping
    public ResponseEntity<MovieResponse> getAllMovies(@RequestHeader("Authorization") String authHeader,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(defaultValue = "title") String sortBy,
                                                      @RequestParam(defaultValue = "asc") String sortDirection,
                                                      @RequestParam(defaultValue = "") String search) {
        String token = authHeader.substring(7);

        MovieResponse movieResponse = movieService.getAllMovies(token, page, size, sortBy, sortDirection, search);

        if (movieResponse.getStatus() == 200) {
            return ResponseEntity.status(HttpStatus.OK).body(movieResponse);
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(movieResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable String id) {
        Optional<Movie> movie = movieService.getMovieById(id);
        return movie.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createMovie(@RequestHeader("Authorization") String authHeader, @RequestBody Movie movie) {
        String token = authHeader.substring(7);

        try {
            Movie createdMovie = movieService.createMovie(token, movie);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable String id, @RequestBody Movie movie) {
        Movie updatedMovie = movieService.updateMovie(id, movie);
        return ResponseEntity.ok(updatedMovie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}

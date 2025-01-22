package com.example.movie.services;

import com.example.movie.clients.UserClient;
import com.example.movie.config.JwtUtil;
import com.example.movie.dtos.MovieResponse;
import com.example.movie.dtos.MovieDTO;
import com.example.movie.dtos.UserResponse;
import com.example.movie.models.Movie;
import com.example.movie.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private UserClient userClient;

    public boolean isMovieOwner(String token, String movieId) {
        try {
            String username = jwtUtil.decodeToken(token);

            Optional<Movie> movieOptional = movieRepository.findById(movieId);

            return movieOptional.isPresent() &&
                    username.equals(movieOptional.get().getOwner());
        } catch (RuntimeException e) {
            return false;
        }
    }

    public MovieResponse getAllMovies(String token) {
        try {
            String username = jwtUtil.decodeToken(token);

            UserResponse user = userClient.getUser(token, username);

            MovieResponse movieResponse = new MovieResponse();

            List<Movie> movies = movieRepository.findAllByOwner(user.getData().getId());
            List<MovieDTO> movieResponseDTOS = new ArrayList<>();

            for (Movie movie : movies) {
                MovieDTO movieResponseDTO = new MovieDTO();
                movieResponseDTO.setId(movie.getId());
                movieResponseDTO.setMovieId(movie.getMovieId());
                movieResponseDTO.setTitle(movie.getTitle());
                movieResponseDTO.setGenres(movie.getGenres());
                movieResponseDTO.setOwner(username);

                movieResponseDTOS.add(movieResponseDTO);
            }

            movieResponse.setData(movieResponseDTOS);

            movieResponse.setStatus(user.getStatus());
            movieResponse.setMessage("Success");
            return movieResponse;
        } catch (Exception e) {
            MovieResponse movieResponse = new MovieResponse();
            movieResponse.setStatus(401);
            movieResponse.setMessage("Unauthorized");
            return movieResponse;
        }
    }

    public Optional<Movie> getMovieById(String id) {
        Optional<Movie> movieOptional = movieRepository.findById(id);
        if (movieOptional.isPresent()) {
            return movieOptional;
        }

        return Optional.empty();
    }

    public Movie createMovie(String token, Movie movie) {
        if (movieRepository.findByMovieId(movie.getMovieId()).isPresent()) {
            throw new RuntimeException("Movie with movieId " + movie.getMovieId() + " already exists.");
        }

        String username = jwtUtil.decodeToken(token);

        try {
            // Gọi API lấy userId
            UserResponse user = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8080/api/v1/users/{username}", username)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .block(); // Chờ response

            // Set owner cho movie
            movie.setOwner(user.getData().getId());

            // Lưu movie
            return movieRepository.save(movie);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user ID", e);
        }
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

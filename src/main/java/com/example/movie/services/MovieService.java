package com.example.movie.services;

import com.example.movie.clients.UserClient;
import com.example.movie.config.JwtUtil;
import com.example.movie.dtos.MovieResponse;
import com.example.movie.dtos.MovieDTO;
import com.example.movie.dtos.UserResponse;
import com.example.movie.models.Movie;
import com.example.movie.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieService {
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    public static final String SORT_DESC = "desc";

    public static final String MESSAGE_FORBIDDEN = "forbidden";
    public static final String MESSAGE_SUCCESS = "Success";
    public static final String MESSAGE_UNAUTHORIZED = "Unauthorized";

    public static final String USER_API_URI = "http://localhost:8080/api/v1/users/{username}";

    public static final String HEADER_AUTHORIZATION = "Authorization";

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private UserClient userClient;

    @Autowired
    private StreamBridge streamBridge;

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

//    public MovieResponse getAllMovies(String token) {
//        try {
//            String username = jwtUtil.decodeToken(token);
//
//            UserResponse user = userClient.getUser(token, username);
//
//            MovieResponse movieResponse = new MovieResponse();
//
//            List<Movie> movies = movieRepository.findAllByOwner(user.getData().getId());
//            List<MovieDTO> movieResponseDTOS = new ArrayList<>();
//
//            for (Movie movie : movies) {
//                MovieDTO movieResponseDTO = new MovieDTO();
//                movieResponseDTO.setId(movie.getId());
//                movieResponseDTO.setMovieId(movie.getMovieId());
//                movieResponseDTO.setTitle(movie.getTitle());
//                movieResponseDTO.setGenres(movie.getGenres());
//                movieResponseDTO.setOwner(username);
//
//                movieResponseDTOS.add(movieResponseDTO);
//            }
//
//            movieResponse.setData(movieResponseDTOS);
//
//            movieResponse.setStatus(user.getStatus());
//            movieResponse.setMessage("Success");
//            return movieResponse;
//        } catch (Exception e) {
//            MovieResponse movieResponse = new MovieResponse();
//            movieResponse.setStatus(401);
//            movieResponse.setMessage("Unauthorized");
//            return movieResponse;
//        }
//    }

    public MovieResponse getAllMovies(String token, int page, int size, String sortBy, String sortDirection, String search) {
        try {
            String username = jwtUtil.decodeToken(token);
            UserResponse user = userClient.getUser (token, username);

            MovieResponse movieResponse = new MovieResponse();
            List<Movie> movies;
            Page<Movie> moviePage;

            Sort sort;
            if (SORT_DESC.equalsIgnoreCase(sortDirection)) {
                sort = Sort.by(sortBy).descending();
            } else {
                sort = Sort.by(sortBy).ascending();
            }

            if (ROLE_ADMIN.equals(user.getData().getRole())) {
                moviePage = movieRepository.findAllByTitleContaining(search, PageRequest.of(page, size, sort));
            } else if (ROLE_USER.equals(user.getData().getRole())) {
                moviePage = movieRepository.findAllByOwnerAndTitleContaining(user.getData().getId(), search, PageRequest.of(page, size, sort));
            } else {
                movieResponse.setStatus(403);
                movieResponse.setMessage(MESSAGE_FORBIDDEN);
                movieResponse.setTotalElements(0);
                movieResponse.setTotalPages(0);
                return movieResponse;
            }

            long totalElements = moviePage.getTotalElements();
            int totalPages = moviePage.getTotalPages();

            List<MovieDTO> movieResponseDTOS = moviePage.getContent().stream()
                    .map(movie -> {
                        MovieDTO movieResponseDTO = new MovieDTO();
                        movieResponseDTO.setId(movie.getId());
                        movieResponseDTO.setMovieId(movie.getMovieId());
                        movieResponseDTO.setTitle(movie.getTitle());
                        movieResponseDTO.setGenres(movie.getGenres());
                        movieResponseDTO.setOwner(movie.getOwner());
                        return movieResponseDTO;
                    })
                    .collect(Collectors.toList());

            movieResponse.setData(movieResponseDTOS);
            movieResponse.setStatus(200);
            movieResponse.setMessage(MESSAGE_SUCCESS);
            movieResponse.setTotalElements(totalElements);
            movieResponse.setTotalPages(totalPages);
            return movieResponse;
        } catch (Exception e) {
            MovieResponse movieResponse = new MovieResponse();
            movieResponse.setStatus(401);
            movieResponse.setMessage(MESSAGE_UNAUTHORIZED);
            movieResponse.setTotalElements(0);
            movieResponse.setTotalPages(0);
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
            UserResponse user = webClientBuilder.build()
                    .get()
                    .uri(USER_API_URI, username)
                    .header(HEADER_AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .block();

            movie.setOwner(user.getData().getId());

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

    public void saveMovie(Movie movie) {
        movieRepository.save(movie);
    }

    public void createAMovie(MovieDTO movieDTO) {
        System.out.println("Sending movie: " + movieDTO.getTitle());
        streamBridge.send("processMovies-in-0", movieDTO);
    }
}

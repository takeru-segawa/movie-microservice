package com.example.movie.repositories;

import com.example.movie.models.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {
    Optional<Movie> findByMovieId(Integer movieId);
    List<Movie> findAllByOwner(String owner);
    Page<Movie> findAllByOwnerAndTitleContaining(String owner, String title, Pageable pageable);
    Page<Movie> findAllByTitleContaining(String title, Pageable pageable);
}

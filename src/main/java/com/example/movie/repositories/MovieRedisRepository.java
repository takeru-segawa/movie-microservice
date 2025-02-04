package com.example.movie.repositories;

import com.example.movie.models.MovieRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRedisRepository extends CrudRepository<MovieRedis, String> {
}

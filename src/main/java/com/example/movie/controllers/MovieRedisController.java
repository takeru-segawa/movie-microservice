package com.example.movie.controllers;

import com.example.movie.dtos.MovieDTO;
import com.example.movie.models.MovieRedis;
import com.example.movie.services.MovieRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/movies-redis")
public class MovieRedisController {
    @Autowired
    private MovieRedisService movieService;

    @GetMapping("/{id}")
    public MovieRedis findById(@PathVariable String id) {
        return movieService.getMovieById(id);
    }

    @PostMapping
    public MovieRedis save(@RequestBody MovieDTO movieDTO) {
        return movieService.createMovie(movieDTO);
    }
}

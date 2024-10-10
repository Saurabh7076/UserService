package com.example.user.service.UserService.controller;

import com.example.user.service.UserService.entities.User;
import com.example.user.service.UserService.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/{userId}")
    //@CircuitBreaker(name="RatingHotelCircuitBreaker", fallbackMethod = "ratingHotelFallback")
    //@Retry(name="RatingHotelCircuitRetry", fallbackMethod = "ratingHotelFallback")
    @RateLimiter(name="RatingHotelCircuitRateLimiter", fallbackMethod = "ratingHotelFallback")
    public ResponseEntity<User> getUserById(@PathVariable String userId){
        User user = userService.getUser(userId);
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<User> ratingHotelFallback(String userId, Exception exception){
        User user = User.builder().email("dummy@gmail.com").name("dummy").about("This is dummy user as other service is failing").build();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUser(){
        List<User> allUser = userService.getAllUser();
        return ResponseEntity.ok(allUser);
    }
}

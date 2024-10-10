package com.example.user.service.UserService.service.implementation;

import com.example.user.service.UserService.entities.Hotel;
import com.example.user.service.UserService.entities.Rating;
import com.example.user.service.UserService.entities.User;
import com.example.user.service.UserService.exception.ResourceNotFoundException;
import com.example.user.service.UserService.externalService.HotelService;
import com.example.user.service.UserService.externalService.RatingService;
import com.example.user.service.UserService.repositories.UserRepository;
import com.example.user.service.UserService.service.UserService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private RatingService ratingService;

    @Override
    public User saveUser(User user) {
        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(String userId) {
        User user =  userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found with given id : "+userId));
        //Rating[] ratingListForUser = restTemplate.getForObject("http://RATINGSERVICE/ratings/users/"+userId, Rating[].class);
        List<Rating> ratingList = ratingService.getUserRating(userId);
        List<Rating> updatedRatingListWithHotel = ratingList.stream().map(rating -> {
            //Using rest template
            // ResponseEntity<Hotel> hotel = restTemplate.getForEntity("http://HOTELSERVICE/hotels/"+rating.getHotelId(), Hotel.class);
            //rating.setHotel(hotel.getBody());
            Hotel hotel = hotelService.getHotel(rating.getHotelId());
            rating.setHotel(hotel);
            return rating;
        }).collect(Collectors.toList());
        user.setRatings(updatedRatingListWithHotel);
        return user;
    }
}

package com.example.user.service.UserService.externalService;

import com.example.user.service.UserService.entities.Hotel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="HOTELSERVICE")
public interface HotelService {
    @GetMapping("/hotels/{hotelId}")
    public Hotel getHotel(@PathVariable String hotelId);
}

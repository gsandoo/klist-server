package com.kk.klist.domain.weather.controller;

import com.kk.klist.domain.weather.dto.response.WeatherOutfitResponse;
import com.kk.klist.domain.weather.service.WeatherOutfitService;
import com.kk.klist.global.response.ApiResponse;
import com.kk.klist.global.security.auth.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherOutfitService weatherOutfitService;

    @GetMapping("/outfit")
    public ApiResponse<WeatherOutfitResponse> getOutfit(
            @LoginUser Long userId,
            @RequestParam double latitude,
            @RequestParam double longitude
    ) {
        return ApiResponse.success(weatherOutfitService.getWeatherOutfit(latitude, longitude));
    }
}

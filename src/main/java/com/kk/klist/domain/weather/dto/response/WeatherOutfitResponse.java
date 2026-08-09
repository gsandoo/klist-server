package com.kk.klist.domain.weather.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record WeatherOutfitResponse(
        WeatherInfo weather,
        OutfitInfo outfit
) {

    public record WeatherInfo(
            double temperature,
            double precipitation,
            String condition,
            LocalDateTime observedAt
    ) {
    }

    public record OutfitInfo(
            String temperatureRange,
            List<String> items,
            List<String> additionalTips
    ) {
    }
}

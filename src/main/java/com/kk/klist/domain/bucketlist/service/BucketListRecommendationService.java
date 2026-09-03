package com.kk.klist.domain.bucketlist.service;

import com.kk.klist.domain.bucketlist.dto.response.BucketListRecommendationResponse;
import com.kk.klist.domain.bucketlist.domain.exception.BucketListErrorCode;
import com.kk.klist.domain.bucketlist.domain.exception.BucketListException;
import com.kk.klist.domain.tour.dto.response.TourSpotResponse;
import com.kk.klist.domain.tour.service.TourService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BucketListRecommendationService {

    private static final int COORDINATE_CACHE_SCALE = 3;
    private static final int DEFAULT_RADIUS_METERS = 20_000;
    private static final int RECOMMENDATION_SIZE = 50;
    private static final int TOUR_API_FIRST_PAGE = 1;
    private static final String DEFAULT_LANGUAGE = "ko";
    private static final double EARTH_RADIUS_METERS = 6_371_000;

    private final TourService tourService;

    public List<BucketListRecommendationResponse> findRecommendations(double latitude, double longitude) {
        validateCoordinates(latitude, longitude);
        double cacheLatitude = roundCoordinate(latitude);
        double cacheLongitude = roundCoordinate(longitude);

        return tourService.findNearby(
                        cacheLatitude,
                        cacheLongitude,
                        DEFAULT_RADIUS_METERS,
                        null,
                        DEFAULT_LANGUAGE,
                        TOUR_API_FIRST_PAGE,
                        RECOMMENDATION_SIZE
                ).getContent().stream()
                .map(spot -> toResponse(spot, latitude, longitude))
                .toList();
    }

    private void validateCoordinates(double latitude, double longitude) {
        if (!Double.isFinite(latitude) || !Double.isFinite(longitude)
                || latitude < -90 || latitude > 90
                || longitude < -180 || longitude > 180) {
            throw new BucketListException(BucketListErrorCode.INVALID_COORDINATES);
        }
    }

    private double roundCoordinate(double coordinate) {
        return BigDecimal.valueOf(coordinate)
                .setScale(COORDINATE_CACHE_SCALE, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private BucketListRecommendationResponse toResponse(
            TourSpotResponse spot,
            double userLatitude,
            double userLongitude
    ) {
        return new BucketListRecommendationResponse(
                spot.contentId(),
                spot.contentTypeId(),
                spot.title(),
                spot.latitude(),
                spot.longitude(),
                spot.imageUrl(),
                spot.address(),
                calculateDistance(userLatitude, userLongitude, spot.latitude(), spot.longitude())
        );
    }

    private Double calculateDistance(
            double userLatitude,
            double userLongitude,
            Double placeLatitude,
            Double placeLongitude
    ) {
        if (placeLatitude == null || placeLongitude == null) {
            return null;
        }
        double latitudeDifference = Math.toRadians(placeLatitude - userLatitude);
        double longitudeDifference = Math.toRadians(placeLongitude - userLongitude);
        double haversine = Math.sin(latitudeDifference / 2) * Math.sin(latitudeDifference / 2)
                + Math.cos(Math.toRadians(userLatitude)) * Math.cos(Math.toRadians(placeLatitude))
                * Math.sin(longitudeDifference / 2) * Math.sin(longitudeDifference / 2);
        return EARTH_RADIUS_METERS * 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
    }
}

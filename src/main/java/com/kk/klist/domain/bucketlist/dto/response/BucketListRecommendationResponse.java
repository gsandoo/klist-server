package com.kk.klist.domain.bucketlist.dto.response;

public record BucketListRecommendationResponse(
        String contentId,
        String contentTypeId,
        String title,
        Double latitude,
        Double longitude,
        String imageUrl,
        String address,
        Double distanceMeters
) {
}

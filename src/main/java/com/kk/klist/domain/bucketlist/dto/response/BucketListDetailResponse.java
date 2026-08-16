package com.kk.klist.domain.bucketlist.dto.response;

import com.kk.klist.domain.bucketlist.domain.entity.BucketList;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BucketListDetailResponse(
        Long bucketListId,
        String title,
        String description,
        String category,
        String placeName,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        String distance,
        String imageUrl,
        boolean isCompleted,
        LocalDateTime completedAt,
        long savedCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static BucketListDetailResponse from(BucketList bucketList, String distance) {
        return new BucketListDetailResponse(
                bucketList.getId(),
                bucketList.getTitle(),
                bucketList.getDescription(),
                bucketList.getCategory().getCode(),
                bucketList.getPlaceName(),
                bucketList.getAddress(),
                bucketList.getLatitude(),
                bucketList.getLongitude(),
                distance,
                bucketList.getImageUrl(),
                bucketList.isCompleted(),
                bucketList.getCompletedAt(),
                0L,
                bucketList.getCreatedAt(),
                bucketList.getUpdatedAt()
        );
    }
}

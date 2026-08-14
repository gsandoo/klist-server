package com.kk.klist.domain.bucketlist.dto.response;

import com.kk.klist.domain.bucketlist.domain.entity.BucketList;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BucketListCreateResponse(
        Long bucketListId,
        String title,
        String description,
        String category,
        String placeName,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        String imageUrl,
        boolean isCompleted,
        LocalDateTime completedAt,
        long savedCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static BucketListCreateResponse from(BucketList bucketList) {
        return new BucketListCreateResponse(
                bucketList.getId(),
                bucketList.getTitle(),
                bucketList.getDescription(),
                bucketList.getCategory().getCode(),
                bucketList.getPlaceName(),
                bucketList.getAddress(),
                bucketList.getLatitude(),
                bucketList.getLongitude(),
                bucketList.getImageUrl(),
                bucketList.isCompleted(),
                bucketList.getCompletedAt(),
                0L,
                bucketList.getCreatedAt(),
                bucketList.getUpdatedAt()
        );
    }
}

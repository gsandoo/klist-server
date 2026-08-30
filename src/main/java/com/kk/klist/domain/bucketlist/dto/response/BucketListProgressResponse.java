package com.kk.klist.domain.bucketlist.dto.response;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record BucketListProgressResponse(
        long totalCount,
        long completedCount,
        BigDecimal progressRate
) {

    private static final int PERCENT_SCALE = 1;

    public static BucketListProgressResponse of(long totalCount, long completedCount) {
        BigDecimal progressRate = totalCount == 0
                ? BigDecimal.ZERO.setScale(PERCENT_SCALE, RoundingMode.HALF_UP)
                : BigDecimal.valueOf(completedCount)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalCount), PERCENT_SCALE, RoundingMode.HALF_UP);
        return new BucketListProgressResponse(totalCount, completedCount, progressRate);
    }
}

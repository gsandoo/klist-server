package com.kk.klist.domain.bucketlist.repository;

import org.springframework.data.domain.Pageable;

public record BucketListSearchCondition(
        Long memberId,
        String categoryCode,
        Boolean completed,
        Pageable pageable
) {
}

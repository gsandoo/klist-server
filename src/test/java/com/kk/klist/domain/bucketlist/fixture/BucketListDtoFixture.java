package com.kk.klist.domain.bucketlist.fixture;

import com.kk.klist.domain.bucketlist.dto.request.BucketListCreateRequest;
import java.math.BigDecimal;

public class BucketListDtoFixture {

    public static BucketListCreateRequest createRequest() {
        return new BucketListCreateRequest(
                "Explore a K-drama filming spot",
                "Visit famous K-drama shooting locations.",
                "K_DRAMA",
                "Bukchon Hanok Village",
                "Bukchon, Seoul",
                new BigDecimal("37.5826000"),
                new BigDecimal("126.9830000"),
                "https://example.com/images/bukchon.jpg"
        );
    }
}

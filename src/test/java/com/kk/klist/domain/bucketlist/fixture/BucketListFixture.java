package com.kk.klist.domain.bucketlist.fixture;

import com.kk.klist.domain.bucketlist.domain.entity.BucketList;
import com.kk.klist.domain.bucketlist.domain.entity.Category;
import java.math.BigDecimal;
import org.springframework.test.util.ReflectionTestUtils;

public class BucketListFixture {

    public static BucketList incompleteBucketList() {
        return BucketList.create(
                1L,
                Category.create("K_DRAMA", "K-drama"),
                "Explore a K-drama filming spot",
                "Visit famous K-drama shooting locations.",
                "Bukchon Hanok Village",
                "Bukchon, Seoul",
                new BigDecimal("37.5826000"),
                new BigDecimal("126.9830000"),
                "https://example.com/images/bukchon.jpg"
        );
    }

    public static BucketList incompleteBucketListWithId(Long bucketListId) {
        BucketList bucketList = incompleteBucketList();
        ReflectionTestUtils.setField(bucketList, "id", bucketListId);
        return bucketList;
    }
}

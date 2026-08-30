package com.kk.klist.domain.bucketlist.repository;

import com.kk.klist.domain.bucketlist.domain.entity.BucketList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface BucketListRepository {

    BucketList save(BucketList bucketList);

    Optional<BucketList> findById(Long bucketListId);

    void delete(BucketList bucketList);

    Page<BucketList> searchBucketList(BucketListSearchCondition condition);

    long countCompletedInPeriod(Long memberId, LocalDateTime start, LocalDateTime end);

    List<BucketList> findAllCompletedInPeriod(Long memberId, LocalDateTime start, LocalDateTime end);
}

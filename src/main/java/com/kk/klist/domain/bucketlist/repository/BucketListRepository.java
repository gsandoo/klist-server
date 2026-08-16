package com.kk.klist.domain.bucketlist.repository;

import com.kk.klist.domain.bucketlist.domain.entity.BucketList;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface BucketListRepository {

    BucketList save(BucketList bucketList);

    Optional<BucketList> findById(Long bucketListId);

    Page<BucketList> searchBucketList(BucketListSearchCondition condition);
}

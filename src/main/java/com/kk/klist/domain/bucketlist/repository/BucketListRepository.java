package com.kk.klist.domain.bucketlist.repository;

import com.kk.klist.domain.bucketlist.domain.entity.BucketList;
import org.springframework.data.domain.Page;

public interface BucketListRepository {

    BucketList save(BucketList bucketList);

    Page<BucketList> searchBucketList(BucketListSearchCondition condition);
}

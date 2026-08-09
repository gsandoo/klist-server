package com.kk.klist.domain.bucketlist.repository;

import com.kk.klist.domain.bucketlist.domain.entity.BucketList;

public interface BucketListRepository {

    BucketList save(BucketList bucketList);
}

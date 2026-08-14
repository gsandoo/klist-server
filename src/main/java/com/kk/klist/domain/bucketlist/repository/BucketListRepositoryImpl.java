package com.kk.klist.domain.bucketlist.repository;

import com.kk.klist.domain.bucketlist.domain.entity.BucketList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BucketListRepositoryImpl implements BucketListRepository {

    private final BucketListJpaRepository bucketListJpaRepository;

    @Override
    public BucketList save(BucketList bucketList) {
        return bucketListJpaRepository.save(bucketList);
    }
}

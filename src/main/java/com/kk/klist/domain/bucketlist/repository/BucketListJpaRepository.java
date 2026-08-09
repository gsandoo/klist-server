package com.kk.klist.domain.bucketlist.repository;

import com.kk.klist.domain.bucketlist.domain.entity.BucketList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BucketListJpaRepository extends JpaRepository<BucketList, Long> {
}

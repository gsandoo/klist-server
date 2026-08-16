package com.kk.klist.domain.bucketlist.repository;

import com.kk.klist.domain.bucketlist.domain.entity.BucketList;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BucketListJpaRepository extends JpaRepository<BucketList, Long> {

    @Override
    @EntityGraph(attributePaths = "category")
    Optional<BucketList> findById(Long bucketListId);
}

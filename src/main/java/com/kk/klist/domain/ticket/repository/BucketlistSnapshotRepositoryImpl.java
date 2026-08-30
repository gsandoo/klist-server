package com.kk.klist.domain.ticket.repository;

import com.kk.klist.domain.ticket.domain.entity.BucketlistSnapshot;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BucketlistSnapshotRepositoryImpl implements BucketlistSnapshotRepository {

    private final BucketlistSnapshotJpaRepository bucketlistSnapshotJpaRepository;

    @Override
    public List<BucketlistSnapshot> saveAll(List<BucketlistSnapshot> snapshots) {
        return bucketlistSnapshotJpaRepository.saveAll(snapshots);
    }
}

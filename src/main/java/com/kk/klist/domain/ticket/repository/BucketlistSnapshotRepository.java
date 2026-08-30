package com.kk.klist.domain.ticket.repository;

import com.kk.klist.domain.ticket.domain.entity.BucketlistSnapshot;
import java.util.List;

public interface BucketlistSnapshotRepository {

    List<BucketlistSnapshot> saveAll(List<BucketlistSnapshot> snapshots);
}

package com.kk.klist.domain.ticket.repository;

import com.kk.klist.domain.ticket.domain.entity.BucketlistSnapshot;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BucketlistSnapshotJpaRepository extends JpaRepository<BucketlistSnapshot, Long> {

    List<BucketlistSnapshot> findAllByTicketId(Long ticketId);
}

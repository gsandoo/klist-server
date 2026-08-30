package com.kk.klist.domain.ticket.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bucketlist_snapshots")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BucketlistSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ticketId;

    private Long originBucketlistId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private LocalDateTime completedAt;

    @Column(nullable = false, length = 30)
    private String categoryCode;

    @Builder
    private BucketlistSnapshot(Long ticketId, Long originBucketlistId, String title,
            LocalDateTime completedAt, String categoryCode) {
        this.ticketId = ticketId;
        this.originBucketlistId = originBucketlistId;
        this.title = title;
        this.completedAt = completedAt;
        this.categoryCode = categoryCode;
    }

    public static BucketlistSnapshot create(Long ticketId, Long originBucketlistId, String title,
            LocalDateTime completedAt, String categoryCode) {
        return BucketlistSnapshot.builder()
                .ticketId(ticketId)
                .originBucketlistId(originBucketlistId)
                .title(title)
                .completedAt(completedAt)
                .categoryCode(categoryCode)
                .build();
    }
}

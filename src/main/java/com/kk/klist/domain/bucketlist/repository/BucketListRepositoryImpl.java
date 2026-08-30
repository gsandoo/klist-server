package com.kk.klist.domain.bucketlist.repository;

import static com.kk.klist.domain.bucketlist.domain.entity.QBucketList.bucketList;

import com.kk.klist.domain.bucketlist.domain.entity.BucketList;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BucketListRepositoryImpl implements BucketListRepository {

    private final BucketListJpaRepository bucketListJpaRepository;
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    @Override
    public BucketList save(BucketList bucketList) {
        return bucketListJpaRepository.save(bucketList);
    }

    @Override
    public Optional<BucketList> findById(Long bucketListId) {
        return bucketListJpaRepository.findById(bucketListId);
    }

    @Override
    public void delete(BucketList bucketList) {
        bucketListJpaRepository.delete(bucketList);
    }

    @Override
    public Page<BucketList> searchBucketList(BucketListSearchCondition condition) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BucketList> contentQuery = criteriaBuilder.createQuery(BucketList.class);
        Root<BucketList> bucketList = contentQuery.from(BucketList.class);
        bucketList.fetch("category", JoinType.INNER);

        contentQuery
                .select(bucketList)
                .where(createPredicates(criteriaBuilder, bucketList, condition))
                .orderBy(
                        criteriaBuilder.desc(bucketList.get("createdAt")),
                        criteriaBuilder.desc(bucketList.get("id"))
                );

        TypedQuery<BucketList> query = entityManager.createQuery(contentQuery);
        query.setFirstResult((int) condition.pageable().getOffset());
        query.setMaxResults(condition.pageable().getPageSize());
        List<BucketList> content = query.getResultList();

        return PageableExecutionUtils.getPage(
                content,
                condition.pageable(),
                () -> countBucketLists(criteriaBuilder, condition)
        );
    }

    private long countBucketLists(CriteriaBuilder criteriaBuilder, BucketListSearchCondition condition) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<BucketList> bucketList = countQuery.from(BucketList.class);
        countQuery
                .select(criteriaBuilder.count(bucketList))
                .where(createPredicates(criteriaBuilder, bucketList, condition));
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    @Override
    public long countCompletedInPeriod(Long memberId, LocalDateTime start, LocalDateTime end) {
        Long count = queryFactory
                .select(bucketList.count())
                .from(bucketList)
                .where(
                        memberIdEq(memberId),
                        completedIsTrue(),
                        completedAtGoe(start),
                        completedAtLt(end)
                )
                .fetchOne();
        return count != null ? count : 0L;
    }

    @Override
    public List<BucketList> findAllCompletedInPeriod(Long memberId, LocalDateTime start, LocalDateTime end) {
        return queryFactory
                .selectFrom(bucketList)
                .where(
                        memberIdEq(memberId),
                        completedIsTrue(),
                        completedAtGoe(start),
                        completedAtLt(end)
                )
                .fetch();
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return bucketList.memberId.eq(memberId);
    }

    private BooleanExpression completedIsTrue() {
        return bucketList.completed.isTrue();
    }

    private BooleanExpression completedAtGoe(LocalDateTime start) {
        return bucketList.completedAt.goe(start);
    }

    private BooleanExpression completedAtLt(LocalDateTime end) {
        return bucketList.completedAt.lt(end);
    }

    private Predicate[] createPredicates(CriteriaBuilder criteriaBuilder, Root<BucketList> bucketList,
            BucketListSearchCondition condition) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(bucketList.get("memberId"), condition.memberId()));

        if (condition.categoryCode() != null) {
            predicates.add(criteriaBuilder.equal(bucketList.get("category").get("code"), condition.categoryCode()));
        }
        if (condition.completed() != null) {
            predicates.add(criteriaBuilder.equal(bucketList.get("completed"), condition.completed()));
        }

        return predicates.toArray(Predicate[]::new);
    }
}

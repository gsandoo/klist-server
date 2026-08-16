package com.kk.klist.domain.bucketlist.service;

import com.kk.klist.domain.bucketlist.domain.entity.BucketList;
import com.kk.klist.domain.bucketlist.domain.entity.Category;
import com.kk.klist.domain.bucketlist.domain.exception.BucketListErrorCode;
import com.kk.klist.domain.bucketlist.domain.exception.BucketListException;
import com.kk.klist.domain.bucketlist.dto.request.BucketListCreateRequest;
import com.kk.klist.domain.bucketlist.dto.response.BucketListCreateResponse;
import com.kk.klist.domain.bucketlist.dto.response.BucketListSummaryResponse;
import com.kk.klist.domain.bucketlist.repository.BucketListRepository;
import com.kk.klist.domain.bucketlist.repository.BucketListSearchCondition;
import com.kk.klist.domain.bucketlist.repository.CategoryRepository;
import com.kk.klist.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BucketListService {

    private static final String ALL_CATEGORIES = "ALL";

    private final BucketListRepository bucketListRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public BucketListCreateResponse createBucketList(Long memberId, BucketListCreateRequest request) {
        Category category = categoryRepository.findByCode(request.category())
                .orElseThrow(() -> new BucketListException(BucketListErrorCode.CATEGORY_NOT_FOUND));

        BucketList bucketList = BucketList.create(
                memberId,
                category,
                request.title(),
                request.description(),
                request.placeName(),
                request.address(),
                request.latitude(),
                request.longitude(),
                request.imageUrl()
        );

        return BucketListCreateResponse.from(bucketListRepository.save(bucketList));
    }

    public PageResponse<BucketListSummaryResponse> findBucketLists(Long memberId, String category,
            Boolean completed, Pageable pageable) {
        String categoryCode = resolveCategoryCode(category);
        BucketListSearchCondition condition = new BucketListSearchCondition(
                memberId,
                categoryCode,
                completed,
                pageable
        );
        Page<BucketListSummaryResponse> bucketLists = bucketListRepository.searchBucketList(condition)
                .map(BucketListSummaryResponse::from);
        return PageResponse.of(bucketLists);
    }

    private String resolveCategoryCode(String category) {
        if (ALL_CATEGORIES.equalsIgnoreCase(category)) {
            return null;
        }
        return categoryRepository.findByCode(category)
                .map(Category::getCode)
                .orElseThrow(() -> new BucketListException(BucketListErrorCode.CATEGORY_NOT_FOUND));
    }
}

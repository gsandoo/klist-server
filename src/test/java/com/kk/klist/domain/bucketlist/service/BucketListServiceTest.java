package com.kk.klist.domain.bucketlist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.kk.klist.domain.bucketlist.domain.entity.BucketList;
import com.kk.klist.domain.bucketlist.domain.entity.Category;
import com.kk.klist.domain.bucketlist.domain.exception.BucketListErrorCode;
import com.kk.klist.domain.bucketlist.domain.exception.BucketListException;
import com.kk.klist.domain.bucketlist.dto.request.BucketListCreateRequest;
import com.kk.klist.domain.bucketlist.dto.response.BucketListCreateResponse;
import com.kk.klist.domain.bucketlist.fixture.BucketListDtoFixture;
import com.kk.klist.domain.bucketlist.repository.BucketListRepository;
import com.kk.klist.domain.bucketlist.repository.CategoryRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BucketListServiceTest {

    @InjectMocks
    private BucketListService bucketListService;

    @Mock
    private BucketListRepository bucketListRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("정상적인 요청으로 버킷리스트를 생성하면 저장 결과가 반환된다")
    void createBucketList_whenValidRequest_savesBucketListAndReturnsResponse() {
        // given
        Long memberId = 1L;
        BucketListCreateRequest request = BucketListDtoFixture.createRequest();
        Category category = Category.create("K_DRAMA", "K-drama");
        given(categoryRepository.findByCode(request.category())).willReturn(Optional.of(category));
        given(bucketListRepository.save(any(BucketList.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        BucketListCreateResponse response = bucketListService.createBucketList(memberId, request);

        // then
        assertThat(response.title()).isEqualTo(request.title());
        assertThat(response.category()).isEqualTo(request.category());
        assertThat(response.isCompleted()).isFalse();
        then(bucketListRepository).should(times(1)).save(any(BucketList.class));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 생성하면 CategoryNotFound 예외가 발생된다")
    void createBucketList_whenCategoryNotFound_throwsCategoryNotFoundException() {
        // given
        BucketListCreateRequest request = BucketListDtoFixture.createRequest();
        given(categoryRepository.findByCode(request.category())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bucketListService.createBucketList(1L, request))
                .isInstanceOf(BucketListException.class)
                .satisfies(error -> assertThat(((BucketListException) error).getErrorCode())
                        .isEqualTo(BucketListErrorCode.CATEGORY_NOT_FOUND));
        then(bucketListRepository).should(never()).save(any(BucketList.class));
    }
}

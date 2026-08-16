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
import com.kk.klist.domain.bucketlist.dto.response.BucketListSummaryResponse;
import com.kk.klist.domain.bucketlist.fixture.BucketListDtoFixture;
import com.kk.klist.domain.bucketlist.fixture.BucketListFixture;
import com.kk.klist.domain.bucketlist.repository.BucketListRepository;
import com.kk.klist.domain.bucketlist.repository.BucketListSearchCondition;
import com.kk.klist.domain.bucketlist.repository.CategoryRepository;
import com.kk.klist.global.response.PageResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class BucketListServiceTest {

    @InjectMocks
    private BucketListService bucketListService;

    @Mock
    private BucketListRepository bucketListRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("전체 카테고리로 내 버킷리스트를 조회하면 페이징된 목록이 반환된다")
    void findBucketLists_whenCategoryAll_returnsPagedBucketLists() {
        // given
        Long memberId = 1L;
        PageRequest pageable = PageRequest.of(0, 10);
        BucketList bucketList = BucketListFixture.incompleteBucketListWithId(21L);
        given(bucketListRepository.searchBucketList(any(BucketListSearchCondition.class)))
                .willReturn(new PageImpl<>(List.of(bucketList), pageable, 1));

        // when
        PageResponse<BucketListSummaryResponse> response =
                bucketListService.findBucketLists(memberId, "ALL", false, pageable);

        // then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().getFirst().bucketListId()).isEqualTo(21L);
        assertThat(response.getContent().getFirst().isCompleted()).isFalse();
        ArgumentCaptor<BucketListSearchCondition> conditionCaptor =
                ArgumentCaptor.forClass(BucketListSearchCondition.class);
        then(bucketListRepository).should(times(1)).searchBucketList(conditionCaptor.capture());
        assertThat(conditionCaptor.getValue().memberId()).isEqualTo(memberId);
        assertThat(conditionCaptor.getValue().categoryCode()).isNull();
        assertThat(conditionCaptor.getValue().completed()).isFalse();
    }

    @Test
    @DisplayName("존재하는 카테고리로 내 버킷리스트를 조회하면 해당 카테고리 조건이 전달된다")
    void findBucketLists_whenCategoryExists_passesCategoryCondition() {
        // given
        Long memberId = 1L;
        PageRequest pageable = PageRequest.of(0, 10);
        Category category = Category.create("K_DRAMA", "K-drama");
        given(categoryRepository.findByCode("K_DRAMA")).willReturn(Optional.of(category));
        given(bucketListRepository.searchBucketList(any(BucketListSearchCondition.class)))
                .willReturn(new PageImpl<>(List.of(), pageable, 0));

        // when
        bucketListService.findBucketLists(memberId, "K_DRAMA", null, pageable);

        // then
        ArgumentCaptor<BucketListSearchCondition> conditionCaptor =
                ArgumentCaptor.forClass(BucketListSearchCondition.class);
        then(bucketListRepository).should(times(1)).searchBucketList(conditionCaptor.capture());
        assertThat(conditionCaptor.getValue().categoryCode()).isEqualTo("K_DRAMA");
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 내 버킷리스트를 조회하면 CategoryNotFound 예외가 발생된다")
    void findBucketLists_whenCategoryNotFound_throwsCategoryNotFoundException() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        given(categoryRepository.findByCode("K_STAR")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bucketListService.findBucketLists(1L, "K_STAR", null, pageable))
                .isInstanceOf(BucketListException.class)
                .satisfies(error -> assertThat(((BucketListException) error).getErrorCode())
                        .isEqualTo(BucketListErrorCode.CATEGORY_NOT_FOUND));
        then(bucketListRepository).should(never()).searchBucketList(any(BucketListSearchCondition.class));
    }

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

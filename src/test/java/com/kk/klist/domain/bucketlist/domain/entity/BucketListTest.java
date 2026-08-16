package com.kk.klist.domain.bucketlist.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.kk.klist.domain.bucketlist.domain.exception.BucketListErrorCode;
import com.kk.klist.domain.bucketlist.domain.exception.BucketListException;
import com.kk.klist.domain.bucketlist.fixture.BucketListFixture;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BucketListTest {

    @Test
    @DisplayName("정상적인 정보로 버킷리스트를 생성하면 미완료 상태로 생성된다")
    void create_whenValidInput_createsIncompleteBucketList() {
        // given

        // when
        BucketList bucketList = BucketListFixture.incompleteBucketList();

        // then
        assertThat(bucketList.getMemberId()).isEqualTo(1L);
        assertThat(bucketList.getCategory().getCode()).isEqualTo("K_DRAMA");
        assertThat(bucketList.isCompleted()).isFalse();
        assertThat(bucketList.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("위도만 입력하면 IncompleteCoordinates 예외가 발생된다")
    void create_whenOnlyLatitudeProvided_throwsIncompleteCoordinatesException() {
        // given
        Category category = Category.create("K_DRAMA", "K-drama");

        // when & then
        assertThatThrownBy(() -> BucketList.create(
                1L, category, "title", null, null, null,
                new BigDecimal("37.5"), null, null
        ))
                .isInstanceOf(BucketListException.class)
                .satisfies(error -> assertThat(((BucketListException) error).getErrorCode())
                        .isEqualTo(BucketListErrorCode.INCOMPLETE_COORDINATES));
    }

    @Test
    @DisplayName("위도 범위를 벗어나면 InvalidCoordinates 예외가 발생된다")
    void create_whenLatitudeOutOfRange_throwsInvalidCoordinatesException() {
        // given
        Category category = Category.create("K_DRAMA", "K-drama");

        // when & then
        assertThatThrownBy(() -> BucketList.create(
                1L, category, "title", null, null, null,
                new BigDecimal("91"), new BigDecimal("126"), null
        ))
                .isInstanceOf(BucketListException.class)
                .satisfies(error -> assertThat(((BucketListException) error).getErrorCode())
                        .isEqualTo(BucketListErrorCode.INVALID_COORDINATES));
    }
}

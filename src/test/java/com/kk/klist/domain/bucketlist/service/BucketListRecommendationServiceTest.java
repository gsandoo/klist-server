package com.kk.klist.domain.bucketlist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.kk.klist.domain.bucketlist.domain.exception.BucketListException;
import com.kk.klist.domain.bucketlist.dto.response.BucketListRecommendationResponse;
import com.kk.klist.domain.tour.dto.response.TourSpotResponse;
import com.kk.klist.domain.tour.service.TourService;
import com.kk.klist.global.response.PageResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class BucketListRecommendationServiceTest {

    @InjectMocks
    private BucketListRecommendationService bucketListRecommendationService;

    @Mock
    private TourService tourService;

    @Test
    @DisplayName("추천 목록을 조회하면 반올림한 위치로 관광 API를 한 번 호출하고 실제 거리를 반환한다")
    void findRecommendations_whenValidCoordinates_returnsNearbyPlaces() {
        // given
        double latitude = 37.56654;
        double longitude = 126.97804;
        TourSpotResponse spot = new TourSpotResponse(
                "126508", "12", "경복궁", 37.5796, 126.9770,
                "https://example.com/gyeongbokgung.jpg", "서울특별시 종로구", 0.0);
        PageResponse<TourSpotResponse> tourResponse = PageResponse.of(
                new PageImpl<>(List.of(spot), PageRequest.of(0, 50), 1));
        given(tourService.findNearby(37.567, 126.978, 20_000, null, "ko", 1, 50))
                .willReturn(tourResponse);

        // when
        List<BucketListRecommendationResponse> result =
                bucketListRecommendationService.findRecommendations(latitude, longitude);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().contentId()).isEqualTo("126508");
        assertThat(result.getFirst().distanceMeters()).isBetween(1_400.0, 1_500.0);
    }

    @Test
    @DisplayName("유효하지 않은 위치로 추천 목록을 조회하면 BucketListException이 발생된다")
    void findRecommendations_whenCoordinatesInvalid_throwsBucketListException() {
        // when & then
        assertThatThrownBy(() -> bucketListRecommendationService.findRecommendations(91.0, 126.9780))
                .isInstanceOf(BucketListException.class);
    }
}

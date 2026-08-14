package com.kk.klist.domain.bucketlist.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kk.klist.domain.bucketlist.dto.request.BucketListCreateRequest;
import com.kk.klist.domain.bucketlist.dto.response.BucketListCreateResponse;
import com.kk.klist.domain.bucketlist.service.BucketListService;
import com.kk.klist.global.security.auth.CustomUserDetails;
import com.kk.klist.global.security.auth.Role;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BucketListController.class)
class BucketListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BucketListService bucketListService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMappingContext;

    @Test
    @DisplayName("POST /api/v1/bucket-lists 요청이 유효하면 201과 생성 정보가 반환된다")
    void createBucketList_whenValidRequest_returns201WithBody() throws Exception {
        // given
        Long memberId = 1L;
        BucketListCreateResponse response = new BucketListCreateResponse(
                21L,
                "Explore a K-drama filming spot",
                "Visit famous K-drama shooting locations.",
                "K_DRAMA",
                "Bukchon Hanok Village",
                "Bukchon, Seoul",
                new BigDecimal("37.5826000"),
                new BigDecimal("126.9830000"),
                "https://example.com/images/bukchon.jpg",
                false,
                null,
                0L,
                LocalDateTime.of(2026, 3, 18, 6, 51),
                LocalDateTime.of(2026, 3, 18, 6, 51)
        );
        given(bucketListService.createBucketList(eq(memberId), any(BucketListCreateRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/bucket-lists")
                        .with(authentication(createAuthentication(memberId)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.bucketListId").value(21L))
                .andExpect(jsonPath("$.data.category").value("K_DRAMA"))
                .andExpect(jsonPath("$.data.isCompleted").value(false));
        then(bucketListService).should(times(1))
                .createBucketList(eq(memberId), any(BucketListCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/bucket-lists 요청의 제목이 비어 있으면 400이 반환된다")
    void createBucketList_whenTitleBlank_returns400() throws Exception {
        // given
        Long memberId = 1L;

        // when & then
        mockMvc.perform(post("/api/v1/bucket-lists")
                        .with(authentication(createAuthentication(memberId)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody().replace("Explore a K-drama filming spot", "")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("G002"))
                .andExpect(jsonPath("$.errors[0].field").value("title"));
    }

    private UsernamePasswordAuthenticationToken createAuthentication(Long memberId) {
        CustomUserDetails userDetails = new CustomUserDetails(memberId, Role.USER);
        return new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());
    }

    private String validRequestBody() {
        return """
                {
                  "title": "Explore a K-drama filming spot",
                  "description": "Visit famous K-drama shooting locations.",
                  "category": "K_DRAMA",
                  "placeName": "Bukchon Hanok Village",
                  "address": "Bukchon, Seoul",
                  "latitude": 37.5826,
                  "longitude": 126.9830,
                  "imageUrl": "https://example.com/images/bukchon.jpg"
                }
                """;
    }
}

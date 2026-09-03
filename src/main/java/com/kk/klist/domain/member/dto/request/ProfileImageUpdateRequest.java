package com.kk.klist.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProfileImageUpdateRequest(

        @NotBlank(message = "이미지 파일이 필요합니다.")
        String profileImage
) {}

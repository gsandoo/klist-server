package com.kk.klist.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OnboardingRequest(

        @NotBlank(message = "필수값입니다.")
        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하여야 합니다.")
        String nickname,

        String profileImage,

        @NotBlank(message = "필수값입니다.")
        @Pattern(
                regexp = "^(KR|US|JP|CN|TW|VN|TH|PH|FR|DE|GB|RU|ES)$",
                message = "유효하지 않은 국가 코드입니다."
        )
        String nationality,

        @NotBlank(message = "필수값입니다.")
        @Pattern(
                regexp = "^(ko|en|ja|zh-CN|zh-TW|ru|es|de|fr)$",
                message = "지원하지 않는 언어입니다."
        )
        String preferredLanguage
) {}

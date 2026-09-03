package com.kk.klist.domain.member.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(

        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하여야 합니다.")
        String nickname,

        @Pattern(
                regexp = "^(KR|US|JP|CN|TW|VN|TH|PH|FR|DE|GB|RU|ES)$",
                message = "유효하지 않은 국가 코드입니다."
        )
        String nationality
) {}

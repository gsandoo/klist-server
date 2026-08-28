package com.kk.klist.domain.member.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record WithdrawRequest(
        @NotEmpty(message = "탈퇴 이유를 최소 1개 이상 입력해주세요.")
        List<String> reasons
) {}

package com.kk.klist.domain.member.controller;

import com.kk.klist.domain.member.dto.response.MemberMeResponse;
import com.kk.klist.domain.member.domain.entity.Member;
import com.kk.klist.domain.member.service.MemberService;
import com.kk.klist.global.response.ApiResponse;
import com.kk.klist.global.security.auth.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberMeResponse>> me(@LoginUser Long memberId) {
        Member member = memberService.getById(memberId);
        return ResponseEntity.ok(ApiResponse.success(MemberMeResponse.from(member)));
    }
}

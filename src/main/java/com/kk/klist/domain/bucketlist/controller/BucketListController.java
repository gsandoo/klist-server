package com.kk.klist.domain.bucketlist.controller;

import com.kk.klist.domain.bucketlist.dto.request.BucketListCreateRequest;
import com.kk.klist.domain.bucketlist.dto.response.BucketListCreateResponse;
import com.kk.klist.domain.bucketlist.service.BucketListService;
import com.kk.klist.global.response.ApiResponse;
import com.kk.klist.global.security.auth.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bucket-lists")
@RequiredArgsConstructor
public class BucketListController {

    private final BucketListService bucketListService;

    @PostMapping
    public ResponseEntity<ApiResponse<BucketListCreateResponse>> createBucketList(
            @LoginUser Long userId,
            @Valid @RequestBody BucketListCreateRequest request
    ) {
        BucketListCreateResponse response = bucketListService.createBucketList(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }
}

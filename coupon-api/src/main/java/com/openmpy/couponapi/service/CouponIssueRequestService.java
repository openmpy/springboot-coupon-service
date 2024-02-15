package com.openmpy.couponapi.service;

import com.openmpy.couponapi.controller.dto.CouponIssueRequestDto;
import com.openmpy.couponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponIssueRequestService {

    private final CouponIssueService couponIssueService;
    private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());

    public void issueRequestV1(CouponIssueRequestDto requestDto) {
        synchronized (this) {
            couponIssueService.issue(requestDto.couponId(), requestDto.userId());
        }
        log.info("쿠폰 발급 완료. couponId: {}, userId: {}", requestDto.couponId(), requestDto.userId());
    }
}

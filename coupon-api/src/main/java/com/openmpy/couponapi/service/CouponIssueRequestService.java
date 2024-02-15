package com.openmpy.couponapi.service;

import com.openmpy.couponapi.controller.dto.CouponIssueRequestDto;
import com.openmpy.couponcore.component.DistributeLockExecutor;
import com.openmpy.couponcore.service.AsyncCouponIssueServiceV1;
import com.openmpy.couponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponIssueRequestService {

    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final AsyncCouponIssueServiceV1 asyncCouponIssueServiceV1;
    private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());

    public void issueRequestV1(CouponIssueRequestDto requestDto) {
//        distributeLockExecutor.execute("lock_" + requestDto.couponId(), 10000, 10000, () ->
//                couponIssueService.issue(requestDto.couponId(), requestDto.userId())
//        );
        couponIssueService.issue(requestDto.couponId(), requestDto.userId());
        log.info("쿠폰 발급 완료. couponId: {}, userId: {}", requestDto.couponId(), requestDto.userId());
    }

    public void asyncIssueRequestV1(CouponIssueRequestDto requestDto) {
        asyncCouponIssueServiceV1.issue(requestDto.couponId(), requestDto.userId());
    }
}

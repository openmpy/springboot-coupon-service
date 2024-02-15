package com.openmpy.couponcore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openmpy.couponcore.component.DistributeLockExecutor;
import com.openmpy.couponcore.exception.CouponIssueException;
import com.openmpy.couponcore.model.Coupon;
import com.openmpy.couponcore.repository.redis.RedisRepository;
import com.openmpy.couponcore.repository.redis.dto.CouponIssueRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.openmpy.couponcore.exception.ErrorCode.*;
import static com.openmpy.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static com.openmpy.couponcore.util.CouponRedisUtils.getIssueRequestQueueKey;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV1 {

    private final RedisRepository redisRepository;
    private final CouponIssueRedisService couponIssueRedisService;
    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void issue(long couponId, long userId) {
        Coupon coupon = couponIssueService.findCoupon(couponId);

        if (!coupon.availableIssueDate()) {
            throw new CouponIssueException(INVALID_COUPON_ISSUE_DATE,
                    "발급 가능한 일자가 아닙니다. couponId: %s, issueStart: %s, issueEnd: %s"
                            .formatted(couponId, coupon.getDateIssuedStart(), coupon.getDateIssuedEnd()));
        }

        distributeLockExecutor.execute("lock_%s".formatted(couponId), 3000, 3000, () -> {
            if (!couponIssueRedisService.availableTotalIssueQuantity(coupon.getTotalQuantity(), couponId)) {
                throw new CouponIssueException(INVALID_COUPON_ISSUE_QUANTITY,
                        "발급 가능한 수량을 초과합니다. couponId: %s, userId: %s".formatted(couponId, userId));
            }
            if (!couponIssueRedisService.availableUserIssueQuantity(couponId, userId)) {
                throw new CouponIssueException(DUPLICATED_COUPON_ISSUE,
                        "이미 발급 요청이 처리됐습니다. couponId: %s, userId: %s".formatted(couponId, userId));
            }

            issueRequest(couponId, userId);
        });
    }

    private void issueRequest(long couponId, long userId) {
        CouponIssueRequest issueRequest = new CouponIssueRequest(couponId, userId);
        try {
            String value = objectMapper.writeValueAsString(issueRequest);
            redisRepository.sAdd(getIssueRequestKey(couponId), String.valueOf(userId));
            redisRepository.rPush(getIssueRequestQueueKey(), value);
        } catch (JsonProcessingException e) {
            throw new CouponIssueException(FAIL_COUPON_ISSUE_REQUEST, "input: %s".formatted(issueRequest));
        }
    }
}

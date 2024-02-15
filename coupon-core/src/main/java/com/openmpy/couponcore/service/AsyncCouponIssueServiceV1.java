package com.openmpy.couponcore.service;

import com.openmpy.couponcore.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV1 {

    private final RedisRepository redisRepository;

    public void issue(long couponId, long userId) {
        // 1. 유정의 요청을 sorted set 적재
        String key = "issue.request.sorted.set.couponId=%s".formatted(couponId);
        redisRepository.zAdd(key, String.valueOf(userId), System.currentTimeMillis());

        // 2. 유저의 요청 순서를 조회
        // 3. 조회 결과를 선착순 조건과 비교
        // 4. 쿠폰 발급 queue에 적재
    }
}

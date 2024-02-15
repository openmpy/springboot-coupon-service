package com.openmpy.couponcore.service;

import com.openmpy.couponcore.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.openmpy.couponcore.util.CouponRedisUtils.getIssueRequestKey;

@RequiredArgsConstructor
@Service
public class CouponIssueRedisService {

    private final RedisRepository redisRepository;

    public boolean availableTotalIssueQuantity(Integer totalQuantity, long couponId) {
        if (totalQuantity == null) {
            return true;
        }

        String key = getIssueRequestKey(couponId);
        return totalQuantity > redisRepository.sCard(key);
    }

    public boolean availableUserIssueQuantity(long couponId, long userId) {
        String key = getIssueRequestKey(couponId);
        return !redisRepository.sIsMember(key, String.valueOf(userId));
    }
}

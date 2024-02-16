package com.openmpy.couponcore.repository.redis.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.openmpy.couponcore.exception.CouponIssueException;
import com.openmpy.couponcore.model.Coupon;
import com.openmpy.couponcore.model.CouponType;

import java.time.LocalDateTime;

import static com.openmpy.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_DATE;
import static com.openmpy.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_QUANTITY;

public record CouponRedisEntity(
        Long id,
        CouponType couponType,
        Integer totalQuantity,
        boolean availableIssueQuantity,

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime dateIssuedStart,

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime dateIssuedEnd
) {
    public CouponRedisEntity(Coupon coupon) {
        this(
                coupon.getId(),
                coupon.getCouponType(),
                coupon.getTotalQuantity(),
                coupon.availableIssueQuantity(),
                coupon.getDateIssuedStart(),
                coupon.getDateIssuedEnd()
        );
    }

    private boolean availableIssueDate() {
        LocalDateTime now = LocalDateTime.now();
        return dateIssuedStart.isBefore(now) && dateIssuedEnd.isAfter(now);
    }

    public void checkIssuableCoupon() {
        if (!availableIssueQuantity) {
            throw new CouponIssueException(INVALID_COUPON_ISSUE_QUANTITY,
                    "모든 발급 수량이 소진되었습니다. couponId: %s".formatted(id));
        }

        if (!availableIssueDate()) {
            throw new CouponIssueException(INVALID_COUPON_ISSUE_DATE,
                    "발급 가능한 일자가 아닙니다. couponId: %s, issueStart: %s, issueEnd: %s".formatted(id, dateIssuedStart, dateIssuedEnd));
        }
    }
}

package com.openmpy.couponcore.model;

import com.openmpy.couponcore.exception.CouponIssueException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.openmpy.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_DATE;
import static com.openmpy.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_QUANTITY;

class CouponTest {

    @Test
    @DisplayName("발급 수량이 남아있다면 true를 반환한다.")
    void availableIssueQuantity_1() throws Exception {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .build();

        // when
        boolean result = coupon.availableIssueQuantity();

        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("발급 수량이 소진되었다면 false를 반환한다.")
    void availableIssueQuantity_2() throws Exception {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .build();

        // when
        boolean result = coupon.availableIssueQuantity();

        // then
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("최대 발급 수량이 설정되지 않았다면 true를 반환한다.")
    void availableIssueQuantity_3() throws Exception {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(null)
                .issuedQuantity(100)
                .build();

        // when
        boolean result = coupon.availableIssueQuantity();

        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("발급 기한이 시작되지 않았다면 false를 반환한다.")
    void availableIssueDate_1() throws Exception {
        // given
        Coupon coupon = Coupon.builder()
                .dateIssuedStart(LocalDateTime.now().plusDays(1))
                .dateIssuedEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when
        boolean result = coupon.availableIssueDate();

        // then
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("발급 기한에 해당되면 true를 반환한다.")
    void availableIssueDate_2() throws Exception {
        // given
        Coupon coupon = Coupon.builder()
                .dateIssuedStart(LocalDateTime.now().minusDays(1))
                .dateIssuedEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when
        boolean result = coupon.availableIssueDate();

        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("발급 기한이 종료되면 false를 반환한다.")
    void availableIssueDate_3() throws Exception {
        // given
        Coupon coupon = Coupon.builder()
                .dateIssuedStart(LocalDateTime.now().minusDays(2))
                .dateIssuedEnd(LocalDateTime.now().minusDays(1))
                .build();

        // when
        boolean result = coupon.availableIssueDate();

        // then
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("발급 수량과 기간이 유효하다면 발급에 성공한다.")
    void issue_1() throws Exception {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssuedStart(LocalDateTime.now().minusDays(1))
                .dateIssuedEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when
        coupon.issue();

        // then
        Assertions.assertEquals(coupon.getIssuedQuantity(), 100);
    }

    @Test
    @DisplayName("발급 수량을 초과하면 예외를 반환한다.")
    void issue_2() throws Exception {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssuedStart(LocalDateTime.now().minusDays(1))
                .dateIssuedEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when & then
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, coupon::issue);
        Assertions.assertEquals(exception.getErrorCode(), INVALID_COUPON_ISSUE_QUANTITY);
    }

    @Test
    @DisplayName("발급 기간이 아니면 예외를 반환한다.")
    void issue_3() throws Exception {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssuedStart(LocalDateTime.now().plusDays(1))
                .dateIssuedEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when & then
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, coupon::issue);
        Assertions.assertEquals(exception.getErrorCode(), INVALID_COUPON_ISSUE_DATE);
    }
}
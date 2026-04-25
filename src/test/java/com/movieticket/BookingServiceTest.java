package com.movieticket;

import com.movieticket.service.BookingService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the pricing logic in {@link BookingService}.
 * These tests do NOT touch the database - they exercise the pure function
 * {@link BookingService#calculateTotal(BigDecimal, List)}.
 */
class BookingServiceTest {

    private final BookingService svc = new BookingService();
    private final BigDecimal base = new BigDecimal("250.00");

    @Test void regularSeatsAreChargedBasePrice() {
        BigDecimal total = svc.calculateTotal(base, List.of("C1", "C2", "D5"));
        assertEquals(new BigDecimal("750.00"), total);
    }

    @Test void rowAIsVipAndSurchargeIsApplied() {
        BigDecimal total = svc.calculateTotal(base, List.of("A1"));
        // 250 + 100 VIP extra = 350
        assertEquals(new BigDecimal("350.00"), total);
    }

    @Test void rowBIsVipAndSurchargeIsApplied() {
        BigDecimal total = svc.calculateTotal(base, List.of("B3"));
        assertEquals(new BigDecimal("350.00"), total);
    }

    @Test void rowsCtoFAreNotVip() {
        for (String row : List.of("C", "D", "E", "F")) {
            BigDecimal total = svc.calculateTotal(base, List.of(row + "1"));
            assertEquals(base, total, "Row " + row + " should not have VIP surcharge");
        }
    }

    @Test void mixedSelectionAddsUpCorrectly() {
        // 2 VIP (A1, B2) + 3 regular (C1, D1, E1) at base 250
        //   2*(250+100) + 3*250 = 700 + 750 = 1450
        BigDecimal total = svc.calculateTotal(base,
                List.of("A1", "B2", "C1", "D1", "E1"));
        assertEquals(new BigDecimal("1450.00"), total);
    }

    @Test void emptySeatListYieldsZero() {
        assertEquals(BigDecimal.ZERO, svc.calculateTotal(base, List.of()));
    }

    @Test void maxSeatsConstantMatchesSpec() {
        // README & UI state maximum of 8 seats per booking
        assertEquals(8, BookingService.MAX_SEATS_PER_BOOKING);
    }

    @Test void vipExtraIsHundred() {
        assertEquals(0, BookingService.VIP_EXTRA.compareTo(new BigDecimal("100.00")));
    }
}

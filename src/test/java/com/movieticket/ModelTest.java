package com.movieticket;

import com.movieticket.model.Booking;
import com.movieticket.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Simple POJO behaviour tests. */
class ModelTest {

    @Test void userIsAdminOnlyWhenRoleIsAdmin() {
        User admin = new User("Admin", "a@x.com", "h", null, User.Role.ADMIN);
        User user  = new User("User",  "u@x.com", "h", null, User.Role.USER);
        assertTrue(admin.isAdmin());
        assertFalse(user.isAdmin());
    }

    @Test void bookingCountsSeatsFromCsv() {
        Booking b = new Booking();
        b.setSeats("A1,A2,B3");
        assertEquals(3, b.countSeats());
    }

    @Test void bookingCountsZeroForEmpty() {
        Booking b = new Booking();
        b.setSeats("");
        assertEquals(0, b.countSeats());
        b.setSeats(null);
        assertEquals(0, b.countSeats());
    }

    @Test void bookingSingleSeat() {
        Booking b = new Booking();
        b.setSeats("C5");
        assertEquals(1, b.countSeats());
    }
}

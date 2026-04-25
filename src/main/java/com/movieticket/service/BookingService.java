package com.movieticket.service;

import com.movieticket.dao.BookingDAO;
import com.movieticket.dao.ShowtimeDAO;
import com.movieticket.model.Booking;
import com.movieticket.model.Showtime;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/** Orchestrates booking creation / cancellation with validation. */
public class BookingService {

    public static final int MAX_SEATS_PER_BOOKING = 8;

    /** VIP rows (A and B) are charged an extra flat amount. */
    public static final BigDecimal VIP_EXTRA = new BigDecimal("100.00");

    private final BookingDAO  bookingDAO  = new BookingDAO();
    private final ShowtimeDAO showtimeDAO = new ShowtimeDAO();

    /** Returns a seat taken set suitable for painting the grid. */
    public Set<String> takenSeats(int showtimeId) throws SQLException {
        return bookingDAO.seatsTakenFor(showtimeId);
    }

    /** Computes total price. Seats in rows A or B get the VIP surcharge. */
    public BigDecimal calculateTotal(BigDecimal basePrice, List<String> seats) {
        BigDecimal total = BigDecimal.ZERO;
        for (String seat : seats) {
            char row = seat.charAt(0);
            BigDecimal p = basePrice;
            if (row == 'A' || row == 'B') p = p.add(VIP_EXTRA);
            total = total.add(p);
        }
        return total;
    }

    public Booking book(int userId, int showtimeId, List<String> chosenSeats)
            throws SQLException, BookingException {

        if (chosenSeats == null || chosenSeats.isEmpty())
            throw new BookingException("Please select at least one seat");
        if (chosenSeats.size() > MAX_SEATS_PER_BOOKING)
            throw new BookingException("Maximum " + MAX_SEATS_PER_BOOKING + " seats per booking");

        Showtime s = showtimeDAO.findById(showtimeId);
        if (s == null) throw new BookingException("Showtime no longer available");

        Set<String> taken = takenSeats(showtimeId);
        for (String seat : chosenSeats) {
            if (taken.contains(seat))
                throw new BookingException("Seat " + seat + " has just been booked. Please refresh.");
        }

        Booking b = new Booking();
        b.setUserId(userId);
        b.setShowtimeId(showtimeId);
        b.setSeats(String.join(",", chosenSeats));
        b.setTotalAmount(calculateTotal(s.getPrice(), chosenSeats));
        b.setStatus(Booking.Status.CONFIRMED);

        if (!bookingDAO.create(b))
            throw new BookingException("Could not save booking, please retry");
        return b;
    }

    public void cancel(int bookingId) throws SQLException, BookingException {
        if (!bookingDAO.cancel(bookingId))
            throw new BookingException("Cancellation failed");
    }

    public List<Booking> historyFor(int userId) throws SQLException {
        return bookingDAO.findByUser(userId);
    }

    public List<Booking> allBookings() throws SQLException {
        return bookingDAO.findAll();
    }

    // ----------------------------------------------------------------
    public static class BookingException extends Exception {
        public BookingException(String msg) { super(msg); }
    }
}

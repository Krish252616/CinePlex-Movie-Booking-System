package com.movieticket.dao;

import com.movieticket.db.DBConnection;
import com.movieticket.model.Booking;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookingDAO {

    public boolean create(Booking b) throws SQLException {
        String sql = "INSERT INTO bookings(user_id,showtime_id,seats,total_amount,status)"
                + " VALUES(?,?,?,?,?)";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, b.getUserId());
            ps.setInt(2, b.getShowtimeId());
            ps.setString(3, b.getSeats());
            ps.setBigDecimal(4, b.getTotalAmount());
            ps.setString(5, b.getStatus().name());
            if (ps.executeUpdate() == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) b.setBookingId(rs.getInt(1));
            }
            return true;
        }
    }

    public boolean cancel(int bookingId) throws SQLException {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE bookings SET status='CANCELLED' WHERE booking_id=?")) {
            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Booking> findByUser(int userId) throws SQLException {
        String sql = base() + " WHERE b.user_id=? ORDER BY b.booked_at DESC";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Booking> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        }
    }

    public List<Booking> findAll() throws SQLException {
        String sql = base() + " ORDER BY b.booked_at DESC";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Booking> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        }
    }

    /** All seat codes already sold (CONFIRMED) for a given showtime. */
    public Set<String> seatsTakenFor(int showtimeId) throws SQLException {
        Set<String> taken = new HashSet<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT seats FROM bookings WHERE showtime_id=? AND status='CONFIRMED'")) {
            ps.setInt(1, showtimeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    for (String s : rs.getString(1).split(","))
                        if (!s.isBlank()) taken.add(s.trim());
                }
            }
        }
        return taken;
    }

    public int countConfirmedBookings() throws SQLException {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COUNT(*) FROM bookings WHERE status='CONFIRMED'");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public java.math.BigDecimal totalRevenue() throws SQLException {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COALESCE(SUM(total_amount),0) FROM bookings WHERE status='CONFIRMED'");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : java.math.BigDecimal.ZERO;
        }
    }

    // --------------------------------------------------------------
    private String base() {
        return "SELECT b.*, u.full_name AS uname, m.title AS mtitle, t.name AS tname,"
             + " s.show_date AS sdate, s.show_time AS stime"
             + " FROM bookings b"
             + " JOIN users u    ON u.user_id=b.user_id"
             + " JOIN showtimes s ON s.showtime_id=b.showtime_id"
             + " JOIN movies m   ON m.movie_id=s.movie_id"
             + " JOIN theatres t ON t.theatre_id=s.theatre_id";
    }

    private Booking map(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setBookingId(rs.getInt("booking_id"));
        b.setUserId(rs.getInt("user_id"));
        b.setShowtimeId(rs.getInt("showtime_id"));
        b.setSeats(rs.getString("seats"));
        b.setTotalAmount(rs.getBigDecimal("total_amount"));
        b.setStatus(Booking.Status.valueOf(rs.getString("status")));
        b.setBookedAt(rs.getTimestamp("booked_at"));
        b.setUserName(rs.getString("uname"));
        b.setMovieTitle(rs.getString("mtitle"));
        b.setTheatreName(rs.getString("tname"));
        b.setShowDate(String.valueOf(rs.getDate("sdate")));
        b.setShowTime(String.valueOf(rs.getTime("stime")));
        return b;
    }
}

package com.movieticket.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Booking {
    public enum Status { CONFIRMED, CANCELLED }

    private int        bookingId;
    private int        userId;
    private int        showtimeId;
    private String     seats;         // "A1,A2,B5"
    private BigDecimal totalAmount;
    private Status     status;
    private Timestamp  bookedAt;

    // display helpers
    private String     userName;
    private String     movieTitle;
    private String     theatreName;
    private String     showDate;
    private String     showTime;

    public Booking() { }

    public int        getBookingId()   { return bookingId; }
    public void       setBookingId(int v){ this.bookingId = v; }
    public int        getUserId()      { return userId; }
    public void       setUserId(int v) { this.userId = v; }
    public int        getShowtimeId()  { return showtimeId; }
    public void       setShowtimeId(int v){ this.showtimeId = v; }
    public String     getSeats()       { return seats; }
    public void       setSeats(String v){ this.seats = v; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void       setTotalAmount(BigDecimal v){ this.totalAmount = v; }
    public Status     getStatus()      { return status; }
    public void       setStatus(Status v){ this.status = v; }
    public Timestamp  getBookedAt()    { return bookedAt; }
    public void       setBookedAt(Timestamp v){ this.bookedAt = v; }
    public String     getUserName()    { return userName; }
    public void       setUserName(String v){ this.userName = v; }
    public String     getMovieTitle()  { return movieTitle; }
    public void       setMovieTitle(String v){ this.movieTitle = v; }
    public String     getTheatreName() { return theatreName; }
    public void       setTheatreName(String v){ this.theatreName = v; }
    public String     getShowDate()    { return showDate; }
    public void       setShowDate(String v){ this.showDate = v; }
    public String     getShowTime()    { return showTime; }
    public void       setShowTime(String v){ this.showTime = v; }

    public int countSeats() {
        return seats == null || seats.isBlank() ? 0 : seats.split(",").length;
    }
}

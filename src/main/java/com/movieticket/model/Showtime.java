package com.movieticket.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

public class Showtime {
    private int        showtimeId;
    private int        movieId;
    private int        theatreId;
    private Date       showDate;
    private Time       showTime;
    private BigDecimal price;

    // denormalised helpers (filled via JOINs for display)
    private String movieTitle;
    private String theatreName;
    private String theatreCity;

    public Showtime() { }

    public int        getShowtimeId()  { return showtimeId; }
    public void       setShowtimeId(int v){ this.showtimeId = v; }
    public int        getMovieId()     { return movieId; }
    public void       setMovieId(int v){ this.movieId = v; }
    public int        getTheatreId()   { return theatreId; }
    public void       setTheatreId(int v){ this.theatreId = v; }
    public Date       getShowDate()    { return showDate; }
    public void       setShowDate(Date v){ this.showDate = v; }
    public Time       getShowTime()    { return showTime; }
    public void       setShowTime(Time v){ this.showTime = v; }
    public BigDecimal getPrice()       { return price; }
    public void       setPrice(BigDecimal v){ this.price = v; }
    public String     getMovieTitle()  { return movieTitle; }
    public void       setMovieTitle(String v){ this.movieTitle = v; }
    public String     getTheatreName() { return theatreName; }
    public void       setTheatreName(String v){ this.theatreName = v; }
    public String     getTheatreCity() { return theatreCity; }
    public void       setTheatreCity(String v){ this.theatreCity = v; }

    @Override public String toString() {
        return showDate + "  " + showTime + "  @  " + theatreName + " - \u20B9" + price;
    }
}

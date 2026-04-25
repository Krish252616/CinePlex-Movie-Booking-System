package com.movieticket.dao;

import com.movieticket.db.DBConnection;
import com.movieticket.model.Showtime;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShowtimeDAO {

    public List<Showtime> findForMovie(int movieId) throws SQLException {
        String sql = "SELECT s.*, m.title AS mtitle, t.name AS tname, t.city AS tcity"
                + " FROM showtimes s"
                + " JOIN movies  m ON m.movie_id=s.movie_id"
                + " JOIN theatres t ON t.theatre_id=s.theatre_id"
                + " WHERE s.movie_id=? AND s.show_date >= CURDATE()"
                + " ORDER BY s.show_date, s.show_time";
        List<Showtime> list = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, movieId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public Showtime findById(int id) throws SQLException {
        String sql = "SELECT s.*, m.title AS mtitle, t.name AS tname, t.city AS tcity"
                + " FROM showtimes s"
                + " JOIN movies  m ON m.movie_id=s.movie_id"
                + " JOIN theatres t ON t.theatre_id=s.theatre_id"
                + " WHERE s.showtime_id=?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    private Showtime map(ResultSet rs) throws SQLException {
        Showtime s = new Showtime();
        s.setShowtimeId(rs.getInt("showtime_id"));
        s.setMovieId(rs.getInt("movie_id"));
        s.setTheatreId(rs.getInt("theatre_id"));
        s.setShowDate(rs.getDate("show_date"));
        s.setShowTime(rs.getTime("show_time"));
        s.setPrice(rs.getBigDecimal("price"));
        s.setMovieTitle(rs.getString("mtitle"));
        s.setTheatreName(rs.getString("tname"));
        s.setTheatreCity(rs.getString("tcity"));
        return s;
    }
}

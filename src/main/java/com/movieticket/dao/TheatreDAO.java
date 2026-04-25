package com.movieticket.dao;

import com.movieticket.db.DBConnection;
import com.movieticket.model.Theatre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TheatreDAO {

    public List<Theatre> findAll() throws SQLException {
        List<Theatre> list = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM theatres ORDER BY name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Theatre findById(int id) throws SQLException {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM theatres WHERE theatre_id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    private Theatre map(ResultSet rs) throws SQLException {
        Theatre t = new Theatre();
        t.setTheatreId(rs.getInt("theatre_id"));
        t.setName(rs.getString("name"));
        t.setCity(rs.getString("city"));
        t.setAddress(rs.getString("address"));
        t.setTotalSeats(rs.getInt("total_seats"));
        return t;
    }
}

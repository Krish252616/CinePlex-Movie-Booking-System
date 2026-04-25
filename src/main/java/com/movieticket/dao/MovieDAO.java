package com.movieticket.dao;

import com.movieticket.db.DBConnection;
import com.movieticket.model.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    public boolean add(Movie m) throws SQLException {
        String sql = "INSERT INTO movies(title,genre,language,duration_min,rating,description,now_showing)"
                + " VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getGenre());
            ps.setString(3, m.getLanguage());
            ps.setInt   (4, m.getDurationMin());
            ps.setString(5, m.getRating());
            ps.setString(6, m.getDescription());
            ps.setBoolean(7, m.isNowShowing());
            if (ps.executeUpdate() == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) m.setMovieId(rs.getInt(1));
            }
            return true;
        }
    }

    public boolean update(Movie m) throws SQLException {
        String sql = "UPDATE movies SET title=?,genre=?,language=?,duration_min=?,"
                + "rating=?,description=?,now_showing=? WHERE movie_id=?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getGenre());
            ps.setString(3, m.getLanguage());
            ps.setInt   (4, m.getDurationMin());
            ps.setString(5, m.getRating());
            ps.setString(6, m.getDescription());
            ps.setBoolean(7, m.isNowShowing());
            ps.setInt   (8, m.getMovieId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int movieId) throws SQLException {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement("DELETE FROM movies WHERE movie_id=?")) {
            ps.setInt(1, movieId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean toggleShowing(int movieId, boolean showing) throws SQLException {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE movies SET now_showing=? WHERE movie_id=?")) {
            ps.setBoolean(1, showing);
            ps.setInt    (2, movieId);
            return ps.executeUpdate() > 0;
        }
    }

    public Movie findById(int id) throws SQLException {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM movies WHERE movie_id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Movie> findAll() throws SQLException {
        return query("SELECT * FROM movies ORDER BY movie_id DESC");
    }

    public List<Movie> findNowShowing() throws SQLException {
        return query("SELECT * FROM movies WHERE now_showing=TRUE ORDER BY title");
    }

    /** Search by title (LIKE) and/or genre/language (exact, null = any). */
    public List<Movie> search(String titleLike, String genre, String language) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM movies WHERE now_showing=TRUE");
        List<Object> params = new ArrayList<>();
        if (titleLike != null && !titleLike.isBlank()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + titleLike + "%");
        }
        if (genre != null && !genre.isBlank() && !"All".equalsIgnoreCase(genre)) {
            sql.append(" AND genre = ?");
            params.add(genre);
        }
        if (language != null && !language.isBlank() && !"All".equalsIgnoreCase(language)) {
            sql.append(" AND language = ?");
            params.add(language);
        }
        sql.append(" ORDER BY title");

        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                List<Movie> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        }
    }

    public int countMovies() throws SQLException {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM movies");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private List<Movie> query(String sql) throws SQLException {
        List<Movie> list = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    private Movie map(ResultSet rs) throws SQLException {
        Movie m = new Movie();
        m.setMovieId(rs.getInt("movie_id"));
        m.setTitle(rs.getString("title"));
        m.setGenre(rs.getString("genre"));
        m.setLanguage(rs.getString("language"));
        m.setDurationMin(rs.getInt("duration_min"));
        m.setRating(rs.getString("rating"));
        m.setDescription(rs.getString("description"));
        m.setPosterPath(rs.getString("poster_path"));
        m.setNowShowing(rs.getBoolean("now_showing"));
        return m;
    }
}

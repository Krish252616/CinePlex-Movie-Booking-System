package com.movieticket.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Single point of access for JDBC Connections.
 *
 * Reads credentials from {@code /db.properties} on the classpath so that
 * passwords are never hardcoded in source files.
 */
public final class DBConnection {

    private static String url;
    private static String user;
    private static String password;

    static {
        try (InputStream in = DBConnection.class.getResourceAsStream("/db.properties")) {
            if (in == null) {
                throw new IllegalStateException(
                        "db.properties not found on classpath (src/main/resources)");
            }
            Properties p = new Properties();
            p.load(in);
            url      = p.getProperty("db.url");
            user     = p.getProperty("db.user");
            password = p.getProperty("db.password");
            Class.forName(p.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
        } catch (IOException | ClassNotFoundException ex) {
            throw new ExceptionInInitializerError(
                    "Failed to initialise DBConnection: " + ex.getMessage());
        }
    }

    private DBConnection() { /* utility class */ }

    /** Opens a brand-new connection. Caller must close it. */
    public static Connection get() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /** Quick connectivity check used on application start-up. */
    public static boolean testConnection() {
        try (Connection c = get()) {
            return c != null && !c.isClosed();
        } catch (SQLException ex) {
            System.err.println("[DB] Connection failed: " + ex.getMessage());
            return false;
        }
    }
}

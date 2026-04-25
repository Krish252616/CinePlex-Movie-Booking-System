package com.movieticket;

import com.movieticket.db.DBConnection;
import com.movieticket.ui.LoginFrame;
import com.movieticket.util.Theme;

import javax.swing.*;

/**
 * Entry point for the CinePlex Movie Ticket Booking System.
 *
 *  1. Applies a dark look-and-feel.
 *  2. Verifies MySQL connectivity.
 *  3. Opens the login screen on the EDT.
 */
public class MainApp {

    public static void main(String[] args) {

        // Apply some Nimbus defaults tuned to our dark theme.
        try {
            for (UIManager.LookAndFeelInfo lf : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(lf.getName())) {
                    UIManager.setLookAndFeel(lf.getClassName());
                    break;
                }
            }
            UIManager.put("control",         Theme.BG_PANEL);
            UIManager.put("info",            Theme.BG_CARD);
            UIManager.put("nimbusBase",      Theme.BG_DARK);
            UIManager.put("nimbusBlueGrey",  Theme.BG_PANEL);
            UIManager.put("nimbusLightBackground", Theme.BG_PANEL);
            UIManager.put("text",            Theme.TEXT);
            UIManager.put("Table.background",      Theme.BG_PANEL);
            UIManager.put("Table.foreground",      Theme.TEXT);
            UIManager.put("Table.gridColor",       Theme.BORDER);
            UIManager.put("TableHeader.background", Theme.BG_CARD);
            UIManager.put("TableHeader.foreground", Theme.TEXT);
        } catch (Exception ignore) { /* fallback to default */ }

        // Verify DB up-front so users get an actionable error instead of
        // confusing stack traces later.
        if (!DBConnection.testConnection()) {
            JOptionPane.showMessageDialog(null,
                    "Could not connect to MySQL.\n\n" +
                    "Please verify:\n" +
                    "  * MySQL server is running on localhost:3306\n" +
                    "  * Database 'cineplex_db' exists (run database/schema.sql)\n" +
                    "  * Credentials in src/main/resources/db.properties are correct",
                    "Database Connection Failed",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}

package com.movieticket.ui;

import com.movieticket.util.Session;
import com.movieticket.util.Theme;
import com.movieticket.util.UI;

import javax.swing.*;
import java.awt.*;

/** Shared top navigation bar used on Home, MyBookings and Admin screens. */
public class NavBar extends JPanel {

    public NavBar(JFrame owner) {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_PANEL);
        setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        JLabel logo = new JLabel("\uD83C\uDFAC  CinePlex");
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        logo.setForeground(Theme.TEXT);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JButton home = UI.secondaryButton("Home");
        home.addActionListener(e -> {
            if (!(owner instanceof HomeFrame)) {
                owner.dispose();
                new HomeFrame(Session.get()).setVisible(true);
            }
        });

        JButton mybookings = UI.secondaryButton("My Bookings");
        mybookings.addActionListener(e -> {
            if (!(owner instanceof MyBookingsFrame)) {
                owner.dispose();
                new MyBookingsFrame().setVisible(true);
            }
        });

        right.add(home);
        right.add(mybookings);

        if (Session.isAdmin()) {
            JButton admin = UI.secondaryButton("Admin Panel");
            admin.addActionListener(e -> {
                if (!(owner instanceof AdminDashboardFrame)) {
                    owner.dispose();
                    new AdminDashboardFrame().setVisible(true);
                }
            });
            right.add(admin);
        }

        JLabel user = UI.label(
                "Hi, " + Session.get().getFullName().split(" ")[0] + "  |",
                Theme.F_SMALL, Theme.TEXT_DIM);
        right.add(user);

        JButton logout = UI.dangerButton("Logout");
        logout.addActionListener(e -> {
            Session.clear();
            owner.dispose();
            new LoginFrame().setVisible(true);
        });
        right.add(logout);

        add(logo,  BorderLayout.WEST);
        add(right, BorderLayout.EAST);
    }
}

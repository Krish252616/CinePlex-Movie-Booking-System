package com.movieticket.ui;

import com.movieticket.dao.BookingDAO;
import com.movieticket.dao.MovieDAO;
import com.movieticket.dao.UserDAO;
import com.movieticket.model.Booking;
import com.movieticket.model.Movie;
import com.movieticket.model.User;
import com.movieticket.util.Theme;
import com.movieticket.util.UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardFrame extends JFrame {

    private final MovieDAO   movieDAO   = new MovieDAO();
    private final UserDAO    userDAO    = new UserDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    public AdminDashboardFrame() {
        setTitle("CinePlex - Admin Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 780);
        setLocationRelativeTo(null);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_DARK);
        root.add(new NavBar(this), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(Theme.BG_PANEL);
        tabs.setForeground(Theme.TEXT);
        tabs.setFont(Theme.F_BODY_BOLD);

        tabs.addTab("Overview",  overviewTab());
        tabs.addTab("Movies",    moviesTab());
        tabs.addTab("Bookings",  bookingsTab());
        tabs.addTab("Users",     usersTab());

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Theme.BG_DARK);
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        body.add(tabs, BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);
        return root;
    }

    // ---------------- Overview ----------------
    private JPanel overviewTab() {
        JPanel p = new JPanel(new GridLayout(1, 4, 16, 16));
        p.setBackground(Theme.BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        try {
            p.add(statCard("Movies",    String.valueOf(movieDAO.countMovies()),    Theme.ACCENT));
            p.add(statCard("Users",     String.valueOf(userDAO.countUsers()),      Theme.GREEN));
            p.add(statCard("Bookings",  String.valueOf(bookingDAO.countConfirmedBookings()), Theme.AMBER));
            p.add(statCard("Revenue",   "Rs " + bookingDAO.totalRevenue(),         Theme.RED));
        } catch (Exception ex) {
            p.add(UI.label("Error: " + ex.getMessage(), Theme.F_BODY, Theme.RED));
        }
        return p;
    }

    private JPanel statCard(String label, String value, Color accent) {
        JPanel c = UI.card();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        JLabel v = UI.label(value, new Font("SansSerif", Font.BOLD, 30), accent);
        JLabel l = UI.label(label, Theme.F_BODY, Theme.TEXT_DIM);
        v.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.add(v);
        c.add(Box.createVerticalStrut(4));
        c.add(l);
        return c;
    }

    // ---------------- Movies ----------------
    private JPanel moviesTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(Theme.BG_DARK);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Title", "Genre", "Language", "Duration", "Rating", "Now Showing"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);

        Runnable reload = () -> {
            model.setRowCount(0);
            try {
                for (Movie m : movieDAO.findAll()) {
                    model.addRow(new Object[]{
                            m.getMovieId(), m.getTitle(), m.getGenre(),
                            m.getLanguage(), m.getDurationMin() + " min",
                            m.getRating(), m.isNowShowing() ? "Yes" : "No"
                    });
                }
            } catch (Exception ex) { /* ignore */ }
        };
        reload.run();

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(Theme.BG_PANEL);
        sp.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setOpaque(false);

        JButton add = UI.primaryButton("Add movie");
        add.addActionListener(e -> { if (showMovieDialog(null)) reload.run(); });

        JButton toggle = UI.secondaryButton("Toggle 'Now Showing'");
        toggle.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) return;
            int id = (Integer) model.getValueAt(r, 0);
            boolean now = "Yes".equals(model.getValueAt(r, 6));
            try {
                movieDAO.toggleShowing(id, !now);
                reload.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton delete = UI.dangerButton("Delete selected");
        delete.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) return;
            int id = (Integer) model.getValueAt(r, 0);
            int ok = JOptionPane.showConfirmDialog(this,
                    "Delete movie " + model.getValueAt(r, 1) + " ? This cascades to showtimes and bookings.",
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;
            try {
                movieDAO.delete(id);
                reload.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        toolbar.add(add);
        toolbar.add(toggle);
        toolbar.add(delete);

        p.add(toolbar, BorderLayout.NORTH);
        p.add(sp,      BorderLayout.CENTER);
        return p;
    }

    private boolean showMovieDialog(Movie existing) {
        JTextField title    = UI.textField(20);
        JTextField genre    = UI.textField(20);
        JTextField language = UI.textField(20);
        JTextField duration = UI.textField(20);
        JTextField rating   = UI.textField(20);
        JTextArea  desc     = new JTextArea(4, 20);
        desc.setLineWrap(true); desc.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Title"));    form.add(title);
        form.add(new JLabel("Genre"));    form.add(genre);
        form.add(new JLabel("Language")); form.add(language);
        form.add(new JLabel("Duration (min)")); form.add(duration);
        form.add(new JLabel("Rating (U/UA/A)")); form.add(rating);
        form.add(new JLabel("Description"));    form.add(new JScrollPane(desc));

        int ok = JOptionPane.showConfirmDialog(this, form, "Add movie",
                JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return false;

        try {
            Movie m = new Movie(title.getText(), genre.getText(), language.getText(),
                    Integer.parseInt(duration.getText().trim()),
                    rating.getText(), desc.getText(), true);
            return movieDAO.add(m);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Duration must be a number",
                    "Invalid input", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // ---------------- Bookings ----------------
    private JPanel bookingsTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(Theme.BG_DARK);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "User", "Movie", "Theatre", "Date", "Time",
                             "Seats", "Amount", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);

        Runnable reload = () -> {
            model.setRowCount(0);
            try {
                for (Booking b : bookingDAO.findAll()) {
                    model.addRow(new Object[]{
                            b.getBookingId(), b.getUserName(),
                            b.getMovieTitle(), b.getTheatreName(),
                            b.getShowDate(), b.getShowTime(),
                            b.getSeats(), "Rs " + b.getTotalAmount(),
                            b.getStatus().name()
                    });
                }
            } catch (Exception ex) { /* ignore */ }
        };
        reload.run();

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setOpaque(false);
        JButton refresh = UI.secondaryButton("Refresh");
        refresh.addActionListener(e -> reload.run());
        JButton cancel = UI.dangerButton("Cancel selected");
        cancel.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) return;
            int id = (Integer) model.getValueAt(r, 0);
            try {
                bookingDAO.cancel(id);
                reload.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        toolbar.add(refresh);
        toolbar.add(cancel);

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(Theme.BG_PANEL);
        sp.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        p.add(toolbar, BorderLayout.NORTH);
        p.add(sp,      BorderLayout.CENTER);
        return p;
    }

    // ---------------- Users ----------------
    private JPanel usersTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(Theme.BG_DARK);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Name", "Email", "Phone", "Role", "Joined"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);

        try {
            List<User> users = userDAO.findAll();
            for (User u : users) {
                model.addRow(new Object[]{
                        u.getUserId(), u.getFullName(), u.getEmail(),
                        u.getPhone(), u.getRole().name(),
                        String.valueOf(u.getCreatedAt())
                });
            }
        } catch (Exception ex) { /* ignore */ }

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(Theme.BG_PANEL);
        sp.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    // ---------------- helpers ----------------
    private JTable styledTable(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setRowHeight(30);
        t.setFont(Theme.F_BODY);
        t.setForeground(Theme.TEXT);
        t.setBackground(Theme.BG_PANEL);
        t.setSelectionBackground(Theme.ACCENT_DARK);
        t.setSelectionForeground(Color.WHITE);
        t.setGridColor(Theme.BORDER);
        t.getTableHeader().setFont(Theme.F_BODY_BOLD);
        t.getTableHeader().setBackground(Theme.BG_CARD);
        t.getTableHeader().setForeground(Theme.TEXT);
        return t;
    }
}

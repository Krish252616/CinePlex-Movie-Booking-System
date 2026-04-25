package com.movieticket.ui;

import com.movieticket.dao.ShowtimeDAO;
import com.movieticket.model.Movie;
import com.movieticket.model.Showtime;
import com.movieticket.util.Session;
import com.movieticket.util.Theme;
import com.movieticket.util.UI;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class MovieDetailFrame extends JFrame {

    private final Movie movie;
    private final ShowtimeDAO showtimeDAO = new ShowtimeDAO();

    public MovieDetailFrame(Movie m) {
        this.movie = m;
        setTitle("CinePlex - " + m.getTitle());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 680);
        setLocationRelativeTo(null);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_DARK);
        root.add(new NavBar(this), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20, 20));
        body.setBackground(Theme.BG_DARK);
        body.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        body.add(buildHeader(),     BorderLayout.NORTH);
        body.add(buildShowtimes(),  BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildHeader() {
        JPanel card = UI.card();
        card.setLayout(new BorderLayout(20, 0));

        // poster block
        JPanel poster = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                int h = Math.abs(movie.getTitle().hashCode());
                Color c1 = new Color((h >> 16) & 0xFF, (h >> 8) & 0xFF, h & 0xFF).darker();
                Color c2 = c1.darker();
                g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(255, 255, 255, 230));
                g2.setFont(new Font("SansSerif", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                String t = movie.getTitle();
                int x = (getWidth() - fm.stringWidth(t)) / 2;
                g2.drawString(t, x, getHeight() / 2);
                g2.dispose();
            }
        };
        poster.setPreferredSize(new Dimension(220, 280));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel title = UI.label(movie.getTitle(), Theme.F_HEADER, Theme.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel meta = UI.label(movie.getGenre() + "  *  " + movie.getLanguage()
                + "  *  " + movie.getDurationMin() + " min  *  "
                + movie.getRating(), Theme.F_BODY, Theme.TEXT_DIM);
        meta.setAlignmentX(Component.LEFT_ALIGNMENT);
        meta.setBorder(BorderFactory.createEmptyBorder(8, 0, 14, 0));

        JTextArea desc = new JTextArea(movie.getDescription() == null
                ? "No description available."
                : movie.getDescription());
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setOpaque(false);
        desc.setFont(Theme.F_BODY);
        desc.setForeground(Theme.TEXT);
        desc.setBorder(null);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(title);
        info.add(meta);
        info.add(desc);

        card.add(poster, BorderLayout.WEST);
        card.add(info,   BorderLayout.CENTER);
        return card;
    }

    private JPanel buildShowtimes() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);

        JLabel h = UI.label("Available Showtimes", Theme.F_TITLE, Theme.TEXT);
        h.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        wrap.add(h, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 3, 14, 14));
        grid.setOpaque(false);

        try {
            List<Showtime> shows = showtimeDAO.findForMovie(movie.getMovieId());
            if (shows.isEmpty()) {
                JLabel none = UI.label("No upcoming shows for this movie.",
                        Theme.F_BODY, Theme.TEXT_DIM);
                wrap.add(none, BorderLayout.CENTER);
                return wrap;
            }
            SimpleDateFormat dFmt = new SimpleDateFormat("EEE, dd MMM");
            SimpleDateFormat tFmt = new SimpleDateFormat("hh:mm a");
            for (Showtime s : shows) grid.add(showtimeCard(s, dFmt, tFmt));
        } catch (Exception ex) {
            wrap.add(UI.label("Error: " + ex.getMessage(), Theme.F_BODY, Theme.RED),
                    BorderLayout.CENTER);
            return wrap;
        }

        JScrollPane sp = new JScrollPane(grid);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        wrap.add(sp, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel showtimeCard(Showtime s, SimpleDateFormat dFmt, SimpleDateFormat tFmt) {
        JPanel c = UI.card();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

        JLabel theatre = UI.label(s.getTheatreName() + ", " + s.getTheatreCity(),
                Theme.F_BODY_BOLD, Theme.TEXT);
        JLabel date    = UI.label(dFmt.format(s.getShowDate()),
                Theme.F_SMALL, Theme.TEXT_DIM);
        JLabel time    = UI.label(tFmt.format(s.getShowTime()),
                Theme.F_TITLE, Theme.ACCENT);
        JLabel price   = UI.label("Base price: \u20B9" + s.getPrice(),
                Theme.F_SMALL, Theme.TEXT_DIM);

        theatre.setAlignmentX(Component.LEFT_ALIGNMENT);
        date.setAlignmentX(Component.LEFT_ALIGNMENT);
        time.setAlignmentX(Component.LEFT_ALIGNMENT);
        price.setAlignmentX(Component.LEFT_ALIGNMENT);

        c.add(theatre);
        c.add(Box.createVerticalStrut(2));
        c.add(date);
        c.add(Box.createVerticalStrut(6));
        c.add(time);
        c.add(Box.createVerticalStrut(4));
        c.add(price);
        c.add(Box.createVerticalStrut(10));

        JButton pick = UI.primaryButton("Select seats");
        pick.setAlignmentX(Component.LEFT_ALIGNMENT);
        pick.addActionListener(e -> {
            if (!Session.isLogged()) {
                JOptionPane.showMessageDialog(this, "Please log in first",
                        "Login required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            dispose();
            new SeatSelectionFrame(s).setVisible(true);
        });
        c.add(pick);
        return c;
    }
}

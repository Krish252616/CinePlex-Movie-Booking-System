package com.movieticket.ui;

import com.movieticket.dao.MovieDAO;
import com.movieticket.model.Movie;
import com.movieticket.model.User;
import com.movieticket.util.Session;
import com.movieticket.util.Theme;
import com.movieticket.util.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class HomeFrame extends JFrame {

    private final MovieDAO movieDAO = new MovieDAO();

    private final JTextField  searchField = UI.textField(20);
    private final JComboBox<String> genreCombo    = new JComboBox<>(new String[]{
            "All", "Action", "Drama", "Comedy", "Sci-Fi", "Thriller", "Romance"});
    private final JComboBox<String> languageCombo = new JComboBox<>(new String[]{
            "All", "Hindi", "English", "Telugu", "Tamil"});

    private final JPanel moviesPanel = new JPanel();

    public HomeFrame(User u) {
        Session.set(u);
        setTitle("CinePlex - Now Showing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 760);
        setLocationRelativeTo(null);
        setContentPane(buildContent());
        refresh();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_DARK);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(new NavBar(this), BorderLayout.NORTH);
        top.add(buildFilterBar(), BorderLayout.SOUTH);
        root.add(top, BorderLayout.NORTH);

        moviesPanel.setLayout(new GridLayout(0, 4, 18, 18));
        moviesPanel.setBackground(Theme.BG_DARK);
        moviesPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JScrollPane sp = new JScrollPane(moviesPanel);
        sp.setBorder(null);
        sp.getViewport().setBackground(Theme.BG_DARK);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        root.add(sp, BorderLayout.CENTER);

        return root;
    }

    private JPanel buildFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        bar.setBackground(Theme.BG_PANEL);
        bar.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        bar.add(UI.label("Search", Theme.F_SMALL, Theme.TEXT_DIM));
        bar.add(searchField);
        bar.add(UI.label("Genre",  Theme.F_SMALL, Theme.TEXT_DIM));
        bar.add(genreCombo);
        bar.add(UI.label("Language", Theme.F_SMALL, Theme.TEXT_DIM));
        bar.add(languageCombo);

        JButton apply = UI.primaryButton("Apply filters");
        apply.addActionListener(e -> refresh());
        bar.add(apply);

        JButton reset = UI.secondaryButton("Reset");
        reset.addActionListener(e -> {
            searchField.setText("");
            genreCombo.setSelectedIndex(0);
            languageCombo.setSelectedIndex(0);
            refresh();
        });
        bar.add(reset);
        return bar;
    }

    private void refresh() {
        moviesPanel.removeAll();
        try {
            List<Movie> list = movieDAO.search(
                    searchField.getText(),
                    (String) genreCombo.getSelectedItem(),
                    (String) languageCombo.getSelectedItem());

            if (list.isEmpty()) {
                JLabel none = UI.label("No movies match your filters.",
                        Theme.F_TITLE, Theme.TEXT_DIM);
                moviesPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                moviesPanel.add(none);
            } else {
                moviesPanel.setLayout(new GridLayout(0, 4, 18, 18));
                for (Movie m : list) moviesPanel.add(movieCard(m));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error loading movies", JOptionPane.ERROR_MESSAGE);
        }
        moviesPanel.revalidate();
        moviesPanel.repaint();
    }

    private JPanel movieCard(Movie m) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Theme.BG_CARD);
        card.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1, true));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Poster placeholder (coloured block with title)
        JPanel poster = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = Theme.ACCENT, c2 = new Color(0x2A, 0x1F, 0x63);
                // vary colours by title hash
                int h = Math.abs(m.getTitle().hashCode());
                c1 = new Color((h >> 16) & 0xFF, (h >> 8) & 0xFF, h & 0xFF).darker();
                c2 = c1.darker();
                g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 220));
                g2.setFont(new Font("SansSerif", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                String t = m.getTitle();
                int x = (getWidth() - fm.stringWidth(t)) / 2;
                g2.drawString(t, x, getHeight() / 2);
                g2.dispose();
            }
        };
        poster.setPreferredSize(new Dimension(240, 180));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(BorderFactory.createEmptyBorder(12, 14, 14, 14));

        JLabel title = UI.label(m.getTitle(), Theme.F_TITLE, Theme.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel meta = UI.label(m.getGenre() + " * " + m.getLanguage()
                + " * " + m.getDurationMin() + " min",
                Theme.F_SMALL, Theme.TEXT_DIM);
        meta.setAlignmentX(Component.LEFT_ALIGNMENT);
        meta.setBorder(BorderFactory.createEmptyBorder(4, 0, 10, 0));

        JLabel rating = UI.label("Certificate: " + m.getRating(),
                Theme.F_SMALL, Theme.AMBER);
        rating.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton book = UI.primaryButton("Book tickets");
        book.setAlignmentX(Component.LEFT_ALIGNMENT);
        book.addActionListener(e -> openDetail(m));

        info.add(title);
        info.add(meta);
        info.add(rating);
        info.add(Box.createVerticalStrut(12));
        info.add(book);

        card.add(poster, BorderLayout.NORTH);
        card.add(info,   BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { openDetail(m); }
            @Override public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(Theme.ACCENT, 2, true));
            }
            @Override public void mouseExited (MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1, true));
            }
        });
        return card;
    }

    private void openDetail(Movie m) {
        dispose();
        new MovieDetailFrame(m).setVisible(true);
    }
}

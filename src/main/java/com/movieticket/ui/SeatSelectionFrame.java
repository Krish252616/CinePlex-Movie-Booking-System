package com.movieticket.ui;

import com.movieticket.model.Booking;
import com.movieticket.model.Showtime;
import com.movieticket.service.BookingService;
import com.movieticket.util.Session;
import com.movieticket.util.Theme;
import com.movieticket.util.UI;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SeatSelectionFrame extends JFrame {

    private static final int ROWS       = 6;          // A..F
    private static final int COLS       = 10;         // 1..10
    private static final String VIP_ROWS = "AB";

    private final Showtime showtime;
    private final BookingService bookingService = new BookingService();

    private final Set<String> selected = new TreeSet<>();
    private Set<String> taken;

    private final JLabel summary = UI.label(" ", Theme.F_BODY, Theme.TEXT);

    public SeatSelectionFrame(Showtime s) {
        this.showtime = s;
        setTitle("CinePlex - Select seats");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 720);
        setLocationRelativeTo(null);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_DARK);
        root.add(new NavBar(this), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(16, 16));
        body.setBackground(Theme.BG_DARK);
        body.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        body.add(buildHeader(),  BorderLayout.NORTH);
        body.add(buildSeatArea(), BorderLayout.CENTER);
        body.add(buildFooter(),  BorderLayout.SOUTH);

        root.add(body, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildHeader() {
        JPanel h = UI.card();
        h.setLayout(new GridLayout(1, 3));
        h.add(UI.label(showtime.getMovieTitle(), Theme.F_TITLE, Theme.TEXT));
        h.add(UI.label(showtime.getTheatreName() + " * " + showtime.getTheatreCity(),
                       Theme.F_BODY, Theme.TEXT_DIM));
        h.add(UI.label(showtime.getShowDate() + "  " + showtime.getShowTime()
                       + "   (Base Rs " + showtime.getPrice() + ")",
                       Theme.F_BODY, Theme.ACCENT));
        return h;
    }

    private JPanel buildSeatArea() {
        JPanel wrap = new JPanel(new BorderLayout(10, 10));
        wrap.setBackground(Theme.BG_DARK);

        // Screen indicator
        JPanel screen = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.ACCENT);
                g2.fillRoundRect(60, 12, getWidth() - 120, 8, 8, 8);
                g2.setColor(Theme.TEXT_DIM);
                g2.setFont(Theme.F_SMALL);
                String txt = "S C R E E N";
                int tw = g2.getFontMetrics().stringWidth(txt);
                g2.drawString(txt, (getWidth() - tw) / 2, 40);
                g2.dispose();
            }
        };
        screen.setPreferredSize(new Dimension(0, 50));
        screen.setBackground(Theme.BG_DARK);

        // Seat grid
        JPanel grid = new JPanel(new GridLayout(ROWS, COLS + 1, 6, 6));
        grid.setBackground(Theme.BG_DARK);
        grid.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        try { taken = bookingService.takenSeats(showtime.getShowtimeId()); }
        catch (Exception ex) { taken = Set.of(); }

        for (int r = 0; r < ROWS; r++) {
            char row = (char) ('A' + r);
            JLabel rowLbl = UI.label("  " + row,
                    Theme.F_BODY_BOLD,
                    VIP_ROWS.indexOf(row) >= 0 ? Theme.SEAT_VIP : Theme.TEXT_DIM);
            grid.add(rowLbl);
            for (int col = 1; col <= COLS; col++) grid.add(buildSeat(row, col));
        }

        // legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 8));
        legend.setBackground(Theme.BG_DARK);
        legend.add(legendBox(Theme.SEAT_FREE,  "Available"));
        legend.add(legendBox(Theme.SEAT_SEL,   "Selected"));
        legend.add(legendBox(Theme.SEAT_TAKEN, "Booked"));
        legend.add(legendBox(Theme.SEAT_VIP,   "VIP (rows A & B)"));

        wrap.add(screen, BorderLayout.NORTH);
        wrap.add(grid,   BorderLayout.CENTER);
        wrap.add(legend, BorderLayout.SOUTH);
        return wrap;
    }

    private JButton buildSeat(char row, int col) {
        String code = row + String.valueOf(col);
        boolean isTaken = taken.contains(code);
        boolean isVip   = VIP_ROWS.indexOf(row) >= 0;

        JButton b = new JButton(String.valueOf(col)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(Theme.F_SMALL);
        b.setForeground(Theme.TEXT);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setPreferredSize(new Dimension(48, 38));
        b.setFocusPainted(false);

        if (isTaken) {
            b.setBackground(Theme.SEAT_TAKEN);
            b.setForeground(Theme.TEXT_DIM);
            b.setEnabled(false);
            b.setToolTipText(code + " (sold)");
        } else {
            b.setBackground(isVip ? Theme.SEAT_VIP : Theme.SEAT_FREE);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setToolTipText(code + (isVip ? " (VIP +Rs100)" : ""));
            b.addActionListener(e -> {
                if (selected.contains(code)) {
                    selected.remove(code);
                    b.setBackground(isVip ? Theme.SEAT_VIP : Theme.SEAT_FREE);
                } else {
                    if (selected.size() >= BookingService.MAX_SEATS_PER_BOOKING) {
                        JOptionPane.showMessageDialog(this,
                                "Maximum " + BookingService.MAX_SEATS_PER_BOOKING
                                + " seats per booking.",
                                "Limit reached", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    selected.add(code);
                    b.setBackground(Theme.SEAT_SEL);
                }
                updateSummary();
            });
        }
        return b;
    }

    private JPanel legendBox(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        JPanel swatch = new JPanel();
        swatch.setBackground(c);
        swatch.setPreferredSize(new Dimension(16, 16));
        p.add(swatch);
        p.add(UI.label(text, Theme.F_SMALL, Theme.TEXT_DIM));
        return p;
    }

    private JPanel buildFooter() {
        JPanel f = UI.card();
        f.setLayout(new BorderLayout(12, 0));

        updateSummary();
        f.add(summary, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton cancel = UI.secondaryButton("Cancel");
        cancel.addActionListener(e -> {
            dispose();
            new HomeFrame(Session.get()).setVisible(true);
        });

        JButton confirm = UI.primaryButton("Confirm Booking");
        confirm.addActionListener(e -> doBooking());

        right.add(cancel);
        right.add(confirm);
        f.add(right, BorderLayout.EAST);
        return f;
    }

    private void updateSummary() {
        if (selected.isEmpty()) {
            summary.setText("No seats selected");
            return;
        }
        BigDecimal total = bookingService.calculateTotal(
                showtime.getPrice(), new ArrayList<>(selected));
        summary.setText("<html><b>Seats:</b> " + String.join(", ", selected)
                + "   &nbsp;&nbsp; <b>Total:</b> &#x20B9;" + total + "</html>");
    }

    private void doBooking() {
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select at least one seat",
                    "Nothing selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "Confirm booking for seats: " + String.join(", ", selected) + " ?",
                "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            Booking b = bookingService.book(
                    Session.get().getUserId(),
                    showtime.getShowtimeId(),
                    new ArrayList<>(selected));

            JOptionPane.showMessageDialog(this,
                    "Booking confirmed!\n\n"
                    + "Booking ID : " + b.getBookingId() + "\n"
                    + "Seats      : " + b.getSeats() + "\n"
                    + "Total      : Rs " + b.getTotalAmount(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            dispose();
            new MyBookingsFrame().setVisible(true);
        } catch (BookingService.BookingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Booking failed", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

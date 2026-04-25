package com.movieticket.ui;

import com.movieticket.model.Booking;
import com.movieticket.service.BookingService;
import com.movieticket.util.Session;
import com.movieticket.util.Theme;
import com.movieticket.util.UI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MyBookingsFrame extends JFrame {

    private final BookingService bookingService = new BookingService();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Movie", "Theatre", "Date", "Time", "Seats", "Amount", "Status"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public MyBookingsFrame() {
        setTitle("CinePlex - My Bookings");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 620);
        setLocationRelativeTo(null);
        setContentPane(buildContent());
        refresh();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_DARK);
        root.add(new NavBar(this), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(12, 12));
        body.setBackground(Theme.BG_DARK);
        body.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel h = UI.label("Your Bookings", Theme.F_HEADER, Theme.TEXT);
        body.add(h, BorderLayout.NORTH);

        table.setRowHeight(32);
        table.setFont(Theme.F_BODY);
        table.setForeground(Theme.TEXT);
        table.setBackground(Theme.BG_PANEL);
        table.setSelectionBackground(Theme.ACCENT_DARK);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(Theme.BORDER);
        table.getTableHeader().setFont(Theme.F_BODY_BOLD);
        table.getTableHeader().setBackground(Theme.BG_CARD);
        table.getTableHeader().setForeground(Theme.TEXT);

        // status column renderer
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSel, boolean hasFoc,
                    int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        t, value, isSel, hasFoc, row, col);
                String v = String.valueOf(value);
                c.setForeground("CONFIRMED".equals(v) ? Theme.GREEN : Theme.RED);
                c.setFont(Theme.F_BODY_BOLD);
                return c;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(Theme.BG_PANEL);
        sp.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        body.add(sp, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);

        JButton refresh = UI.secondaryButton("Refresh");
        refresh.addActionListener(e -> refresh());

        JButton cancel = UI.dangerButton("Cancel selected booking");
        cancel.addActionListener(e -> cancelSelected());

        footer.add(refresh);
        footer.add(cancel);
        body.add(footer, BorderLayout.SOUTH);

        root.add(body, BorderLayout.CENTER);
        return root;
    }

    private void refresh() {
        model.setRowCount(0);
        try {
            List<Booking> list = bookingService.historyFor(Session.get().getUserId());
            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "You haven't made any bookings yet.",
                        "No bookings", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            for (Booking b : list) {
                model.addRow(new Object[]{
                        b.getBookingId(),
                        b.getMovieTitle(),
                        b.getTheatreName(),
                        b.getShowDate(),
                        b.getShowTime(),
                        b.getSeats(),
                        "Rs " + b.getTotalAmount(),
                        b.getStatus().name()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please pick a booking to cancel.",
                    "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String status = String.valueOf(model.getValueAt(row, 7));
        if ("CANCELLED".equals(status)) {
            JOptionPane.showMessageDialog(this, "Booking is already cancelled.",
                    "Already cancelled", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (Integer) model.getValueAt(row, 0);
        int ok = JOptionPane.showConfirmDialog(this,
                "Cancel booking #" + id + " ? This cannot be undone.",
                "Confirm cancellation", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            bookingService.cancel(id);
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

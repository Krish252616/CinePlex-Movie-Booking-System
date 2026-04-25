package com.movieticket.util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Small factory of pre-styled Swing widgets. */
public final class UI {

    private UI() { }

    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    public static JButton primaryButton(String text) {
        return styledButton(text, Theme.ACCENT, Color.WHITE, Theme.ACCENT_DARK);
    }

    public static JButton secondaryButton(String text) {
        return styledButton(text, Theme.BG_CARD, Theme.TEXT, Theme.BORDER);
    }

    public static JButton dangerButton(String text) {
        return styledButton(text, Theme.RED, Color.WHITE, new Color(0xB0, 0x30, 0x40));
    }

    private static JButton styledButton(String text, Color bg, Color fg, Color hoverBg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(Theme.F_BODY_BOLD);
        b.setForeground(fg);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color base = bg;
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(hoverBg); }
            @Override public void mouseExited (MouseEvent e) { b.setBackground(base); }
        });
        return b;
    }

    public static JTextField textField(int cols) {
        JTextField f = new JTextField(cols);
        stylise(f);
        return f;
    }

    public static JPasswordField passwordField(int cols) {
        JPasswordField f = new JPasswordField(cols);
        stylise(f);
        return f;
    }

    public static void stylise(JTextField f) {
        f.setBackground(Theme.BG_CARD);
        f.setForeground(Theme.TEXT);
        f.setCaretColor(Theme.TEXT);
        f.setFont(Theme.F_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }

    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(Theme.BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        return p;
    }

    public static Border titledBorder(String title) {
        Border line = BorderFactory.createLineBorder(Theme.BORDER);
        javax.swing.border.TitledBorder t = BorderFactory.createTitledBorder(
                line, title, javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP, Theme.F_BODY_BOLD, Theme.TEXT_DIM);
        return t;
    }
}

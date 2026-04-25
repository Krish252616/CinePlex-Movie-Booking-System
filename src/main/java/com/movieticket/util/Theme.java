package com.movieticket.util;

import java.awt.Color;
import java.awt.Font;

/** Central place for colours and fonts so every screen looks consistent. */
public final class Theme {

    // Dark cinema palette
    public static final Color BG_DARK     = new Color(0x0F, 0x14, 0x1E); // deep navy
    public static final Color BG_PANEL    = new Color(0x1A, 0x21, 0x2F); // slightly lighter
    public static final Color BG_CARD     = new Color(0x24, 0x2C, 0x3D);
    public static final Color BORDER      = new Color(0x2F, 0x38, 0x4B);

    public static final Color ACCENT      = new Color(0x6C, 0x63, 0xFF); // electric indigo
    public static final Color ACCENT_DARK = new Color(0x55, 0x4D, 0xDC);

    public static final Color TEXT        = new Color(0xEA, 0xEC, 0xF2);
    public static final Color TEXT_DIM    = new Color(0x9A, 0xA3, 0xB8);

    public static final Color GREEN       = new Color(0x3B, 0xD6, 0x7F); // confirmed
    public static final Color RED         = new Color(0xE5, 0x50, 0x60); // cancelled
    public static final Color AMBER       = new Color(0xFF, 0xB8, 0x3B);

    // Seat colours
    public static final Color SEAT_FREE   = new Color(0x3E, 0x48, 0x5E);
    public static final Color SEAT_SEL    = ACCENT;
    public static final Color SEAT_TAKEN  = new Color(0x4A, 0x2A, 0x33);
    public static final Color SEAT_VIP    = new Color(0x8B, 0x60, 0x2B);

    // Fonts
    public static final Font  F_HEADER    = new Font("SansSerif", Font.BOLD, 22);
    public static final Font  F_TITLE     = new Font("SansSerif", Font.BOLD, 18);
    public static final Font  F_BODY      = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font  F_BODY_BOLD = new Font("SansSerif", Font.BOLD, 14);
    public static final Font  F_SMALL     = new Font("SansSerif", Font.PLAIN, 12);

    private Theme() { }
}

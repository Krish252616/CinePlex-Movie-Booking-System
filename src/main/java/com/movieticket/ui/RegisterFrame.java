package com.movieticket.ui;

import com.movieticket.service.AuthService;
import com.movieticket.util.Theme;
import com.movieticket.util.UI;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private final AuthService auth = new AuthService();

    private final JTextField     nameField  = UI.textField(22);
    private final JTextField     emailField = UI.textField(22);
    private final JTextField     phoneField = UI.textField(22);
    private final JPasswordField passField  = UI.passwordField(22);
    private final JPasswordField confField  = UI.passwordField(22);

    public RegisterFrame() {
        setTitle("CinePlex - Create Account");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 640);
        setLocationRelativeTo(null);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(Theme.BG_PANEL);

        JPanel card = UI.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = UI.label("Create your account", Theme.F_HEADER, Theme.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sub   = UI.label("It only takes a minute", Theme.F_SMALL, Theme.TEXT_DIM);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        card.add(title); card.add(sub);

        card.add(block("Full name",        nameField));
        card.add(Box.createVerticalStrut(10));
        card.add(block("Email",            emailField));
        card.add(Box.createVerticalStrut(10));
        card.add(block("Phone (10 digits, optional)", phoneField));
        card.add(Box.createVerticalStrut(10));
        card.add(block("Password (min 6 chars)", passField));
        card.add(Box.createVerticalStrut(10));
        card.add(block("Confirm password", confField));
        card.add(Box.createVerticalStrut(22));

        JButton create = UI.primaryButton("Create Account");
        create.setAlignmentX(Component.LEFT_ALIGNMENT);
        create.addActionListener(e -> doRegister());
        card.add(create);

        card.add(Box.createVerticalStrut(10));

        JButton back = UI.secondaryButton("Back to sign in");
        back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        card.add(back);

        getRootPane().setDefaultButton(create);
        wrap.add(card);
        return wrap;
    }

    private JPanel block(String caption, JComponent field) {
        JPanel b = new JPanel();
        b.setOpaque(false);
        b.setLayout(new BoxLayout(b, BoxLayout.Y_AXIS));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = UI.label(caption, Theme.F_SMALL, Theme.TEXT_DIM);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.add(l); b.add(Box.createVerticalStrut(4)); b.add(field);
        return b;
    }

    private void doRegister() {
        String pass = new String(passField.getPassword());
        String conf = new String(confField.getPassword());
        if (!pass.equals(conf)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match",
                    "Check passwords", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            auth.register(nameField.getText(), emailField.getText(), pass, phoneField.getText());
            JOptionPane.showMessageDialog(this,
                    "Account created! Please sign in.",
                    "Welcome", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginFrame().setVisible(true);
        } catch (AuthService.AuthException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Registration failed", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

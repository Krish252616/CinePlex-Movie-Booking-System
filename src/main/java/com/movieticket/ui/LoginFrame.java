package com.movieticket.ui;

import com.movieticket.model.User;
import com.movieticket.service.AuthService;
import com.movieticket.util.Theme;
import com.movieticket.util.UI;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final AuthService auth = new AuthService();

    private final JTextField     emailField = UI.textField(22);
    private final JPasswordField passField  = UI.passwordField(22);

    public LoginFrame() {
        setTitle("CinePlex - Sign In");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(880, 560);
        setLocationRelativeTo(null);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new GridLayout(1, 2));
        root.add(buildBrandPanel());
        root.add(buildFormPanel());
        return root;
    }

    private JPanel buildBrandPanel() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0x2A, 0x1F, 0x63),
                        getWidth(), getHeight(), Theme.BG_DARK);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(60, 50, 40, 40));

        JLabel logo = new JLabel("\uD83C\uDFAC  CinePlex");
        logo.setFont(new Font("SansSerif", Font.BOLD, 42));
        logo.setForeground(Color.WHITE);

        JLabel tag = new JLabel("Book your perfect seat.");
        tag.setFont(Theme.F_TITLE);
        tag.setForeground(new Color(0xCF, 0xD2, 0xE5));
        tag.setBorder(BorderFactory.createEmptyBorder(12, 2, 0, 0));

        JLabel sub = new JLabel(
                "<html><div style='width:280px;color:#AAB0CA;font-size:12px;line-height:1.5;'>"
              + "Browse the latest movies, choose your favourite seats and get instant"
              + " digital tickets. Welcome back!</div></html>");
        sub.setBorder(BorderFactory.createEmptyBorder(30, 2, 0, 0));

        p.add(logo);
        p.add(tag);
        p.add(sub);
        return p;
    }

    private JPanel buildFormPanel() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(Theme.BG_PANEL);

        JPanel form = UI.card();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel title = UI.label("Sign in to your account", Theme.F_HEADER, Theme.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = UI.label("Use your email and password", Theme.F_SMALL, Theme.TEXT_DIM);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        form.add(title);
        form.add(sub);
        form.add(fieldBlock("Email",    emailField));
        form.add(Box.createVerticalStrut(12));
        form.add(fieldBlock("Password", passField));
        form.add(Box.createVerticalStrut(22));

        JButton signIn = UI.primaryButton("Sign In");
        signIn.setAlignmentX(Component.LEFT_ALIGNMENT);
        signIn.addActionListener(e -> doLogin());
        form.add(signIn);

        form.add(Box.createVerticalStrut(14));

        JButton register = UI.secondaryButton("Create new account");
        register.setAlignmentX(Component.LEFT_ALIGNMENT);
        register.addActionListener(e -> {
            dispose();
            new RegisterFrame().setVisible(true);
        });
        form.add(register);

        form.add(Box.createVerticalStrut(20));
        JLabel demo = new JLabel(
                "<html><div style='color:#9AA3B8;font-size:11px;line-height:1.6;'>"
              + "Demo logins &mdash;<br>"
              + "User  : rahul@gmail.com / user123<br>"
              + "Admin : admin@cineplex.com / admin123</div></html>");
        demo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(demo);

        // Enter key triggers login
        getRootPane().setDefaultButton(signIn);

        wrap.add(form);
        return wrap;
    }

    private JPanel fieldBlock(String caption, JComponent field) {
        JPanel b = new JPanel();
        b.setOpaque(false);
        b.setLayout(new BoxLayout(b, BoxLayout.Y_AXIS));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = UI.label(caption, Theme.F_SMALL, Theme.TEXT_DIM);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.add(l);
        b.add(Box.createVerticalStrut(4));
        b.add(field);
        return b;
    }

    private void doLogin() {
        String email = emailField.getText();
        String pass  = new String(passField.getPassword());
        try {
            User u = auth.login(email, pass);
            dispose();
            new HomeFrame(u).setVisible(true);
        } catch (AuthService.AuthException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Sign-in failed", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

package com.movieticket.service;

import com.movieticket.dao.UserDAO;
import com.movieticket.model.User;
import com.movieticket.util.PasswordUtil;
import com.movieticket.util.Session;

import java.sql.SQLException;

/** Authentication / registration business logic. */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String email, String plainPassword) throws SQLException, AuthException {
        if (email == null || email.isBlank() || plainPassword == null || plainPassword.isBlank())
            throw new AuthException("Email and password are required");

        User u = userDAO.findByEmail(email.trim().toLowerCase());
        if (u == null)
            throw new AuthException("No account found for this email");

        if (!PasswordUtil.matches(plainPassword, u.getPassword()))
            throw new AuthException("Incorrect password");

        Session.set(u);
        return u;
    }

    public User register(String fullName, String email, String plainPassword,
                         String phone) throws SQLException, AuthException {
        if (fullName == null || fullName.isBlank())
            throw new AuthException("Full name is required");
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$"))
            throw new AuthException("Please enter a valid email address");
        if (plainPassword == null || plainPassword.length() < 6)
            throw new AuthException("Password must be at least 6 characters");
        if (phone != null && !phone.isBlank() && !phone.matches("^\\d{10}$"))
            throw new AuthException("Phone must be exactly 10 digits");

        email = email.trim().toLowerCase();
        if (userDAO.findByEmail(email) != null)
            throw new AuthException("An account with this email already exists");

        User u = new User(fullName.trim(), email,
                PasswordUtil.hash(plainPassword),
                phone, User.Role.USER);
        if (!userDAO.register(u))
            throw new AuthException("Registration failed, please try again");
        return u;
    }

    public void logout() { Session.clear(); }

    // ----------------------------------------------------------------
    public static class AuthException extends Exception {
        public AuthException(String msg) { super(msg); }
    }
}

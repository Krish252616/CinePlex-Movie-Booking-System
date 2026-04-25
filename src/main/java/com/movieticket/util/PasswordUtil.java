package com.movieticket.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Simple SHA-256 hashing utility.
 *
 * In a real production system you would use BCrypt/Argon2 with a salt,
 * but for a college project SHA-256 is an acceptable demonstration of
 * secure storage (plaintext passwords are never saved in the DB).
 */
public final class PasswordUtil {

    private PasswordUtil() { }

    public static String hash(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] out = md.digest(plain.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : out) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public static boolean matches(String plain, String storedHash) {
        return hash(plain).equalsIgnoreCase(storedHash);
    }
}

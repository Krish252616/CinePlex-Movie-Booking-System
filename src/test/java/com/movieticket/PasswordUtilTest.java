package com.movieticket;

import com.movieticket.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PasswordUtil}.
 * Covers hashing determinism, non-reversibility and mismatch detection.
 */
class PasswordUtilTest {

    @Test void hashIsDeterministic() {
        assertEquals(PasswordUtil.hash("secret"), PasswordUtil.hash("secret"));
    }

    @Test void differentInputsYieldDifferentHashes() {
        assertNotEquals(PasswordUtil.hash("abc"), PasswordUtil.hash("abcd"));
    }

    @Test void hashIsNotEqualToPlaintext() {
        String plain = "MyP@ssw0rd!";
        assertNotEquals(plain, PasswordUtil.hash(plain));
    }

    @Test void hashLengthIs64HexChars() {
        // SHA-256 -> 32 bytes -> 64 hex chars
        assertEquals(64, PasswordUtil.hash("anything").length());
        assertTrue(PasswordUtil.hash("anything").matches("^[0-9a-f]{64}$"));
    }

    @Test void matchesAcceptsCorrectPassword() {
        String hash = PasswordUtil.hash("open-sesame");
        assertTrue(PasswordUtil.matches("open-sesame", hash));
    }

    @Test void matchesRejectsWrongPassword() {
        String hash = PasswordUtil.hash("real-password");
        assertFalse(PasswordUtil.matches("wrong-password", hash));
    }

    @Test void seededHashesMatchKnownValues() {
        // Values used in database/schema.sql  --  must stay in sync.
        assertEquals(
            "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9",
            PasswordUtil.hash("admin123"));
        assertEquals(
            "e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446",
            PasswordUtil.hash("user123"));
    }
}

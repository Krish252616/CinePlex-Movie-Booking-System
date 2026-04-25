package com.movieticket.model;

import java.sql.Timestamp;

public class User {
    public enum Role { USER, ADMIN }

    private int       userId;
    private String    fullName;
    private String    email;
    private String    password;   // hashed
    private String    phone;
    private Role      role;
    private Timestamp createdAt;

    public User() { }

    public User(String fullName, String email, String password,
                String phone, Role role) {
        this.fullName = fullName;
        this.email    = email;
        this.password = password;
        this.phone    = phone;
        this.role     = role;
    }

    public int       getUserId()    { return userId; }
    public void      setUserId(int v){ this.userId = v; }
    public String    getFullName()  { return fullName; }
    public void      setFullName(String v){ this.fullName = v; }
    public String    getEmail()     { return email; }
    public void      setEmail(String v){ this.email = v; }
    public String    getPassword()  { return password; }
    public void      setPassword(String v){ this.password = v; }
    public String    getPhone()     { return phone; }
    public void      setPhone(String v){ this.phone = v; }
    public Role      getRole()      { return role; }
    public void      setRole(Role v){ this.role = v; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void      setCreatedAt(Timestamp v){ this.createdAt = v; }

    public boolean isAdmin() { return role == Role.ADMIN; }

    @Override public String toString() { return fullName + " <" + email + ">"; }
}

package com.movieticket.util;

import com.movieticket.model.User;

/** Singleton holder for the currently-logged-in user. */
public final class Session {

    private static User currentUser;

    private Session() { }

    public static void set(User u)    { currentUser = u; }
    public static User get()          { return currentUser; }
    public static void clear()        { currentUser = null; }
    public static boolean isLogged()  { return currentUser != null; }
    public static boolean isAdmin()   { return currentUser != null && currentUser.isAdmin(); }
}

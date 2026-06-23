package com.example.facerecognitionsystem.model;

import java.time.LocalDateTime;

/** Menyimpan pengguna yang sedang login selama aplikasi berjalan. */
public final class UserSession {
    private static UserSession instance;
    private final String userName;
    private final LocalDateTime loginTime;

    private UserSession(String userName) {
        this.userName = userName;
        this.loginTime = LocalDateTime.now();
    }

    public static synchronized void login(String userName) { instance = new UserSession(userName); }
    public static synchronized UserSession getInstance() { return instance; }
    public static synchronized boolean isLoggedIn() { return instance != null; }
    public static synchronized void logout() { instance = null; }
    public String getUserName() { return userName; }
    public LocalDateTime getLoginTime() { return loginTime; }
}

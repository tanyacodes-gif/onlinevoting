package com.voting.service;

import com.voting.dao.UserDAO;
import com.voting.model.User;

public class AuthService {

    private UserDAO userDAO = new UserDAO();
    private User loggedInUser = null;

    // Login - returns user if credentials are correct
    public User login(String username, String password) {
        User user = userDAO.getUserByCredentials(username, password);
        if (user != null) {
            loggedInUser = user;
            System.out.println("Login successful: " + user.getUsername());
        } else {
            System.out.println("Invalid credentials!");
        }
        return user;
    }

    public void logout() {
        if (loggedInUser != null) {
            System.out.println("User logged out: " + loggedInUser.getUsername());
            loggedInUser = null;
        }
    }
    public User getLoggedInUser() {
        return loggedInUser;
    }
    public boolean isLoggedIn() {
        return loggedInUser != null;
    }
    public boolean isAdmin() {
        return loggedInUser != null && "ADMIN".equals(loggedInUser.getRole());
    }

    public boolean isVoter() {
        return loggedInUser != null && "VOTER".equals(loggedInUser.getRole());
    }

    public boolean isCandidate() {
        return loggedInUser != null && "CANDIDATE".equals(loggedInUser.getRole());
    }
}
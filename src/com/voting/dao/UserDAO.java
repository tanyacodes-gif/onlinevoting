package com.voting.dao;

import com.voting.config.DBConnection;
import com.voting.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User getUserByCredentials(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getBoolean("has_voted")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user: " + e.getMessage());
        }
        return null;
    }

    public boolean hasVoted(int userId) {
        String query = "SELECT has_voted FROM users WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("has_voted");
            }
        } catch (SQLException e) {
            System.out.println("Error checking vote status: " + e.getMessage());
        }
        return false;
    }

    public boolean markAsVoted(int userId) {
        String query = "UPDATE users SET has_voted = true WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error updating vote status: " + e.getMessage());
        }
        return false;
    }

    public boolean registerUser(String username, String password, String role) {
        if (username == null || username.trim().isEmpty()) return false;
        if (password == null || password.trim().isEmpty()) return false;
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username.trim());
            stmt.setString(2, password.trim());
            stmt.setString(3, role);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
        }
        return false;
    }

    // Admin Voter Management: fetch all non-admin users
    public List<User> getAllVoters() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role != 'ADMIN' ORDER BY role, username";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getBoolean("has_voted")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching voters: " + e.getMessage());
        }
        return users;
    }

    // Admin Voter Management: delete a user (admins are protected)
    public boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE id = ? AND role != 'ADMIN'";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
        return false;
    }

    // Check if a username already exists (used in RegisterFrame validation)
    public boolean usernameExists(String username) {
        String query = "SELECT id FROM users WHERE username = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username.trim());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking username: " + e.getMessage());
        }
        return false;
    }
}
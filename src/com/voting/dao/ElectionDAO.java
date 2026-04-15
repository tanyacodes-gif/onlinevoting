package com.voting.dao;

import com.voting.config.DBConnection;
import com.voting.model.ElectionSettings;
import java.sql.*;

public class ElectionDAO {
    public ElectionSettings getElectionSettings() {
        String query = "SELECT * FROM election_settings WHERE id = 1";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ElectionSettings(
                    rs.getInt("id"),
                    rs.getTimestamp("start_time"),
                    rs.getTimestamp("end_time"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error fetching election settings: " + e.getMessage());
        }
        return null;
    }
    public boolean isElectionActive() {
        ElectionSettings settings = getElectionSettings();
        if (settings == null) return false;
        return "ACTIVE".equals(settings.getStatus());
    }
    public boolean startElection(Timestamp startTime, Timestamp endTime) {
        String query = "UPDATE election_settings SET status = 'ACTIVE', " +
                      "start_time = ?, end_time = ? WHERE id = 1";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setTimestamp(1, startTime);
            stmt.setTimestamp(2, endTime);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error starting election: " + e.getMessage());
        }
        return false;
    }
    public boolean stopElection() {
        String query = "UPDATE election_settings SET status = 'INACTIVE' WHERE id = 1";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error stopping election: " + e.getMessage());
        }
        return false;
    }

    public boolean resetElection() {
        String query = "UPDATE election_settings SET status = 'INACTIVE', " +
                      "start_time = NULL, end_time = NULL WHERE id = 1";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            int rows = stmt.executeUpdate();

            String resetVotes = "UPDATE users SET has_voted = false";
            PreparedStatement stmt2 = conn.prepareStatement(resetVotes);
            stmt2.executeUpdate();

            String clearVotes = "DELETE FROM votes";
            PreparedStatement stmt3 = conn.prepareStatement(clearVotes);
            stmt3.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error resetting election: " + e.getMessage());
        }
        return false;
    }
}

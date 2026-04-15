package com.voting.dao;

import com.voting.config.DBConnection;
import com.voting.model.Vote;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoteDAO {
    public boolean castVote(int voterId, int candidateId) {
        String query = "INSERT INTO votes (voter_id, candidate_id) VALUES (?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, voterId);
            stmt.setInt(2, candidateId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error casting vote: " + e.getMessage());
        }
        return false;
    }
    //(Admin only)
    public List<Vote> getAllVotes() {
        List<Vote> votes = new ArrayList<>();
        String query = "SELECT * FROM votes";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                votes.add(new Vote(
                    rs.getInt("id"),
                    rs.getInt("voter_id"),
                    rs.getInt("candidate_id"),
                    rs.getTimestamp("timestamp")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching votes: " + e.getMessage());
        }
        return votes;
    }
    public int getTotalVotes() {
        String query = "SELECT COUNT(*) FROM votes";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching total votes: " + e.getMessage());
        }
        return 0;
    }
    public int getVotesByCandidate(int candidateId) {
        String query = "SELECT COUNT(*) FROM votes WHERE candidate_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, candidateId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching votes by candidate: " + e.getMessage());
        }
        return 0;
    }
}
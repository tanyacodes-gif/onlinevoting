package com.voting.dao;

import com.voting.config.DBConnection;
import com.voting.model.Candidate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidateDAO {

    public List<Candidate> getAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        String query = "SELECT * FROM candidates";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                candidates.add(new Candidate(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("party")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching candidates: " + e.getMessage());
        }
        return candidates;
    }
    //(Admin only)
    public boolean addCandidate(String name, String party) {
        String query = "INSERT INTO candidates (name, party) VALUES (?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, party);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error adding candidate: " + e.getMessage());
        }
        return false;
    }
    public boolean removeCandidate(int candidateId) {
        String query = "DELETE FROM candidates WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, candidateId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error removing candidate: " + e.getMessage());
        }
        return false;
    }
    public int getVoteCount(int candidateId) {
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
            System.out.println("Error fetching vote count: " + e.getMessage());
        }
        return 0;
    }
}
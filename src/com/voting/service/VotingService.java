package com.voting.service;

import com.voting.dao.CandidateDAO;
import com.voting.dao.ElectionDAO;
import com.voting.dao.UserDAO;
import com.voting.dao.VoteDAO;
import com.voting.model.Candidate;
import java.util.List;

public class VotingService {

    private UserDAO userDAO = new UserDAO();
    private VoteDAO voteDAO = new VoteDAO();
    private CandidateDAO candidateDAO = new CandidateDAO();
    private ElectionDAO electionDAO = new ElectionDAO();

    // Core voting method — checks all conditions before casting vote
    public String castVote(int voterId, int candidateId) {

        // Step 1 — Check if election is active
        if (!electionDAO.isElectionActive()) {
            return "ELECTION_NOT_ACTIVE";
        }

        // Step 2 — Check if voter has already voted
        if (userDAO.hasVoted(voterId)) {
            return "ALREADY_VOTED";
        }

        // Step 3 — Cast the vote
        boolean voteCast = voteDAO.castVote(voterId, candidateId);
        if (!voteCast) {
            return "VOTE_FAILED";
        }

        // Step 4 — Mark voter as voted
        boolean marked = userDAO.markAsVoted(voterId);
        if (!marked) {
            return "MARK_FAILED";
        }

        return "SUCCESS";
    }

    // Get all candidates for display in JTable
    public List<Candidate> getAllCandidates() {
        return candidateDAO.getAllCandidates();
    }

    // Get vote count for a specific candidate
    public int getVoteCount(int candidateId) {
        return voteDAO.getVotesByCandidate(candidateId);
    }

    // Get total votes cast
    public int getTotalVotes() {
        return voteDAO.getTotalVotes();
    }

    // Get vote percentage for a candidate
    public double getVotePercentage(int candidateId) {
        int total = getTotalVotes();
        if (total == 0) return 0.0;
        int candidateVotes = getVoteCount(candidateId);
        return ((double) candidateVotes / total) * 100;
    }

    // Check if election is active
    public boolean isElectionActive() {
        return electionDAO.isElectionActive();
    }
}
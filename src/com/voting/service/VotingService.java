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

    
    public String castVote(int voterId, int candidateId) {

        if (!electionDAO.isElectionActive()) {
            return "ELECTION_NOT_ACTIVE";
        }
        if (userDAO.hasVoted(voterId)) {
            return "ALREADY_VOTED";
        }
        boolean voteCast = voteDAO.castVote(voterId, candidateId);
        if (!voteCast) {
            return "VOTE_FAILED";
        }

        boolean marked = userDAO.markAsVoted(voterId);
        if (!marked) {
            return "MARK_FAILED";
        }

        return "SUCCESS";
    }
    public List<Candidate> getAllCandidates() {
        return candidateDAO.getAllCandidates();
    }

    public int getVoteCount(int candidateId) {
        return voteDAO.getVotesByCandidate(candidateId);
    }
    public int getTotalVotes() {
        return voteDAO.getTotalVotes();
    }
    public double getVotePercentage(int candidateId) {
        int total = getTotalVotes();
        if (total == 0) return 0.0;
        int candidateVotes = getVoteCount(candidateId);
        return ((double) candidateVotes / total) * 100;
    }

    public boolean isElectionActive() {
        return electionDAO.isElectionActive();
    }
}
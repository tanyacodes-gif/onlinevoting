package com.voting.model;

import java.sql.Timestamp;

public class Vote {
    private int id;
    private int voterId;
    private int candidateId;
    private Timestamp timestamp;

    public Vote(int id, int voterId, int candidateId, Timestamp timestamp) {
        this.id = id;
        this.voterId = voterId;
        this.candidateId = candidateId;
        this.timestamp = timestamp;
    }

    public Vote() {}

    public int getId() { return id; }
    public int getVoterId() { return voterId; }
    public int getCandidateId() { return candidateId; }
    public Timestamp getTimestamp() { return timestamp; }

    public void setId(int id) { this.id = id; }
    public void setVoterId(int voterId) { this.voterId = voterId; }
    public void setCandidateId(int candidateId) { this.candidateId = candidateId; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "Vote{id=" + id + ", voterId=" + voterId + ", candidateId=" + candidateId + ", timestamp=" + timestamp + "}";
    }
}

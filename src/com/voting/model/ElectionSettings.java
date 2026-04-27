package com.voting.model;

import java.sql.Timestamp;

public class ElectionSettings {
    private int id;
    private Timestamp startTime;
    private Timestamp endTime;
    private String status;

    public ElectionSettings(int id, Timestamp startTime, Timestamp endTime, String status) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }
    public ElectionSettings() {}

    public int getId() { return id; }
    public Timestamp getStartTime() { return startTime; }
    public Timestamp getEndTime() { return endTime; }
    public String getStatus() { return status; }

    public void setId(int id) { this.id = id; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }
    public void setEndTime(Timestamp endTime) { this.endTime = endTime; }
    public void setStatus(String status) { this.status = status; }

    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }

    @Override
    public String toString() {
        return "ElectionSettings{id=" + id + ", status='" + status + "', startTime=" + startTime + ", endTime=" + endTime + "}";
    }
}

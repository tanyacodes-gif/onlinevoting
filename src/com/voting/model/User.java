package com.voting.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private boolean hasVoted;

    public User(int id, String username, String password, String role, boolean hasVoted) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.hasVoted = hasVoted;
    }

    public User() {}

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public boolean isHasVoted() { return hasVoted; }

    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setHasVoted(boolean hasVoted) { this.hasVoted = hasVoted; }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role='" + role + "', hasVoted=" + hasVoted + "}";
    }
}

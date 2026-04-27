package com.voting.model;

public class Candidate {
    private int id;
    private String name;
    private String party;

    public Candidate(int id, String name, String party) {
        this.id = id;
        this.name = name;
        this.party = party;
    }

    public Candidate() {}

    public int getId() { return id; }
    public String getName() { return name; }
    public String getParty() { return party; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setParty(String party) { this.party = party; }

    @Override
    public String toString() {
        return "Candidate{id=" + id + ", name='" + name + "', party='" + party + "'}";
    }
}
package com.voting.view;

import com.voting.model.Candidate;
import com.voting.model.User;
import com.voting.service.AuthService;
import com.voting.service.VotingService;
import com.voting.util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CandidatePanel extends JFrame {

    private AuthService authService;
    private VotingService votingService;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JLabel totalVotesLabel;

    public CandidatePanel(AuthService authService) {
        this.authService = authService;
        this.votingService = new VotingService();
        initUI();
        SessionManager sessionManager = new SessionManager(authService, this, () -> {
    dispose();
    new LoginFrame(new AuthService());
    });
    sessionManager.start();
    }

    private void initUI() {
        User user = authService.getLoggedInUser();

        setTitle("Online Voting System - Candidate Panel");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 30, 60));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Welcome, " + user.getUsername() + " 🏆");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.YELLOW);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.YELLOW);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        // Results table
        String[] columns = {"ID", "Candidate Name", "Party", "Votes", "Percentage"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        resultsTable = new JTable(tableModel);
        resultsTable.setRowHeight(30);
        resultsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        resultsTable.setFont(new Font("Arial", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Live Vote Results"));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(240, 248, 255));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        totalVotesLabel = new JLabel("Total Votes: 0", SwingConstants.CENTER);
        totalVotesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalVotesLabel.setForeground(new Color(30, 30, 60));

        JButton refreshButton = new JButton("🔃 Refresh Results");
        refreshButton.setBackground(new Color(70, 130, 180));
        refreshButton.setForeground(Color.YELLOW);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setPreferredSize(new Dimension(180, 35));

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));

        JLabel electionLabel = new JLabel();
        if (votingService.isElectionActive()) {
            electionLabel.setText("🟢 Election is currently ACTIVE");
            electionLabel.setForeground(new Color(40, 167, 69));
        } else {
            electionLabel.setText("🔴 Election is currently INACTIVE");
            electionLabel.setForeground(new Color(220, 53, 69));
        }
        electionLabel.setFont(new Font("Arial", Font.BOLD, 13));
        electionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        bottomPanel.add(electionLabel, BorderLayout.NORTH);
        bottomPanel.add(totalVotesLabel, BorderLayout.CENTER);
        bottomPanel.add(refreshButton, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadResults();

        refreshButton.addActionListener(e -> loadResults());

        logoutButton.addActionListener(e -> {
            authService.logout();
            dispose();
            new LoginFrame(new AuthService());
        });

        setVisible(true);
    }

    private void loadResults() {
        tableModel.setRowCount(0);
        List<Candidate> candidates = votingService.getAllCandidates();
        int totalVotes = votingService.getTotalVotes();

        for (Candidate c : candidates) {
            int votes = votingService.getVoteCount(c.getId());
            double percentage = votingService.getVotePercentage(c.getId());
            tableModel.addRow(new Object[]{
                c.getId(),
                c.getName(),
                c.getParty(),
                votes,
                String.format("%.1f%%", percentage)
            });
        }

        totalVotesLabel.setText("Total Votes Cast: " + totalVotes);
    }
}
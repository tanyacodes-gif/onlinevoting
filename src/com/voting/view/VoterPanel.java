package com.voting.view;

import com.voting.model.Candidate;
import com.voting.model.User;
import com.voting.service.AuthService;
import com.voting.service.VotingService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VoterPanel extends JFrame {

    private AuthService authService;
    private VotingService votingService;
    private JTable candidateTable;
    private DefaultTableModel tableModel;
    private JButton voteButton;
    private JButton logoutButton;
    private JLabel statusLabel;
    private JLabel welcomeLabel;

    public VoterPanel(AuthService authService) {
        this.authService = authService;
        this.votingService = new VotingService();
        initUI();
    }

    private void initUI() {
        User user = authService.getLoggedInUser();

        setTitle("Online Voting System - Voter Panel");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 30, 60));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        welcomeLabel = new JLabel("Welcome, " + user.getUsername() + " 👤");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.WHITE);

        logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        // Center — candidate table
        String[] columns = {"ID", "Candidate Name", "Party"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        candidateTable = new JTable(tableModel);
        candidateTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        candidateTable.setRowHeight(30);
        candidateTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        candidateTable.setFont(new Font("Arial", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(candidateTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Select a Candidate to Vote"));

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(240, 248, 255));

        voteButton = new JButton("🗳 Cast My Vote");
        voteButton.setFont(new Font("Arial", Font.BOLD, 14));
        voteButton.setBackground(new Color(40, 167, 69));
        voteButton.setForeground(Color.WHITE);
        voteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        voteButton.setPreferredSize(new Dimension(200, 40));

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));

        bottomPanel.add(voteButton, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        // Add to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadCandidates();

        if (user.isHasVoted()) {
            voteButton.setEnabled(false);
            statusLabel.setForeground(new Color(220, 53, 69));
            statusLabel.setText("You have already voted!");
        }

        voteButton.addActionListener(e -> handleVote());

        logoutButton.addActionListener(e -> {
            authService.logout();
            dispose();
            new LoginFrame(new AuthService());
        });

        setVisible(true);
    }

    private void loadCandidates() {
        tableModel.setRowCount(0);
        List<Candidate> candidates = votingService.getAllCandidates();
        for (Candidate c : candidates) {
            tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getParty()});
        }
    }

    private void handleVote() {
        int selectedRow = candidateTable.getSelectedRow();
        if (selectedRow == -1) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Please select a candidate first!");
            return;
        }

        int candidateId = (int) tableModel.getValueAt(selectedRow, 0);
        String candidateName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to vote for " + candidateName + "?\nThis action cannot be undone!",
            "Confirm Vote",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        User user = authService.getLoggedInUser();
        String result = votingService.castVote(user.getId(), candidateId);

        switch (result) {
            case "SUCCESS":
                statusLabel.setForeground(new Color(40, 167, 69));
                statusLabel.setText("✅ Vote cast successfully for " + candidateName + "!");
                voteButton.setEnabled(false);
                break;
            case "ALREADY_VOTED":
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("❌ You have already voted!");
                voteButton.setEnabled(false);
                break;
            case "ELECTION_NOT_ACTIVE":
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("❌ Election is not active right now!");
                break;
            default:
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("❌ Something went wrong. Try again!");
                break;
        }
    }
}
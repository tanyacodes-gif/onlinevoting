package com.voting.view;

import com.voting.dao.CandidateDAO;
import com.voting.dao.ElectionDAO;
import com.voting.dao.UserDAO;
import com.voting.model.Candidate;
import com.voting.model.User;
import com.voting.service.AuthService;
import com.voting.service.VotingService;
import com.voting.util.ElectionTimer;
import com.voting.util.SessionManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.util.List;

public class AdminPanel extends JFrame {

    private AuthService authService;
    private VotingService votingService;
    private CandidateDAO candidateDAO;
    private ElectionDAO electionDAO;
    private UserDAO userDAO;

    // Candidate tab
    private JTable candidateTable;
    private DefaultTableModel candidateTableModel;
    private JLabel statusLabel;

    // Election controls
    private JLabel electionStatusLabel;
    private JLabel timerLabel;
    private ElectionTimer electionTimer;
    private JTextField durationField;

    // Voter management tab
    private JTable voterTable;
    private DefaultTableModel voterTableModel;
    private JLabel voterStatusLabel;

    // Session
    private SessionManager sessionManager;

    public AdminPanel(AuthService authService) {
        this.authService = authService;
        this.votingService = new VotingService();
        this.candidateDAO = new CandidateDAO();
        this.electionDAO = new ElectionDAO();
        this.userDAO = new UserDAO();
        initUI();
        startSession();
    }

    private void startSession() {
        sessionManager = new SessionManager(authService, this, () -> {
            dispose();
            new LoginFrame(new AuthService());
        });
        sessionManager.start();
    }

    private void initUI() {
        setTitle("Online Voting System - Admin Panel");
        setSize(950, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Top Bar ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 30, 60));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.YELLOW);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.YELLOW);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        // --- Tabbed Pane ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));
        tabbedPane.addTab("  Election & Candidates  ", buildElectionTab());
        tabbedPane.addTab("  Voter Management  ", buildVoterTab());

        // --- Status bar ---
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        add(mainPanel);
        loadCandidates();
        loadVoters();

        // --- Logout ---
        logoutButton.addActionListener(e -> {
            sessionManager.stop();
            authService.logout();
            electionTimer.reset();
            dispose();
            new LoginFrame(new AuthService());
        });

        setVisible(true);
    }

    // ===================== ELECTION & CANDIDATES TAB =====================

    private JPanel buildElectionTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Election Control panel ---
        JPanel electionPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        electionPanel.setBackground(new Color(240, 248, 255));
        electionPanel.setBorder(BorderFactory.createTitledBorder("Election Control"));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row1.setBackground(new Color(240, 248, 255));

        electionStatusLabel = new JLabel();
        updateElectionStatusLabel();
        electionStatusLabel.setFont(new Font("Arial", Font.BOLD, 13));

        JButton startElectionButton = makeButton("Start Election", new Color(40, 167, 69));
        JButton stopElectionButton  = makeButton("Stop Election",  new Color(255, 193, 7));
        stopElectionButton.setForeground(Color.BLACK);
        JButton resetElectionButton = makeButton("Reset Election", new Color(220, 53, 69));

        row1.add(electionStatusLabel);
        row1.add(startElectionButton);
        row1.add(stopElectionButton);
        row1.add(resetElectionButton);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row2.setBackground(new Color(240, 248, 255));

        JLabel durationLabel = new JLabel("Duration (minutes):");
        durationLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        durationField = new JTextField("60", 5);

        timerLabel = new JLabel("Timer: --:--:--");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(Color.GRAY);

        electionTimer = new ElectionTimer(timerLabel, electionDAO, () -> {
            updateElectionStatusLabel();
            loadCandidates();
            statusLabel.setForeground(new Color(220, 53, 69));
            statusLabel.setText("Election time is up! Election stopped automatically.");
        });

        row2.add(durationLabel);
        row2.add(durationField);
        row2.add(timerLabel);
        electionPanel.add(row1);
        electionPanel.add(row2);

        // --- Candidate Table ---
        String[] cols = {"ID", "Candidate Name", "Party", "Votes", "Percentage"};
        candidateTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        candidateTable = new JTable(candidateTableModel);
        candidateTable.setRowHeight(30);
        candidateTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        candidateTable.setFont(new Font("Arial", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(candidateTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Candidates & Live Results"));

        // --- Candidate Management ---
        JPanel candidatePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        candidatePanel.setBackground(new Color(240, 248, 255));
        candidatePanel.setBorder(BorderFactory.createTitledBorder("Candidate Management"));

        JTextField nameField  = new JTextField(12);
        nameField.setToolTipText("Candidate full name");
        JTextField partyField = new JTextField(12);
        partyField.setToolTipText("Party name");

        JButton addButton     = makeButton("Add Candidate",   new Color(70, 130, 180));
        JButton removeButton  = makeButton("Remove Selected", new Color(220, 53, 69));
        JButton refreshButton = makeButton("Refresh",         new Color(108, 117, 125));
        JButton chartButton   = makeButton("View Chart",      new Color(111, 66, 193));
        JButton regVoterBtn   = makeButton("Register User",   new Color(23, 162, 184));

        JPanel candRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        candRow1.setBackground(new Color(240, 248, 255));
        candRow1.add(new JLabel("Name:")); candRow1.add(nameField);
        candRow1.add(new JLabel("Party:")); candRow1.add(partyField);
        candRow1.add(addButton);

        JPanel candRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        candRow2.setBackground(new Color(240, 248, 255));
        candRow2.add(removeButton);
        candRow2.add(refreshButton);
        candRow2.add(chartButton);
        candRow2.add(regVoterBtn);

        candidatePanel.add(candRow1);
        candidatePanel.add(candRow2);

        panel.add(electionPanel, BorderLayout.NORTH);
        panel.add(scrollPane,    BorderLayout.CENTER);
        panel.add(candidatePanel, BorderLayout.SOUTH);

        // --- Election Button Actions ---
        startElectionButton.addActionListener(e -> {
            String durationText = durationField.getText().trim();
            if (durationText.isEmpty()) {
                showStatus("Please enter election duration in minutes!", Color.RED);
                return;
            }
            int duration;
            try {
                duration = Integer.parseInt(durationText);
                if (duration <= 0) {
                    showStatus("Duration must be greater than 0!", Color.RED);
                    return;
                }
            } catch (NumberFormatException ex) {
                showStatus("Please enter a valid number for duration!", Color.RED);
                return;
            }
            Timestamp now = new Timestamp(System.currentTimeMillis());
            Timestamp end = new Timestamp(System.currentTimeMillis() + (duration * 60 * 1000L));
            if (electionDAO.startElection(now, end)) {
                showStatus("Election started for " + duration + " minutes!", new Color(40, 167, 69));
                updateElectionStatusLabel();
                electionTimer.start(duration);
            } else {
                showStatus("Failed to start election. Check database connection.", Color.RED);
            }
        });

        stopElectionButton.addActionListener(e -> {
            if (electionDAO.stopElection()) {
                showStatus("Election stopped!", new Color(255, 193, 7));
                updateElectionStatusLabel();
                electionTimer.stop();
            } else {
                showStatus("Failed to stop election.", Color.RED);
            }
        });

        resetElectionButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reset the election?\nAll votes will be permanently deleted!",
                "Confirm Reset", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (electionDAO.resetElection()) {
                    showStatus("Election reset successfully!", new Color(220, 53, 69));
                    updateElectionStatusLabel();
                    electionTimer.reset();
                    loadCandidates();
                    loadVoters();
                } else {
                    showStatus("Failed to reset election.", Color.RED);
                }
            }
        });

        addButton.addActionListener(e -> {
            String name  = nameField.getText().trim();
            String party = partyField.getText().trim();
            if (name.isEmpty() || party.isEmpty()) {
                showStatus("Please enter both candidate name and party!", Color.RED);
                return;
            }
            if (name.length() < 2) {
                showStatus("Candidate name must be at least 2 characters!", Color.RED);
                return;
            }
            if (candidateDAO.addCandidate(name, party)) {
                showStatus("Candidate \"" + name + "\" added successfully!", new Color(40, 167, 69));
                nameField.setText("");
                partyField.setText("");
                loadCandidates();
            } else {
                showStatus("Failed to add candidate. Please try again.", Color.RED);
            }
        });

        removeButton.addActionListener(e -> {
            int selectedRow = candidateTable.getSelectedRow();
            if (selectedRow == -1) {
                showStatus("Please select a candidate to remove!", Color.RED);
                return;
            }
            String candidateName = (String) candidateTableModel.getValueAt(selectedRow, 1);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Remove candidate \"" + candidateName + "\"?",
                "Confirm Remove", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int candidateId = (int) candidateTableModel.getValueAt(selectedRow, 0);
                if (candidateDAO.removeCandidate(candidateId)) {
                    showStatus("Candidate removed successfully!", new Color(40, 167, 69));
                    loadCandidates();
                } else {
                    showStatus("Failed to remove candidate.", Color.RED);
                }
            }
        });

        refreshButton.addActionListener(e -> {
            loadCandidates();
            showStatus("Refreshed.", new Color(108, 117, 125));
        });

        chartButton.addActionListener(e -> new ResultsChart(authService));
        regVoterBtn.addActionListener(e -> new RegisterFrame(authService));

        return panel;
    }

    // ===================== VOTER MANAGEMENT TAB =====================

    private JPanel buildVoterTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Voter table
        String[] cols = {"ID", "Username", "Role", "Has Voted"};
        voterTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        voterTable = new JTable(voterTableModel);
        voterTable.setRowHeight(30);
        voterTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        voterTable.setFont(new Font("Arial", Font.PLAIN, 13));
        voterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(voterTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Registered Voters & Candidates"));

        // Controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        controlPanel.setBackground(new Color(240, 248, 255));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        JButton refreshVotersBtn = makeButton("Refresh List",  new Color(70, 130, 180));
        JButton addVoterBtn      = makeButton("Register User", new Color(40, 167, 69));
        JButton deleteVoterBtn   = makeButton("Delete Selected", new Color(220, 53, 69));

        controlPanel.add(refreshVotersBtn);
        controlPanel.add(addVoterBtn);
        controlPanel.add(deleteVoterBtn);

        voterStatusLabel = new JLabel("", SwingConstants.LEFT);
        voterStatusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(voterStatusLabel);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        // Actions
        refreshVotersBtn.addActionListener(e -> {
            loadVoters();
            voterStatusLabel.setForeground(new Color(108, 117, 125));
            voterStatusLabel.setText("List refreshed.");
        });

        addVoterBtn.addActionListener(e -> new RegisterFrame(authService));

        deleteVoterBtn.addActionListener(e -> {
            int selectedRow = voterTable.getSelectedRow();
            if (selectedRow == -1) {
                voterStatusLabel.setForeground(Color.RED);
                voterStatusLabel.setText("Please select a user to delete!");
                return;
            }
            int userId       = (int)    voterTableModel.getValueAt(selectedRow, 0);
            String username  = (String) voterTableModel.getValueAt(selectedRow, 1);
            boolean hasVoted = "Yes".equals(voterTableModel.getValueAt(selectedRow, 3));

            String warning = hasVoted
                ? "User \"" + username + "\" has already voted.\nDeleting them will NOT remove their vote.\nContinue?"
                : "Delete user \"" + username + "\"? This cannot be undone.";

            int confirm = JOptionPane.showConfirmDialog(this, warning,
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (userDAO.deleteUser(userId)) {
                    voterStatusLabel.setForeground(new Color(40, 167, 69));
                    voterStatusLabel.setText("User \"" + username + "\" deleted.");
                    loadVoters();
                } else {
                    voterStatusLabel.setForeground(Color.RED);
                    voterStatusLabel.setText("Failed to delete user.");
                }
            }
        });

        return panel;
    }

    // ===================== HELPERS =====================

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.YELLOW);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        return btn;
    }

    private void showStatus(String msg, Color color) {
        statusLabel.setForeground(color);
        statusLabel.setText(msg);
    }

    private void loadCandidates() {
        candidateTableModel.setRowCount(0);
        List<Candidate> candidates = votingService.getAllCandidates();
        for (Candidate c : candidates) {
            int votes = votingService.getVoteCount(c.getId());
            double pct = votingService.getVotePercentage(c.getId());
            candidateTableModel.addRow(new Object[]{
                c.getId(), c.getName(), c.getParty(),
                votes, String.format("%.1f%%", pct)
            });
        }
    }

    private void loadVoters() {
        voterTableModel.setRowCount(0);
        List<User> users = userDAO.getAllVoters();
        for (User u : users) {
            voterTableModel.addRow(new Object[]{
                u.getId(),
                u.getUsername(),
                u.getRole(),
                u.isHasVoted() ? "Yes" : "No"
            });
        }
    }

    private void updateElectionStatusLabel() {
        if (electionDAO.isElectionActive()) {
            electionStatusLabel.setForeground(new Color(40, 167, 69));
            electionStatusLabel.setText("Status: ACTIVE");
        } else {
            electionStatusLabel.setForeground(new Color(220, 53, 69));
            electionStatusLabel.setText("Status: INACTIVE");
        }
    }
}
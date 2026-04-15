package com.voting.view;

import com.voting.dao.CandidateDAO;
import com.voting.dao.ElectionDAO;
import com.voting.model.Candidate;
import com.voting.service.AuthService;
import com.voting.service.VotingService;
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
    private JTable candidateTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JLabel electionStatusLabel;

    public AdminPanel(AuthService authService) {
        this.authService = authService;
        this.votingService = new VotingService();
        this.candidateDAO = new CandidateDAO();
        this.electionDAO = new ElectionDAO();
        initUI();
    }
    private void initUI() {
        setTitle("Online Voting System - Admin Panel");
        setSize(850, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 30, 60));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Admin Dashboard 🛡");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.YELLOW);

        JButton logoutButton = new JButton("Logout");
         logoutButton.setOpaque(true);
         logoutButton.setBorderPainted(false);
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.YELLOW);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        JPanel electionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        electionPanel.setBackground(new Color(240, 248, 255));
        electionPanel.setBorder(BorderFactory.createTitledBorder("Election Control"));

        electionStatusLabel = new JLabel();
        updateElectionStatusLabel();
        electionStatusLabel.setFont(new Font("Arial", Font.BOLD, 13));

        JButton startElectionButton = new JButton("▶ Start Election");
        startElectionButton.setOpaque(true);
        startElectionButton.setBorderPainted(false);
        startElectionButton.setBackground(new Color(40, 167, 69));
        startElectionButton.setForeground(Color.WHITE);
        startElectionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton stopElectionButton = new JButton("⏹ Stop Election");
        stopElectionButton.setOpaque(true);
        stopElectionButton.setBorderPainted(false);
        stopElectionButton.setBackground(new Color(255, 193, 7));
        stopElectionButton.setForeground(Color.BLACK);
        stopElectionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton resetElectionButton = new JButton("🔄 Reset Election");
        resetElectionButton.setOpaque(true);
        resetElectionButton.setBorderPainted(false);
        resetElectionButton.setBackground(new Color(220, 53, 69));
        resetElectionButton.setForeground(Color.YELLOW);
        resetElectionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        electionPanel.add(electionStatusLabel);
        electionPanel.add(startElectionButton);
        electionPanel.add(stopElectionButton);
        electionPanel.add(resetElectionButton);

        // Candidate table
        String[] columns = {"ID", "Candidate Name", "Party", "Votes", "Percentage"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        candidateTable = new JTable(tableModel);
        candidateTable.setRowHeight(30);
        candidateTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        candidateTable.setFont(new Font("Arial", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(candidateTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Candidates & Results"));

        // Candidate control panel
        JPanel candidatePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        candidatePanel.setBackground(new Color(240, 248, 255));
        candidatePanel.setBorder(BorderFactory.createTitledBorder("Candidate Management"));

        JTextField nameField = new JTextField(12);
        nameField.setToolTipText("Candidate Name");
        JTextField partyField = new JTextField(12);
        partyField.setToolTipText("Party Name");

        JButton addButton = new JButton("➕ Add Candidate");
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);
        addButton.setBackground(new Color(70, 130, 180));
        addButton.setForeground(Color.YELLOW);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton removeButton = new JButton("➖ Remove Selected");
        removeButton.setOpaque(true);
        removeButton.setBorderPainted(false);
        removeButton.setBackground(new Color(220, 53, 69));
        removeButton.setForeground(Color.YELLOW);
        removeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton refreshButton = new JButton("🔃 Refresh");
        refreshButton.setOpaque(true);
        refreshButton.setBorderPainted(false);
        refreshButton.setBackground(new Color(108, 117, 125));
        refreshButton.setForeground(Color.YELLOW);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton chartButton = new JButton("📊 View Chart");
        chartButton.setOpaque(true);
        chartButton.setBorderPainted(false);
        chartButton.setBackground(new Color(111, 66, 193));
        chartButton.setForeground(Color.YELLOW);
        chartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        row1.setBackground(new Color(240, 248, 255));
        row1.add(new JLabel("Name:"));
        row1.add(nameField);
        row1.add(new JLabel("Party:"));
        row1.add(partyField);
        row1.add(addButton);
        
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        row2.setBackground(new Color(240, 248, 255));
        row2.add(removeButton);
        row2.add(refreshButton);
        row2.add(chartButton);
        
        candidatePanel.add(row1);
        candidatePanel.add(row2);

        // Status label
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(240, 248, 255));
        bottomPanel.add(candidatePanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        // Center panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(electionPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Load candidates
        loadCandidates();

        // Button actions
        startElectionButton.addActionListener(e -> {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            Timestamp end = new Timestamp(System.currentTimeMillis() + 3600000); // 1 hour
            if (electionDAO.startElection(now, end)) {
                statusLabel.setForeground(new Color(40, 167, 69));
                statusLabel.setText("✅ Election started successfully!");
                updateElectionStatusLabel();
            }
        });

        stopElectionButton.addActionListener(e -> {
            if (electionDAO.stopElection()) {
                statusLabel.setForeground(new Color(255, 193, 7));
                statusLabel.setText("⏹ Election stopped!");
                updateElectionStatusLabel();
            }
        });

        resetElectionButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reset the election?\nAll votes will be deleted!",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                if (electionDAO.resetElection()) {
                    statusLabel.setForeground(new Color(220, 53, 69));
                    statusLabel.setText("🔄 Election reset successfully!");
                    updateElectionStatusLabel();
                    loadCandidates();
                }
            }
        });

        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String party = partyField.getText().trim();
            if (name.isEmpty() || party.isEmpty()) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Please enter candidate name and party!");
                return;
            }
            if (candidateDAO.addCandidate(name, party)) {
                statusLabel.setForeground(new Color(40, 167, 69));
                statusLabel.setText("✅ Candidate added successfully!");
                nameField.setText("");
                partyField.setText("");
                loadCandidates();
            }
        });

        removeButton.addActionListener(e -> {
            int selectedRow = candidateTable.getSelectedRow();
            if (selectedRow == -1) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Please select a candidate to remove!");
                return;
            }
            int candidateId = (int) tableModel.getValueAt(selectedRow, 0);
            if (candidateDAO.removeCandidate(candidateId)) {
                statusLabel.setForeground(new Color(40, 167, 69));
                statusLabel.setText("✅ Candidate removed successfully!");
                loadCandidates();
            }
        });

        refreshButton.addActionListener(e -> loadCandidates());
        chartButton.addActionListener(e -> new ResultsChart(authService));

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
    }

    private void updateElectionStatusLabel() {
        if (electionDAO.isElectionActive()) {
            electionStatusLabel.setForeground(new Color(40, 167, 69));
            electionStatusLabel.setText("Status: 🟢 ACTIVE");
        } else {
            electionStatusLabel.setForeground(new Color(220, 53, 69));
            electionStatusLabel.setText("Status: 🔴 INACTIVE");
        }
    }
}

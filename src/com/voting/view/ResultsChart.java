package com.voting.view;

import com.voting.model.Candidate;
import com.voting.service.AuthService;
import com.voting.service.VotingService;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ResultsChart extends JFrame {

    private VotingService votingService;

    public ResultsChart(AuthService authService) {
        this.votingService = new VotingService();
        initUI();
    }

    private void initUI() {
        setTitle("Online Voting System - Results Chart");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 30, 60));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Live Election Results");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.YELLOW);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(70, 130, 180));
        refreshButton.setForeground(Color.YELLOW);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setOpaque(true);
        refreshButton.setBorderPainted(false);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(refreshButton, BorderLayout.EAST);

        // Chart panel
        ChartPanel chartPanel = new ChartPanel(votingService);
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(new Color(240, 248, 255));

        JLabel totalLabel = new JLabel("Total Votes Cast: " + votingService.getTotalVotes());
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalLabel.setForeground(new Color(30, 30, 60));
        bottomPanel.add(totalLabel);

        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(220, 53, 69));
        closeButton.setForeground(Color.YELLOW);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setOpaque(true);
        closeButton.setBorderPainted(false);
        bottomPanel.add(closeButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(chartPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Button actions
        refreshButton.addActionListener(e -> {
            chartPanel.refresh();
            totalLabel.setText("Total Votes Cast: " + votingService.getTotalVotes());
            repaint();
        });

        closeButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    // Inner class that draws the bar chart
    class ChartPanel extends JPanel {

        private VotingService votingService;
        private List<Candidate> candidates;
        private int totalVotes;

        private Color[] barColors = {
            new Color(70, 130, 180),
            new Color(40, 167, 69),
            new Color(220, 53, 69),
            new Color(255, 193, 7),
            new Color(111, 66, 193),
            new Color(23, 162, 184)
        };

        public ChartPanel(VotingService votingService) {
            this.votingService = votingService;
            refresh();
        }

        public void refresh() {
            candidates = votingService.getAllCandidates();
            totalVotes = votingService.getTotalVotes();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 60;
            int chartHeight = height - padding * 2;
            int chartWidth = width - padding * 2;

            if (candidates == null || candidates.isEmpty()) {
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.setColor(Color.GRAY);
                g2d.drawString("No candidates available",
                              width / 2 - 100, height / 2);
                return;
            }

            int barCount = candidates.size();
            int barWidth = Math.min(80, (chartWidth / barCount) - 20);
            int gap = (chartWidth - (barWidth * barCount)) / (barCount + 1);

            // Draw axes
            g2d.setColor(new Color(200, 200, 200));
            g2d.drawLine(padding, padding, padding, height - padding);
            g2d.drawLine(padding, height - padding,
                        width - padding, height - padding);

            // Draw Y axis labels and grid
            g2d.setFont(new Font("Arial", Font.PLAIN, 11));
            for (int i = 0; i <= 10; i++) {
                int y = height - padding - (i * chartHeight / 10);
                g2d.setColor(Color.GRAY);
                g2d.drawLine(padding - 5, y, padding, y);
                g2d.drawString(i * 10 + "%", padding - 40, y + 5);
                g2d.setColor(new Color(230, 230, 230));
                g2d.drawLine(padding, y, width - padding, y);
            }

            // Draw bars
            for (int i = 0; i < barCount; i++) {
                Candidate c = candidates.get(i);
                int votes = votingService.getVoteCount(c.getId());
                double percentage = totalVotes == 0 ? 0 :
                                   ((double) votes / totalVotes) * 100;

                int barHeight = (int) ((percentage / 100) * chartHeight);
                int x = padding + gap + i * (barWidth + gap);
                int y = height - padding - barHeight;

                // Draw bar
                Color barColor = barColors[i % barColors.length];
                g2d.setColor(barColor);
                g2d.fillRoundRect(x, y, barWidth, barHeight, 10, 10);

                // Draw border
                g2d.setColor(barColor.darker());
                g2d.drawRoundRect(x, y, barWidth, barHeight, 10, 10);

                // Percentage on top
                g2d.setColor(new Color(30, 30, 60));
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                String pctText = String.format("%.1f%%", percentage);
                int textX = x + (barWidth -
                           g2d.getFontMetrics().stringWidth(pctText)) / 2;
                g2d.drawString(pctText, textX, y - 5);

                // Vote count inside bar
                if (barHeight > 25) {
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, 11));
                    String voteText = votes + " votes";
                    int voteTextX = x + (barWidth -
                                   g2d.getFontMetrics().stringWidth(voteText)) / 2;
                    g2d.drawString(voteText, voteTextX, y + 20);
                }

                // Candidate name below bar
                g2d.setColor(new Color(30, 30, 60));
                g2d.setFont(new Font("Arial", Font.PLAIN, 11));
                String name = c.getName().length() > 10 ?
                             c.getName().substring(0, 10) + "..." : c.getName();
                int nameX = x + (barWidth -
                           g2d.getFontMetrics().stringWidth(name)) / 2;
                g2d.drawString(name, nameX, height - padding + 20);

                // Party below name
                g2d.setColor(Color.GRAY);
                g2d.setFont(new Font("Arial", Font.ITALIC, 10));
                String party = c.getParty().length() > 10 ?
                              c.getParty().substring(0, 10) + "..." : c.getParty();
                int partyX = x + (barWidth -
                            g2d.getFontMetrics().stringWidth(party)) / 2;
                g2d.drawString(party, partyX, height - padding + 35);
            }

            // Chart title
            g2d.setColor(new Color(30, 30, 60));
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            String chartTitle = "Vote Distribution by Candidate";
            g2d.drawString(chartTitle,
                          (width - g2d.getFontMetrics()
                          .stringWidth(chartTitle)) / 2, padding - 10);
        }
    }
}

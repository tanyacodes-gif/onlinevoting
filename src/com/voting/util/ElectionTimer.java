package com.voting.util;

import com.voting.dao.ElectionDAO;
import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;

public class ElectionTimer {

    private Timer swingTimer;
    private long endTimeMillis;
    private JLabel timerLabel;
    private ElectionDAO electionDAO;
    private Runnable onElectionEnd;

    public ElectionTimer(JLabel timerLabel, ElectionDAO electionDAO, 
                        Runnable onElectionEnd) {
        this.timerLabel = timerLabel;
        this.electionDAO = electionDAO;
        this.onElectionEnd = onElectionEnd;
    }

    // Start timer with duration in minutes
    public void start(int durationMinutes) {
        endTimeMillis = System.currentTimeMillis() + 
                       (durationMinutes * 60 * 1000L);

        // Update timer every second
        swingTimer = new Timer(1000, e -> updateTimer());
        swingTimer.start();

        updateTimer();
    }

    // Stop timer manually
    public void stop() {
        if (swingTimer != null && swingTimer.isRunning()) {
            swingTimer.stop();
            timerLabel.setText("Timer: Stopped");
            timerLabel.setForeground(new Color(220, 53, 69));
        }
    }

    // Reset timer display
    public void reset() {
        if (swingTimer != null && swingTimer.isRunning()) {
            swingTimer.stop();
        }
        timerLabel.setText("Timer: --:--:--");
        timerLabel.setForeground(Color.GRAY);
    }

    // Called every second to update display
    private void updateTimer() {
        long remaining = endTimeMillis - System.currentTimeMillis();

        if (remaining <= 0) {
            // Time is up — stop election automatically
            swingTimer.stop();
            timerLabel.setText("Timer: 00:00:00");
            timerLabel.setForeground(new Color(220, 53, 69));

            // Stop election in database
            electionDAO.stopElection();

            // Notify AdminPanel to update UI
            if (onElectionEnd != null) {
                onElectionEnd.run();
            }

            JOptionPane.showMessageDialog(null,
                "Election time is up! Election has been stopped automatically.",
                "Election Ended",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Calculate hours, minutes, seconds
        long hours = remaining / 3600000;
        long minutes = (remaining % 3600000) / 60000;
        long seconds = (remaining % 60000) / 1000;

        // Format as HH:MM:SS
        String timeText = String.format("Timer: %02d:%02d:%02d", 
                                       hours, minutes, seconds);
        timerLabel.setText(timeText);

        // Change color to red when less than 1 minute remaining
        if (remaining < 60000) {
            timerLabel.setForeground(new Color(220, 53, 69));
        } else {
            timerLabel.setForeground(new Color(40, 167, 69));
        }
    }

    public boolean isRunning() {
        return swingTimer != null && swingTimer.isRunning();
    }
}
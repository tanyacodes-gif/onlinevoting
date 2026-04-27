package com.voting.util;

import com.voting.service.AuthService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SessionManager {

    private static final int TIMEOUT_MINUTES = 10;
    private static final int TIMEOUT_MS = TIMEOUT_MINUTES * 60 * 1000;
    private static final int WARNING_MS = 60 * 1000; // warn 1 min before

    private Timer timeoutTimer;
    private Timer warningTimer;
    private AuthService authService;
    private JFrame parentFrame;
    private Runnable onTimeout;
    private boolean warningShown = false;

    public SessionManager(AuthService authService, JFrame parentFrame, Runnable onTimeout) {
        this.authService = authService;
        this.parentFrame = parentFrame;
        this.onTimeout = onTimeout;
    }

    public void start() {
        // Warning timer fires 1 minute before timeout
        warningTimer = new Timer(TIMEOUT_MS - WARNING_MS, e -> {
            warningShown = true;
            showWarning();
        });
        warningTimer.setRepeats(false);
        warningTimer.start();

        // Main timeout timer
        timeoutTimer = new Timer(TIMEOUT_MS, e -> performLogout());
        timeoutTimer.setRepeats(false);
        timeoutTimer.start();

        // Register activity listeners on the parent frame
        registerActivityListeners(parentFrame);
    }

    public void stop() {
        if (timeoutTimer != null) timeoutTimer.stop();
        if (warningTimer != null) warningTimer.stop();
    }

    // Call this whenever user does something — resets the countdown
    public void resetTimer() {
        warningShown = false;
        if (timeoutTimer != null) {
            timeoutTimer.restart();
        }
        if (warningTimer != null) {
            warningTimer.restart();
        }
    }

    private void registerActivityListeners(JFrame frame) {
        AWTEventListener activityListener = event -> resetTimer();

        Toolkit.getDefaultToolkit().addAWTEventListener(
            activityListener,
            AWTEvent.MOUSE_EVENT_MASK |
            AWTEvent.KEY_EVENT_MASK |
            AWTEvent.MOUSE_MOTION_EVENT_MASK
        );
    }

    private void showWarning() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                parentFrame,
                "⚠️  Your session will expire in 1 minute due to inactivity.\n" +
                "Click anywhere or press a key to stay logged in.",
                "Session Expiring Soon",
                JOptionPane.WARNING_MESSAGE
            );
        });
    }

    private void performLogout() {
        SwingUtilities.invokeLater(() -> {
            stop();
            JOptionPane.showMessageDialog(
                null,
                "🔒  You have been logged out due to inactivity (" + TIMEOUT_MINUTES + " minutes).",
                "Session Expired",
                JOptionPane.INFORMATION_MESSAGE
            );
            authService.logout();
            if (onTimeout != null) onTimeout.run();
        });
    }
}
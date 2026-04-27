package com.voting.view;

import com.voting.dao.UserDAO;
import com.voting.service.AuthService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class RegisterFrame extends JFrame {

    private AuthService authService;
    private UserDAO userDAO;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private JLabel statusLabel;

    public RegisterFrame(AuthService authService) {
        this.authService = authService;
        this.userDAO = new UserDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Online Voting System - Register User");
        setSize(460, 440);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(30, 30, 60));
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Register New User");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        mainPanel.add(usernameField, gbc);

        // Username hint
        JLabel usernameHint = new JLabel("Min. 3 characters, letters/numbers only");
        usernameHint.setForeground(new Color(180, 180, 180));
        usernameHint.setFont(new Font("Arial", Font.ITALIC, 10));
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.insets = new Insets(0, 10, 6, 10);
        mainPanel.add(usernameHint, gbc);
        gbc.insets = new Insets(8, 10, 8, 10);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 3;
        mainPanel.add(passwordField, gbc);

        // Password hint
        JLabel passwordHint = new JLabel("Min. 6 characters");
        passwordHint.setForeground(new Color(180, 180, 180));
        passwordHint.setFont(new Font("Arial", Font.ITALIC, 10));
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.insets = new Insets(0, 10, 6, 10);
        mainPanel.add(passwordHint, gbc);
        gbc.insets = new Insets(8, 10, 8, 10);

        // Confirm Password
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(confirmLabel, gbc);

        confirmPasswordField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 5;
        mainPanel.add(confirmPasswordField, gbc);

        // Role
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 6;
        mainPanel.add(roleLabel, gbc);

        roleComboBox = new JComboBox<>(new String[]{"VOTER", "CANDIDATE"});
        roleComboBox.setBackground(Color.WHITE);
        gbc.gridx = 1; gbc.gridy = 6;
        mainPanel.add(roleComboBox, gbc);

        // Buttons row
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setBackground(new Color(30, 30, 60));

        JButton registerButton = new JButton("Register");
        styleButton(registerButton, new Color(40, 167, 69));

        JButton clearButton = new JButton("Clear");
        styleButton(clearButton, new Color(108, 117, 125));

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(220, 53, 69));

        btnPanel.add(registerButton);
        btnPanel.add(clearButton);
        btnPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 10, 8, 10);
        mainPanel.add(btnPanel, gbc);

        // Status label
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 8;
        gbc.insets = new Insets(2, 10, 8, 10);
        mainPanel.add(statusLabel, gbc);

        add(mainPanel);

        // Actions
        registerButton.addActionListener(e -> handleRegister());
        clearButton.addActionListener(e -> clearFields());
        cancelButton.addActionListener(e -> dispose());

        // Allow pressing Enter to submit
        getRootPane().setDefaultButton(registerButton);

        setVisible(true);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.YELLOW);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(100, 32));
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        String role = (String) roleComboBox.getSelectedItem();

        // --- Input Validation ---
        if (username.isEmpty()) {
            showError("Username cannot be empty.");
            usernameField.requestFocus();
            return;
        }
        if (username.length() < 3) {
            showError("Username must be at least 3 characters.");
            usernameField.requestFocus();
            return;
        }
        if (!username.matches("[a-zA-Z0-9_]+")) {
            showError("Username may only contain letters, numbers, and underscores.");
            usernameField.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            showError("Password cannot be empty.");
            passwordField.requestFocus();
            return;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            passwordField.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocus();
            return;
        }

        // --- Error Handling: duplicate username check before DB insert ---
        try {
            if (userDAO.usernameExists(username)) {
                showError("Username \"" + username + "\" already exists. Try another.");
                usernameField.requestFocus();
                return;
            }

            boolean success = userDAO.registerUser(username, password, role);
            if (success) {
                statusLabel.setForeground(new Color(40, 167, 69));
                statusLabel.setText("User \"" + username + "\" registered successfully as " + role + "!");
                clearFields();
            } else {
                showError("Registration failed. Please try again.");
            }
        } catch (Exception ex) {
            showError("An error occurred: " + ex.getMessage());
            System.out.println("RegisterFrame error: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        statusLabel.setForeground(new Color(220, 53, 69));
        statusLabel.setText(message);
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        roleComboBox.setSelectedIndex(0);
        statusLabel.setText("");
        usernameField.requestFocus();
    }
}
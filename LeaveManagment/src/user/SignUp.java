package user;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import dao.DBConnection;

public class SignUp extends JFrame {
    public SignUp() {
        setTitle("Sign Up - Leave Manager");
        setSize(500, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel gradientPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(158, 127, 255), 0, getHeight(), new Color(206, 147, 216));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradientPanel.setLayout(null);

        JButton goBack = new JButton("← Go Back");
        goBack.setBounds(20, 20, 100, 30);
        goBack.setBackground(new Color(180, 150, 255));
        goBack.setForeground(Color.WHITE);
        goBack.setFocusPainted(false);
        goBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goBack.addActionListener(e -> {
            dispose();
            new Login().setVisible(true);
        });
        gradientPanel.add(goBack);

        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(Color.WHITE);
        card.setBounds(80, 80, 330, 550);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(150, 100, 255));
        title.setBounds(80, 20, 200, 30);
        card.add(title);

        int y = 70, gap = 60;

        // Full Name
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setBounds(30, y, 200, 20);
        card.add(nameLabel);
        JTextField nameField = new JTextField();
        nameField.setBounds(30, y + 20, 270, 30);
        nameField.setToolTipText("Enter your full name");
        card.add(nameField);

        y += gap;
        JLabel usernameLabel = new JLabel("User Name");
        usernameLabel.setBounds(30, y, 200, 20);
        card.add(usernameLabel);
        JTextField userField = new JTextField();
        userField.setBounds(30, y + 20, 270, 30);
        userField.setToolTipText("Choose a unique username");
        card.add(userField);

        y += gap;
        JLabel mobileLabel = new JLabel("Phone No");
        mobileLabel.setBounds(30, y, 200, 20);
        card.add(mobileLabel);
        JTextField mobileField = new JTextField();
        mobileField.setBounds(30, y + 20, 270, 30);
        mobileField.setToolTipText("Enter a 10-digit phone number");
        card.add(mobileField);

        y += gap;
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setBounds(30, y, 200, 20);
        card.add(emailLabel);
        JTextField emailField = new JTextField();
        emailField.setBounds(30, y + 20, 270, 30);
        emailField.setToolTipText("Enter a valid email address");
        card.add(emailField);

        y += gap;
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(30, y, 200, 20);
        card.add(passwordLabel);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(30, y + 20, 270, 30);
        passwordField.setToolTipText("Enter a strong password");
        card.add(passwordField);
        
        y += gap;
        JLabel confirmLabel = new JLabel("Confirm Password");
        confirmLabel.setBounds(30, y, 200, 20);
        card.add(confirmLabel);
        JPasswordField confirmPassword = new JPasswordField();
        confirmPassword.setBounds(30, y + 20, 270, 30);
        confirmPassword.setToolTipText("Re-enter your password");
        card.add(confirmPassword);

        JButton register = new JButton("Register");
        register.setBackground(new Color(130, 70, 255));
        register.setForeground(Color.WHITE);
        register.setFocusPainted(false);
        register.setCursor(new Cursor(Cursor.HAND_CURSOR));
        register.setBounds(30, y + 70, 270, 40);
        card.add(register);

        register.addActionListener(e -> {
            String name = nameField.getText().trim();
            String user = userField.getText().trim();
            String mobile = mobileField.getText().trim();
            String email = emailField.getText().trim();
            String pass = new String(passwordField.getPassword());
            String confirmPass = new String(confirmPassword.getPassword());

            if (name.isEmpty() || user.isEmpty() || mobile.isEmpty() || email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields");
            } else if (!mobile.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid 10-digit mobile number");
            } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address");
            } else if (!pass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match");
            } else {
                try (Connection conn = new DBConnection().getConnection()) {
                    // Check username
                    try (PreparedStatement checkUser = conn.prepareStatement("SELECT * FROM users WHERE Username = ?")) {
                        checkUser.setString(1, user);
                        try (ResultSet rsUser = checkUser.executeQuery()) {
                            if (rsUser.next()) {
                                JOptionPane.showMessageDialog(this, "Username already taken. Please choose another one.");
                                return;
                            }
                        }
                    }

                    // Check email
                    try (PreparedStatement checkEmail = conn.prepareStatement("SELECT * FROM users WHERE Email = ?")) {
                        checkEmail.setString(1, email);
                        try (ResultSet rsEmail = checkEmail.executeQuery()) {
                            if (rsEmail.next()) {
                                JOptionPane.showMessageDialog(this, "Email already registered.");
                                return;
                            }
                        }
                    }

                    // Insert new user in database 
                    try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users (Name, Username, Mobile, Email, Password) VALUES (?, ?, ?, ?, ?)"
                    )) {
                        ps.setString(1, name);
                        ps.setString(2, user);
                        ps.setString(3, mobile);
                        ps.setString(4, email);
                        ps.setString(5, pass);
                        ps.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.");
                    dispose();
                    new Login().setVisible(true);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Something went wrong.");
                }
            }
        });

        gradientPanel.add(card);
        add(gradientPanel);
        setVisible(true);
    }
}
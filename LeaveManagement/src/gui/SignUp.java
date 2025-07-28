package gui;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import dao.DBConnection;

public class SignUp extends JFrame {
    public SignUp() {
        setTitle("Sign Up - Leave Manager");
        setSize(500, 600);
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
        goBack.addActionListener(e -> {
            dispose();
            new UserLogin();
        });
        gradientPanel.add(goBack);

        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(Color.WHITE);
        card.setBounds(80, 100, 330, 430);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(150, 100, 255));
        title.setBounds(80, 20, 200, 30);
        card.add(title);

        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setBounds(30, 70, 200, 20);
        card.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(30, 90, 270, 30);
        card.add(nameField);

        JLabel mobileLabel = new JLabel("Phone No");
        mobileLabel.setBounds(30, 130, 200, 20);
        card.add(mobileLabel);

        JTextField mobileField = new JTextField();
        mobileField.setBounds(30, 150, 270, 30);
        card.add(mobileField);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setBounds(30, 190, 200, 20);
        card.add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setBounds(30, 210, 270, 30);
        card.add(emailField);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(30, 250, 200, 20);
        card.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(30, 270, 270, 30);
        card.add(passwordField);

        JLabel confirmLabel = new JLabel("Confirm Password");
        confirmLabel.setBounds(30, 310, 200, 20);
        card.add(confirmLabel);

        JPasswordField confirmPassword = new JPasswordField();
        confirmPassword.setBounds(30, 330, 270, 30);
        card.add(confirmPassword);

        JButton register = new JButton("Register");
        register.setBackground(new Color(130, 70, 255));
        register.setForeground(Color.WHITE);
        register.setFocusPainted(false);
        register.setBounds(30, 380, 270, 40);
        card.add(register);

        register.addActionListener(e -> {
            String name = nameField.getText();
            String mobile = mobileField.getText();
            String email = emailField.getText();
            String pass = new String(passwordField.getPassword());
            String confirmPass = new String(confirmPassword.getPassword());

            if (name.isEmpty() || mobile.isEmpty() || email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields");
            } else if (!pass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match");
            } else {
                try {
                    DBConnection db = new DBConnection();
                    Connection conn = db.getConnection();
                    PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users (Name, Mobile, Email, Password) VALUES (?, ?, ?, ?)"
                    );
                    ps.setString(1, name);
                    ps.setString(2, mobile);
                    ps.setString(3, email);
                    ps.setString(4, pass);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.");
                    conn.close();
                    dispose();
                    new UserLogin();
                } catch (SQLIntegrityConstraintViolationException ex) {
                    JOptionPane.showMessageDialog(this, "Email already registered.");
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

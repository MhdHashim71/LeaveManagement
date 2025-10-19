package admin;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import dao.DBConnection;
import main.Main;

public class Login extends JFrame {
    public Login() {
        setTitle("Admin Login");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Gradient Panel
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
            new Main();
            Main.main(null);
        });
        gradientPanel.add(goBack);

        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        card.setBounds(75, 100, 350, 300);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel subtitle = new JLabel("ADMIN LOGIN");
        subtitle.setFont(new Font("Times New Roman", Font.BOLD, 16));
        subtitle.setBounds(100, 20, 250, 20);
        card.add(subtitle);

        JLabel username = new JLabel("User Name");
        username.setBounds(40, 60, 200, 20);
        card.add(username);

        JTextField nameField = new JTextField();
        nameField.setBounds(40, 80, 270, 30);
        card.add(nameField);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(40, 120, 200, 20);
        card.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(40, 140, 270, 30);
        card.add(passwordField);

        JButton forgotPass = new JButton("Forgot password?");
        forgotPass.setBounds(180, 175, 130, 20);
        forgotPass.setFont(new Font("SansSerif", Font.PLAIN, 11));
        forgotPass.setForeground(new Color(150, 100, 255));
        forgotPass.setContentAreaFilled(false);
        forgotPass.setBorderPainted(false);
        forgotPass.setFocusPainted(false);
        forgotPass.addActionListener(e -> JOptionPane.showMessageDialog(this, "Redirect to Forgot Password page"));
        card.add(forgotPass);

        JButton login = new JButton("Login");
        login.setBackground(new Color(130, 70, 255));
        login.setForeground(Color.WHITE);
        login.setFocusPainted(false);
        login.setBounds(40, 210, 270, 40);
        card.add(login);

        login.addActionListener(e -> {
            String email = nameField.getText();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both email and password");
            } else {
                if (validateLogin(email, password)) {
                    JOptionPane.showMessageDialog(null, "Login Successful!");
                    dispose();
                    new Leave();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid email or password");
                }
            }
        });

        gradientPanel.add(card);
        add(gradientPanel);
        setVisible(true);
    }

    private boolean validateLogin(String username, String password) {
        boolean isValid = false;
        try {
            DBConnection db = new DBConnection();
            Connection conn = db.getConnection();

            String sql = "SELECT * FROM admin WHERE `User Name` = ? AND Password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                isValid = true;
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isValid;
    }
}
package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


class FrontPage extends JFrame {
    public FrontPage() {
        setTitle("Leave Manager");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.black);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.DARK_GRAY);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(Box.createVerticalGlue());

        JLabel title = new JLabel("Leave Manager");
        title.setForeground(Color.red);
        title.setFont(new Font("Times New Roman", Font.BOLD, 32));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(title);

        JLabel subtitle = new JLabel("Choose your role to continue");
        subtitle.setForeground(Color.LIGHT_GRAY);
        subtitle.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(subtitle);

        JButton adminButton = new JButton("ADMIN");
        styleButton(adminButton, "#4f8cff");
        
        JButton userButton = new JButton("USER");
        styleButton(userButton, "#2ecc71");

        adminButton.addActionListener(e -> {
            new admin.Login();
            dispose();
        });

        userButton.addActionListener(e -> {
            new user.Login();
            dispose();
        });

        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(adminButton);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(userButton);

        JLabel supportLabel = new JLabel("Need help? Contact support");
        supportLabel.setForeground(Color.GRAY);
        supportLabel.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        supportLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        supportLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        supportLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "Contact: support@company.com");
            }
        });

        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(supportLabel);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);
        setVisible(true);
    }

    private void styleButton(JButton button, String colorHex) {
        button.setBackground(Color.decode(colorHex));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Times New Roman", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }
}

public class Main {
	public static void main(String[]args) {
		new FrontPage();
	}
}
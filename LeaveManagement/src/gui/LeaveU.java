package gui;

import java.awt.*;
import javax.swing.*;

public class LeaveU extends JFrame{
	public LeaveU() {
		setTitle("Leave Manager-User");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.white);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.white);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(Box.createVerticalGlue());

        JLabel title = new JLabel("Leave Manager-User");
        title.setForeground(Color.red);
        title.setFont(new Font("Times New Roman", Font.BOLD, 32));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(title);
        
        JButton goBack = new JButton("← Go Back");
        goBack.setBounds(20, 20, 100, 30);
        goBack.setBackground(new Color(180, 150, 255));
        goBack.setForeground(Color.WHITE);
        goBack.setFocusPainted(false);
        goBack.addActionListener(e -> {
            dispose();
            new UserLogin();
        });
	}
}

package gui;

import javax.swing.*;
import java.awt.*;

public class FrontPage extends JFrame{
	public FrontPage() {
		setTitle("Leave Management");
		setSize(200,300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		JPanel pane = new JPanel();
		pane.setLayout(new GridLayout(2,1,10,10));
		
		JButton admin = new JButton("ADMIN");
		JButton user = new JButton("USER");
		
		pane.add(admin);
		pane.add(user);
		
		add(pane);
		setVisible(true);
	}
}

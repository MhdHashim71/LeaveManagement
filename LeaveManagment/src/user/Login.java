package user;

import dao.DBConnection;
import main.Main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame{
	public Login() {
		 setTitle("Login - Leave Manager");
         setSize(500, 600);
	     setDefaultCloseOperation(EXIT_ON_CLOSE);
	     setLocationRelativeTo(null);
	     
	     // Gradient Background Panel
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

	     JPanel card = new JPanel();
	     card.setLayout(null);
	     card.setBackground(Color.WHITE);
         card.setBounds(75, 100, 350, 400);        
         card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

	     JLabel title = new JLabel("Welcome Back");
	     title.setFont(new Font("SansSerif", Font.BOLD, 22));
	     title.setForeground(new Color(150, 100, 255));
         title.setBounds(100, 20, 200, 30);
         card.add(title);
	     
         JLabel subtitle = new JLabel("Sign in to your account to continue");
	     subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
	     subtitle.setBounds(70, 50, 250, 20);
         card.add(subtitle);

         JLabel emailLabel = new JLabel("Email or Username");
	     emailLabel.setBounds(40, 90, 200, 20);
	     card.add(emailLabel);

         JTextField emailField = new JTextField();
	     emailField.setBounds(40, 110, 270, 30);
	     card.add(emailField);

	     JLabel passwordLabel = new JLabel("Password");
	     passwordLabel.setBounds(40, 150, 200, 20);
	     card.add(passwordLabel);   

	     JPasswordField passwordField = new JPasswordField();
	     passwordField.setBounds(40, 170, 270, 30);
	     card.add(passwordField);

	     JButton forgotPass = new JButton("Forgot password?");
	     forgotPass.setBounds(200, 210, 110, 20);
	     forgotPass.setFont(new Font("SansSerif", Font.PLAIN, 11));
	     forgotPass.setForeground(new Color(150, 100, 255));
         forgotPass.setContentAreaFilled(false);        
         forgotPass.setBorderPainted(false);
	     forgotPass.setFocusPainted(false);
	     forgotPass.addActionListener(e -> JOptionPane.showMessageDialog(this, "Redirect to Forgot Password page"));
         card.add(forgotPass);

	     JButton signIn = new JButton("Sign In");
	     signIn.setBackground(new Color(130, 70, 255));
         signIn.setForeground(Color.WHITE);
         signIn.setFocusPainted(false);	        
         signIn.setBounds(40, 250, 270, 40);
	     card.add(signIn);

	     JButton signup = new JButton("Don't have an account? Sign up");
         signup.setFont(new Font("SansSerif", Font.PLAIN, 11));
         signup.setForeground(new Color(130, 70, 255));
	     signup.setBounds(90, 310, 200, 20);
         signup.setContentAreaFilled(false);
         signup.setBorderPainted(false);
         signup.setFocusPainted(false);
	     signup.addActionListener(e -> {
	    	 dispose();
	    	 new SignUp();
	     });
	     card.add(signup);

	     signIn.addActionListener(new ActionListener() {
	    	 public void actionPerformed(ActionEvent e) {
	    		 String input = emailField.getText().trim();
	             String password = new String(passwordField.getPassword());

	             if (input.isEmpty() || password.isEmpty()) {
	            	 JOptionPane.showMessageDialog(null, "Please enter both username/email and password");
	             } else {
	                 String username = getUsernameIfValid(input, password);
	                 if (username != null) {
	                	 JOptionPane.showMessageDialog(Login.this, "Login Successful!");
	                     dispose();
	                     new Leave(username);
	                 } else
	                     JOptionPane.showMessageDialog(null, "Invalid username/email or password");
	             }
	         }
	     });
	     gradientPanel.add(card);
	     add(gradientPanel);
	     setVisible(true);
	}

	private String getUsernameIfValid(String input, String password) {
	    String username = null;
	    try {
	        DBConnection db = new DBConnection();
	        Connection conn = db.getConnection();
	        String sql = "SELECT Username FROM users WHERE (email = ? OR Username = ?) AND password = ?";
	        PreparedStatement stmt = conn.prepareStatement(sql);
	        stmt.setString(1, input);
	        stmt.setString(2, input);
	        stmt.setString(3, password);

	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            username = rs.getString("Username");
	        }
	        rs.close();
	        stmt.close();
	        conn.close();
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        JOptionPane.showMessageDialog(this, "Database error");
	    }
	    return username;
	}
}
package user;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;
import dao.DBConnection;

public class Leave extends JFrame {

    private final String username;
    private int totalRequests = 0, approvedRequests = 0, rejectedRequests = 0, pendingRequests = 0;

    public Leave(String user) {
        this.username = user;
        this.usernameCheck();

        // Fetch leave stats
        fetchLeaveData();

        // Frame setup
        setTitle("Employee Dashboard - Leave Manager");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        add(buildSidebar(), BorderLayout.WEST);

        // Top bar
        add(buildTopBar(), BorderLayout.NORTH);

        // Main content
        add(new JScrollPane(buildMainPanel()), BorderLayout.CENTER);

        setVisible(true);
    }

    private void usernameCheck() {
        if (username == null || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username not provided!");
            dispose();
            new Login().setVisible(true);
        }
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(40, 40, 80));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("Employee Portal", JLabel.CENTER);
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD, 16));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(logo);

        String[] menuItems = {"Dashboard", "Apply Leave", "View Status"};
        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setMaximumSize(new Dimension(180, 40));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setBackground(new Color(60, 60, 120));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 14));

            btn.addActionListener(e -> {
                switch (item) {
                    case "Dashboard":
                        dispose();
                        new Leave(username).setVisible(true);
                        break;
                    case "Apply Leave":
                        new ApplyLeave(username).setVisible(true);
                        break;
                    case "View Status":
                        new ViewStatus(username).setVisible(true);
                        break;
                }
            });

            sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
            sidebar.add(btn);
        }
        return sidebar;
    }

    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setPreferredSize(new Dimension(0, 50));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JLabel heading = new JLabel("Employee Dashboard");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        topBar.add(heading, BorderLayout.WEST);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        topRight.setBackground(Color.WHITE);

        JLabel notifications = new JLabel();
        updateNotificationIcon(notifications);

        JLabel profile = new JLabel("👤");
        profile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPopupMenu profileMenu = new JPopupMenu();
        JMenuItem viewProfile = new JMenuItem("Profile");
        JMenuItem logout = new JMenuItem("Logout");
        viewProfile.addActionListener(e -> JOptionPane.showMessageDialog(null, "Profile clicked!"));
        logout.addActionListener(e -> {
            JOptionPane.showMessageDialog(logout, "Logged Out Successfully!");
            dispose();
            new Login().setVisible(true);
        });
        profileMenu.add(viewProfile);
        profileMenu.add(logout);

        profile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                profileMenu.show(profile, 0, profile.getHeight());
            }
        });

        topRight.add(notifications);
        topRight.add(profile);
        topBar.add(topRight, BorderLayout.EAST);

        return topBar;
    }

    private JPanel buildMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcome = new JLabel("Welcome back, " + username + "!");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 20));
        welcome.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(welcome);

        JLabel overview = new JLabel("Here's an overview of your leave status");
        overview.setFont(new Font("SansSerif", Font.PLAIN, 14));
        overview.setForeground(Color.GRAY);
        mainPanel.add(overview);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.add(createStatCard("Total Requests", String.valueOf(totalRequests), "All leave requests"));
        statsPanel.add(createStatCard("Approved", String.valueOf(approvedRequests), "Approved requests"));
        statsPanel.add(createStatCard("Rejected", String.valueOf(rejectedRequests), "Rejected requests"));
        statsPanel.add(createStatCard("Pending", String.valueOf(pendingRequests), "Awaiting approval"));
        mainPanel.add(statsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Bottom Panels
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomPanel.setBackground(Color.WHITE);

        // Quick Actions
        JPanel quickActions = new JPanel();
        quickActions.setLayout(new BoxLayout(quickActions, BoxLayout.Y_AXIS));
        quickActions.setBackground(Color.WHITE);
        quickActions.setBorder(BorderFactory.createTitledBorder("Quick Actions"));

        JButton applyBtn = new JButton("Apply for Leave");
        applyBtn.setBackground(new Color(70, 130, 255));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setFocusPainted(false);
        applyBtn.addActionListener(e -> new ApplyLeave(username).setVisible(true));

        JButton viewBtn = new JButton("View Leave Status");
        viewBtn.setBackground(new Color(230, 230, 250));
        viewBtn.addActionListener(e -> new ViewStatus(username).setVisible(true));

        quickActions.add(applyBtn);
        quickActions.add(Box.createRigidArea(new Dimension(0, 10)));
        quickActions.add(viewBtn);
        bottomPanel.add(quickActions);

        // Recent Requests
        JPanel recentPanel = new JPanel();
        recentPanel.setLayout(new BoxLayout(recentPanel, BoxLayout.Y_AXIS));
        recentPanel.setBackground(Color.WHITE);
        recentPanel.setBorder(BorderFactory.createTitledBorder("Recent Leave Requests"));
        loadRecentRequests(recentPanel);
        bottomPanel.add(recentPanel);
        mainPanel.add(bottomPanel);
        return mainPanel;
    }

    private JPanel createStatCard(String title, String value, String subtitle) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(245, 245, 255));
        card.setBorder(new CompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 20));

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);

        card.add(lblTitle);
        card.add(lblValue);
        card.add(lblSub);
        return card;
    }

    private void fetchLeaveData() {
        totalRequests = approvedRequests = rejectedRequests = pendingRequests = 0;

        String sql = "SELECT status, COUNT(*) as count FROM leave_requests WHERE username=? GROUP BY status";

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, username);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("status").toLowerCase();
                    int count = rs.getInt("count");
                    totalRequests += count;

                    switch (status) {
                        case "approved" -> approvedRequests = count;
                        case "pending" -> pendingRequests = count;
                        case "rejected" -> rejectedRequests = count;
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading leave requests: " + ex.getMessage());
        }
    }

    private void loadRecentRequests(JPanel recentPanel) {
        recentPanel.removeAll();

        String sql = "SELECT leave_type, status, start_date, end_date FROM leave_requests " +
                     "WHERE username=? ORDER BY id DESC LIMIT 3";

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, username);

            try (ResultSet rs = pst.executeQuery()) {
                boolean hasRequests = false;

                while (rs.next()) {
                    hasRequests = true;
                    String type = rs.getString("leave_type");
                    String status = rs.getString("status");
                    java.sql.Date start = rs.getDate("start_date");
                    java.sql.Date end = rs.getDate("end_date");

                    long days = 0;
                    if (start != null && end != null) {
                        days = java.time.temporal.ChronoUnit.DAYS.between(
                                start.toLocalDate(), end.toLocalDate()) + 1;
                    }

                    Color statusColor = switch (status.toLowerCase()) {
                        case "approved" -> new Color(0, 180, 80);
                        case "pending" -> new Color(255, 165, 0);
                        default -> Color.RED;
                    };

                    recentPanel.add(createRequest(type, status, start + " to " + end, days + " days", statusColor));
                    recentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }

                if (!hasRequests) {
                    JLabel noData = new JLabel("No recent requests found.");
                    noData.setForeground(Color.GRAY);
                    recentPanel.add(noData);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            recentPanel.add(new JLabel("Error loading requests."));
        }

        recentPanel.revalidate();
        recentPanel.repaint();
    }

    private JPanel createRequest(String title, String status, String date, String days, Color statusColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(250, 250, 255));
        panel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));

        JLabel lblTitle = new JLabel("<html><b>" + title + "</b><br>" + date + "</html>");
        lblTitle.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel lblStatus = new JLabel(status);
        lblStatus.setOpaque(true);
        lblStatus.setBackground(statusColor);
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel lblDays = new JLabel(days);
        lblDays.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setOpaque(false);
        rightPanel.add(lblDays);
        rightPanel.add(lblStatus);

        panel.add(lblTitle, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        return panel;
    }

    private void updateNotificationIcon(JLabel label) {
        int pending = pendingRequests;
        label.setText(pending > 0 ? "🔔 " + pending : "🔔");
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, pending + " pending leave requests");
            }
            public void mouseEntered(MouseEvent e) {
                label.setForeground(Color.BLUE);
            }
            public void mouseExited(MouseEvent e) {
                label.setForeground(Color.BLACK);
            }
        });
    }
}
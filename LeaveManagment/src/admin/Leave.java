package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import dao.DBConnection;

public class Leave extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JLabel totalLabel, approvedLabel, rejectedLabel, pendingLabel;

    public Leave() {
        setTitle("Leave Manager - Admin");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(90, 60, 200));
        topBar.setPreferredSize(new Dimension(0, 60));

        JLabel title = new JLabel("Leave Manager - Admin");
        title.setForeground(Color.white);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        topBar.add(title, BorderLayout.WEST);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        topRight.setOpaque(false);

        // Notifications icon
        JLabel notifications = new JLabel("🔔");
        notifications.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        notifications.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        notifications.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "You have new notifications!");
            }
        });

        // Profile icon
        JLabel profile = new JLabel("👤");
        profile.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        profile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPopupMenu profileMenu = new JPopupMenu();
        JMenuItem viewProfile = new JMenuItem("Profile");
        viewProfile.addActionListener(e -> JOptionPane.showMessageDialog(null, "Profile clicked!"));
        JMenuItem logout = new JMenuItem("Logout");
        logout.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Logged Out Successfully!");
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
        add(topBar, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(240, 240, 255));
        sidebar.setLayout(new GridLayout(6, 1, 0, 10));
        sidebar.setPreferredSize(new Dimension(200, 0));

        String[] tabs = {"Home", "View Requests", "Users"}; // removed Logout
        for (String tab : tabs) {
            JButton btn = new JButton(tab);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btn.setBackground(Color.white);
            btn.addActionListener(e -> switchTab(tab));
            sidebar.add(btn);
        }
        add(sidebar, BorderLayout.WEST);

        //  Main Content
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        add(contentPanel, BorderLayout.CENTER);

        // Tabs
        contentPanel.add(createHomePanel(), "Home");
        contentPanel.add(createRequestPanel(), "View Requests");
        contentPanel.add(createUsersPanel(), "Users");

        setVisible(true);
        loadRequestStats();
    }

    // Home Panel
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.white);

        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        statsPanel.setBackground(Color.white);

        totalLabel = createStatCard("Total Requests", "0", new Color(120, 100, 255));
        approvedLabel = createStatCard("Approved", "0", new Color(60, 180, 90));
        rejectedLabel = createStatCard("Rejected", "0", new Color(255, 90, 90));
        pendingLabel = createStatCard("Pending", "0", new Color(255, 180, 70));

        statsPanel.add(totalLabel);
        statsPanel.add(approvedLabel);
        statsPanel.add(rejectedLabel);
        statsPanel.add(pendingLabel);

        JLabel heading = new JLabel("Dashboard Overview", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        panel.add(heading, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);

        return panel;
    }

    // View Requests Panel
    private JPanel createRequestPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.white);

        JLabel heading = new JLabel("All Leave Requests", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(heading, BorderLayout.NORTH);

        String[] cols = {"ID", "Username", "Type", "Start", "End", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton approveBtn = new JButton("Approve");
        approveBtn.setBackground(new Color(60, 180, 90));
        approveBtn.setForeground(Color.white);
        JButton rejectBtn = new JButton("Reject");
        rejectBtn.setBackground(new Color(255, 90, 90));
        rejectBtn.setForeground(Color.white);
        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        loadRequests(model);

        // Approve / Reject listeners
        approveBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) model.getValueAt(selectedRow, 0);
                updateRequestStatus(id, "Approved");
                model.setValueAt("Approved", selectedRow, 5);
                loadRequestStats();
            } else JOptionPane.showMessageDialog(this, "Select a request first!");
        });

        rejectBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) model.getValueAt(selectedRow, 0);
                updateRequestStatus(id, "Rejected");
                model.setValueAt("Rejected", selectedRow, 5);
                loadRequestStats();
            } else JOptionPane.showMessageDialog(this, "Select a request first!");
        });

        return panel;
    }

    private void loadRequests(DefaultTableModel model) {
        model.setRowCount(0);
        try (Connection conn = new DBConnection().getConnection()) {
            String sql = "SELECT id, username, leave_type, start_date, end_date, status FROM leave_requests";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("username"), rs.getString("leave_type"),
                        rs.getDate("start_date"), rs.getDate("end_date"), rs.getString("status")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRequestStatus(int id, String status) {
        try (Connection conn = new DBConnection().getConnection()) {
            String sql = "UPDATE leave_requests SET status=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Users Panel
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.white);

        JLabel heading = new JLabel("All Users", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(heading, BorderLayout.NORTH);

        String[] cols = {"Username", "Name", "Email", "Mobile no."};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(60, 150, 250));
        refreshBtn.setForeground(Color.white);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadUsers(model));
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        bottomPanel.add(refreshBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Initial load
        loadUsers(model);

        return panel;
    }

    // Load Users from database
    private void loadUsers(DefaultTableModel model) {
        model.setRowCount(0); // Clear previous rows
        try (Connection conn = new DBConnection().getConnection()) {
            String sql = "SELECT Username, Name, Email, Mobile FROM users";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("Username"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Mobile")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load users from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // Stat Card
    private JLabel createStatCard(String title, String value, Color color) {
        JLabel label = new JLabel("<html><center>" + title + "<br><b>" + value + "</b></center></html>", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setOpaque(true);
        label.setBackground(color);
        label.setForeground(Color.white);
        label.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        return label;
    }

    // Load Stats
    private void loadRequestStats() {
        try (Connection conn = new DBConnection().getConnection()) {
            Statement stmt = conn.createStatement();

            ResultSet total = stmt.executeQuery("SELECT COUNT(*) FROM leave_requests");
            total.next();
            totalLabel.setText("<html><center>Total Requests<br><b>" + total.getInt(1) + "</b></center></html>");

            ResultSet approved = stmt.executeQuery("SELECT COUNT(*) FROM leave_requests WHERE status='Approved'");
            approved.next();
            approvedLabel.setText("<html><center>Approved<br><b>" + approved.getInt(1) + "</b></center></html>");

            ResultSet rejected = stmt.executeQuery("SELECT COUNT(*) FROM leave_requests WHERE status='Rejected'");
            rejected.next();
            rejectedLabel.setText("<html><center>Rejected<br><b>" + rejected.getInt(1) + "</b></center></html>");

            ResultSet pending = stmt.executeQuery("SELECT COUNT(*) FROM leave_requests WHERE status='Pending'");
            pending.next();
            pendingLabel.setText("<html><center>Pending<br><b>" + pending.getInt(1) + "</b></center></html>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Switch Tab
    private void switchTab(String tab) {
        cardLayout.show(contentPanel, tab);
        if (tab.equals("Home")) 
        	loadRequestStats();
    }
}
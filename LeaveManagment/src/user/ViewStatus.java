package user;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

import dao.DBConnection;

public class ViewStatus extends JFrame {
    private final String username;
    private JTable table;
    public ViewStatus(String user) {
        this.username = user;

        setTitle("Leave Status - Leave Manager");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadLeaveData();
        setVisible(true);
    }

    private void loadLeaveData() {
        String sql = "SELECT leave_type, start_date, end_date, days, status FROM leave_requests WHERE username=? ORDER BY id DESC";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                DefaultTableModel model = new DefaultTableModel(
                        new String[]{"Type", "Start", "End", "Days", "Status"}, 0);

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("leave_type"),
                            rs.getDate("start_date"),
                            rs.getDate("end_date"),
                            rs.getInt("days"),
                            rs.getString("status")
                    });
                }
                table.setModel(model);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading leave data: " + ex.getMessage());
        }
    }
}
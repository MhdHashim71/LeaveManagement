package user;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Calendar;
import java.util.Properties;
import java.util.Date;

import org.jdatepicker.impl.*;
import dao.DBConnection;

public class ApplyLeave extends JFrame {
    private final String username;
    private JComboBox<String> typeBox;
    private JTextArea reasonArea;
    private JDatePickerImpl startDatePicker, endDatePicker;
    public ApplyLeave(String user) {
        this.username = user;

        setTitle("Apply Leave - Leave Manager");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Leave Type
        panel.add(new JLabel("Leave Type:"));
        typeBox = new JComboBox<>(new String[]{"Casual", "Sick", "Vacation", "Others"});
        panel.add(typeBox);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Start Date Picker
        panel.add(new JLabel("Start Date:"));
        startDatePicker = createDatePicker();
        panel.add(startDatePicker);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // End Date Picker
        panel.add(new JLabel("End Date:"));
        endDatePicker = createDatePicker();
        panel.add(endDatePicker);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Reason
        panel.add(new JLabel("Reason:"));
        reasonArea = new JTextArea(4, 20);
        panel.add(new JScrollPane(reasonArea));

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Submit Button
        JButton submitBtn = new JButton("Submit Request");
        submitBtn.setBackground(new Color(70, 130, 255));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.addActionListener(e -> submitLeaveRequest());

        panel.add(submitBtn);
        add(panel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JDatePickerImpl createDatePicker() {
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        UtilDateModel model = new UtilDateModel();
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        return new JDatePickerImpl(datePanel, new DateLabelFormatter());
    }

    private void submitLeaveRequest() {
        String type = typeBox.getSelectedItem().toString();
        String reason = reasonArea.getText().trim();

        Date startDateValue = (Date) startDatePicker.getModel().getValue();
        Date endDateValue = (Date) endDatePicker.getModel().getValue();

        if (startDateValue == null || endDateValue == null || reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields and select dates.");
            return;
        }

        if (endDateValue.before(startDateValue)) {
            JOptionPane.showMessageDialog(this, "End date cannot be before start date.");
            return;
        }

        long days = (endDateValue.getTime() - startDateValue.getTime()) / (1000 * 60 * 60 * 24) + 1;

        try (Connection conn = new DBConnection().getConnection()) {
            // Insert leave request
            String sql = "INSERT INTO leave_requests(username, leave_type, start_date, end_date, status, days, reason) " +
                         "VALUES (?, ?, ?, ?, 'pending', ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, username);
                pst.setString(2, type);
                pst.setDate(3, new java.sql.Date(startDateValue.getTime()));
                pst.setDate(4, new java.sql.Date(endDateValue.getTime()));
                pst.setInt(5, (int) days);
                pst.setString(6, reason);

                int inserted = pst.executeUpdate();

                if (inserted > 0) {
                    // Update pending requests count in users table
                    String updateSql = "UPDATE users SET PendingRequests = PendingRequests + 1 WHERE Username=?";
                    try (PreparedStatement updatePst = conn.prepareStatement(updateSql)) {
                        updatePst.setString(1, username);
                        updatePst.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(this, "Leave request submitted successfully!");
                    dispose();
                    new Leave(username).setVisible(true);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error submitting request: " + ex.getMessage());
        }
    }

    // DateLabelFormatter for JDatePicker
    public static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final String datePattern = "yyyy-MM-dd";
        private final java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws java.text.ParseException {
            return dateFormatter.parse(text);
        }

        @Override
        public String valueToString(Object value) throws java.text.ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }
}
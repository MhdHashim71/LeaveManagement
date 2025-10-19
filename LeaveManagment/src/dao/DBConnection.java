package dao;

import java.sql.*;

public class DBConnection {
	public String url ="jdbc:mysql://localhost:3306/leave_management";
	public String user = "root";
	public String pass = "Hashim@2004";
	public Connection getConnection() throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection(url, user, pass);
	}
}
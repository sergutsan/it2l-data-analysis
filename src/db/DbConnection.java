package db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
	private static Connection CONNECTION = null;

	public static Connection getConnection() throws ClassNotFoundException, SQLException, IOException {
		if (CONNECTION == null) {
			// Access DB old-style, with JDBC
			Class.forName("com.mysql.jdbc.Driver");
			ConnectionData cd = new ConnectionData("./connectionData.txt");
			String params = "?useUnicode=true&amp;characterEncoding=UTF8";
			String url = "jdbc:mysql://" + cd.hostname + ":" + cd.port + "/" + cd.dbName + params; 
			CONNECTION = DriverManager.getConnection(url, cd.login, cd.password);
		}
		return CONNECTION;
	}

	// Close database connection
	public static void closeConnection() throws SQLException {
		if (CONNECTION != null) {
			CONNECTION.close();
		}
	}
	

}

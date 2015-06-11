package db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
			ConnectionData cd = new ConnectionData("connectionData.txt");
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

class ConnectionData {
	public String dbName;
	public String login;
	public String password;
	public String hostname;
	public String port;
	
	public ConnectionData(String filename) throws IOException {
		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
			this.dbName   = in.readLine();
			this.login    = in.readLine();
			this.password = in.readLine();
			this.hostname = in.readLine();
			this.port     = in.readLine();
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println("Data file: " + filename + " does not exist.");
			throw new IOException(e);
		} catch (IOException e) {
			System.out.println("Corrupt connection data file: " + filename + ".");
			throw new IOException(e);
		}
	}
}

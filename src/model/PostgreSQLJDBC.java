package model;

import java.sql.Connection;
import java.sql.DriverManager;


public class PostgreSQLJDBC {
	
	private Connection connection;

	public PostgreSQLJDBC(String url, String user, String pwd) {
	      try {
	         Class.forName("org.postgresql.Driver");
	         this.setConnection(DriverManager.
	        		 getConnection(url, user, pwd));
	      } catch (Exception e) {
	         e.printStackTrace();
	         System.err.println(e.getClass().getName()+": "+e.getMessage());
	         System.exit(0);
	      }
	      System.out.println("Opened database successfully");
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}

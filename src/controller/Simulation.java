package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import model.PostgreSQLJDBC;

public class Simulation {
	public Simulation() {
		Connection SimulatorConnection = 
				new PostgreSQLJDBC("jdbc:postgresql://manny.db.elephantsql.com:5432/", 
						"juynhvrm", 
						"H-FZ4Jrenwgd_c2Xzte7HLsYJzB_6q5D").getConnection();
		Connection EMConnection = 
				new PostgreSQLJDBC("jdbc:postgresql://manny.db.elephantsql.com:5432/", 
						"ngcbqvhq", 
						"Ppjleq3n6HQF5qPheDze2QFzG4LHxTAf").getConnection();

		
		System.out.println("Simulation initialized successfully");
		
		// Variable initializations are to be done here
		String query = "select * from fire";
		ResultSet rs = null;
		
		
		try {
			PreparedStatement pst = SimulatorConnection.prepareStatement(query);
			rs = pst.executeQuery();
			if (rs.next()) {
				 ResultSetMetaData rsmd = rs.getMetaData();
	            System.out.printf("got the following results : %s : %s, %s : %s, %s : %s", 
	            		rsmd.getColumnLabel(1), rs.getString(1), 
	            		rsmd.getColumnLabel(2), rs.getString(2), 
	            		rsmd.getColumnLabel(3), rs.getString(3));
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}


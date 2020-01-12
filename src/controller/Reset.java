package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;

import model.PostgreSQLJDBC;

public class Reset {
	
	private Connection SimulatorConnection;
	private Connection EmergencyManagerConnection;

	public Reset() {
		
		try {
			
			PostgreSQLJDBC postgreSQLJDBCSim = new PostgreSQLJDBC("jdbc:postgresql://manny.db.elephantsql.com:5432/", 
					"juynhvrm", 
					"H-FZ4Jrenwgd_c2Xzte7HLsYJzB_6q5D");
			
			SimulatorConnection = postgreSQLJDBCSim.getConnection();
			
			PostgreSQLJDBC postgreSQLJDBCEM = new PostgreSQLJDBC("jdbc:postgresql://manny.db.elephantsql.com:5432/", 
					"ngcbqvhq", 
					"Ppjleq3n6HQF5qPheDze2QFzG4LHxTAf");
			
			EmergencyManagerConnection = postgreSQLJDBCEM.getConnection();
			
			if(postgreSQLJDBCSim.hasConnection() && postgreSQLJDBCEM.hasConnection()) {
				
				// Reseting emergency manager database
				this.resetEM();
				
				// Reseting simulator database
				this.resetSim();
				
				System.out.printf("Closing conection with " + postgreSQLJDBCSim.getUser() + "... ");
				SimulatorConnection.close();
				System.out.println("Closed");
				System.out.printf("Closing conection with " + postgreSQLJDBCEM.getUser() + "... ");
				EmergencyManagerConnection.close();
				System.out.println("Closed");
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void resetEM() {
		
		System.out.println("Emergency manager cleanup");
		
		try {
			String fireEngine = "update fire_engine set x_pos = (select real_x from real_pos where id = (select id_real_pos from station where id = id_station)), y_pos = (select real_y from real_pos where id = (select id_real_pos from station where id = id_station)), busy = false";
			PreparedStatement pstFireEngine = EmergencyManagerConnection.prepareStatement(fireEngine);
			pstFireEngine.executeUpdate();
			System.out.println(pstFireEngine);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Fire engine cleanup");
		}
			
		try {
			String fire = "update fire set intensity = 0, handled = 0";
			PreparedStatement pstFire = EmergencyManagerConnection.prepareStatement(fire);
			pstFire.executeUpdate();
			System.out.println(pstFire);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Fire cleanup");
		}
		
		try {
			String intervention = "delete from intervention";
			PreparedStatement pstIntervention = EmergencyManagerConnection.prepareStatement(intervention);
			pstIntervention.executeUpdate();
			System.out.println(pstIntervention);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Intervention cleanup");
		}		
		
		System.out.println();
		
	}
	
	private void resetSim() {
		
		System.out.println("Simulator cleanup");
			
		try {
			String fire = "update fire set intensity = 0, precise_intensity = 0";
			PreparedStatement pstFire = SimulatorConnection.prepareStatement(fire);
			pstFire.executeUpdate();
			System.out.println(pstFire);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Fire cleanup");
		}		
		
		System.out.println();
		
	}
	
}

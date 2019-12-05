package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.TimerTask;

import model.PostgreSQLJDBC;

public class fireTimer extends TimerTask {
	
	public void run() {
		Connection SimulatorConnection = 
				new PostgreSQLJDBC("jdbc:postgresql://manny.db.elephantsql.com:5432/", 
						"juynhvrm", 
						"H-FZ4Jrenwgd_c2Xzte7HLsYJzB_6q5D").getConnection();
		
		String UpdateQuery = "UPDATE fire SET intensity = ? WHERE x_pos = ? AND y_pos = ?";

		try {
			
			PreparedStatement pst = SimulatorConnection.prepareStatement(UpdateQuery);
			int newIntensity = 1;
			int Xpos = new Random().nextInt(9);
			int Ypos = new Random().nextInt(5);
			pst.setInt(1, newIntensity);
			pst.setInt(2, Xpos);
			pst.setInt(3, Ypos);
			System.out.println(pst.toString());
			pst.executeUpdate();
			SimulatorConnection.close();
			System.out.println("Intensity update completed successfully");
	        }
		catch (SQLException e) {
			e.printStackTrace();
		}
    }
}

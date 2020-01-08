package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

import model.PostgreSQLJDBC;
import model.Sensor;

public class fireTimer extends TimerTask {
	
	int fireFactor = 1;
	Random rand = new Random();
	
	public void run() {
		Connection SimulatorConnection = 
				new PostgreSQLJDBC("jdbc:postgresql://manny.db.elephantsql.com:5432/", 
						"juynhvrm", 
						"H-FZ4Jrenwgd_c2Xzte7HLsYJzB_6q5D").getConnection();
		
		String getSimActiveSensorsQuery = "select id, intensity from fire";
		
		Connection EmergencyManagerConnection = 
				new PostgreSQLJDBC("jdbc:postgresql://manny.db.elephantsql.com:5432/", 
						"ngcbqvhq", 
						"Ppjleq3n6HQF5qPheDze2QFzG4LHxTAf").getConnection();
		
		String getEmActiveSensorsQuery = "select f.id, f.intensity, f.handled, p.real_x, p.real_y from fire f join real_pos p on f.id_real_pos = p.id";

		try {
			// Get Sensors from Sim
			PreparedStatement pstSimActiveSensors = SimulatorConnection.prepareStatement(getSimActiveSensorsQuery);
			ResultSet resultSetSimActiveSensors = pstSimActiveSensors.executeQuery();
			
			SimulatorConnection.close();
			
			List<Sensor> activeSimSensors = new ArrayList<>();
			
			while (resultSetSimActiveSensors.next()) {
				
				int id = Integer.parseInt(resultSetSimActiveSensors.getString("id"));
				int intensity = Integer.parseInt(resultSetSimActiveSensors.getString("intensity"));
				
				activeSimSensors.add(new Sensor(id, intensity));
			}
			
			// Get Sensors from Em, keep their intensities
			PreparedStatement pstEmActiveSensors = EmergencyManagerConnection.prepareStatement(getEmActiveSensorsQuery);
			ResultSet resultSetEmActiveSensors = pstEmActiveSensors.executeQuery();
			
			EmergencyManagerConnection.close();
			
			List<Sensor> activeEmSensors = new ArrayList<>();
			
			while (resultSetEmActiveSensors.next()) {
				int id = Integer.parseInt(resultSetEmActiveSensors.getString("id"));
				double x = Double.parseDouble(resultSetEmActiveSensors.getString("real_x"));
				double y = Double.parseDouble(resultSetEmActiveSensors.getString("real_y"));
				int intensity = Integer.parseInt(resultSetEmActiveSensors.getString("intensity"));
				int handled = Integer.parseInt(resultSetEmActiveSensors.getString("handled"));
				
				activeEmSensors.add(new Sensor(id, x, y, intensity, handled));
			}

			for(Sensor sensor : activeSimSensors){
				for(Sensor sensorEm : activeEmSensors) {
					// update handled values while keeping the simulated intensity (which reflects reality).
					// display a warming if intensities differ.
					if(sensor.getId() == sensorEm.getId()) {
						System.out.println("match : ");
						System.out.println(sensor.toString());
						System.out.println(sensorEm.toString());
						sensor.setHandled(sensorEm.getHandled());
						sensor.setX(sensorEm.getX());
						sensor.setY(sensorEm.getY());
//						if(sensor.getIntensity() != sensorEm.getIntensity()) {
//							System.out.println("WARNING - Simulated and accounted intensities differ.");
//						}
					}
				}
			}
			System.out.println(activeSimSensors.toString());
			
			for(Sensor sensor : activeSimSensors){
				// update handled values
				// TODO add "AND sensor.hasFireEnginesOnScene
				if(sensor.getIntensity() > 0) {
					if(sensor.getHandled() > 0) {
						if(sensor.getHandled() >= sensor.getIntensity()) {
							//  reduce intensity
							sensor.setIntensity(sensor.getIntensity() - fireFactor);
						} else {
							// quarter intensity increase speed
							sensor.setIntensity(sensor.getIntensity() + fireFactor/4);
						}
					} else {
						// increase intensity
						sensor.setIntensity(sensor.getIntensity() + fireFactor/4);
					}
				} else {
					// 1/120 chance for it to start
					if(rand.nextInt(120) == 1) {
						sensor.setIntensity(rand.nextInt(9));
					}
				}
				// call updateIntensity
				Connection updateConn = 
						new PostgreSQLJDBC("jdbc:postgresql://manny.db.elephantsql.com:5432/", 
								"juynhvrm", 
								"H-FZ4Jrenwgd_c2Xzte7HLsYJzB_6q5D").getConnection();
				sensor.updateIntensity(updateConn); // auto closes connection
			}
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
    }
}

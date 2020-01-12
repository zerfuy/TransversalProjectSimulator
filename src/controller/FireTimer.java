package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

import model.FireEngine;
import model.PostgreSQLJDBC;
import model.Sensor;

public class FireTimer extends TimerTask {

	int fireFrequency = 220;
	int fireRange = 9;
	int fireFluctuation = 14;
	
	int debug = 1;
	Random rand = new Random();
	
	List<Sensor> emSensors;
	List<Sensor> simSensors;
	List<FireEngine> fireEngines;
	Connection SimulatorConnection;
	Connection EmergencyManagerConnection;
	
	public FireTimer() {
		
	}
	
	public void run() {
		
		
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
				System.out.println();
				
				// Getting sensors (from sim db with em db for additionnal data)
				this.getSensors();
				
				// Getting fire engines from em db
				this.getFireEngines();
				
				// Fire manipulation
				this.updateSensors();
				
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
		
		System.out.println("/*******************************************************/");
		
    }
	
	public void getFireEngines() {
		
		fireEngines = new ArrayList<>();
		
		try {
			
			// Get busy fire engines from EM
			String getBusyFireEnginesQuery = "select id, rank, x_pos, y_pos from fire_engine where busy = true";
			PreparedStatement pstBusyFireEngines = EmergencyManagerConnection.prepareStatement(getBusyFireEnginesQuery);
			ResultSet resultSetBusyFireEngines = pstBusyFireEngines.executeQuery();
			
			while (resultSetBusyFireEngines.next()) {		
				int id = Integer.parseInt(resultSetBusyFireEngines.getString("id"));
				int rank = Integer.parseInt(resultSetBusyFireEngines.getString("rank"));
				double x = Double.parseDouble(resultSetBusyFireEngines.getString("x_pos"));
				double y = Double.parseDouble(resultSetBusyFireEngines.getString("y_pos"));
				
				fireEngines.add(new FireEngine(id, rank, x, y));
			}
			
		}  catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Got all busy fire engines (" + fireEngines.size() + ")");
		if(debug>0) {
			for(FireEngine fireEngine : fireEngines) {
				System.out.println("\t" + fireEngine);
			}
		}
		System.out.println();
		
	}
	
	public void getSensors() {
		
		simSensors = new ArrayList<>();
		emSensors = new ArrayList<>();
		
		try {
			
			// Get Sensors from Sim
			String getSimActiveSensorsQuery = "select id, intensity, precise_intensity from fire";
			PreparedStatement pstSimActiveSensors = SimulatorConnection.prepareStatement(getSimActiveSensorsQuery);
			ResultSet resultSetSimActiveSensors = pstSimActiveSensors.executeQuery();
			
			while (resultSetSimActiveSensors.next()) {		
				int id = Integer.parseInt(resultSetSimActiveSensors.getString("id"));
				int intensity = Integer.parseInt(resultSetSimActiveSensors.getString("intensity"));
				float precise_intensity = Float.parseFloat(resultSetSimActiveSensors.getString("precise_intensity"));
				
				simSensors.add(new Sensor(id, intensity, precise_intensity));
			}
			
			// Get Sensors from Em, keep their intensities
			String getEmActiveSensorsQuery = "select f.id, f.intensity, f.handled, p.real_x, p.real_y from fire f join real_pos p on f.id_real_pos = p.id";
			PreparedStatement pstEmActiveSensors = EmergencyManagerConnection.prepareStatement(getEmActiveSensorsQuery);
			ResultSet resultSetEmActiveSensors = pstEmActiveSensors.executeQuery();
			
			while (resultSetEmActiveSensors.next()) {
				int id = Integer.parseInt(resultSetEmActiveSensors.getString("id"));
				double x = Double.parseDouble(resultSetEmActiveSensors.getString("real_x"));
				double y = Double.parseDouble(resultSetEmActiveSensors.getString("real_y"));
				int intensity = Integer.parseInt(resultSetEmActiveSensors.getString("intensity"));
				int handled = Integer.parseInt(resultSetEmActiveSensors.getString("handled"));
		
				emSensors.add(new Sensor(id, x, y, intensity, handled));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("Got all sim & em sensors ( " + emSensors.size() + " & " + simSensors.size() + " )");

		for(Sensor sensor : simSensors){
			Sensor s = null;
			for(Sensor sensorEm : emSensors) {
				// update handled values while keeping the simulated intensity (which reflects reality).
				if(sensor.getId() == sensorEm.getId()) {
					sensor.setHandled(sensorEm.getHandled());
					sensor.setX(sensorEm.getX());
					sensor.setY(sensorEm.getY());
					s = sensorEm;
					break;
				}
			}
			emSensors.remove(s);
			
			if(debug>0 && sensor.getIntensity() > 0) {
				System.out.println("Sensor: " + sensor.getId() + ", Fire: " + sensor.getHandled() + "/" + sensor.getIntensity() + ", Pos : " + sensor.getX() + " - " + sensor.getY());
			}
			
			sensor.setConn(SimulatorConnection);
		}
		System.out.println();
		
	}
	
	public void updateSensors() {
		
		try {
			
			for(Sensor sensor : simSensors){
				
				boolean update = false;
				
				if(sensor.getIntensity() > 0 ) { // Fire ongoing
					System.out.println("\tFire is ongoing for sensor " + sensor.getId());
					
					int rankValue = this.getRankOnSite(sensor);
					float fireIntensity = sensor.getPreciseIntensity();
					
					sensor.setPreciseIntensity(this.getNewIntensity(rankValue, fireIntensity));
					if(sensor.getIntensity() == 0) {
						System.out.println("\tFire extinguished !");
					}
					update = true;
					
					
				} else { // No fire ongoing
					
					// Generate fire at random
					if(rand.nextInt(this.fireFrequency) == 1) {
						int intensity = this.getRandomFireValue();
						sensor.setPreciseIntensity(intensity);
						
						System.out.println("\tNew fire generated with value " + intensity + " for sensor " + sensor.getId());
						update = true;
					}
				}
				
				if(update) {
					// Update the simulated sensor value if necessary
					sensor.updateIntensity();
					System.out.println();
				}	
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public int getRandomFireValue() {
		
		return rand.nextInt(this.fireRange);
		
	}
	
	public int getRankOnSite(Sensor sensor) {
		
		int rank = 0;

		for(FireEngine fireEngine : fireEngines) {
			if(sensor.isOnSite(fireEngine)) {
				rank += fireEngine.getRank();
			}
		}
		
		System.out.println("\tFire " + sensor.getId() + " has rank " + rank + " on site");
		
		return rank;
	}
	
	public float getNewIntensity(int rankValue, float oldFireIntensity) {
		
		int orderRandVariation = rankValue == 0 ? 5 : 10;
		
		float rankFireVariation = (float)rankValue / 5;
		float intensity = oldFireIntensity - rankFireVariation;
		
		System.out.printf("\tOld intensity: " + oldFireIntensity + ", rankFireVariation: " + rankFireVariation);
		
		if(intensity > 0) {
			float randFluctuation = ((float)rand.nextInt(fireFluctuation) - (float)fireFluctuation / 2) / (orderRandVariation*fireRange) * oldFireIntensity;
			intensity = intensity + randFluctuation;
			
			System.out.printf(", randFluctuation: " + randFluctuation);
		}
		
		intensity = intensity >= 0 ? intensity : 0;
		intensity = intensity <= 9 ? intensity : 9;
		
		System.out.println(", New intensity: " + intensity);
		
		return intensity;
	}
	
	public void setEnvironement(List<Sensor> emSensors, List<Sensor> simSensors, List<FireEngine> fireEngines) {
		
		this.emSensors = emSensors;
		this.simSensors = simSensors;
		this.fireEngines = fireEngines;
		
	}
}

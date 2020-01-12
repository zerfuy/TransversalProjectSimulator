package controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import com.google.maps.model.LatLng;

import model.Intervention;
import model.PostgreSQLJDBC;

public class FireEngineTimer extends TimerTask {
	
	private int debug = 1;
	private Connection EMConnection;
	private List<Intervention> interventions;
	
	public void run() {

		try {
			
			PostgreSQLJDBC PostgreSQLJDBCEM = new PostgreSQLJDBC("jdbc:postgresql://manny.db.elephantsql.com:5432/", 
					"ngcbqvhq", 
					"Ppjleq3n6HQF5qPheDze2QFzG4LHxTAf");
			
			EMConnection = PostgreSQLJDBCEM.getConnection();
			
			// Getting interventions from em database
			this.getInterventions();
			
			// Moving fire engines
			this.moveFireEngines();
			
			System.out.printf("Closing conection with " + PostgreSQLJDBCEM.getUser() + "... ");
			EMConnection.close();
			System.out.println("Closed");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("/*******************************************************/");

    }
	
	private void getInterventions() {
		
		interventions = new ArrayList<>();
		
		try {
			
			String Query = "select f.id, route, speed, f.x_pos, f.y_pos from fire_engine f, intervention i where f.id = i.id_fire_engine";
			PreparedStatement pst = EMConnection.prepareStatement(Query);
			ResultSet resultSet = pst.executeQuery();
			
			while (resultSet.next()) {

				int id = Integer.parseInt(resultSet.getString("id"));
				String route = resultSet.getString("route");
				int speed = Integer.parseInt(resultSet.getString("speed"));
				double x = Double.parseDouble(resultSet.getString("x_pos"));
				double y = Double.parseDouble(resultSet.getString("y_pos"));
				
				interventions.add(new Intervention(id, route, speed, x, y));
				
			}
			
			System.out.println("Got all interventions (" + interventions.size() + ")");
			if(debug>0) {
				for(Intervention intervention : interventions) {
					System.out.println("\t" + intervention);
				}
			}
			System.out.println();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private void moveFireEngines() {
		
		for(Intervention intervention : interventions) {
		
			if(intervention.getRoute() != null) {
				
				List<LatLng> route = intervention.getRoute();
				double lat = 0;
				double lng = 0;
				
				System.out.println(route.get(0));
				
				do {		
					lat = new BigDecimal(route.get(intervention.getSPos()).lat).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
					lng = new BigDecimal(route.get(intervention.getSPos()).lng).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
					
					intervention.increaseSpeed();
					
				} while(intervention.getSPos() < route.size() && (Math.abs(lat - intervention.getX()) > 0.0001 || Math.abs(lng - intervention.getY()) > 0.0001));
				
				if(intervention.getSPos() < route.size()) {
					lat = new BigDecimal(route.get(intervention.getSPos()).lat).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
					lng = new BigDecimal(route.get(intervention.getSPos()).lng).setScale(4, RoundingMode.HALF_EVEN).doubleValue();	
				} else {
					lat = intervention.getRoute().get(intervention.getRoute().size()-1).lat;
					lng = intervention.getRoute().get(intervention.getRoute().size()-1).lng;
				}
				
				lat = new BigDecimal(lat).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
				lng = new BigDecimal(lng).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
				
				System.out.println();
				System.out.println("\t" + lat + "\t" + lng);
				
				System.out.println("Fire engine " + intervention.getId());
				System.out.println("\tlat: " + lat + ", lng: " + lng);
				System.out.println("\tx: " + intervention.getX() + ", y: " + intervention.getY());
				
				try {
					
					String UpdateQuery = "update fire_engine set x_pos = ?, y_pos = ? where id = ?";
					PreparedStatement pstUpdate = EMConnection.prepareStatement(UpdateQuery);
					pstUpdate.setDouble(1, lat);
					pstUpdate.setDouble(2, lng);
					pstUpdate.setInt(3, intervention.getId());
					pstUpdate.executeUpdate();
					System.out.println("\troute.size() : " + intervention.getRoute().size());
					System.out.println("\tpstUpdate : " + pstUpdate);
					
					System.out.println();
					
				}  catch (SQLException e) {
					e.printStackTrace();
				}
			}	
				
		}
		
	}

}

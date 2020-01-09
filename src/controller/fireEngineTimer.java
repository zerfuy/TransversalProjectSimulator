package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import model.Intervention;
import model.PostgreSQLJDBC;

public class fireEngineTimer extends TimerTask {
	
	private int debug = 1;
	private String Query = "select f.id, route, speed, f.x_pos, f.y_pos from fire_engine f, intervention i where f.id = i.id_fire_engine";
	private String UpdateQuery = "update fire_engine set x_pos = ?, y_pos = ? where id = ?";
	private String SimFireEngineQuery = "select id, step, route from fire_engine where step <> 0 and id in (";
	private String SimFireEngineUpdateQuery = "update fire_engine set step = ?, route = ? where id = ?";
	private Connection EMConnection;
	private Connection SimConnection;
	private List<Intervention> interventions;
	
	public void run() {
		
		// TODO : check si pos camion = first ou alors donner avancement du camion dans la db simu ?
		
		
				// TODO : update fire_engine pos, according to it's speed & it's route.
				// route calculation is done by the EM manager webserver
				/*System.out.println(route);*/

		try {
			
			PostgreSQLJDBC PostgreSQLJDBCEM = new PostgreSQLJDBC("jdbc:postgresql://manny.db.elephantsql.com:5432/", 
					"ngcbqvhq", 
					"Ppjleq3n6HQF5qPheDze2QFzG4LHxTAf");
			
			EMConnection = PostgreSQLJDBCEM.getConnection();
			
			PostgreSQLJDBC PostgreSQLJDBCSim = new PostgreSQLJDBC("jdbc:postgresql://manny.db.elephantsql.com:5432/", 
					"juynhvrm", 
					"H-FZ4Jrenwgd_c2Xzte7HLsYJzB_6q5D");
			
			SimConnection = PostgreSQLJDBCSim.getConnection();
			
			// Getting interventions from em database
			this.getInterventions();
			
			// Moving fire engines
			this.moveFireEngines();
			
			System.out.printf("Closing conection with " + PostgreSQLJDBCEM.getUser() + "... ");
			EMConnection.close();
			System.out.println("Closed");
			System.out.printf("Closing conection with " + PostgreSQLJDBCSim.getUser() + "... ");
			SimConnection.close();
			System.out.println("Closed");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("/*******************************************************/");
		
//		String polyline = "ejmvG{yz\\jC_ACUk@oC{@iBGOg@cAKa@BA?ABC?G?EAERQfAyADHDBJ@NNJTT`AdAxGH\\FTJVl@pAdBlDHNDNFNBRBPRjCF|B?z@Sf@KPKBMGCGY_@i@aAU[}@w@_@c@CUC_@DI?K?KAIEIIEIAIDGHCH?J?HMRYh@_AhCQ`@OVSX{@rAgGlI}CpDkE|E}G~HuE|FwApBwApBqAxBw@zAs@tAi@jAYn@Wv@Wv@Ol@Mj@Q|@OdAGd@Eh@E`@Cd@ElAClA?fADfAFzAJpANfAL|@Hh@Np@`@bB^zAHp@Z|AJh@PjALfADVDVNj@Lz@Px@Ll@p@vCTf@^p@L\\?L@LBJBHBHDHFDDDFBHBH?J?BADCFEFEDIDIBI@E@K@K?M?KCKAK?K?K@MBIBKDIDIBEHMBQBS?Q?KAIAMAMGSGUKUKMIKOSQSSQSQWQUOUMSKWIQGWGYG[E[AW?M?a@Bi@DUDYFKBSFOBSBO@OAMCMIIGIIKMIMEQEOESEWIg@G[G]G?GAGBGDAFC~@ElA?V@ZDVDPBBHRJTNPLLNHNFKz@k@Q_AIUA_@CA?qCs@I?I@IBuAbAiAf@MBYDE@a@BA?g@AE@C?C@qA^G@GDEBg@f@wAdBW\\IFIFiEnBC@iAbAEFwGdLMkE?o@?C?_@@I";
//		
//		EncodedPolyline a = new EncodedPolyline(polyline);
//		
//		List<LatLng> route = a.decodePath();
//		
//		System.out.println(route);
    }
	
	private void getInterventions() {
		
		interventions = new ArrayList<>();
		
		List<String> fireEngineIds = new ArrayList<>();
		
		try {
			
			PreparedStatement pst = EMConnection.prepareStatement(Query);
			ResultSet resultSet = pst.executeQuery();
			
			while (resultSet.next()) {

				int id = Integer.parseInt(resultSet.getString("id"));
				String route = resultSet.getString("route");
				int speed = Integer.parseInt(resultSet.getString("speed"));
				double x = Double.parseDouble(resultSet.getString("x_pos"));
				double y = Double.parseDouble(resultSet.getString("y_pos"));
				
				interventions.add(new Intervention(id, route, speed, x, y));
				fireEngineIds.add(Integer.toString(id));
				
			}
			
			String q = SimFireEngineQuery + String.join(",", fireEngineIds) + ")";
			
			PreparedStatement pstSim = SimConnection.prepareStatement(q);
			ResultSet resultSetSim = pstSim.executeQuery();
			
			while (resultSetSim.next()) {
				
				int id = Integer.parseInt(resultSetSim.getString("id"));
				String route = resultSetSim.getString("route");
				int step = Integer.parseInt(resultSetSim.getString("step"));
				
				if(route.length() > 1) {
					for(Intervention intervention : interventions) {
						if (intervention.getId() == id) {
							intervention.setRoute(route);
							intervention.setStep(step);
							break;
						}
					}
				}
				
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
				
				intervention.increaseStep();
				
				
				
				double lat = 0.0;
				double lng = 0.0;
				if(intervention.getRoute().size() > intervention.getSpeed() * intervention.getStep()) {
					lat = intervention.getRoute().get(intervention.getSpeed() * intervention.getStep()).lat;
					lng = intervention.getRoute().get(intervention.getSpeed() * intervention.getStep()).lng;
				} else {
					lat = intervention.getRoute().get(intervention.getRoute().size()).lat;
					lng = intervention.getRoute().get(intervention.getRoute().size()).lng;
				}
				System.out.println("Fire engine " + intervention.getId() + "\n\tlat: " + lat + ", lng: " + lng + "\n\tx: " + intervention.getX() + ", y: " + intervention.getY() + "\n\tstep: " + intervention.getStep());
				
				try {
					
					PreparedStatement pstUpdate = EMConnection.prepareStatement(UpdateQuery);
					pstUpdate.setDouble(1, lat);
					pstUpdate.setDouble(2, lng);
					pstUpdate.setInt(3, intervention.getId());
					pstUpdate.executeUpdate();
					System.out.println("\troute.size() : " + intervention.getRoute().size());
					System.out.println("\tpstUpdate : " + pstUpdate);
					
					
					PreparedStatement pstUpdateSim = SimConnection.prepareStatement(SimFireEngineUpdateQuery);
					pstUpdateSim.setInt(1, intervention.getStep());
					pstUpdateSim.setString(2, intervention.getRouteStr());
					pstUpdateSim.setInt(3, intervention.getId());
					pstUpdateSim.executeUpdate();
					System.out.println("\tpstUpdateSim : " + pstUpdateSim);
					
					System.out.println();
					
				}  catch (SQLException e) {
					e.printStackTrace();
				}
			}	
				
		}
		
	}
}

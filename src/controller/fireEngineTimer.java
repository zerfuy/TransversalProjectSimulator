package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;

import model.PostgreSQLJDBC;
import model.Sensor;

public class fireEngineTimer extends TimerTask {
	
	public void run() {
		Connection EMConnection = 
				new PostgreSQLJDBC("jdbc:postgresql://manny.db.elephantsql.com:5432/", 
						"ngcbqvhq", 
						"Ppjleq3n6HQF5qPheDze2QFzG4LHxTAf").getConnection();
		
		// TODO : check si pos camion = first ou alors donner avancement du camion dans la db simu ?
		String Query = "select f.id, route, speed, f.x_pos, f.y_pos from fire_engine f, intervention i where f.id = i.id_fire_engine";
		
		try {
			// format info
			PreparedStatement pst = EMConnection.prepareStatement(Query);
			ResultSet resultSet = pst.executeQuery();

			while (resultSet.next()) {
				int id = Integer.parseInt(resultSet.getString("id"));
				String tpRoute = resultSet.getString("route");
				EncodedPolyline a = new EncodedPolyline(tpRoute);
				List<LatLng> route = a.decodePath();
				int speed = Integer.parseInt(resultSet.getString("speed"));
				double x_pos = Double.parseDouble(resultSet.getString("x_pos"));
				double y_pos = Double.parseDouble(resultSet.getString("y_pos"));
				// TODO : update fire_engine pos, according to it's speed & it's route.
				// route calculation is done by the EM manager webserver
				
				String UpdateQuery = "UPDATE fire_engine SET x_pos = ?, y_pos = ? WHERE id = ?";
	
				PreparedStatement pstUpdate = EMConnection.prepareStatement(UpdateQuery);
				double lat = 0.0;
				double lng = 0.0;
				if(route.size() > speed) {
					lat = route.get(speed).lat;
					lng = route.get(speed).lng;
					System.out.println("lats : ");
					System.out.println(lat);
					System.out.println(x_pos);
					while(Math.abs(lat - x_pos) <= 0.00001 && Math.abs(lng - y_pos) <= 0.00001) {
						speed += speed;
						lat = route.get(speed).lat;
						lng = route.get(speed).lng;
					}
				} else {
					lat = route.get(route.size()).lat;
					lng = route.get(route.size()).lng;
				}
				
				pstUpdate.setDouble(1, lat);
				pstUpdate.setDouble(2, lng);
				pstUpdate.setInt(3, id);
				pstUpdate.executeUpdate();
				System.out.println("route.size() : " + route.size());
				System.out.println("pstUpdate : " + pstUpdate);
				
				EMConnection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

//		String polyline = "ejmvG{yz\\jC_ACUk@oC{@iBGOg@cAKa@BA?ABC?G?EAERQfAyADHDBJ@NNJTT`AdAxGH\\FTJVl@pAdBlDHNDNFNBRBPRjCF|B?z@Sf@KPKBMGCGY_@i@aAU[}@w@_@c@CUC_@DI?K?KAIEIIEIAIDGHCH?J?HMRYh@_AhCQ`@OVSX{@rAgGlI}CpDkE|E}G~HuE|FwApBwApBqAxBw@zAs@tAi@jAYn@Wv@Wv@Ol@Mj@Q|@OdAGd@Eh@E`@Cd@ElAClA?fADfAFzAJpANfAL|@Hh@Np@`@bB^zAHp@Z|AJh@PjALfADVDVNj@Lz@Px@Ll@p@vCTf@^p@L\\?L@LBJBHBHDHFDDDFBHBH?J?BADCFEFEDIDIBI@E@K@K?M?KCKAK?K?K@MBIBKDIDIBEHMBQBS?Q?KAIAMAMGSGUKUKMIKOSQSSQSQWQUOUMSKWIQGWGYG[E[AW?M?a@Bi@DUDYFKBSFOBSBO@OAMCMIIGIIKMIMEQEOESEWIg@G[G]G?GAGBGDAFC~@ElA?V@ZDVDPBBHRJTNPLLNHNFKz@k@Q_AIUA_@CA?qCs@I?I@IBuAbAiAf@MBYDE@a@BA?g@AE@C?C@qA^G@GDEBg@f@wAdBW\\IFIFiEnBC@iAbAEFwGdLMkE?o@?C?_@@I";
//		
//		EncodedPolyline a = new EncodedPolyline(polyline);
//		
//		List<LatLng> route = a.decodePath();
//		
//		System.out.println(route);
    }
}

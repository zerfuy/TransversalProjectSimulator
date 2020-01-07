package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.TimerTask;

import model.PostgreSQLJDBC;

public class fireEngineTimer extends TimerTask {
	
	public void run() {
//		Connection SimulatorConnection = 
//				new PostgreSQLJDBC("jdbc:postgresql://manny.db.elephantsql.com:5432/", 
//						"juynhvrm", 
//						"H-FZ4Jrenwgd_c2Xzte7HLsYJzB_6q5D").getConnection();
		
		String UpdateQuery = "UPDATE fire SET intensity = ? WHERE id = ?";
		
//		String polyline = "ejmvG{yz\\jC_ACUk@oC{@iBGOg@cAKa@BA?ABC?G?EAERQfAyADHDBJ@NNJTT`AdAxGH\\FTJVl@pAdBlDHNDNFNBRBPRjCF|B?z@Sf@KPKBMGCGY_@i@aAU[}@w@_@c@CUC_@DI?K?KAIEIIEIAIDGHCH?J?HMRYh@_AhCQ`@OVSX{@rAgGlI}CpDkE|E}G~HuE|FwApBwApBqAxBw@zAs@tAi@jAYn@Wv@Wv@Ol@Mj@Q|@OdAGd@Eh@E`@Cd@ElAClA?fADfAFzAJpANfAL|@Hh@Np@`@bB^zAHp@Z|AJh@PjALfADVDVNj@Lz@Px@Ll@p@vCTf@^p@L\\?L@LBJBHBHDHFDDDFBHBH?J?BADCFEFEDIDIBI@E@K@K?M?KCKAK?K?K@MBIBKDIDIBEHMBQBS?Q?KAIAMAMGSGUKUKMIKOSQSSQSQWQUOUMSKWIQGWGYG[E[AW?M?a@Bi@DUDYFKBSFOBSBO@OAMCMIIGIIKMIMEQEOESEWIg@G[G]G?GAGBGDAFC~@ElA?V@ZDVDPBBHRJTNPLLNHNFKz@k@Q_AIUA_@CA?qCs@I?I@IBuAbAiAf@MBYDE@a@BA?g@AE@C?C@qA^G@GDEBg@f@wAdBW\\IFIFiEnBC@iAbAEFwGdLMkE?o@?C?_@@I";
//		
//		List<LatLng> route = PolyUtil.decode(polyline);
		
		/*function decode(str, precision) {
		    var index = 0,
		        lat = 0,
		        lng = 0,
		        coordinates = [],
		        shift = 0,
		        result = 0,
		        byte = null,
		        latitude_change,
		        longitude_change,
		        factor = Math.pow(10, precision || 5);

		    while (index < str.length) {

		        // Reset shift, result, and byte
		        byte = null;
		        shift = 0;
		        result = 0;

		        do {
		            byte = str.charCodeAt(index++) - 63;
		            result |= (byte & 0x1f) << shift;
		            shift += 5;
		        } while (byte >= 0x20);

		        latitude_change = ((result & 1) ? ~(result >> 1) : (result >> 1));

		        shift = result = 0;

		        do {
		            byte = str.charCodeAt(index++) - 63;
		            result |= (byte & 0x1f) << shift;
		            shift += 5;
		        } while (byte >= 0x20);

		        longitude_change = ((result & 1) ? ~(result >> 1) : (result >> 1));

		        lat += latitude_change;
		        lng += longitude_change;

		        coordinates.push([lat / factor, lng / factor]);
		    }

		    return coordinates;
		};
		*/
    }
}

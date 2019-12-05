package controller;

import java.sql.Connection;

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
		
	}
}


package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import model.PostgreSQLJDBC;

public class Simulation {
	
	public Simulation() {
		System.out.println("Simulation initialized successfully");

		Timer timer = new Timer();
		timer.schedule(new fireTimer(), 0, 15000);
		
		//timer.schedule(new fireEngineTimer(), 0, 15000);
		
	}
}


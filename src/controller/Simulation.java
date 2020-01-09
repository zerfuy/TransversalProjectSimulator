package controller;

import java.util.Timer;

public class Simulation {
	
	public Simulation() {
		System.out.println("Simulation initialized successfully");

		Timer timer = new Timer();
		//timer.schedule(new fireTimer(), 0, 15000);
		timer.schedule(new fireEngineTimer(), 0, 15000);
		
	}
}


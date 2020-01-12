package controller;

import java.util.Timer;

public class Simulation {
	
	public Simulation() {
		System.out.println("Simulation initialized successfully");

		Timer timer = new Timer();
		timer.schedule(new FireTimer(), 0, 15000);
		timer.schedule(new FireEngineTimer(), 0, 15000);
		
	}
}


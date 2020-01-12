package launcher;

import controller.Reset;
import controller.Simulation;

public class SimulationLauncher {
	
	private static boolean reset = true;
	
	public static void main(String[] args) {
		
		if(reset) {
			Reset reset = new Reset();
		} else {
			Simulation simulation = new Simulation();
		}
		
	}
}

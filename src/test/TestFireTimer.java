package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import controller.FireTimer;
import model.FireEngine;
import model.Sensor;

public class TestFireTimer {

	@Test
	// Test the detection of trucks being on site of a fire
	public void testGetRankOnSite() {
		
		List<FireEngine> fireEngines = new ArrayList<>();
		List<Sensor> emSensors = new ArrayList<>();
		List<Sensor> simSensors = new ArrayList<>();
		
		fireEngines.add(new FireEngine(1,4,1,1));
		fireEngines.add(new FireEngine(2,2,2,2));
		fireEngines.add(new FireEngine(3,3,2,2));
		fireEngines.add(new FireEngine(4,9,3,3));
		fireEngines.add(new FireEngine(4,1,3,3));
		fireEngines.add(new FireEngine(5,2,3,3));
		fireEngines.add(new FireEngine(6,3,3,3));
		fireEngines.add(new FireEngine(7,1,5,5));
		fireEngines.add(new FireEngine(8,1,5,5));
		
		Sensor sensor1 = new Sensor(1,1,1,0,0);
		Sensor sensor2 = new Sensor(2,2,2,0,0);
		Sensor sensor3 = new Sensor(3,3,3,0,0);
		Sensor sensor4 = new Sensor(4,4,4,0,0);
		emSensors.add(sensor1);
		emSensors.add(sensor1);
		
		FireTimer fireTimer = new FireTimer();
		fireTimer.setEnvironement(emSensors, simSensors, fireEngines);
		
		assertEquals(fireTimer.getRankOnSite(sensor1), 4);
		assertEquals(fireTimer.getRankOnSite(sensor2), 5);
		assertEquals(fireTimer.getRankOnSite(sensor3), 15);
		assertEquals(fireTimer.getRankOnSite(sensor4), 0);
		
	}
	
	@Test
	// Test that the intensity of a fire always decrease when an enough rank value is on site
	public void testGetNewIntensity() {
		
		FireTimer fireTimer = new FireTimer();
		int i, j;
		boolean working = true;
		
		for(i=1;i<9;i++) {
			for(j=0;j<100;j++) {
				float newVal = fireTimer.getNewIntensity(i, i);
				if(newVal>i)
					working = false;
			}
		}
		
		assertTrue(working);
	}
	
}

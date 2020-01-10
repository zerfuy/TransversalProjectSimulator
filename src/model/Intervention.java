package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;

public class Intervention {
	
	private int id;
	private List<LatLng> route;
	private int speed;
	private int sPos;
	private double x;
	private double y;
	private String routeStr;
	
	public Intervention(int id, String route, int speed, double x, double y) {
		super();
		
		this.id = id;
		this.speed = speed;
		this.sPos = 0;
		this.x = new BigDecimal(x).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
		this.y = new BigDecimal(y).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
		
		if(route.length() > 1) {
			EncodedPolyline polyline = new EncodedPolyline(route);
			this.route = polyline.decodePath();
			this.routeStr = route;
		} else {
			this.route = null;
		}
		
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public List<LatLng> getRoute() {
		return route;
	}
	
	public void setRoute(String route) {
		if(route.length() > 1) {
			EncodedPolyline polyline = new EncodedPolyline(route);
			this.route = polyline.decodePath();
			this.routeStr = route;
		} else {
			this.route = null;
		}
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public String getRouteStr() {
		return this.routeStr;
	}
	
	public void increaseSpeed() {
		this.sPos += this.speed;
	}
	
	public int getSPos() {
		return this.sPos;
	}
	
	@Override
	public String toString() {
		return "Intervention [id=" + id + ", route: " + (route != null) + ", speed=" + speed + ", x=" + x + ", y=" + y + "]";
	}

}

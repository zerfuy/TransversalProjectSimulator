package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Sensor {

	private int id;
	private double x;
	private double y;
	private int intensity;
	private float precise_intensity;
	private int handled;
	private String UpdateQuery = "UPDATE fire SET intensity = ?, precise_intensity = ? WHERE id = ?";;
	private Connection conn;
	
	public Sensor(int id, double x, double y, int intensity, int handled) {
		super();
		this.id = id;
		this.x = new BigDecimal(x).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
		this.y = new BigDecimal(y).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
		this.intensity = intensity;
		this.handled = handled;
	}

	public Sensor(int id, int intensity, float precise_intensity) {
		super();
		this.id = id;
		this.intensity = intensity;
		this.precise_intensity = precise_intensity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getIntensity() {
		return intensity;
	}

	public void setIntensity(int intensity) {
		this.intensity = intensity;
	}
	
	public float getPreciseIntensity() {
		return precise_intensity;
	}

	public void setPreciseIntensity(float precise_intensity) {
		this.precise_intensity = precise_intensity;
		this.intensity = Math.round(precise_intensity);
		if(this.intensity == 0) {
			this.intensity = precise_intensity == 0 ? 0 : 1;
		}
	}

	public int getHandled() {
		return handled;
	}

	public void setHandled(int handled) {
		this.handled = handled;
	}
	
	public Connection getConn() {
		return this.conn;
	}
	
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	
	public void updateIntensity() {
		
		try {
			
			PreparedStatement pst = this.conn.prepareStatement(this.UpdateQuery);
			pst.setInt(1, this.getIntensity());
			pst.setFloat(2, this.getPreciseIntensity());
			pst.setInt(3, this.getId());
			pst.executeUpdate();
			System.out.println("\tIntensity update completed successfully: sensor " + this.getId() + " set to " + this.getPreciseIntensity());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isOnSite(FireEngine fireEngine) {
		
		if(Math.abs(fireEngine.getX() - this.x) > 0.0001) {
			return false;
		}
		
		if(Math.abs(fireEngine.getY() - this.y) > 0.0001) {
			return false;
		}
		
		return true;
	}

	@Override
	public String toString() {
		return "Sensor [id=" + id + ", x=" + x + ", y=" + y + ", intensity=" + intensity + ", precise_intensity="
				+ precise_intensity + ", handled=" + handled + "]";
	}
	
}

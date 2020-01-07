package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class Sensor {

	private int id;
	private double x;
	private double y;
	private int intensity;
	private int handled;
	
	public Sensor(int id, double x, double y, int intensity, int handled) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
		this.intensity = intensity;
		this.handled = handled;
	}

	public Sensor(int id2, int intensity) {
		super();
		this.id = id2;
		this.intensity = intensity;
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

	public int getHandled() {
		return handled;
	}

	public void setHandled(int handled) {
		this.handled = handled;
	}
	
	public void updateIntensity(Connection conn) {
		String UpdateQuery = "UPDATE fire SET intensity = ? WHERE id = ?";
		
		try {
			
			PreparedStatement pst = conn.prepareStatement(UpdateQuery);
			int id = new Random().nextInt(59);
			pst.setInt(1, this.getIntensity());
			pst.setInt(2, this.getId());
			System.out.println(pst.toString());
			pst.executeUpdate();
			conn.close();
			System.out.println("Intensity update completed successfully");
	        }
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Sensor [id=" + id + ", x=" + x + ", y=" + y + ", intensity=" + intensity + ", handled=" + handled + "]";
	}
	
}

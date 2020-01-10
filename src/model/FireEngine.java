package model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FireEngine {

	private int id;
	private int rank;
	private double x;
	private double y;
	
	public FireEngine(int id, int rank, double x, double y) {
		super();
		this.id = id;
		this.rank = rank;	
		this.x = new BigDecimal(x).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
		this.y = new BigDecimal(y).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
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

	@Override
	public String toString() {
		return "FireEngine [id=" + id + ", rank=" + rank + ", x=" + x + ", y=" + y + "]";
	}
	
}

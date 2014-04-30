package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.graphActivities;

public class Coordinate {

	private double xAxis;
	private double yAxis;
	public Coordinate(double xAxis, double yAxis){
		this.xAxis = xAxis;
		this.yAxis = yAxis;
	}
	public double getX() {
		return xAxis;
	}
	public void setX(double xAxis) {
		this.xAxis = xAxis;
	}
	public double getY() {
		return yAxis;
	}
	public void setY(double yAxis) {
		this.yAxis = yAxis;
	}
}

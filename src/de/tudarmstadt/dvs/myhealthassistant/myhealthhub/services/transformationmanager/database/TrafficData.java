package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database;

public class TrafficData {
	
	private String trafficDate;
	private String trafficType;
	private double xValue;
	private double yValue;
	
	
	public TrafficData(String trafficDate, String trafficType, double xValue,
			double yValue) {
		this.trafficDate = trafficDate;
		this.trafficType = trafficType;
		this.xValue = xValue;
		this.yValue = yValue;
	}
	public String getTrafficDate() {
		return trafficDate;
	}
	public void setTrafficDate(String trafficDate) {
		this.trafficDate = trafficDate;
	}
	public String getTrafficType() {
		return trafficType;
	}
	public void setTrafficType(String trafficType) {
		this.trafficType = trafficType;
	}
	public double getxValue() {
		return xValue;
	}
	public void setxValue(double xValue) {
		this.xValue = xValue;
	}
	public double getyValue() {
		return yValue;
	}
	public void setyValue(double yValue) {
		this.yValue = yValue;
	}
}

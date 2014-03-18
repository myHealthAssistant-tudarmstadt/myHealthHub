/* 
 * Copyright (C) 2014 TU Darmstadt, Hessen, Germany.
 * Department of Computer Science Databases and Distributed Systems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */ 
 
 /**
 * 
 */
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository;

/**
 * @author Christian Seeger
 *
 */
public abstract class AbstractSensorType {

	/** sensor types */
	public static String ACCELEROMETER = "accelerometer";
	public static String BALL_IN_TUBE_SENSOR = "ball_in_tube";
	public static String BLOOD_PRESSURE_SENSOR = "blood_pressure_sensor";
	public static String ECG = "ecg";
	public static String HEART_RATE_SENSOR = "heartrate";
	public static String PIR_SENSOR = "pir";
	public static String REED = "reed";
	public static String SCALE = "scale";
	public static String TEMPERATURE = "temperature";
	public static String LIGHT = "light";
		
	/** status types */
	public static int STATUS_DISCONNECTED = 0;
	public static int STATUS_CONNECTED = 1;
	public static int STATUS_LISTENING = 2;
	public static int STATUS_CONNECTING = 3;


	public final String sensorID;
	public final String sensorMAC;
	public static String sensorType;
	public static String description;
	public static String properties;
	public static String[] sensorEventTypes;
	public static int[] sensorStatusTypes;

	public AbstractSensorType(String sensorID, String sensorMAC) {
		this.sensorID = sensorID;
		this.sensorMAC = sensorMAC;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getProperties() {
		return properties;
	}
	
	public String getSensorID() {
		return sensorID;
	}

	public String getSensorMAC() {
		return sensorMAC;
	}

	public String getSensorReadingType(int i) {
		if(i<sensorEventTypes.length && i>=0) 
			return sensorEventTypes[i];
		return null;
	}

	public String[] getSensorReadingTypes() {
		return sensorEventTypes;
	}

	public String getSensorType() {
		return sensorType;
	}

	public int[] getStatusTypes() {
		return sensorStatusTypes;
	}
}
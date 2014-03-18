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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;

/**
 * @author Christian Seeger
 * 
 */
public abstract class AccSensorEvent extends SensorReadingEvent {

	public int x_mean;
	public int y_mean;
	public int z_mean;
	public int x_var;
	public int y_var;
	public int z_var;

	/**
	 * Creates an acceleration event.
	 * 
	 * @param eventID
	 *            Event ID.
	 * @param timestamp
	 *            Timestamp of event.
	 * @param producerID
	 *            ID of event producer.
	 * @param sensorType
	 *            Sensor type of event producer.
	 * @param timeOfMeasurement
	 *            Time of measurement.
	 */
	public AccSensorEvent(String eventType, String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement) {
		super(eventType, eventID, timestamp, producerID, sensorType, timeOfMeasurement);
	}

	public AccSensorEvent(String eventType, String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement, int x_mean,
			int y_mean, int z_mean, int x_var, int y_var, int z_var) {
		this(eventType, eventID, timestamp, producerID, sensorType, timeOfMeasurement);

		this.x_mean = x_mean;
		this.y_mean = y_mean;
		this.z_mean = z_mean;
		this.x_var = x_var;
		this.y_var = y_var;
		this.z_var = z_var;
	}

	/**
	 * Set acceleration reading data
	 * 
	 * @param x_mean
	 * @param y_mean
	 * @param z_mean
	 * @param x_var
	 * @param y_var
	 * @param z_var
	 */
	public void setAccData(int x_mean, int y_mean, int z_mean, int x_var,
			int y_var, int z_var) {
		this.x_mean = x_mean;
		this.y_mean = y_mean;
		this.z_mean = z_mean;
		this.x_var = x_var;
		this.y_var = y_var;
		this.z_var = z_var;
	}

	@Override
	public int describeContents() {
		return 0;
	}


}
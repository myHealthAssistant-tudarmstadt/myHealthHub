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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import android.os.Parcel;
import android.os.Parcelable;

public abstract class WeightEvent extends SensorReadingEvent {

	public static String EVENT_TYPE = SensorReadingEvent.WEIGHT;

	private String unit;
	private int weight;

	/**
	 * Creates an WEIGHT measurement event.
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
	public WeightEvent(String eventType, String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement) {
		super(eventType, eventID, timestamp, producerID, sensorType,
				timeOfMeasurement);

	}

	public WeightEvent(String eventType, String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement, int myWeight,
			String myUnit) {
		this(eventType, eventID, timestamp, producerID, sensorType, timeOfMeasurement);

		weight = myWeight;
		unit = myUnit;
	}

	/**
	 * Sets WEIGHT information
	 * 
	 * @param WEIGHT
	 *            Body WEIGHT.
	 * @param unit
	 *            Unit of measurement.
	 */
	public void setWeightData(int weight, String unit) {
		this.weight = weight;
		this.unit = unit;
	}

	/**
	 * Returns measured body WEIGHT.
	 * 
	 * @return Weight
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * Returns unit.
	 * 
	 * @return Unit
	 */
	public String getUnit() {
		return unit;
	}

}
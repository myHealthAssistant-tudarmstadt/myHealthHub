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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.environmental;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;

public abstract class AbstractEnvironmentalEvent extends SensorReadingEvent {

	public static String ENVIRONMENTAL_READING_EVENT = READING_EVENT+".environmental";

	public static String ENVIRONMENT_ACTIVITY = ENVIRONMENTAL_READING_EVENT+".environmentactivity";

	
	/* Presence readings */
	public static String PRESENCE = ENVIRONMENTAL_READING_EVENT+".presence";
	public static String OCCUPANCY = PRESENCE+".occupancy";
	public static String REDPIN = PRESENCE+".redpin";
	
	protected String location;
	protected String object;
	
	/**
	 * Constructor of a environmental sensor reading event.
	 * @param eventID Event ID.
	 * @param EVENT_TYPE Event type.
	 * @param timestamp Timestamp of event.
	 * @param producerID ID of event producer.
	 * @param sensorType Sensor type of event producer.
	 * @param timeOfMeasurement Time of measurement.
	 * @param location Location of sensor.
	 * @param object Specific opject to which sensor is attached/integrated.
	 * @param reading Sensor reading.
	 */
	public AbstractEnvironmentalEvent(String eventType, String eventID, 
	String timestamp, String producerID, String sensorType,
	String timeOfMeasurement, String location, String object) {
		super(eventType, eventID, timestamp, producerID, 
				sensorType, timeOfMeasurement);
		this.location = location;
		this.object = object;	
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getObject() {
		return object;
	}

	public abstract String getValue();
}
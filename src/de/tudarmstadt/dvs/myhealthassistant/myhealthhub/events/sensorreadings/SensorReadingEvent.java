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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;

/**
 * @author Christian Seeger
 *
 */
public abstract class SensorReadingEvent extends Event {
	
	public static String READING_EVENT = EVENT_ROOT+".Reading";
		
	/** .physiological.cardiovascular */
	public static String HEART_RATE = READING_EVENT+".Physiological.Cardiovascular.ECG.HeartRate";
	public static String ECG_STREAM = READING_EVENT+".Physiological.Cardiovascular.ECG.ECGStream";
	//public static String RR_INTERVAL = READING_EVENT+".Physiological.Cardiovascular.ECG.RRInterval";
	public static String BLOOD_PRESSURE = READING_EVENT+".Physiological.Cardiovascular.BloodPressure";
	public static String HR_FIDELITY = READING_EVENT+".Physiological.Cardiovascular.ECG.HRFidelity";
	
	/** .physiological.calorieexpenditure */
	//public static String CALORIE_EXPENDITURE = READING_EVENT+".Physiological.CalorieExpenditure";
		
	/** .environmental */
	public static String ENVIRONMENTAL = READING_EVENT+".Environmental";
		
	/** .environmental.presence */
	public static String OCCUPANCY = ENVIRONMENTAL + ".Occupancy";
	
	/** .environmental.raw */
	public static String ENVIRONMENTAL_RAW = ENVIRONMENTAL+".Raw";
	public static String AMBIENT_LIGHT = ENVIRONMENTAL_RAW+".AmbientLight";
	public static String AMBIENT_PRESSURE = ENVIRONMENTAL_RAW+".AmbientPressure";
	public static String BALL_IN_TUBE = ENVIRONMENTAL_RAW+".BallInTube";
	public static String HUMIDITY = ENVIRONMENTAL_RAW+".Humidity";
	public static String PROXIMITY = ENVIRONMENTAL_RAW+".Proximity";
	public static String PASSIVE_INFRARED = ENVIRONMENTAL_RAW+".PassiveInfrared";
	public static String REED_SWITCH = ENVIRONMENTAL_RAW+".ReedSwitch";
	public static String ROOM_TEMPERATURE = ENVIRONMENTAL_RAW+".RoomTemperature";
	
	
	/** .physical */
	public static String PHYSICAL = READING_EVENT+".Physical";
	private static String INERTIAL = PHYSICAL+".Inertial";
	public static String ACCELEROMETER = INERTIAL+".Accelerometer";
	public static String ACCELEROMETER_KNEE = ACCELEROMETER+".Knee";
	public static String ACCELEROMETER_ANKLE = ACCELEROMETER+".Ankle";
	public static String ACCELEROMETER_WRIST = ACCELEROMETER+".Wrist";
	public static String ACCELEROMETER_CHEST = ACCELEROMETER+".Chest";
	public static String COUNT_ACCELEROMETER = INERTIAL+".CountAccelerometer";
	public static String COUNT_ACCELEROMETER_ANKLE = COUNT_ACCELEROMETER+".Ankle";
	public static String COUNT_ACCELEROMETER_WRIST = COUNT_ACCELEROMETER+".Wrist";
	public static String COUNT_ACCELEROMETER_CHEST = COUNT_ACCELEROMETER+".Chest";
	public static String GYROSCOPE = INERTIAL+".Gyroscope";
	public static String MAGNETIC_FIELD = INERTIAL+".MagneticField";
	
	public static String WEIGHT = PHYSICAL+".Weight";
	public static String WEIGHT_IN_KG = WEIGHT+".Kg";
	public static String WEIGHT_IN_LBS = WEIGHT+".Lbs";
	
	public static String BODY_TEMPERATURE = PHYSICAL+".BodyTemperature";
	public static String BODY_TEMPERATURE_IN_CELSIUS = BODY_TEMPERATURE+".Celsius";
	public static String BODY_TEMPERATURE_IN_FAHRENHEIT = BODY_TEMPERATURE+".Fahrenheit";
	
	/** .physical.activity */
	public static String ACTIVITY = PHYSICAL+".Activity";
	public static String ACTIVITY_DAILY = ACTIVITY+".Daily";
	public static String ACTIVITY_SSWRC = ACTIVITY_DAILY; // In order to stay compatible
	public static String ACTIVITY_GYM = ACTIVITY+".Gym";
	public static String ACTIVITY_GYM_WORKOUT = ACTIVITY+".Gymworkout";
	public static String ACTIVITY_FITNESSTRAIL = ACTIVITY+".FitnessTrail";
	public static String ACTIVITY_REHA = ACTIVITY+".Reha";
	public static String EXERCISE_SET = ACTIVITY+".ExerciseSet";

	
	public String sensorType;
	public String timeOfMeasurement;
	
	/**
	 * Constructor of a sensor reading event.
	 * @param eventID Event ID.
	 * @param EVENT_TYPE Event type.
	 * @param timestamp Timestamp of event.
	 * @param producerID ID of event producer.
	 * @param sensorType Sensor type of event producer.
	 * @param timeOfMeasurement Time of measurement.
	 */
	public SensorReadingEvent(String eventType, String eventID, String timestamp, String producerID, String sensorType,
			String timeOfMeasurement) {
		super(eventType, eventID, timestamp, producerID);
				
		this.sensorType = sensorType;
		this.timeOfMeasurement = timeOfMeasurement;
	}
	
	/**
	 * Returns the sensor type.
	 * @return sensor type
	 */
	public String getSensorType() {
		return sensorType;
	}
	
	/**
	 * Returns the time of measurement.
	 * @return time of measurement
	 */
	public String getTimeOfMeasurement() {
		return timeOfMeasurement;
	}

	
}
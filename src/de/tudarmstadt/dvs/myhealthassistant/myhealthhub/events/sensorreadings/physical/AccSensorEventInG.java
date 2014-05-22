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
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Hieu Ha
 * 
 */
public class AccSensorEventInG extends SensorReadingEvent {

	public static String EVENT_TYPE = SensorReadingEvent.ACCELEROMETER_IN_G;

	public double x_mean;
	public double y_mean;
	public double z_mean;
	public double x_var;
	public double y_var;
	public double z_var;

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
	public AccSensorEventInG(String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement) {
		super(EVENT_TYPE, eventID, timestamp, producerID, sensorType, timeOfMeasurement);
	}

	public AccSensorEventInG(String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement, double x_mean,
			double y_mean, double z_mean, double x_var, double y_var, double z_var) {
		this(eventID, timestamp, producerID, sensorType, timeOfMeasurement);

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

	public static final Parcelable.Creator<AccSensorEventInG> CREATOR = new Parcelable.Creator<AccSensorEventInG>() {

		@Override
		public AccSensorEventInG createFromParcel(Parcel source) {
			return new AccSensorEventInG(source);
		}

		@Override
		public AccSensorEventInG[] newArray(int size) {
			return new AccSensorEventInG[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(eventType);
		dest.writeString(eventID);
		dest.writeString(timestamp);
		dest.writeString(producerID);
		dest.writeString(sensorType);
		dest.writeString(timeOfMeasurement);
		dest.writeDouble(x_mean);
		dest.writeDouble(y_mean);
		dest.writeDouble(z_mean);
		dest.writeDouble(x_var);
		dest.writeDouble(y_var);
		dest.writeDouble(z_var);
	}

	private AccSensorEventInG(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString(),
				source.readString(), source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		x_mean = source.readDouble();
		y_mean = source.readDouble();
		z_mean = source.readDouble();
		x_var = source.readDouble();
		y_var = source.readDouble();
		z_var = source.readDouble();
	}
}
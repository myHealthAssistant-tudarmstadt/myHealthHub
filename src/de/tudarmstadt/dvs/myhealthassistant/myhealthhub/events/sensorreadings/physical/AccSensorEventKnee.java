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
 * @author Christian Seeger
 * 
 */
public class AccSensorEventKnee extends AccSensorEvent {

	public static String EVENT_TYPE = SensorReadingEvent.ACCELEROMETER_KNEE;


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
	public AccSensorEventKnee(String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement) {
		super(EVENT_TYPE, eventID, timestamp, producerID, sensorType, timeOfMeasurement);
	}

	public AccSensorEventKnee(String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement, int x_mean,
			int y_mean, int z_mean, int x_var, int y_var, int z_var) {
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

	public static final Parcelable.Creator<AccSensorEventKnee> CREATOR = new Parcelable.Creator<AccSensorEventKnee>() {

		@Override
		public AccSensorEventKnee createFromParcel(Parcel source) {
			return new AccSensorEventKnee(source);
		}

		@Override
		public AccSensorEventKnee[] newArray(int size) {
			return new AccSensorEventKnee[size];
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
		dest.writeInt(x_mean);
		dest.writeInt(y_mean);
		dest.writeInt(z_mean);
		dest.writeInt(x_var);
		dest.writeInt(y_var);
		dest.writeInt(z_var);
	}

	private AccSensorEventKnee(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString(),
				source.readString(), source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		x_mean = source.readInt();
		y_mean = source.readInt();
		z_mean = source.readInt();
		x_var = source.readInt();
		y_var = source.readInt();
		z_var = source.readInt();
	}
}
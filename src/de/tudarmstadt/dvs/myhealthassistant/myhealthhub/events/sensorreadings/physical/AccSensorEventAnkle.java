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
public class AccSensorEventAnkle extends AccPeakSensorEvent {

	public static String EVENT_TYPE = SensorReadingEvent.ACCELEROMETER_ANKLE;


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
	public AccSensorEventAnkle(String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement) {
		super(EVENT_TYPE, eventID, timestamp, producerID, sensorType, timeOfMeasurement);
	}

	public AccSensorEventAnkle(String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement, int x_mean,
			int y_mean, int z_mean, int x_var, int y_var, int z_var,
			boolean x_peak, boolean y_peak, boolean z_peak) {
		this(eventID, timestamp, producerID, sensorType, timeOfMeasurement);

		this.x_mean = x_mean;
		this.y_mean = y_mean;
		this.z_mean = z_mean;
		this.x_var = x_var;
		this.y_var = y_var;
		this.z_var = z_var;
		this.x_peak = x_peak;
		this.y_peak = y_peak;
		this.z_peak = z_peak;
	}



	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<AccSensorEventAnkle> CREATOR = new Parcelable.Creator<AccSensorEventAnkle>() {

		@Override
		public AccSensorEventAnkle createFromParcel(Parcel source) {
			return new AccSensorEventAnkle(source);
		}

		@Override
		public AccSensorEventAnkle[] newArray(int size) {
			return new AccSensorEventAnkle[size];
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
		dest.writeInt(x_peak ? 1 : 0);
		dest.writeInt(y_peak ? 1 : 0);
		dest.writeInt(z_peak ? 1 : 0);
	}

	private AccSensorEventAnkle(final Parcel source) {
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
		x_peak = (source.readInt()==1) ? true : false;
		y_peak = (source.readInt()==1) ? true : false;
		z_peak = (source.readInt()==1) ? true : false;
	}
}
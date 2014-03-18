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

/**
 * @author Christian Seeger
 * 
 */
public class CountAccSensorEvent extends SensorReadingEvent {

	public static String EVENT_TYPE = SensorReadingEvent.COUNT_ACCELEROMETER;

	public int x_peak_pos;
	public int x_peak_neg;
	public int y_peak_pos;
	public int y_peak_neg;
	public int z_peak_pos;
	public int z_peak_neg;
	public int x_mean;
	public int y_mean;
	public int z_mean;
	public int x_var;
	public int y_var;
	public int z_var;

	/**
	 * Creates an acceleration and counting event.
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
	public CountAccSensorEvent(String eventID, String timestamp,
			String producerID, String sensorType, String timeOfMeasurement) {
		super(EVENT_TYPE, eventID, timestamp, producerID, sensorType, timeOfMeasurement);

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

	/**
	 * Set counting data
	 * 
	 * @param x_peak_pos
	 * @param x_peak_neg
	 * @param y_peak_pos
	 * @param y_peak_neg
	 * @param z_peak_pos
	 * @param z_peak_neg
	 */
	public void setCountData(int x_peak_pos, int x_peak_neg, int y_peak_pos,
			int y_peak_neg, int z_peak_pos, int z_peak_neg) {
		this.x_peak_pos = x_peak_pos;
		this.x_peak_neg = x_peak_neg;
		this.y_peak_pos = y_peak_pos;
		this.y_peak_neg = y_peak_neg;
		this.z_peak_pos = z_peak_pos;
		this.z_peak_neg = z_peak_neg;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<CountAccSensorEvent> CREATOR = new Parcelable.Creator<CountAccSensorEvent>() {

		@Override
		public CountAccSensorEvent createFromParcel(Parcel source) {
			return new CountAccSensorEvent(source);
		}

		@Override
		public CountAccSensorEvent[] newArray(int size) {
			return new CountAccSensorEvent[size];
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
		dest.writeInt(x_peak_pos);
		dest.writeInt(x_peak_neg);
		dest.writeInt(y_peak_pos);
		dest.writeInt(y_peak_neg);
		dest.writeInt(z_peak_pos);
		dest.writeInt(z_peak_neg);
	}

	private CountAccSensorEvent(final Parcel source) {
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
		x_peak_pos = source.readInt();
		x_peak_neg = source.readInt();
		y_peak_pos = source.readInt();
		y_peak_neg = source.readInt();
		z_peak_pos = source.readInt();
		z_peak_neg = source.readInt();
	}

}
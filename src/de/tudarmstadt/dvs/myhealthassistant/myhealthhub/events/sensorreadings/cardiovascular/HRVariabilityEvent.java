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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.cardiovascular;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Ha (copied from HeartRateEvent.java)
 * 
 */
public class HRVariabilityEvent extends SensorReadingEvent {

	public static String EVENT_TYPE = SensorReadingEvent.HR_VARIABILITY;

	public int value;
	public int batteryLevel;
	public int beatCounter;
	public long hrvValue;

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
	 * @param value
	 *            Heart rate value.
	 */
	public HRVariabilityEvent(String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement, int heartRateValue, long hrvValue) {
		super(EVENT_TYPE, eventID, timestamp, producerID, sensorType,
				timeOfMeasurement);

		this.value = heartRateValue;
		this.hrvValue = hrvValue;
	}

	public HRVariabilityEvent(String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement, int value,
			int batteryLevel, int beatCounter, int hrvValue) {
		this(eventID, timestamp, producerID, sensorType, timeOfMeasurement,
				value, hrvValue);

		this.value = value;
		this.batteryLevel = batteryLevel;
		this.beatCounter = beatCounter;
		this.hrvValue = hrvValue;
	}

	public int getValue() {
		return value;
	}
	
	public long getHrV() {
		return hrvValue;
	}

	public static final Parcelable.Creator<HRVariabilityEvent> CREATOR = new Parcelable.Creator<HRVariabilityEvent>() {

		@Override
		public HRVariabilityEvent createFromParcel(Parcel source) {
			return new HRVariabilityEvent(source);
		}

		@Override
		public HRVariabilityEvent[] newArray(int size) {
			return new HRVariabilityEvent[size];
		}

	};

	private HRVariabilityEvent(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(),
				source.readString(), source.readString(), source.readString());
		readFromParcel(source);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(eventType);
		dest.writeString(eventID);
		dest.writeString(timestamp);
		dest.writeString(producerID);
		dest.writeString(sensorType);
		dest.writeString(timeOfMeasurement);
		dest.writeInt(value);
		dest.writeInt(batteryLevel);
		dest.writeInt(beatCounter);
		dest.writeLong(hrvValue);
	}

	public void readFromParcel(final Parcel source) {
		value = source.readInt();
		batteryLevel = source.readInt();
		beatCounter = source.readInt();
		hrvValue = source.readLong();
	}

	@Override
	public int describeContents() {
		return 0;
	}

}
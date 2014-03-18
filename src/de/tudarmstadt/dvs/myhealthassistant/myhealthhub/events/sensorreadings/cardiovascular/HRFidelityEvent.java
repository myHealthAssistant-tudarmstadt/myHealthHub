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
public class HRFidelityEvent extends SensorReadingEvent {

	public static String EVENT_TYPE = SensorReadingEvent.HR_FIDELITY;

	public int value;
	public int batteryLevel;
	public int beatCounter;
	public int fidelityLevel;

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
	public HRFidelityEvent(String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement, int heartRateValue, int fidelityValue) {
		super(EVENT_TYPE, eventID, timestamp, producerID, sensorType,
				timeOfMeasurement);

		this.value = heartRateValue;
		this.fidelityLevel = fidelityValue;
	}

	public HRFidelityEvent(String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement, int value,
			int batteryLevel, int beatCounter, int fidelityValue) {
		this(eventID, timestamp, producerID, sensorType, timeOfMeasurement,
				value, fidelityValue);

		this.batteryLevel = batteryLevel;
		this.beatCounter = beatCounter;
		this.fidelityLevel = fidelityValue;
	}

	public int getValue() {
		return value;
	}
	
	public int getFidelity() {
		return fidelityLevel;
	}

	public static final Parcelable.Creator<HRFidelityEvent> CREATOR = new Parcelable.Creator<HRFidelityEvent>() {

		@Override
		public HRFidelityEvent createFromParcel(Parcel source) {
			return new HRFidelityEvent(source);
		}

		@Override
		public HRFidelityEvent[] newArray(int size) {
			return new HRFidelityEvent[size];
		}

	};

	private HRFidelityEvent(final Parcel source) {
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
		dest.writeInt(fidelityLevel);
	}

	public void readFromParcel(final Parcel source) {
		value = source.readInt();
		batteryLevel = source.readInt();
		beatCounter = source.readInt();
		fidelityLevel = source.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

}
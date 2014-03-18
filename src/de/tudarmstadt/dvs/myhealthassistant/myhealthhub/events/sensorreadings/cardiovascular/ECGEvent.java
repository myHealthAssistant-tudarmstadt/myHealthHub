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
 * @author Christian Seeger
 * 
 */
public class ECGEvent extends SensorReadingEvent {

	public static String EVENT_TYPE = SensorReadingEvent.ECG_STREAM;

	private int[] ecgValues;
	private int samplingRate;

	/**
	 * Creates an ECG stream event.
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
	public ECGEvent(String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement) {
		super(EVENT_TYPE, eventID, timestamp, producerID, sensorType, timeOfMeasurement);
	}

	/**
	 * Creates an ECG stream event.
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
	 * @param ecgValues
	 *            ECG values.
	 * @param samplingRate
	 *            Sampling rate.
	 */
	public ECGEvent(String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement, int[] ecgValues,
			int samplingRate) {

		this(eventID, timestamp, producerID, sensorType, timeOfMeasurement);

		this.ecgValues = ecgValues;
		this.samplingRate = samplingRate;
	}

	public int[] getEcgValues() {
		return ecgValues;
	}

	public int getSamplingRate() {
		return samplingRate;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<ECGEvent> CREATOR = new Parcelable.Creator<ECGEvent>() {

		@Override
		public ECGEvent createFromParcel(Parcel source) {
			return new ECGEvent(source);
		}

		@Override
		public ECGEvent[] newArray(int size) {
			return new ECGEvent[size];
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
		dest.writeInt(samplingRate);
		dest.writeIntArray(ecgValues);
	}

	private ECGEvent(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString(),
				source.readString(), source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		samplingRate = source.readInt();
		ecgValues = source.createIntArray();
	}
}
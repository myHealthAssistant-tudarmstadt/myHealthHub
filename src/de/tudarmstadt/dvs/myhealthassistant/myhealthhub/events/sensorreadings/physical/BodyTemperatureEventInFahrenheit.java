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


import android.os.Parcel;
import android.os.Parcelable;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;

/**
 * @author Christian Seeger
 * 
 */
public class BodyTemperatureEventInFahrenheit extends SensorReadingEvent {

	public static String EVENT_TYPE = SensorReadingEvent.BODY_TEMPERATURE_IN_FAHRENHEIT;

	public int bodyTemperature;

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
	protected BodyTemperatureEventInFahrenheit(String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement) {
		super(EVENT_TYPE, eventID, timestamp, producerID, sensorType, timeOfMeasurement);
	}

	/**
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
	 * @param bodyTemperature
	 * 			  Body temperature in Fahrenheit
	 */
	public BodyTemperatureEventInFahrenheit(String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement, int bodyTemperature) {
		this(eventID, timestamp, producerID, sensorType, timeOfMeasurement);

		this.bodyTemperature = bodyTemperature;
	}

	public int getTemperature() {
		return bodyTemperature;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<BodyTemperatureEventInFahrenheit> CREATOR = new Parcelable.Creator<BodyTemperatureEventInFahrenheit>() {

		@Override
		public BodyTemperatureEventInFahrenheit createFromParcel(Parcel source) {
			return new BodyTemperatureEventInFahrenheit(source);
		}

		@Override
		public BodyTemperatureEventInFahrenheit[] newArray(int size) {
			return new BodyTemperatureEventInFahrenheit[size];
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
		dest.writeInt(bodyTemperature);
	}

	private BodyTemperatureEventInFahrenheit(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString(),
				source.readString(), source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		bodyTemperature = source.readInt();
	}
}
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

public class WeightEventInLbs extends WeightEvent {

	public static String EVENT_TYPE = SensorReadingEvent.WEIGHT_IN_LBS;

	private String unit;
	private int weight;

	/**
	 * Creates an WEIGHT measurement event.
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
	public WeightEventInLbs(String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement) {
		super(EVENT_TYPE, eventID, timestamp, producerID, sensorType,
				timeOfMeasurement);
		
		unit = "1/10 lbs";
	}

	public WeightEventInLbs(String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement, int myWeight) {
		this(eventID, timestamp, producerID, sensorType, timeOfMeasurement);

		weight = myWeight;		
	}

	/**
	 * Sets WEIGHT information
	 * 
	 * @param WEIGHT
	 *            Body WEIGHT.
	 * @param unit
	 *            Unit of measurement.
	 */
	public void setWeightData(int weight, String unit) {
		this.weight = weight;
		this.unit = unit;
	}

	/**
	 * Returns measured body WEIGHT.
	 * 
	 * @return Weight
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * Returns unit.
	 * 
	 * @return Unit
	 */
	public String getUnit() {
		return unit;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<WeightEventInLbs> CREATOR = new Parcelable.Creator<WeightEventInLbs>() {

		@Override
		public WeightEventInLbs createFromParcel(Parcel source) {
			return new WeightEventInLbs(source);
		}

		@Override
		public WeightEventInLbs[] newArray(int size) {
			return new WeightEventInLbs[size];
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
		dest.writeInt(weight);
		dest.writeString(unit);
	}

	private WeightEventInLbs(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString(),
				source.readString(), source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		weight = source.readInt();
		unit = source.readString();
	}

}
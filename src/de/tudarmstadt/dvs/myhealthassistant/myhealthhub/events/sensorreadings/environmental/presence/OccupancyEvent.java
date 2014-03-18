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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.environmental.presence;

import android.os.Parcel;
import android.os.Parcelable;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.environmental.AbstractEnvironmentalEvent;

/**
 * @author Christian Seeger
 * 
 */
public class OccupancyEvent extends AbstractEnvironmentalEvent {

	private String sensorName;
	private boolean isOccupied;
	
	public static String EVENT_TYPE = SensorReadingEvent.OCCUPANCY;

	public OccupancyEvent(String eventID, String timestamp, String producerID,
			String sensorType, String timeOfMeasurement, String location,
			String object, String sensorName, boolean reading) {
		super(EVENT_TYPE, eventID, timestamp, producerID, sensorType,
				timeOfMeasurement, location, object);
		this.sensorName = sensorName;
		this.isOccupied = reading;
	}
	
	public String getSensorName() {
		return sensorName;
	}
	
	@Override
	public String getValue() {
		return (isOccupied ? "true" : "false");
	}
	
	public boolean isOccupied() {
		return isOccupied;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<OccupancyEvent> CREATOR = new Parcelable.Creator<OccupancyEvent>() {

		@Override
		public OccupancyEvent createFromParcel(Parcel source) {
			return new OccupancyEvent(source);
		}

		@Override
		public OccupancyEvent[] newArray(int size) {
			return new OccupancyEvent[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(EVENT_TYPE);
		dest.writeString(eventID);
		dest.writeString(timestamp);
		dest.writeString(producerID);
		dest.writeString(sensorType);
		dest.writeString(timeOfMeasurement);
		dest.writeString(location);
		dest.writeString(object);
		dest.writeString(sensorName);
		dest.writeInt(isOccupied ? 1 : 0);
	}

	private OccupancyEvent(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(),
				source.readString(), source.readString(), source.readString(),
				source.readString(), source.readString());
		sensorName = source.readString();
		isOccupied = source.readInt() == 1 ? true : false;
	}


}
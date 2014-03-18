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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.environmental.presence;

import android.os.Parcel;
import android.os.Parcelable;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.environmental.AbstractEnvironmentalEvent;

/**
 * @author Christian Seeger
 * 
 */
public class RedpinLocationEvent extends AbstractEnvironmentalEvent {

	public static String EVENT_TYPE = AbstractEnvironmentalEvent.REDPIN;

	public String mapName = "";
	public String mapURL = "";
	public int mapXcord = 0;
	public int mapYcord = 0;
	public int accuracy = 0;

	public RedpinLocationEvent(String eventID, String timestamp,
			String producerID, String sensorType, String timeOfMeasurement,
			String location) {
		super(EVENT_TYPE, eventID, timestamp, producerID, sensorType, timeOfMeasurement,
				location, "REDPIN");

	}

	public void setRedpinData(String mapName, String mapURL, int mapXcord,
			int mapYcord, int accuracy) {
		this.mapName = mapName;
		this.mapURL = mapURL;
		this.mapXcord = mapXcord;
		this.mapYcord = mapYcord;
		this.accuracy = accuracy;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<RedpinLocationEvent> CREATOR = new Parcelable.Creator<RedpinLocationEvent>() {

		@Override
		public RedpinLocationEvent createFromParcel(Parcel source) {
			return new RedpinLocationEvent(source);
		}

		@Override
		public RedpinLocationEvent[] newArray(int size) {
			return new RedpinLocationEvent[size];
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
		dest.writeString(location);
		dest.writeString(object);
		dest.writeString(mapName);
		dest.writeString(mapURL);
		dest.writeInt(mapXcord);
		dest.writeInt(mapYcord);
		dest.writeInt(accuracy);
	}

	private RedpinLocationEvent(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString(),
				source.readString(), source.readString(), source.readString(),
				source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		mapName = source.readString();
		mapURL = source.readString();
		mapXcord = source.readInt();
		mapYcord = source.readInt();
		accuracy = source.readInt();
	}

	@Override
	public String getValue() {
		return "Present at loaction: " + location;
	}
}
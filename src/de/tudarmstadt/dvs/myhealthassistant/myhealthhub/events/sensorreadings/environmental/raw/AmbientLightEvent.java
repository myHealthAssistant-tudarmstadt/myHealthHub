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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.environmental.raw;

import android.os.Parcel;
import android.os.Parcelable;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.environmental.AbstractEnvironmentalEvent;

/**
 * @author Christian Seeger
 * 
 */
public class AmbientLightEvent extends AbstractEnvironmentalEvent {

	public static String EVENT_TYPE = SensorReadingEvent.AMBIENT_LIGHT;

	public static String UNIT_LUMEN = "lumen";
	public static String UNIT_LUX = "lux";

	private Float lightvalue;
	private String unit;

	public AmbientLightEvent(String eventID, String timestamp,
			String producerID, String sensorType, String timeOfMeasurement,
			String location, String object, Float lightvalue, String unit) {
		super(EVENT_TYPE, eventID, timestamp, producerID, sensorType, timeOfMeasurement,
				location, object);

		this.lightvalue = lightvalue;
		this.unit = unit;
	}

	public Float getAmbientLight() {
		return lightvalue;
	}

	public String getUnit() {
		return unit;
	}

	@Override
	public String getValue() {
		return lightvalue + "";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<AmbientLightEvent> CREATOR = new Parcelable.Creator<AmbientLightEvent>() {

		@Override
		public AmbientLightEvent createFromParcel(Parcel source) {
			return new AmbientLightEvent(source);
		}

		@Override
		public AmbientLightEvent[] newArray(int size) {
			return new AmbientLightEvent[size];
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
		dest.writeFloat(lightvalue);
		dest.writeString(unit);
	}

	private AmbientLightEvent(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString(),
				source.readString(), source.readString(), source.readString(),
				source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		lightvalue = source.readFloat();
		unit = source.readString();
	}
}
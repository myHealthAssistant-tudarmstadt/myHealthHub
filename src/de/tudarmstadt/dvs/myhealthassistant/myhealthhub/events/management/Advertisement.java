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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Christian Seeger
 *
 */
public class Advertisement extends ManagementEvent {

	public static String EVENT_TYPE = ADVERTISEMENT;
	
	private String packageName;
	private String advertisedEventType;
	private String JSONEncodedProperties;
	
	public Advertisement(String eventID, String timestamp,
			String producerID, String packageName, String advertisedEventType, String JSONEncodedProperties) {
		super(ADVERTISEMENT, eventID, timestamp, producerID);
		this.packageName = packageName;
		this.advertisedEventType = advertisedEventType;
		this.JSONEncodedProperties = JSONEncodedProperties;
	}



	public String getPackageName() {
		return packageName;
	}



	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}



	public String getAdvertisedEventType() {
		return advertisedEventType;
	}



	public void setAdvertisedEventType(String advertisedEventType) {
		this.advertisedEventType = advertisedEventType;
	}



	public String getJSONEncodedProperties() {
		return JSONEncodedProperties;
	}



	public void setJSONEncodedProperties(String jSONEncodedProperties) {
		JSONEncodedProperties = jSONEncodedProperties;
	}



	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<Advertisement> CREATOR = new Parcelable.Creator<Advertisement>() {

		public Advertisement createFromParcel(Parcel source) {
			return new Advertisement(source);
		}

		public Advertisement[] newArray(int size) {
			return new Advertisement[size];
		}
	};

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(eventType);
		dest.writeString(eventID);
		dest.writeString(timestamp);
		dest.writeString(producerID);
		dest.writeString(packageName);
		dest.writeString(advertisedEventType);
		dest.writeString(JSONEncodedProperties);
	}

	private Advertisement(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		packageName = source.readString();
		advertisedEventType = source.readString();
		JSONEncodedProperties = source.readString();
	}

}
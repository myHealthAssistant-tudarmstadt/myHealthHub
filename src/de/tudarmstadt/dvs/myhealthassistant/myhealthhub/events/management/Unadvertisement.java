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
public class Unadvertisement extends ManagementEvent {

	public static String EVENT_TYPE = UNADVERTISEMENT;
	
	private String packageName;
	private String unadvertisedEventType;
	
	public Unadvertisement(String eventID, String timestamp,
			String producerID, String packageName, String unadvertisedEventType) {
		super(UNADVERTISEMENT, eventID, timestamp, producerID);
		this.packageName = packageName;
		this.unadvertisedEventType = unadvertisedEventType;
	}



	public String getPackageName() {
		return packageName;
	}



	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}



	public String getUnadvertisedEventType() {
		return unadvertisedEventType;
	}



	public void setUnadvertisedEventType(String unadvertisedEventType) {
		this.unadvertisedEventType = unadvertisedEventType;
	}



	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<Unadvertisement> CREATOR = new Parcelable.Creator<Unadvertisement>() {

		public Unadvertisement createFromParcel(Parcel source) {
			return new Unadvertisement(source);
		}

		public Unadvertisement[] newArray(int size) {
			return new Unadvertisement[size];
		}
	};

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(eventType);
		dest.writeString(eventID);
		dest.writeString(timestamp);
		dest.writeString(producerID);
		dest.writeString(packageName);
		dest.writeString(unadvertisedEventType);
	}

	private Unadvertisement(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		packageName = source.readString();
		unadvertisedEventType = source.readString();
	}
}
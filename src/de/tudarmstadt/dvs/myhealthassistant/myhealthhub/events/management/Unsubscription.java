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
 * @author chris
 *
 */
public class Unsubscription  extends ManagementEvent {

	public static String EVENT_TYPE = UNSUBSCRIPTION;
	
	private String packageName;
	private String unsubscriptionSensorReadings;

	public Unsubscription(String eventID, String timestamp,
			String producerID, String packageName, String unsubscriptionSensorReadings) {
		super(UNSUBSCRIPTION, eventID, timestamp, producerID);
		this.packageName = packageName;
		this.unsubscriptionSensorReadings = unsubscriptionSensorReadings;
	}	

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}




	public String getUnsubscriptionSensorReadings() {
		return unsubscriptionSensorReadings;
	}




	public void setUnsubscriptionSensorReadings(String unsubscriptionSensorReadings) {
		this.unsubscriptionSensorReadings = unsubscriptionSensorReadings;
	}




	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<Unsubscription> CREATOR = new Parcelable.Creator<Unsubscription>() {

		public Unsubscription createFromParcel(Parcel source) {
			return new Unsubscription(source);
		}

		public Unsubscription[] newArray(int size) {
			return new Unsubscription[size];
		}
	};

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(eventType);
		dest.writeString(eventID);
		dest.writeString(timestamp);
		dest.writeString(producerID);
		dest.writeString(packageName);
		dest.writeString(unsubscriptionSensorReadings);
	}

	private Unsubscription(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		packageName = source.readString();
		unsubscriptionSensorReadings = source.readString();
	}
}
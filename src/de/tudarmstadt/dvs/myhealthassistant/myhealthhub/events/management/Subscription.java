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
 * Implements an event subscription.
 * @author chris
 *
 */
public class Subscription extends ManagementEvent {
	
	public static String EVENT_TYPE = SUBSCRIPITON;
	
	private String packageName;
	private String subscriptionSensorReadings;
	
	public Subscription(String eventID, String timestamp,
			String producerID, String packageName, String subscriptionSensorReadings) {
		super(SUBSCRIPITON, eventID, timestamp, producerID);
		this.packageName = packageName;
		this.subscriptionSensorReadings = subscriptionSensorReadings;
	}

	

	public String getPackageName() {
		return packageName;
	}



	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}



	public String getSubscriptionSensorReadings() {
		return subscriptionSensorReadings;
	}



	public void setSubscriptionSensorReadings(String subscriptionSensorReadings) {
		this.subscriptionSensorReadings = subscriptionSensorReadings;
	}



	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<Subscription> CREATOR = new Parcelable.Creator<Subscription>() {

		public Subscription createFromParcel(Parcel source) {
			return new Subscription(source);
		}

		public Subscription[] newArray(int size) {
			return new Subscription[size];
		}
	};

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(eventType);
		dest.writeString(eventID);
		dest.writeString(timestamp);
		dest.writeString(producerID);
		dest.writeString(packageName);
		dest.writeString(subscriptionSensorReadings);
	}

	private Subscription(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		packageName = source.readString();
		subscriptionSensorReadings = source.readString();
	}
}
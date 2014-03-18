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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.adapter.MApplication;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

public class Announcement extends ManagementEvent{
	
	public static String EVENT_TYPE = ANNOUNCEMENT;
	
	private String transmittedEventType;
	private String packageName;
	private int announcement;

	public static final int DATABASE_SHOW_WITH_ALL_SUBSCRIPTIONS_AND_UNSUBSCRIPTIONS = 99;
	public static final int DATABASE_CLEAR_ALL = 100;

	public static final int SENSOR_DISCONNECTED = 16;
	public static final int SENSOR_CONNECTED = 15;
	public static final int SENSOR_CONNECTING = 17;

	public static final int SUBSCRIPTOPN_SUBSCRIBER_ALREADY_IN_LIST = 14;
	public static final int UNADVERTISMENT_SUCCESSFULL = 13;
	public static final int ALL_AVAILABLE_EVENT_TYPES = 12;
	public static final int GET_ALL_AVAILABLE_EVENT_TYPES = 11;
	public static final int INVALID_EVENT_TYPE_ARGUMENTS = 10;
	public static final int ADVERTISMENT_SUCCESSFULL = 9;
	public static final int ADVERTSIMENT_UNSUCESSFULL_PRODUCER_ALREADY_IN_LIST = 8;
	public static final int SECURITY_PERMISSION_REMOVED = 7;
	public static final int EVENT_TYPE_DOES_NOT_MATCH_NAME_CONVENTION = 6;
	public static final int MANAGEMENTEVENT_UNSUCCESSFULL_APPLICATION_NOT_INSTALLED = 5; // not addressable either
	public static final int SUBSCRIPTION_UNSUCCESSFULL_NO_PERMISSION = 4;
	public static final int SUBSCRIPTION_UNSUCCESSFULL_INVALID_EVENT_TYPE = 3;
	public static final int SUBSCRIPTION_SUCCESSFULL = 2;

	public static final int UNADVERTISMENT_EVENT_TYPE_NOT_LONGER_AVAILABLE = 1;
	public static final int ADVERTISMENT_NEW_EVENT_TYPE_AVAILABLE = 0;
	
	public static final int START_PERFORMANCE_ANALYSIS = 101;	
	public static final int STOP_PERFORMANCE_ANALYSIS = 102;
	
	//not addressable due to invalid packageName
		//public static final int INVALID_PACKAGE_NAME = 8;
	
	public Announcement(String eventID, String timestamp,
			String producerID, String transmittedEventType, String packageName, int announcement) {
		super(ANNOUNCEMENT, eventID, timestamp, producerID);
		this.transmittedEventType = transmittedEventType;
		this.packageName = packageName;
		this.announcement = announcement;
	}
	
	private SparseArray<String> outputArray;
	/**
	 * parse from a StringArray in resource file
	 * to get a map of id and String
	 * The input String from file must have formatted
	 * as "id|text"
	 * @param stringArrayResourceId
	 */
	private SparseArray<String> parseStringArray(int stringArrayResourceId) {
	    String[] stringArray = MApplication.getContext().getResources().getStringArray(stringArrayResourceId);
	    outputArray = new SparseArray<String>(stringArray.length);
	    for (String entry : stringArray) {
	        String[] splitResult = entry.split("\\|", 2);
	        outputArray.put(Integer.valueOf(splitResult[0]), splitResult[1]);
	    }
	    return outputArray;
	}
	
	public String getAnncouncementText() {
		if (outputArray == null)
			parseStringArray(R.array.announcement_array);

		return outputArray.get(announcement);
	}
		
	public String getTransmittedEventType() {
		return transmittedEventType;
	}

	public void setTransmittedEventType(String transmittedEventType) {
		this.transmittedEventType = transmittedEventType;
	}



	public String getPackageName() {
		return packageName;
	}



	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}



	public int getAnnouncement() {
		return announcement;
	}



	public void setAnnouncement(int announcement) {
		this.announcement = announcement;
	}



	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<Announcement> CREATOR = new Parcelable.Creator<Announcement>() {

		public Announcement createFromParcel(Parcel source) {
			return new Announcement(source);
		}

		public Announcement[] newArray(int size) {
			return new Announcement[size];
		}
	};
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(eventType);
		dest.writeString(eventID);
		dest.writeString(timestamp);
		dest.writeString(producerID);
		dest.writeString(transmittedEventType);
		dest.writeString(packageName);
		dest.writeInt(announcement);
	}

	private Announcement(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		transmittedEventType = source.readString();
		packageName = source.readString();
		announcement = source.readInt();
	}
}
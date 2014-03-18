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

import android.os.Parcel;
import android.os.Parcelable;

public class StartProducer extends ManagementEvent{

	public static String EVENT_TYPE = STARTPRODUCER;
	
	private String eventTypeProducerID;
	private String startEventType;
	
	public StartProducer(String eventID, String timestamp,
			String producerID, String eventTypeProducerID, String startEventType) {
		super(STARTPRODUCER, eventID, timestamp, producerID);
		this.eventTypeProducerID = eventTypeProducerID;
		this.startEventType = startEventType;
	}

	public String getEventTypeProducerID() {
		return eventTypeProducerID;
	}
	
	public String getStartEventType() {
		return startEventType;
	}
	
	public String getShortStartEventType() {
		if(startEventType.contains(".")) {
    		return startEventType.substring(startEventType.lastIndexOf(".")+1, startEventType.length());	
    	} else {
    		return startEventType;
    	}
	}
	
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<StartProducer> CREATOR = new Parcelable.Creator<StartProducer>() {

		public StartProducer createFromParcel(Parcel source) {
			return new StartProducer(source);
		}

		public StartProducer[] newArray(int size) {
			return new StartProducer[size];
		}
	};
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(eventType);
		dest.writeString(eventID);
		dest.writeString(timestamp);
		dest.writeString(producerID);
		dest.writeString(eventTypeProducerID);
		dest.writeString(startEventType);
	}

	private StartProducer(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		eventTypeProducerID = source.readString();
		startEventType = source.readString();
	}
}
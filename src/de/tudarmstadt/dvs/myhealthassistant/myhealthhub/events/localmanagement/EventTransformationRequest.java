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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.localmanagement;

import android.os.Parcel;
import android.os.Parcelable;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;

/**
 * Event transformation request consisting of either a start request or a stop request. 
 * Since requesting a transformation can take a while, request and response are asynchronous. 
 * @author Christian Seeger
 *
 */
public class EventTransformationRequest extends Event {
	
	public static String EVENT_TYPE = LocalManagementEvent.EVENT_TRANSFORMATION_REQUEST;
	
	public final static int TYPE_REQUEST_TRANSFORMATION = 1;
	public final static int TYPE_STOP_TRANSFORMATION = 2;
		
	private String[] advertisedEvents;

	private String eventSubscription;
	private int requestType;

	/**
	 * Event transformation request consisting of either an start request or a stop request. 
	 * @param eventID
	 * @param timestamp
	 * @param producerID
	 * @param requestType <code>TYPE_REQUEST_TRANSFORMATION</code> or <code>TYPE_STOP_TRANSFORMATION</code>
	 * @param advertisedEvents
	 * @param eventSubscription
	 */
	public EventTransformationRequest(String eventID,
			String timestamp, String producerID, int requestType,
			String[] advertisedEvents, String eventSubscription) {
		super(EVENT_TYPE, eventID, timestamp, producerID);
		this.requestType = requestType;
		this.advertisedEvents = advertisedEvents;
		this.eventSubscription = eventSubscription;
	}
	
	
	/**
	 * @return the advertisedEvents
	 */
	public String[] getAdvertisedEvents() {
		return advertisedEvents;
	}

	/**
	 * @return the eventSubscription
	 */
	public String getEventSubscription() {
		return eventSubscription;
	}

	/**
	 * @return the requestType
	 */
	public int getRequestType() {
		return requestType;
	}	
	
	
	
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<EventTransformationRequest> CREATOR = new Parcelable.Creator<EventTransformationRequest>() {

		
		public EventTransformationRequest createFromParcel(Parcel source) {
			return new EventTransformationRequest(source);
		}

		
		public EventTransformationRequest[] newArray(int size) {
			return new EventTransformationRequest[size];
		}
	};

	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(eventType);
		dest.writeString(eventID);
		dest.writeString(timestamp);
		dest.writeString(producerID);
		dest.writeInt(requestType);
		dest.writeString(eventSubscription);
		dest.writeStringArray(advertisedEvents);
	}

	private EventTransformationRequest(final Parcel source) {
		super(source.readString(), source.readString(), source.readString(), source.readString());
		readFromParcel(source);
	}

	public void readFromParcel(final Parcel source) {
		requestType = source.readInt();
		eventSubscription = source.readString();
		advertisedEvents = source.createStringArray();
	}
}
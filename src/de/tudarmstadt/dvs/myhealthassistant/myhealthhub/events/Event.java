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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events;

import android.os.Parcelable;

/**
 * @author Christian Seeger
 *
 */
public abstract class Event implements Parcelable {

	public final static String EVENT_ROOT = "de.tudarmstadt.dvs.myHealthAssistant.Event";
	
	public final static String PARCELABLE_EXTRA_EVENT_TYPE = "event_type";
	public final static String PARCELABLE_EXTRA_EVENT = "event";
	public final static String PARCELABLE_EXTRA_AVAILABLE_EVENTS = "available_events";
	
	public static String EVENT_TYPE;
	
	protected String eventType;
	protected String eventID;
	protected String timestamp;
	protected String producerID;
	
	/**
	 * Event constructor.
	 * @param eventID Event ID.
	 * @param EVENT_TYPE Event type.
	 * @param timestamp Timestamp of event.
	 * @param producerID ID of event producer.
	 */
	public Event(final String eventType, String eventID, String timestamp, String producerID) {
		this.eventType = eventType;
		this.eventID = eventID;		
		this.timestamp = timestamp;
		this.producerID = producerID;
	}
	
	/**
	 * Returns the event ID.
	 * @return event ID
	 */
	public String getID() {
		return eventID;
	}
	
	/**
	 * Returns the event timestamp.
	 * @return timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}
		
	/**
	 * Returns the event type.
	 * @return event type
	 */
	public String getEventType() {
		return eventType;
	}
	
	/**
	 * Returns the event producer ID.
	 * @return producer ID
	 */
	public String getProducerID() {
		return producerID;
	}
	
	/**
	 * Returns only the last part after the "." of an event type. 
	 * @return Short event type
	 */
    public String getShortEventType() {
    	if(eventType.contains(".")) {
    		return eventType.substring(eventType.lastIndexOf(".")+1, eventType.length());	
    	} else {
    		return eventType;
    	}
    	
    }
    
	/**
	 * Returns only the last part after the "." of a producer ID. 
	 * @return Short producer ID
	 */
    public String getShortProducerID() {
    	if(producerID.contains(".")) {
    		return producerID.substring(producerID.lastIndexOf(".")+1, producerID.length());	
    	} else {
    		return producerID;
    	}
    	
    }
    

}
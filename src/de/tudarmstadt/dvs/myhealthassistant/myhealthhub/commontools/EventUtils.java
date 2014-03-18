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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools;

import android.util.Log;


/**
 * @author Chris
 *
 */
public class EventUtils {
	public int runningNumber;
	private String sensorID;
	private String eventTypeShort;
	
	/**
	 * 
	 * @param EVENT_TYPE sensor type
	 * @param sensorID sensor id
	 */
	public EventUtils(String eventType, String sensorID) {
		this.sensorID = sensorID;
		this.eventTypeShort = getShortEventType(eventType);
	}
	

	/**
	 * Returns an event ID consisting of eventType_SensorID_runningNumber_Timestamp.
	 * @return event ID
	 */
	public String getEventID() {
		return eventTypeShort+"_"+sensorID+"_#"+increaseRunningNumber()+"_"+getTimestamp();
	}
	
	/**
	 * Returns only the last part after the "." of a producer ID. 
	 * @return Short producer ID
	 */
    public String getShortEventType(String eventType) {
    	if(eventType.contains("myhealthassistant.event.")) {
    		return eventType.substring(eventType.lastIndexOf("myhealthassistant.event.")+24, eventType.length());	
    	} else {
    		return eventType;
    	}
    	
    }
	
	public int increaseRunningNumber() {
		if(runningNumber<Integer.MAX_VALUE) {
			return runningNumber++;
		} else {
			return runningNumber=0;
		}
	}
	
	public String getTimestamp() {
		//return Calendar.getInstance().getTime().toGMTString();
		return (String) android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", new java.util.Date());
	}
	
}
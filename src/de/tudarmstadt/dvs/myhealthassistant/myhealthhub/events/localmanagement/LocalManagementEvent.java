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

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;

/**
 * @author Christian Seeger
 *
 */
public abstract class LocalManagementEvent extends Event {

	protected final static String LOCAL_MANAGEMENT_EVENT = EVENT_ROOT+".LocalManagement";
	
	/**
	 * @return the localManagementEvent
	 */
	public static String getLocalManagementEvent() {
		return LOCAL_MANAGEMENT_EVENT;
	}

	public final static String EVENT_TRANSFORMATION_REQUEST = LOCAL_MANAGEMENT_EVENT+".EventTransformationRequest";
	public final static String EVENT_TRANSFORMATION_RESPONSE= LOCAL_MANAGEMENT_EVENT+".EventTransformationResponse";
	
	public LocalManagementEvent(String eventType, String eventID,
			String timestamp, String producerID) {
		super(eventType, eventID, timestamp, producerID);
	}
}
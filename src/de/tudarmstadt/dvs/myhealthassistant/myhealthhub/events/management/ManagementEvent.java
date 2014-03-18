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

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;

/**
 * @author chris
 *
 */
public abstract class ManagementEvent extends Event {

	protected final static String MANAGEMENT = EVENT_ROOT+".Management";
	
	public final static String SUBSCRIPITON = MANAGEMENT+".Subscription";
	public final static String UNSUBSCRIPTION = MANAGEMENT+".Unsubscription";
	
	public final static String ADVERTISEMENT = MANAGEMENT+".Advertisement";
	public final static String UNADVERTISEMENT = MANAGEMENT+".Unadvertisement";
	
	public final static String STARTPRODUCER = MANAGEMENT+".StartProducer";
	public final static String STOPPRODUCER = MANAGEMENT+".StopProducer";
	
	public final static String ANNOUNCEMENT = MANAGEMENT+".Announcement";
		
	
		/**
	 * @return the management
	 */
	public static String getManagement() {
		return MANAGEMENT;
	}



	public ManagementEvent(String eventType, String eventID, String timestamp,
			String producerID) {
		super(eventType, eventID, timestamp, producerID);
		
	}

}
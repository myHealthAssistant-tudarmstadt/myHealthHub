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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.environmental;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.environmental.AbstractEnvironmentalEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.AbstractSensorType;

public class PIRSensor extends AbstractEnvironmentalSensorType {

	public PIRSensor(String sensorID, String sensorMAC, String room, String location) {		
		super(sensorID, sensorMAC, room, location);
		sensorType = PIR_SENSOR;
		description = "A Passive Infrared sensor (PIR sensor) " +
				"is an electronic device that measures infrared (IR) " +
				"light radiating from objects in its field of view.";
		properties = "Working area: 5m, 100° horizontal, 82° vertical";
		sensorEventTypes = new String[] {SensorReadingEvent.PASSIVE_INFRARED};
		sensorStatusTypes = new int[] {STATUS_DISCONNECTED, STATUS_CONNECTED};
	}
}
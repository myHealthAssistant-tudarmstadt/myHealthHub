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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.cardiovascular;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.AbstractSensorType;

/**
 * @author Chris
 *
 */
public final class CorscienceECGChestStrapSensor extends AbstractSensorType {

	public CorscienceECGChestStrapSensor(String sensorID, String sensorMAC) {
		super(sensorID, sensorMAC);
		sensorType = "ecg";
		description = "ECG sensor manufactored by Corscience";
		properties = "200Hz sampling rate";
		sensorEventTypes = new String[] {SensorReadingEvent.ECG_STREAM}; 
		sensorStatusTypes = new int[] {STATUS_DISCONNECTED, STATUS_CONNECTED};	
	}
	
}
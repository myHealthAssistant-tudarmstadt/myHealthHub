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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.environmental.configurations;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.environmental.EnvironmentalSensorConfiguration;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.environmental.AbstractEnvironmentalSensorType;

/**
 * @author Christian Seeger
 *
 */
public class RovingSensorConfigurationHD extends EnvironmentalSensorConfiguration {
	
	public RovingSensorConfigurationHD() {
		super();
		
		// Besteckschublade
		MACtoSensorTypeMapping.put("00:06:66:14:e2:70", AbstractEnvironmentalSensorType.BALL_IN_TUBE_SENSOR);
		MACtoLocationMapping.put("00:06:66:14:e2:70", KITCHEN);
		MACtoObjectTypeMapping.put("00:06:66:14:e2:70", OBJ_DRAWER);
		
		// Stuhl Küche
		MACtoSensorTypeMapping.put("00:06:66:14:e4:f0", AbstractEnvironmentalSensorType.BALL_IN_TUBE_SENSOR);
		MACtoLocationMapping.put("00:06:66:14:e4:f0", KITCHEN);
		MACtoObjectTypeMapping.put("00:06:66:14:e4:f0", OBJ_CHAIR);
		
		// Stuhl Esszimmer
		MACtoSensorTypeMapping.put("00:06:66:14:e5:99", AbstractEnvironmentalSensorType.BALL_IN_TUBE_SENSOR);
		MACtoLocationMapping.put("00:06:66:14:e5:99", LIVING_ROOM);
		MACtoObjectTypeMapping.put("00:06:66:14:e5:99", OBJ_CHAIR);
		
		// Shampoo 
		MACtoSensorTypeMapping.put("00:06:66:14:cb:8a", AbstractEnvironmentalSensorType.BALL_IN_TUBE_SENSOR);
		MACtoLocationMapping.put("00:06:66:14:cb:8a", BATH_ROOM);
		MACtoObjectTypeMapping.put("00:06:66:14:cb:8a", OBJ_SHOWER);
		
		// Zahncreme
		MACtoSensorTypeMapping.put("00:06:66:14:e6:2b", AbstractEnvironmentalSensorType.BALL_IN_TUBE_SENSOR);
		MACtoLocationMapping.put("00:06:66:14:e6:2b", BATH_ROOM);
		MACtoObjectTypeMapping.put("00:06:66:14:e6:2b", OBJ_TOOTH_BRUSH);
		
		// Balkontür
		MACtoSensorTypeMapping.put("00:06:66:14:e4:ef", AbstractEnvironmentalSensorType.REED);
		MACtoLocationMapping.put("00:06:66:14:e4:ef", LIVING_ROOM);
		MACtoObjectTypeMapping.put("00:06:66:14:e4:ef", OBJ_DOOR);
	}
}
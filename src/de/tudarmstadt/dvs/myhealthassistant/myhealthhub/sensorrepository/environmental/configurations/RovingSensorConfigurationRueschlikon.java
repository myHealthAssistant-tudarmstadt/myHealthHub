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
public class RovingSensorConfigurationRueschlikon extends EnvironmentalSensorConfiguration {
	
	public RovingSensorConfigurationRueschlikon() {
		super();
		
		MACtoSensorTypeMapping.put("00:06:66:14:e2:70", AbstractEnvironmentalSensorType.REED);
		MACtoLocationMapping.put("00:06:66:14:e2:70", KITCHEN);
		MACtoObjectTypeMapping.put("00:06:66:14:e2:70", OBJ_FRIDGE);
		
		MACtoSensorTypeMapping.put("00:06:66:14:e4:f0", AbstractEnvironmentalSensorType.PIR_SENSOR);
		MACtoLocationMapping.put("00:06:66:14:e4:f0", ENTRANCE);
		MACtoObjectTypeMapping.put("00:06:66:14:e4:f0", OBJ_WALL);
		
		MACtoSensorTypeMapping.put("00:06:66:14:e5:99", AbstractEnvironmentalSensorType.REED);
		MACtoLocationMapping.put("00:06:66:14:e5:99", ENTRANCE);
		MACtoObjectTypeMapping.put("00:06:66:14:e5:99", OBJ_DOOR);
		
		MACtoSensorTypeMapping.put("00:06:66:14:cb:8a", AbstractEnvironmentalSensorType.BALL_IN_TUBE_SENSOR);
		MACtoLocationMapping.put("00:06:66:14:cb:8a", LIVING_ROOM);
		MACtoObjectTypeMapping.put("00:06:66:14:cb:8a", OBJ_CHAIR);
		
		MACtoSensorTypeMapping.put("00:06:66:14:e6:2b", AbstractEnvironmentalSensorType.PIR_SENSOR);
		MACtoLocationMapping.put("00:06:66:14:e6:2b", BATH_ROOM);
		MACtoObjectTypeMapping.put("00:06:66:14:e6:2b", OBJ_SHOWER);
		
		MACtoSensorTypeMapping.put("00:06:66:14:e4:ef", AbstractEnvironmentalSensorType.BALL_IN_TUBE_SENSOR);
		MACtoLocationMapping.put("00:06:66:14:e4:ef", BATH_ROOM);
		MACtoObjectTypeMapping.put("00:06:66:14:e4:ef", OBJ_TOOTH_BRUSH);
	}
}
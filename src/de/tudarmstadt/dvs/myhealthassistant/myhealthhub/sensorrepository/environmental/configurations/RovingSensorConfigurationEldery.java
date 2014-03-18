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

import java.io.ObjectOutputStream.PutField;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.environmental.AbstractEnvironmentalSensorType;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.environmental.EnvironmentalSensorConfigurationWithNames;

/**
 * @author Christian Seeger
 *
 */
public class RovingSensorConfigurationEldery extends EnvironmentalSensorConfigurationWithNames {
	
	public RovingSensorConfigurationEldery() {
		super();
	
		String MAC;
		
		MAC = "00:06:66:14:e5:99";
		MACtoSensorTypeMapping.put(MAC, AbstractEnvironmentalSensorType.BALL_IN_TUBE_SENSOR);
		MACtoLocationMapping.put(MAC, ENTRANCE);
		//MACtoObjectTypeMapping.put(MAC, OBJ_DOOR);
		MACtoObjectTypeMapping.put(MAC, "Toilette");
		MACtoNameMapping.put(MAC, "Peggy");

		MAC = "00:06:66:14:e6:2b";
		MACtoSensorTypeMapping.put(MAC, AbstractEnvironmentalSensorType.BALL_IN_TUBE_SENSOR);
		MACtoLocationMapping.put(MAC, BATH_ROOM);
		//MACtoObjectTypeMapping.put(MAC, OBJ_SHOWER);
		MACtoObjectTypeMapping.put(MAC, "Deo-Schrank");
		MACtoNameMapping.put(MAC, "Amy");

		MAC = "00:06:66:14:e5:b2";
		MACtoSensorTypeMapping.put(MAC, AbstractEnvironmentalSensorType.BALL_IN_TUBE_SENSOR);
		MACtoLocationMapping.put(MAC, BATH_ROOM);
		//MACtoObjectTypeMapping.put(MAC, OBJ_SHOWER);
		MACtoObjectTypeMapping.put(MAC, "Wasserkocher");
		MACtoNameMapping.put(MAC, "Maggy");

		MAC = "00:06:66:14:e4:14";
		MACtoSensorTypeMapping.put(MAC, AbstractEnvironmentalSensorType.BALL_IN_TUBE_SENSOR);
		MACtoLocationMapping.put(MAC, BATH_ROOM);
		//MACtoObjectTypeMapping.put(MAC, OBJ_SHOWER);
		MACtoObjectTypeMapping.put(MAC, "Stuhl Wohnzimmer Fabienne");
		MACtoNameMapping.put(MAC, "Kristy");
		
		MAC = "00:06:66:14:e5:a7";
		MACtoSensorTypeMapping.put(MAC, AbstractEnvironmentalSensorType.BALL_IN_TUBE_SENSOR);
		MACtoLocationMapping.put(MAC, BATH_ROOM);
		//MACtoObjectTypeMapping.put(MAC, OBJ_SHOWER);
		MACtoObjectTypeMapping.put(MAC, "Besteckschublade");
		MACtoNameMapping.put(MAC, "Lily");	
		
		MAC = "00:06:66:14:cb:e4";
		MACtoSensorTypeMapping.put(MAC, AbstractEnvironmentalSensorType.BALL_IN_TUBE_SENSOR);
		MACtoLocationMapping.put(MAC, BATH_ROOM);
		//MACtoObjectTypeMapping.put(MAC, OBJ_SHOWER);
		MACtoObjectTypeMapping.put(MAC, "Stuhl Küche Fabienne");
		MACtoNameMapping.put(MAC, "Polly");	

		MAC = "00:06:66:14:e4:f0";
		MACtoSensorTypeMapping.put(MAC, AbstractEnvironmentalSensorType.BALL_IN_TUBE_SENSOR);
		MACtoLocationMapping.put(MAC, BATH_ROOM);
		//MACtoObjectTypeMapping.put(MAC, OBJ_SHOWER);
		MACtoObjectTypeMapping.put(MAC, "Eingangstür");
		MACtoNameMapping.put(MAC, "Macy");	

		MAC = "00:06:66:14:cb:8a";
		MACtoSensorTypeMapping.put(MAC, AbstractEnvironmentalSensorType.BALL_IN_TUBE_SENSOR);
		MACtoLocationMapping.put(MAC, BATH_ROOM);
		//MACtoObjectTypeMapping.put(MAC, OBJ_SHOWER);
		MACtoObjectTypeMapping.put(MAC, "Stuhl Küche Christian");
		MACtoNameMapping.put(MAC, "Lucy");	

		MAC = "00:06:66:14:e5:70";
		MACtoSensorTypeMapping.put(MAC, AbstractEnvironmentalSensorType.BALL_IN_TUBE_SENSOR);
		MACtoLocationMapping.put(MAC, BATH_ROOM);
		//MACtoObjectTypeMapping.put(MAC, OBJ_SHOWER);
		MACtoObjectTypeMapping.put(MAC, "Stuhl Wohnzimmer Christian");
		MACtoNameMapping.put(MAC, "Lizzy");	

		MAC = "00:06:66:14:e5:7d";
		MACtoSensorTypeMapping.put(MAC, AbstractEnvironmentalSensorType.BALL_IN_TUBE_SENSOR);
		MACtoLocationMapping.put(MAC, BATH_ROOM);
		//MACtoObjectTypeMapping.put(MAC, OBJ_SHOWER);
		MACtoObjectTypeMapping.put(MAC, "Kühlschrank");
		MACtoNameMapping.put(MAC, "Marcy");	

		MAC = "00:06:66:14:e4:3d";
		MACtoSensorTypeMapping.put(MAC, AbstractEnvironmentalSensorType.BALL_IN_TUBE_SENSOR);
		MACtoLocationMapping.put(MAC, BATH_ROOM);
		//MACtoObjectTypeMapping.put(MAC, OBJ_SHOWER);
		MACtoObjectTypeMapping.put(MAC, "Kaffeemaschine");
		MACtoNameMapping.put(MAC, "Bonny");	

		MAC = "00:06:66:14:e4:ef";
		MACtoSensorTypeMapping.put(MAC, AbstractEnvironmentalSensorType.PIR_SENSOR);
		MACtoLocationMapping.put(MAC, BATH_ROOM);
		//MACtoObjectTypeMapping.put(MAC, OBJ_SHOWER);
		MACtoObjectTypeMapping.put(MAC, "Dusche");
		MACtoNameMapping.put(MAC, "Johnny");	
	

		MAC = "00:06:66:14:e2:70";
		MACtoSensorTypeMapping.put(MAC, AbstractEnvironmentalSensorType.PIR_SENSOR);
		MACtoLocationMapping.put(MAC, BATH_ROOM);
		//MACtoObjectTypeMapping.put(MAC, OBJ_SHOWER);
		MACtoObjectTypeMapping.put(MAC, "Schlafzimmer");
		MACtoNameMapping.put(MAC, "Mike");	
		
		
		
}
	
}
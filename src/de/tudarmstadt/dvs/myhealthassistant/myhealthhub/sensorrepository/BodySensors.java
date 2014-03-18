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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository;

/**
 * @author Christian Seeger
 *
 */
public class BodySensors {

	/** 
	 * Cardiovascular sensors 
	 */
	public static String HEART_RATE_SENSOR_HXM_ID = "Zephyr_HxM_Heart_Rate";
	public static String HEART_RATE_SENSOR_HXM_MAC = "00:07:80:98:72:81";
	
	public static String HEART_RATE_SENSOR_POLAR_BLUETOOTH_ID = "Polar_Bluetooth_Heart_Rate";
	public static String HEART_RATE_SENSOR_POLAR_BLUETOOTH_MAC = "00:22:D0:02:B3:42";
		
	public static String ECG_CORSCIENCE_ID = "Corscience_ECG_chest_strap_Sensor";
	public static String ECG_CORSCIENCE_MAC = "00:A0:96:32:A0:1F";
	
	public static String BLOOD_PRESSURE_STATIONARY_BOSO_ID = "Boso_Stationary_Blood_Pressure_Sensor";
	public static String BLOOD_PRESSURE_STATIONARY_BOSO_MAC = "00:A0:96:2F:C5:36";

	public static String BLOOD_PRESSURE_STATIONARY_BOSO_ID_439 = "Boso_Stationary_Blood_Pressure_Sensor_439";
	public static String BLOOD_PRESSURE_STATIONARY_BOSO_MAC_439 = "00:A0:96:2A:4B:93";
	
	//public static String BLOOD_PRESSURE_STATIONARY_BOSO_ID_439 = "Boso_Stationary_Blood_Pressure_Sensor_B93";
	//public static String BLOOD_PRESSURE_STATIONARY_BOSO_MAC_439 = "00:A0:96:2A:4B:93";
	
	public static String BLOOD_PRESSURE_STATIONARY_OMRON_ID = "Omron_Stationary_Blood_Pressure_Sensor";
	public static String BLOOD_PRESSURE_STATIONARY_OMRON_MAC = "00:A0:96:13:ED:B0";  

	
	/** 
	 * Physical sensors 
	 */
	public static String ACC_HEDGEHOG_LEG_ID = "ESS_Hedgehog_Accelerometer_Leg";
	//public static String ACC_HEDGEHOG_LEG_MAC = "00:12:F3:06:29:D6";
	//public static String ACC_HEDGEHOG_LEG_MAC = "00:12:F3:0A:B9:3D";  // Jenny
	//public static String ACC_HEDGEHOG_LEG_MAC = "A0:F4:50:6A:32:ED";  // HTC
	public static String ACC_HEDGEHOG_LEG_MAC = "00:12:F3:1E:45:43"; // 2. neuer Sensor
	
	public static String ACC_HEDGEHOG_CHEST_ID = "ESS_Hedgehog_Accelerometer_Chest";
	//public static String ACC_HEDGEHOG_CHEST_MAC = "00:12:F3:0A:B9:23";
	public static String ACC_HEDGEHOG_CHEST_MAC = "A0:F4:50:6A:32:ED";  // HTC
	
	public static String ACC_HEDGEHOG_WRIST_ID = "ESS_Hedgehog_Accelerometer_Wrist";
	//public static String ACC_HEDGEHOG_WRIST_MAC = "00:12:F3:06:29:D6"; // Jenny
	public static String ACC_HEDGEHOG_WRIST_MAC = "00:12:F3:1E:45:33"; // neuer Sensor
	
	public static String ACC_HEDGEHOG_DEBUG_ID = "ESS_Hedgehog_Debug";
	//public static String ACC_HEDGEHOG_DEBUG_MAC = "00:12:F3:1E:45:33"; // neuer Sensor
	//public static String ACC_HEDGEHOG_DEBUG_MAC = "00:12:F3:1E:45:43"; // 2. neuer Sensor
	public static String ACC_HEDGEHOG_DEBUG_MAC = "A0:F4:50:6A:32:ED"; // HTC
	
	public static String SCALE_IEM_ID = "IEM_Scale";
	public static String SCALE_IEM_MAC = "00:07:80:87:40:25";
	
}
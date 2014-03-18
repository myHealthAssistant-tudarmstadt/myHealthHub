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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub;

/**
 * @author Chris
 *
 */
public final class Preferences {
	
	public static String PREFERENCES = "de.tudarmstadt.dvs.myhealthhub.preferences";
	
	public static String AUTO_CONNECT_ENABLED = "autoConnectEnabled";
	public static String AUTO_CONNECT_WARMUP = "autoConnectWarmUp";
	public static String AUTO_CONNECT_TIME_UNTIL_NEXT_DEVICE = "autoConnectTimeUntilNextDevice";
	public static String AUTO_CONNECT_TIME_UNTIL_NEXT_CHECK = "autoConnectTimeUntilNextCheck";
	
	public static String ENABLE_HXM_HEART_RATE = "enableHxMHeartRate";
	public static String ENABLE_HEART_RATE_POLAR_BT = "enableHeartRatePolarBT";
	public static String ENABLE_CORSCIENCE_ECG = "enableCorscienceECG";
	public static String ENABLE_IEM_SCALE = "enableIEMScale";
	public static String ENABLE_BOSO_BLOOD_PRESSURE = "enableBosoBloodPressure";
	public static String ENABLE_ACC_LEG = "enableAccLeg";
	public static String ENABLE_ACC_CHEST = "enableAccChest";
	public static String ENABLE_ACC_WRIST = "enableAccWrist";
	public static String ENABLE_ACC_DEBUG = "enableAccDebug";
	public static String ENABLE_ROVING = "enableRoving";
	public static String ENABLE_INFRA_WOT = "enableInfraWOT";
	
	public static String USER_NAME = "userName";
	public static String SYS_MONITORING = "SystemMonitorPref";
	public static String NOTIFICATION_EMAIL = "notificationEmail";
	public static String NOTIFICATION_EMAIL_ALARMS = "emailNotificationAlarms";
	public static String NOTIFICATION_EMAIL_INFORMATION = "emailNotificationInformation";
	
	public static String TM_REMOTE_REPOSITORY_HOST = "tmRemoteRepositoryHost";
	public static String TM_REMOTE_REPOSITORY_PORT = "tmRemoteRepositoryPort";
	
	public static String XML_FILE_MAC_TO_SENSOR = "xmlFileMACSensor";
	public static String XML_FILE_ENV_RULES = "xmlFileEnvrSensingRules";
}
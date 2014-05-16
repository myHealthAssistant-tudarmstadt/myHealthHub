package unpublish;

import java.util.UUID;

import android.content.Context;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools.EventUtils;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.cardiovascular.HeartRateEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensormodules.AbstractBluetoothSensorModule;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.AbstractSensorType;

public class ZephyrHxMModule extends AbstractBluetoothSensorModule {

    private static String MY_TAG = "ZephyrHxMModule";
    
    private static boolean D = false;
	
    // Name for the SDP record when creating server socket
    private static String SENSOR_BLUETOOTH_NAME = "ZephyrHxMModule";
    
    // Unique UUID for this application. 
    private static UUID SENSOR_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private byte[] readBufTemp = new byte[1];
	
	/**
	 * Constructor
	 * @param context
	 * @param handler
	 */
	public ZephyrHxMModule(Context context, AbstractSensorType sensor) {
		super(context, sensor, true, SENSOR_UUID, SENSOR_BLUETOOTH_NAME, MY_TAG);
		myEventUtils = new EventUtils(
				mySensor.getSensorReadingType(0), mySensor.getSensorID());
		
		if(D)Log.d(TAG,"New sensor created with ID: "+mySensor.getSensorID()+
				" and sensor reading type: "+mySensor.getSensorReadingType(0));		
	}
	
	@Override
	protected void deliverPacket(byte[] packet, int bytes) {
		   if(bytes == 1) {
	        	readBufTemp = packet.clone();
	        	if(D)printPacket(bytes, packet, "1st step:");
	        } else if (bytes == 59) {
	        	//if(D)printPacket(bytes, packet, "2nd step:");
	        	//if(D)printPacket(1, readBufTemp, "...");
	        	byte[] newPacket = new byte[60];
	        	System.arraycopy(readBufTemp, 0, newPacket, 0, 1);
	        	System.arraycopy(packet, 0, newPacket, 1, 59);
	        	//if(D)printPacket(bytes+1, newPacket, "3rd step:");
	        	
	        	deliverPacket(newPacket);
	        	readBufTemp = new byte[1];
	        } else {
	        	if(D)printPacket(bytes, packet, "correct:");
	        	deliverPacket(packet); 
	        }
	}

	private void deliverPacket(byte[] packet) {
		
		if(ZephyrHxMUtils.validHxmPacket(packet)) {
			//if(D)Log.i(TAG, "valid packet");
			//if(D)printPacketInHex(packet.length, packet, "xxx Zephyr: ");
				
			// Create HR Event
			HeartRateEvent heartRateEvent = new HeartRateEvent(
					myEventUtils.getEventID(), 
					myEventUtils.getTimestamp(),
					mySensor.getSensorID(),
					mySensor.getSensorType(),
					myEventUtils.getTimestamp(), 0);
			heartRateEvent = ZephyrHxMUtils.parseHrmPacket(packet, heartRateEvent);
			
			// Send event
			sendSensorReading(heartRateEvent);
			
		} else {
			Log.e(TAG, "Skipping invalid packet.");
		}		
	}

	// Debugging
	public void printPacket(int bytes, byte[] buffer, String info) {
		String text = "";
		for(int i = 0; i < bytes; i++){
			text += i+": "+buffer[i]+" |";
		}
		Log.d(TAG, info+" "+text);
	}
	
	public void printPacketInHex(int bytes, byte[] buffer, String info) {
		String text = "";
		for(int i = 0; i < bytes; i++){
			text += i+": "+java.lang.Integer.toHexString(buffer[i]&0xff)+" |";
		}
		if(D)Log.d(TAG, info+" "+text);
	}

}

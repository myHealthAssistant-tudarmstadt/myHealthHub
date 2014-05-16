package unpublish;

import android.util.Log;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.cardiovascular.HeartRateEvent;


/**
 * <p>
 * Operations to read BT packets from Zephyr Devices
 * 
 * <p>
 * See the documents here:
 * <p>
 * http://www.zephyrtech.co.nz/support/softwaredevelopmentkit
 * <p>
 * http://www.zephyrtech.co.nz/assets/pdfs/bluetooth_hxm_api_guide.pdf
 * 
 * 
 * @author <a href="mailto:brad.zdanivsky@gmail.com">Brad Zdanivsky</a>
 * 
 */
public class ZephyrHxMUtils {

	// Debugging
	private static final String TAG = "heartbeatSensorZephyr";
	private static final boolean D = true;
	
	/** Zephyr packet constants */
	public final static int checksumPolynomial = 0x8C;

	/** HXM message id */
	public final static byte HXM_ID = 0x26;

	/** End of text */
	public final static byte ETX = 0x03;

	/** StartProducer of text */
	public final static byte STX = 0x02;

	/** HXM packet size */
	public final static byte HXM_DLC = 0x37;

	/**
	 * Do a CBC check on this packet
	 * 
	 * @param packet
	 *            to test
	 * @return true if error free
	 */
	public static boolean checkCRC(byte[] packet) {

		int crc = 0;
		for (int i = 2; i < 57; i++)
			crc = ZephyrHxMUtils.ChecksumPushByte(crc, ZephyrHxMUtils
					.readUnsignedByte(packet[i]));

		/** Then compare to the packet CRC */
		if (crc == ZephyrHxMUtils.readUnsignedByte(packet[57]))
			return true;

		return false;
	}

	/** CRC check taken from Zephyr PDF's */
	private static int ChecksumPushByte(int currentChecksum, int newByte) {

		currentChecksum = (currentChecksum ^ newByte);

		for (int bit = 0; bit < 8; bit++) {

			if ((currentChecksum & 1) == 1)
				currentChecksum = ((currentChecksum >> 1) ^ checksumPolynomial);

			else
				currentChecksum = (currentChecksum >> 1);
		}

		return currentChecksum;
	}

	/**
	 * @param b
	 *            is the byte to convert
	 * @return a integer from the given byte
	 */
	private static int readUnsignedByte(byte b) {
		return (b & 0xff);
	}


	/**
	 * 
	 * @param packet
	 *            of bytes
	 * @param index
	 *            of the byte to parse in the byte array
	 * @return a String of the indexed byte
	 */
	private static String parseString(byte[] packet, int index) {
		String hex = byteToHex(packet[index]);
		short value = Short.parseShort(hex, 16);
		return String.valueOf(value);
	}

	/**
	 * 
	 * @param packet
	 *            of bytes
	 * @param index
	 *            of the byte to parse in the byte array
	 * @return a String of the indexed byte
	 */
	private static short parseShort(byte[] packet, int index) {
		String hex = byteToHex(packet[index]);
		return Short.parseShort(hex, 16);
	}


	/**
	 * Convert a byte to a hex string.
	 * 
	 * @param data
	 *            the byte to convert
	 * @return String the converted byte
	 */
	private static String byteToHex(byte data) {
		StringBuffer buf = new StringBuffer();
		buf.append(toHexChar((data >>> 4) & 0x0F));
		buf.append(toHexChar(data & 0x0F));
		return buf.toString();
	}

	/**
	 * Convert a byte array to a hex string.
	 * 
	 * @param data
	 *            the byte[] to convert
	 * @return String the converted byte[]
	 */
	private static String bytesToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			buf.append(byteToHex(data[i]));
		}
		return buf.toString();
	}

	/**
	 * Convert an int to a hex char.
	 * 
	 * @param i
	 *            is the int to convert
	 * @return char the converted char
	 */
	private static char toHexChar(int i) {
		if ((0 <= i) && (i <= 9))
			return (char) ('0' + i);
		else
			return (char) ('a' + (i - 10));
	}


	/**
	 * Convert a RAW bluetooth packet an XML command object
	 * 
	 * @param packet
	 *            is the RAW bytes from the SPP
	 * @return Command is the same command but with the HRM elements added
	 */
	public static HeartRateEvent parseHrmPacket(byte[] packet, HeartRateEvent heartbeatEvent) {

		heartbeatEvent.batteryLevel = parseShort(packet, 11);
		heartbeatEvent.value = parseShort(packet, 12);
		heartbeatEvent.beatCounter = parseShort(packet, 13);
		
		
		return heartbeatEvent;
	}

	/**
	 * Check if this is valid HXM packet
	 * 
	 * @param packet
	 *            of RAW bytes to test before parsing
	 * @return true if this is an error free transmission
	 */
	public static boolean validHxmPacket(byte[] packet) {

		if (packet == null)
			return false;

		if (packet.length != 60) {
			/** most common, happens when not in sync with HXM */
			if(D)Log.d(TAG, "wrong packet size on HXM");
			return false;
		}

		if (packet[0] != STX) {
			if(D)Log.d(TAG, "STX error on HXM");
			return false;
		}

		if (packet[1] != HXM_ID) {
			if(D)Log.d(TAG, "MSG_ID error on HXM");
			return false;
		}

		if (packet[2] != HXM_DLC) {
			if(D)Log.d(TAG, "DLC error on HXM");
			return false;
		}

		if (packet[59] != ETX) {
			if(D)Log.d(TAG, "ETC error on HXM");
			return false;
		}

		if (ZephyrHxMUtils.checkCRC(packet)) {
			if(D)Log.d(TAG, "CRC error on HXM");
			return false;
		}

		/** all is well, parse this one */
		return true;
	}
	
    /**
     * Basic byte array add to end of byte array.
     * 
     * @param dest
     * @param input
     * @param startIndex
     * @param bytes
     * 
     * @return true if successful
     */
    public static boolean add(final byte[] dest, final byte[] input, final int startIndex, final int bytes) {

        if (startIndex + bytes > dest.length) {
            // System.out.println("ZehyrUtils.add(d,i,s):  Cannot add more bytes than there are in destination array");
            // Cannot add more bytes than there are in destination array
            return false;
        }

        for (int i = 0; i < bytes; i++) {
            dest[i + startIndex] = input[i];
        }

        return true;
    }

}

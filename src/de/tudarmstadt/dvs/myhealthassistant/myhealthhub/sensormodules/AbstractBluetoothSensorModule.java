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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensormodules;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools.EventUtils;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.Advertisement;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.Announcement;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.ManagementEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.Unadvertisement;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensorrepository.AbstractSensorType;

/**
 * @author Christian Seeger
 *
 */
public abstract class AbstractBluetoothSensorModule extends AbstractSensorModule {
	
	// Debugging
	protected String TAG;
	protected boolean D = true;
	
    // Name for the SDP record when creating server socket
    protected String BLUETOOTH_NAME; // = "HealthMonitor";

    // Unique UUID for this application
    protected UUID MY_UUID; // = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
    protected final BluetoothAdapter mAdapter;
    protected int mState;
    protected AcceptThread mAcceptThread;
    protected ConnectThread mConnectThread;
    protected ConnectedThread mConnectedThread;
    
    // Constants that indicate the current connection state
    protected static final int STATE_NONE = 0;       // we're doing nothing
    protected static final int STATE_LISTEN = 1;     // now listening for incoming connections
    protected static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    protected static final int STATE_CONNECTED = 3;  // now connected to a remote device
        
    protected Context context;
    
	protected String myHealthHubReceiver = AbstractChannel.RECEIVER;
	
	protected AbstractSensorType mySensor;
	//protected EventUtils myStatusEventUtils;
	protected EventUtils myManagementUtils;
	protected EventUtils myEventUtils;
	
	protected boolean isActiveModule;
	private boolean isStartTriggered;

    /**
     * Constructor. Initializes sensor module and sends a corresponding advertisement message
     * @param context  The UI Activity Context
     * @param sensor  Sensor type 
     * @param isActiveModule true if sensor module actively needs to connect to sensor.
     */
    public AbstractBluetoothSensorModule(Context context, AbstractSensorType sensor, boolean isActiveModule,
    		UUID uuid, String bluetoothName, String tag) {
        super(sensor.getSensorID()+"-module", context);

        this.context = context;
        this.mySensor = sensor;
        this.isActiveModule = isActiveModule;
        this.MY_UUID = uuid;
        this.BLUETOOTH_NAME = bluetoothName;
        this.TAG = tag;
        
        isStartTriggered = false;
        
    	mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        
        // Event utilities
        //myStatusEventUtils = new EventUtils(StatusEvent.getStatusEvent(), mySensor.getSensorID());
        
        // For publishing advertisements
        myManagementUtils = new EventUtils(ManagementEvent.getManagement(), mySensor.getSensorID());
    }
    
    /**
     * Initializes the sensor module by sending an advertisement message.
     */
    public void initializeSensorModule() {
        // TODO add properties such as start/stop and activen/non-active
        Advertisement ad = new Advertisement(myManagementUtils.getEventID(), 
        		myManagementUtils.getTimestamp(), 
        		mySensor.getSensorID(),
        		context.getApplicationContext().getPackageName(),
        		mySensor.getSensorReadingType(0), 
        		"-");
        sendToChannel(ad, AbstractChannel.MANAGEMENT); 
    }
    
    /**
     * Destroys the sensor module and sends an un-advertisement.
     */
    public void destroySensorModule() {
	    stop();
    	
    	// Send unadvertise
	    Unadvertisement unAd = new Unadvertisement(
	    		myManagementUtils.getEventID(), 
	    		myManagementUtils.getTimestamp(), 
	    		mySensor.getSensorID(), 
	    		context.getPackageName(),
	    		mySensor.getSensorReadingType(0));
	    sendToChannel(unAd, AbstractChannel.MANAGEMENT);
	    
	    //TODO test it.
	   /* try {
			finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }
    

    /**
     * In Bluetooth master mode, it tries to connect to the sensor.
     * In Bluetooth slave mode, it opens a socket for incoming connections.
     */
    public synchronized void start() {
    	isStartTriggered = true;
    	if(isActiveModule) {
    		Log.d(TAG, "Start() was triggered: I am active.");
    		trySensorConnection();
    	} else {
    		Log.d(TAG, "Start() was triggered: I am passive.");
    		startAcceptThread();
    	}
    }
    
    /**
	 * StopProducer all threads
	 */
	public synchronized void stop() {
		isStartTriggered = false;
	    if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
	    if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
	    if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
	    setState(STATE_NONE);
	}
	
	/**
	 * Returns whether the module acts as a Bluetooth master, i.e. actively connects
	 * to a sensor, or waits for an incoming connection as a Bluetooth slave.
	 * @return true if sensor module acts as a Bluetooth master.
	 */
	public boolean isActiveModule() {
		return isActiveModule;
	}

	/**
	 * Informs about an active Bluetooth connection.
	 * @return true if currently connected to another device.
	 */
	public boolean isConnected() {
		return (mState == STATE_CONNECTED);
	}
	
	/**
	 * Returns the event type a sensor is producing
	 * @return event type this module is producing
	 */
	public String getProducingEventType() {
		return mySensor.getSensorReadingType(0);
	}

	/**
	 *  Opens socket for accepting a Bluetooth connection. (slave mode)
	 */
	protected synchronized void startAcceptThread() {
	    // Cancel any thread attempting to make a connection
	    if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
	
	    // Cancel any thread currently running a connection
	    if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
	
	    // StartProducer the thread to listen on a BluetoothServerSocket
	    if (mAcceptThread == null) {
	        mAcceptThread = new AcceptThread();
	        mAcceptThread.start();
	    }
	    setState(STATE_LISTEN);
	}

	protected synchronized void trySensorConnection() {
		//if(D)Log.d(TAG, "trying to connect to: "+mySensor.getSensorMAC());
		connect(mAdapter.getRemoteDevice(mySensor.getSensorMAC()));
	}


	/**
	 * StartProducer the ConnectThread to initiate a connection to a remote device.
	 * @param device  The BluetoothDevice to connect
	 */
	protected synchronized void connect(BluetoothDevice device) {
	    if (D) Log.d(TAG, "Connect to: " + device.getAddress());
	
	    // Cancel any thread attempting to make a connection
	    if (mState == STATE_CONNECTING) {
	        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
	    }
	
	    // Cancel any thread currently running a connection
	    if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
	
	    // StartProducer the thread to connect with the given device
	    mConnectThread = new ConnectThread(device);
	    mConnectThread.start();
	    setState(STATE_CONNECTING);
	}

	/**
	 * StartProducer the ConnectThread to initiate a connection to a remote device.
	 * @param device  The BluetoothDevice to connect
	 */
	/*private synchronized void connect(String sensorMAC) {
		connect(mAdapter.getRemoteDevice(sensorMAC));
	}*/

	
	
    /**
     * Processes an incoming Bluetooth packet.
     * @param packet Byte array containing packet content
     * @param bytes Length of byte array
     */
    protected abstract void deliverPacket(byte[] packet, int bytes);
    	
    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    protected synchronized void setState(int state) {
        if (D) Log.d(TAG, "Bluetooth state: " + mState + " -> " + state);
        mState = state;
    
        // Set announcement
        int sensorConnection;
        switch(state) {
        case STATE_CONNECTED: sensorConnection = Announcement.SENSOR_CONNECTED; break;
        case STATE_CONNECTING: sensorConnection = Announcement.SENSOR_CONNECTING; break;
        default: sensorConnection = Announcement.SENSOR_DISCONNECTED;
        }
        
        Announcement announcement = new Announcement(
				myEventUtils.getEventID(), 
				myEventUtils.getTimestamp(), 
				mySensor.getSensorID(), 
				mySensor.getSensorReadingType(0), 
				context.getPackageName(), 
				sensorConnection);
        sendToChannel(announcement, AbstractChannel.MANAGEMENT);
    }
    

    /**
     * StartProducer the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    protected synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "connected to: " + device.getAddress());

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}

        // StartProducer the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        setState(STATE_CONNECTED);
    }
    
    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    protected void connectionFailed() {
        //previous setState(STATE_LISTEN);
    	setState(STATE_NONE);
    }
    
    
    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    protected void connectionLost() {
    	//previous setState(STATE_LISTEN);
    	setState(STATE_NONE);
    	
    	// restart listening if passive module
    	if(!isActiveModule) start();
    }   


    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    protected class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
            	Log.i(TAG, "Open Bluetooth server socket with " +
            			"MY_UUID: "+MY_UUID+
            			" and BLUETOOTH_NAME: "+BLUETOOTH_NAME);
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(BLUETOOTH_NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            setName("AcceptThread");
            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                	socket = mmServerSocket.accept();
                	if(!socket.getRemoteDevice().getAddress().equals(mySensor.getSensorMAC())) {
                		Log.i(TAG, "Close socket because MAC address does not fit. (Connecting: "+
                	socket.getRemoteDevice().getAddress()+", required: "+mySensor.getSensorMAC()+")");
                		socket.close();
                	}
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (this) {
                        switch (mState) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            // Situation normal. StartProducer the connected thread.
                            connected(socket, socket.getRemoteDevice());
                            break;
                        case STATE_NONE:
                        case STATE_CONNECTED:
                            // Either not ready or already connected. Terminate new socket.
                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e(TAG, "Could not close unwanted socket", e);
                            }
                            break;
                        }
                    }
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    protected class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
            	Log.i(TAG, "UUID: "+MY_UUID.toString());
            	tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp; 
          //  mmSocket.getRemoteDevice();
        }

        public void run() {
        	if(D)Log.d(TAG, "I'm in connect thread");
        	
        	setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                // StartProducer the service over to restart listening mode
                // Commented out for daily monitoring
                //ZephyrHeartbeatSensorModule.this.start();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (this) {
                mConnectThread = null;
            }

            // StartProducer the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    protected class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
        	Log.d(TAG, "create ConnectedThread: "+socket.toString());
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
        	Log.d(TAG, "Listening...");
        	
            //byte[] buffer = new byte[1024];
            byte[] buffer = new byte[60];
            int bytes;

            // Keep listening to the InputStream while connected
            while(true) { //while (isStartTriggered) {
                try {
                	
                	// Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    
                    // Create packet with correct size
                    byte[] packet = new byte[bytes];
                    System.arraycopy(buffer, 0, packet, 0, bytes);
                    deliverPacket(packet, bytes);
                	Log.d(TAG, "xxx called: "+TAG);   
                    
                    //deliverPacket(buffer, bytes);                    
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

		public void cancel() {
            try {
                mmSocket.close();
                mmInStream.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }
    }
    
}
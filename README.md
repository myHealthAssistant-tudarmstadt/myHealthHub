myHealthHub
===========

The project proposes a middleware for simplifying the development and deployment of 
body sensor network (BSN) solutions as well as their integration in ambient sensing 
environments. The middleware handles a variety of sensors and copes with complexity
of heterogeneous and changing sensor constellations; properties of BSNs that become
more and more apparent. Information from sensors are collected and made available 
for various applications consistently.


Build and Run
==============

The following software is required to build the application:
- Java JDK SE 1.6
- Android SDK + Eclipse or Android Developer Tools Bundle (ADT) 
- Android 4.03 (Sdk version API15) or Android 4.2.2 (Sdk version API17) has to be installed from Android SDK Manager 
	
Run in Eclipse:
- In Eclipse, go File-> Import-> Android-> Exiting Android Code into Workspace-> Next
- Choose the root Directory as this Project -> Finish
- Wait for Eclipse finish building all projects and confirm no errors
- To run right click on project choose "run as" -> "Android Application"

Usage
==============
Using myHealthHub is straight forward. Suppose you have a sensor (a Polar Heart rate sensor since other sensor decoder aren't included in myHealthHub due to license aggrement).
First you need to pair it with Android:
- Wet your Polar Heart Rate Monitor to increase conductivity and put it on
- Go to Android Settings - Tap Wireless & Network
- Open Bluetooth Settings
- Tap Scan Devices
- Your Heart Rate Monitor should appear below “Bluetooth devices”. Tap to pair and enter in PIN 0000 (or 1234, depends on manufacturer) to pair
- Your Polar will now be paired. To delete a pairing, open Settings next to paired device, and choose to unpair

To start Open myHealthHub:
- Tap on Pulse Sensor type to open its Settings
- Tap Sensor Type to choose device's type as Polar Bluetooth Heart Rate for raw data decoding
- Tap Bluetooth Device to choose Mac Address of pairing sensor above
- Back to main menu, now you can enable or disable Polar Sensor, which will allow myHealthHub to advertise/unadvertise Heart Rate data and other Applications can receive the data when they subscribe to myHealthHub.


Create Android Application to work with myHealthHub
==============

You have two ways to integrate myHealthHub into your project:

 1. Download [myHealthHub.jar](https://drive.google.com/folderview?id=0B1K7WjMkB5fqemlhX3FVd2tnUUE) file and copy it into the libs folder of your project.

 2. Download or clone the git repository and link your project with the myHealthHub library project.

### Establish connection to myHealthHub


		/** Connection to myHealthHub Service */
		Intent myHealthHubIntent = new Intent(
				IMyHealthHubRemoteService.class.getName());
		getActivity().bindService(myHealthHubIntent,
				myHealthAssistantRemoteConnection, Context.BIND_AUTO_CREATE);

[...]

	private boolean connectedToHMM;
	/**
	 * Service connection to myHealthHub remote service. This connection is
	 * needed in order to start myHealthHub. Furthermore, it is used inform the
	 * application about the connection status.
	 */
	private ServiceConnection myHealthAssistantRemoteConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			Toast.makeText(getActivity().getApplicationContext(),
					"Connected to myHealthAssistant", Toast.LENGTH_LONG).show();
			connectedToHMM = true;
		}

		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "I am disconnected.");
			connectedToHMM = false;
		}
	};


### Register BroadcastReceiver to myHealthHub's events


		/*
		 * register reading receiver for the desired event types. You can also
		 * register individual receivers for specific event types by having
		 * multiple "myReadingReceivers".
		 */
		ReadingEventReceiver myReadingReceiver = new ReadingEventReceiver();
		IntentFilter inFil = new IntentFilter();
		// filter start/stop events and sensor connectivity information
		inFil.addAction(AbstractChannel.MANAGEMENT);
		inFil.addAction(NotificationEvent.EVENT_TYPE);
		// filter Accelerometer event type
		inFil.addAction(SensorReadingEvent.ACCELEROMETER);
		inFil.addAction(SensorReadingEvent.ACCELEROMETER_KNEE);

		getActivity().registerReceiver(myReadingReceiver, inFil);




[...]

	/**
	 * Event receiver implemented as a Android BroadcastReceiver for receiving
	 * myHealthHub sensor reading events.
	 */
	private class ReadingEventReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			/* Get event type and the event itself */
			Event evt = intent.getParcelableExtra(Event.PARCELABLE_EXTRA_EVENT);
			String eventType = evt.getEventType();

			// consume the individual event types
			// if event is of type announcement in order to inform about sensor
			// connectivity:
			if (eventType.equals(Announcement.EVENT_TYPE)) {
				int announcement = ((Announcement) evt).getAnnouncement();

				/* Applies for CONSUMER side ================ */

				if (announcement == Announcement.SENSOR_CONNECTED) {
				// sensor is connected

				} else if (announcement == Announcement.SENSOR_DISCONNECTED) {
					// sensor is disconnected
				}
				
			}else if (eventType.equals(StartProducer.EVENT_TYPE)) {
				// incoming start producer event telling that a specific event
				// type is desired.

			} else if (eventType.equals(StopProducer.EVENT_TYPE)) {
				// incoming stop producer event telling the the specific event
				// type is not needed anymore.

			} else if (eventType.equals(SensorReadingEvent.ACCELEROMETER_KNEE)) {
				AccSensorEvent acc = (AccSensorEvent) evt;
				// extract the incoming Accelerometer event to receive data needed	
			}
		}
	}



License
==============

Copyright (C) 2014 TU Darmstadt, Hessen, Germany. 
Department of Computer Science Databases and Distributed Systems.
This program is distributed under the terms of the GNU GPL v3. 
See the LICENSE file for license rights and limitations.


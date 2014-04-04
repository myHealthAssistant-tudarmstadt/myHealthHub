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
Using myHealthHub is straight forward. Suppose you have a sensor (a Polar Heart rate sensor since other sensor decoder aren't included in myHealthHub due to license aggrement), first you need to pair it with Android:
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
- Back to main menu, now you can enable or disable Polar Sensor, which will allow myHealthHub to advertise/unadvertise Heart Rate data and other Applications to subscribe to it

License
==============

Copyright (C) 2014 TU Darmstadt, Hessen, Germany. 
Department of Computer Science Databases and Distributed Systems.
This program is distributed under the terms of the GNU GPL v3. 
See the LICENSE file for license rights and limitations.


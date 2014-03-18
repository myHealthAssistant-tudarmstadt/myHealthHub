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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.IMyHealthHubRemoteService;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.MyHealthHubWithFragments;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools.JSONParser;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools.mail.GMailSender;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Idee ist, dass myHealthAssistant regelmäßig (alle 15 Minuten?) nach dem
 * Batteriestatus und der aktuellen (GPS) Position schaut und das an einen
 * Server weiterleitet. Falls das System also keine Energie mehr hat und es sein
 * kann, dass der Patient in einer kritischen Situation ist, kann man die letzte
 * Position nachschauen.
 * 
 * @author HieuHa
 * 
 */
public class SystemMonitor extends Service {

	private int batteryPercent = 0;
	private JSONParser jParser = new JSONParser();

	@Override
	public IBinder onBind(Intent arg0) {
		Log.e(SystemMonitor.class.getSimpleName(), "onBind()");
		return iservicestub;
	}

	private IMyHealthHubRemoteService.Stub iservicestub = new IMyHealthHubRemoteService.Stub() {

		@Override
		public int getStatus() throws RemoteException {
			// / Write here, code to be executed in background
			Log.e(SystemMonitor.class.getSimpleName(),
					"Hello World From Remote Service!!");
			return 0;
		}
	};

	private LocationResult locationResult = new LocationResult() {
		@Override
		public void gotLocation(Location location) {
			// Got the location!
			publishResult(location);
		}
	};

	private BroadcastReceiver batteryLvlReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			batteryPercent = (level * 100) / scale;

		}

	};

	@Override
	public void onCreate() {
		super.onCreate();
		// Use filter for Monitor Significant Changes in Battery Level only
		// Generally speaking, the impact of constantly monitoring the
		// battery
		// level has a greater impact on the battery than your app's normal
		// behavior, so it's good practice to only monitor significant
		// changes
		// in battery levelspecifically when the device enters or exits a
		// low
		// battery state.
		IntentFilter ifilter = new IntentFilter();
		ifilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		// ifilter.addAction(Intent.ACTION_BATTERY_OKAY);
		this.registerReceiver(batteryLvlReceiver, ifilter);

		// get Location
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(this, locationResult);

		// scheduleNextUpdate();

		// Notify user of this service
		systemNotice();

		this.stopSelf();

	}

	private int systemNotice() {
		int t = START_STICKY;
		Log.e(SystemMonitor.class.getSimpleName(),
				"call me redundant BABY!  onStartCommand service");

		int myID = android.os.Process.myPid();
		// The intent to launch when the user clicks the expanded notification
		Intent intent = new Intent(this, MyHealthHubWithFragments.class);
		// Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendIntent = PendingIntent
				.getActivity(this, 0, intent, 0);

		Notification notice = new Notification.Builder(getApplicationContext())
				.setSmallIcon(R.drawable.ic_launcher)
				.setWhen(System.currentTimeMillis())
				.setContentTitle(getPackageName())
				.setContentText("System Monitor running")
				.setContentIntent(pendIntent).getNotification();

		notice.flags |= Notification.FLAG_AUTO_CANCEL;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(myID, notice);

		return t;
	}

	@Override
	public void onDestroy() {
		Log.e(SystemMonitor.class.getSimpleName(),
				"OnDestroy: /** Disconnection **/");

		// stopr this service
		stopForeground(true);

		this.unregisterReceiver(batteryLvlReceiver);
		super.onDestroy();
	}

	private void publishResult(Location loc) {
		String t = getTimestamp() + "\n";
		t += "Longitude:" + loc.getLongitude() + "\n";
		t += "Latitude:" + loc.getLatitude() + "\n";
		t += "BatteryPercent: " + batteryPercent + "\n";
		t += "-----------------------------------------";

		SharedPreferences prefs = getApplicationContext().getSharedPreferences(
				"personal", Context.MODE_PRIVATE);

		phoneNr = prefs.getString(getString(R.string.p_emcy_phone_nr), "");

		// TODO: check Internet State before sending

		// send message to server
		JSONArray jsa = new JSONArray();
		JSONObject jso = new JSONObject();
		try {
			jso.put("Timestamp", getTimestamp());
			jso.put("Longitude", loc.getLongitude());
			jso.put("Latitude", loc.getLatitude());
			jso.put("PercentBattery", batteryPercent);
			jsa.put(jso);
			sendNotification(jsa);
		} catch (JSONException e) {
			Log.e(SystemMonitor.class.getSimpleName(), e.getLocalizedMessage());
		}

		// send message to a gmail acc
		new AsyncTask<String, Void, Void>() {

			@Override
			protected Void doInBackground(String... params) {
				String message = params[0];
				GMailSender sender = new GMailSender(
						null,
						null);
				try {
					sender.sendMail("myHealthAssistant: SystemMonitor",
							message,
							null,
							null);
				} catch (Exception e) {
					Log.e(SystemMonitor.class.getSimpleName(), e.getMessage());
				}
				return null;
			}

		}.execute(t, null, null);

	}

	private String phoneNr;

	private void sendNotification(JSONArray t) {
		new AsyncTask<JSONArray, Void, Void>() {
			@Override
			protected Void doInBackground(JSONArray... params) {
				JSONArray message = params[0];

				try {

					// Building Parameters
					List<NameValuePair> pairs = new ArrayList<NameValuePair>();
					pairs.add(new BasicNameValuePair("message", message
							.toString()));
					pairs.add(new BasicNameValuePair("phone", phoneNr));

					JSONObject json = jParser.makeHttpRequest(
							getString(R.string.send_url), "POST", pairs);

					// check log cat for response
					if (json != null) {
						String response = json.getString("message");
						Log.e(SystemMonitor.class.getSimpleName(),
								"Post Response: " + response);
					} else
						Log.e(SystemMonitor.class.getSimpleName(),
								"No Response!!");
				} catch (JSONException e) {
					Log.e(SystemMonitor.class.getSimpleName(),
							"JSONException: " + e.getMessage());
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void v) {
			}
		}.execute(t, null, null);
	}

	/**
	 * Returns the current time as "yyyy-MM-dd hh:mm:ss".
	 * 
	 * @return time stamp
	 */
	private String getTimestamp() {
		return (String) android.text.format.DateFormat.format(
				"yyyy-MM-dd hh:mm:ss", new java.util.Date());
	}

	/**
	 * First of all I check what providers are enabled. Some may be disabled on
	 * the device, some may be disabled in application manifest. If any provider
	 * is available I start location listeners and timeout timer. It's 20
	 * seconds in my example, may not be enough for GPS so you can enlarge it.
	 * If I get update from location listener I use the provided value. I stop
	 * listeners and timer. If I don't get any updates and timer elapses I have
	 * to use last known values. I grab last known values from available
	 * providers and choose the most recent of them.
	 * 
	 * @author Fedor (stackoverflow.com)
	 * 
	 */
	private class MyLocation {
		private Timer timer1;
		private int timeout = 20000;
		private LocationManager lm;
		private LocationResult locationResult;
		private boolean gps_enabled = false;
		private boolean network_enabled = false;

		public boolean getLocation(Context context, LocationResult result) {
			// I use LocationResult callback class to pass location value from
			// MyLocation to user code.
			locationResult = result;
			if (lm == null)
				lm = (LocationManager) context
						.getSystemService(Context.LOCATION_SERVICE);

			// exceptions will be thrown if provider is not permitted.
			try {
				gps_enabled = lm
						.isProviderEnabled(LocationManager.GPS_PROVIDER);
			} catch (Exception ex) {
			}
			try {
				network_enabled = lm
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			} catch (Exception ex) {
			}

			// don't start listeners if no provider is enabled
			if (!gps_enabled && !network_enabled)
				return false;

			if (gps_enabled)
				lm.requestSingleUpdate(LocationManager.GPS_PROVIDER,
						locationListenerGps, Looper.myLooper());
			// lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
			// locationListenerGps);
			if (network_enabled)
				lm.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,
						locationListenerGps, Looper.myLooper());
			// lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
			// 0, locationListenerNetwork);
			timer1 = new Timer();
			timer1.schedule(new GetLastLocation(), timeout);
			return true;
		}

		LocationListener locationListenerGps = new LocationListener() {
			public void onLocationChanged(Location location) {
				timer1.cancel();
				locationResult.gotLocation(location);
				lm.removeUpdates(this);
				lm.removeUpdates(locationListenerNetwork);
			}

			public void onProviderDisabled(String provider) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}
		};

		LocationListener locationListenerNetwork = new LocationListener() {
			public void onLocationChanged(Location location) {
				timer1.cancel();
				locationResult.gotLocation(location);
				lm.removeUpdates(this);
				lm.removeUpdates(locationListenerGps);
			}

			public void onProviderDisabled(String provider) {
				Toast.makeText(getApplicationContext(), "GPS disabled!",
						Toast.LENGTH_SHORT).show();
			}

			public void onProviderEnabled(String provider) {
				Toast.makeText(getApplicationContext(), "GPS enabled!",
						Toast.LENGTH_SHORT).show();
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				Toast.makeText(getApplicationContext(), "GPS status changed!",
						Toast.LENGTH_SHORT).show();
			}
		};

		class GetLastLocation extends TimerTask {
			@Override
			public void run() {
				lm.removeUpdates(locationListenerGps);
				lm.removeUpdates(locationListenerNetwork);

				Location net_loc = null, gps_loc = null;
				if (gps_enabled)
					gps_loc = lm
							.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (network_enabled)
					net_loc = lm
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

				// if there are both values use the latest one
				if (gps_loc != null && net_loc != null) {
					if (gps_loc.getTime() > net_loc.getTime())
						locationResult.gotLocation(gps_loc);
					else
						locationResult.gotLocation(net_loc);
					return;
				}

				if (gps_loc != null) {
					locationResult.gotLocation(gps_loc);
					return;
				}
				if (net_loc != null) {
					locationResult.gotLocation(net_loc);
					return;
				}
				locationResult.gotLocation(null);
			}
		}

	}

	public static abstract class LocationResult {
		public abstract void gotLocation(Location location);
	}
}
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

package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.adapter.InternalSensorListAdapter;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.Subscription;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.Unsubscription;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.environmental.raw.AmbientLightEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.AccSensorEventKnee;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.InternalSensorService;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database.LocalTransformationDBMS;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.database.TrafficData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author HieuHa
 * 
 *         This represents activities of each internal sensor data received as
 *         linear plotted graph.
 */
public class GraphFragment extends Fragment {

	// for debugging
	private static final String TAG = GraphFragment.class.getSimpleName();
	private static boolean D = true;
	private View rootView;
	private static boolean barType = true;
	private long timespan = DateUtils.SECOND_IN_MILLIS; // the asynchronous time
														// between each data
														// collected
	private String lastAddedLightData = "";
	private String lastAddedAccData = "";

	private Double readingLightValue = 0.0d;
	private Double readingAccValue = 0.0d;

	// used for an increasing event ID
	private int eventCounter;

	private int accDataCounter; // count number of acc data received in a min
	private int lightDataCounter;

	private int accGraphCounter;
	private int lightGraphCounter;
	private ArrayList<GraphViewData> data_light;
	private ArrayList<GraphViewData> data_acc;
	private SharedPreferences pref;

	// for receiving reading events:
	private ReadingEventReceiver myReadingAccReceiver = new ReadingEventReceiver();
	private boolean readingAcc = false;
	private ReadingEventReceiver myReadingLightReceiver = new ReadingEventReceiver();
	private boolean readingLight = false;

	private boolean isRecording;

	private String lightGrpDes = "Light in lux/s";
	private String motionGrpDes = "Motion Strength/s";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_with_graph, container,
				false);

		if (D)
			Log.d(TAG, TAG + ": onCreateView");

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		eventCounter = 0;

		accDataCounter = 0;
		lightDataCounter = 0;
		// Graph representation
		accGraphCounter = 0; // maxDataCount;
		lightGraphCounter = 0;

		data_light = new ArrayList<GraphViewData>();
		data_acc = new ArrayList<GraphViewData>();

		pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

		clearCharts();
		CheckBox cBox = (CheckBox) rootView.findViewById(R.id.start_record);
		cBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				startRecording(isChecked);
				Log.d(TAG, "Recording sensors Evt = " + isChecked);
			}
		});

		Button refrBtn = (Button) rootView.findViewById(R.id.refresh_btn);
		refrBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// redraw chart
//				addTrafficToDB(data_light, "28-04-2014", lightGrpDes);
//				addTrafficToDB(data_acc, "28-04-2014", motionGrpDes); FIXME
				clearCharts();
			}
		});
		

		TextView atDate = (TextView) rootView.findViewById(R.id.at_date);
		atDate.setText(getCurrentDate());
		
		Button backBtn = (Button) rootView.findViewById(R.id.date_back);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dateBackAndForth(true);
			}
		});
		
		Button forthBtn = (Button) rootView.findViewById(R.id.date_next);
		forthBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dateBackAndForth(false);
				
			}
		});

	}

	private void startRecording(boolean start) {
		// isRecording = start;

		if (start) {
			if (pref.getBoolean(InternalSensorListAdapter.PREF_SENSOR_TYPE
					+ Sensor.TYPE_ACCELEROMETER, false)) {
				subscriptToEvent(SensorReadingEvent.ACCELEROMETER_KNEE);
				/*
				 * register reading receiver for the desired event types. You
				 * can also register individual receivers for specific event
				 * types by having multiple "myReadingReceivers".
				 */
				IntentFilter inFil = new IntentFilter();
				inFil.addAction(SensorReadingEvent.ACCELEROMETER_KNEE);
				getActivity().registerReceiver(myReadingAccReceiver, inFil);
				readingAcc = true;
			}
			if (pref.getBoolean(InternalSensorListAdapter.PREF_SENSOR_TYPE
					+ Sensor.TYPE_LIGHT, false)) {
				subscriptToEvent(SensorReadingEvent.AMBIENT_LIGHT);
				IntentFilter inFil = new IntentFilter();
				inFil.addAction(SensorReadingEvent.AMBIENT_LIGHT);
				getActivity().registerReceiver(myReadingLightReceiver, inFil);
				readingLight = true;

			}
		} else {
			// store data to db
//			TextView atDate = (TextView) rootView.findViewById(R.id.at_date);
//			addTrafficToDB(data_light, atDate.getText().toString(), lightGrpDes);
//			addTrafficToDB(data_acc, atDate.getText().toString(), motionGrpDes);
//			FIXME
			// publish un-subscription
			unsubscriptToEvent(SensorReadingEvent.ACCELEROMETER_KNEE);
			unsubscriptToEvent(SensorReadingEvent.AMBIENT_LIGHT);

			// unregisterReceiver
			if (readingAcc)
				getActivity().unregisterReceiver(myReadingAccReceiver);

			if (readingLight)
				getActivity().unregisterReceiver(myReadingLightReceiver);

			readingAcc = false;
			readingLight = false;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		CheckBox cBox = (CheckBox) rootView.findViewById(R.id.start_record);
		cBox.setChecked(false);
		super.onPause();
	}

	private void dateBackAndForth(boolean back) {
		TextView atDate = (TextView) rootView.findViewById(R.id.at_date);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		try {
			Date today = sdf.parse(atDate.getText().toString());
			Calendar cal = Calendar.getInstance();
			cal.setTime(today);

			if (back) {
				cal.add(Calendar.DATE, -1);
			} else {
				cal.add(Calendar.DATE, 1);
			}

			String newDate = sdf.format(cal.getTime());
			data_light = updateTrafficOnDate(newDate, lightGrpDes);
			data_acc = updateTrafficOnDate(newDate, motionGrpDes);
			redrawCharts();
			atDate.setText(newDate);
			

		} catch (ParseException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}

	}

	private LocalTransformationDBMS transformationDB;
	private ArrayList<GraphView.GraphViewData> updateTrafficOnDate(String date, String type) {
		// initialize database
		this.transformationDB = new LocalTransformationDBMS(getActivity()
				.getApplicationContext());
		transformationDB.open();
		ArrayList<TrafficData> list = transformationDB
				.getAllTrafficFromDate(date, type);
		ArrayList<GraphView.GraphViewData> data = new ArrayList<GraphView.GraphViewData>();
		for (TrafficData t : list) {
			GraphViewData x = new GraphViewData(t.getxValue(), t.getyValue());
			data.add(x);
		}
		transformationDB.close();
		
		return data;
	}

	private void addTrafficToDB(ArrayList<GraphView.GraphViewData> list,
			String date, String type) {
		this.transformationDB = new LocalTransformationDBMS(getActivity()
				.getApplicationContext());
		transformationDB.open();
		for (GraphViewData x : list) {
			transformationDB.addTraffic(date, type, x.valueX, x.valueY);
		}
		transformationDB.close();
	}

	private void createGraph(String graphTitle, GraphViewSeries series,
			int Rid, boolean isBarChart, int dataSize) {
		GraphView graphView = null;

		if (isBarChart) {
			graphView = new BarGraphView(getActivity().getApplicationContext(),
					graphTitle);
			// graphView.setVerticalLabels(new String[] { "high", "mid", "low"
			// });
			graphView.setManualYAxisBounds(11.0d, 9.0d);
			// graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
			// @Override
			// public String formatLabel(double value, boolean isValueX) {
			// if (!isValueX) {
			// if (value <= 10) {
			// return "low";
			// } else if (10 < value && value < 11) {
			// return "mid";
			// } else {
			// return "hgh";
			// }
			// }
			// return null; // let graphview generate X-axis label for us
			// }
			// });
		} else
			graphView = new LineGraphView(
					getActivity().getApplicationContext(), graphTitle);

		// add data
		graphView.addSeries(series);
		// set view port, start=2, size=10
		// graphView.setViewPort(2, 10);
		graphView.setScrollable(false);
		// optional - activate scaling / zooming
		// graphView.setScalable(true);
		// optional - legend
		// graphView.setShowLegend(true);
		graphView.getGraphViewStyle().setNumVerticalLabels(3);
		graphView.getGraphViewStyle().setNumHorizontalLabels(5);

		// graphView.getGraphViewStyle().setNumVerticalLabels(7);
		// graphView.setManualYAxisBounds(300.0d, 0.0d);
		// graphView.setVerticalLabels(new String[] { "hgh", "mid", "low" });
		// graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
		// @Override
		// public String formatLabel(double value, boolean isValueX) {
		// if (!isValueX) {
		// if (value < 200) {
		// return "low";
		// } else if (value < 400) {
		// return "mid";
		// } else {
		// return "hgh";
		// }
		// }
		// return null; // let graphview generate X-axis label for us
		// }
		// });

		final LinearLayout layout = (LinearLayout) rootView.findViewById(Rid);
		layout.removeAllViews();
		layout.addView(graphView);
		rootView.postInvalidate();
	}

	private void clearCharts() {
		GraphViewData[] dataList = new GraphViewData[1];
		dataList[0] = new GraphViewData(0.0, 0.0);
		GraphViewSeries gvs_light = new GraphViewSeries(dataList);
		createGraph(lightGrpDes, gvs_light, R.id.light_graph, !barType, 1);
		data_light = new ArrayList<GraphView.GraphViewData>();

		createGraph(motionGrpDes, gvs_light, R.id.motion_graph, barType, 1);
		data_acc = new ArrayList<GraphView.GraphViewData>();
	}

	private void redrawCharts() {
		GraphViewData[] dataList = new GraphViewData[data_light.size()];
		for (int i = 0; i < data_light.size(); i++) {
			dataList[i] = data_light.get(i);
		}
		GraphViewSeries gvs_light = new GraphViewSeries(dataList);
		createGraph(lightGrpDes, gvs_light, R.id.light_graph, !barType,
				lightGraphCounter);

		GraphViewData[] dataAcc = new GraphViewData[data_acc.size()];
		for (int i = 0; i < data_acc.size(); i++) {
			dataAcc[i] = data_acc.get(i);
		}
		GraphViewSeries gvs_acc = new GraphViewSeries(dataAcc);
		createGraph(motionGrpDes, gvs_acc, R.id.motion_graph, barType,
				accGraphCounter);
	}

	private void subscriptToEvent(String evtType) {
		// generate subscription
		Subscription sub = new Subscription("PubSubExampleEvent"
				+ eventCounter++, getTimestamp(), "PubSubExample",
				getActivity().getPackageName(), evtType);

		// publish subscription
		publishManagemntEvent(sub);

	}

	private void unsubscriptToEvent(String evtType) {
		// generate subscription
		Unsubscription sub = new Unsubscription("PubSubExampleEvent"
				+ eventCounter++, getTimestamp(), "PubSubExample",
				getActivity().getPackageName(), evtType);

		// publish subscription
		publishManagemntEvent(sub);

	}

	/**
	 * Publishes a management event.
	 * 
	 * @param managementEvent
	 */
	private void publishManagemntEvent(Event managementEvent) {
		publishEvent(managementEvent, AbstractChannel.MANAGEMENT);
	}

	/**
	 * Publishes an event on a specific myHealthHub channel.
	 * 
	 * @param event
	 *            that shall be published.
	 * @param channel
	 *            on which the event shall be published.
	 */
	private void publishEvent(Event event, String channel) {
		Intent i = new Intent();
		// add event
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT_TYPE, event.getEventType());
		i.putExtra(Event.PARCELABLE_EXTRA_EVENT, event);

		// set channel
		i.setAction(channel);

		// set receiver package
		i.setPackage(AbstractChannel.MY_HEALTH_HUB_PACKAGE_NAME);

		// sent intent
		if (getActivity() != null)
			getActivity().sendBroadcast(i);
		// mAdapter.notifyDataSetChanged();
	}

	/*
	 * ==========================================================================
	 * ===== The following code is interesting for EVENT CONSUMERS only.
	 * ========
	 * =======================================================================
	 */
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

			// for debugging:
			// Log.e(TAG, "ReadingEventReceiver of type: " + eventType);
			// Log.e(TAG,
			// "ReadingEventReceiver with producerID: "
			// + evt.getShortProducerID());

			// event of type light sensing
			if (eventType.equals(AmbientLightEvent.EVENT_TYPE)) {
				AmbientLightEvent lightEvent = (AmbientLightEvent) evt;
				float lightV = lightEvent.getAmbientLight();

				String timeOfMess = lightEvent.getTimeOfMeasurement();
				if (lastAddedLightData.isEmpty())
					lastAddedLightData = timeOfMess;
				else {
					if (minutesDiff(lastAddedLightData, timeOfMess)) {
						// after each min the added up data being divided by
						// average and add to graph series
						GraphViewData data = new GraphViewData(
								lightGraphCounter, readingLightValue
										/ lightDataCounter);
						data_light.add(data);
						GraphViewData[] dataList = new GraphViewData[data_light
								.size()];
						for (int i = 0; i < data_light.size(); i++) {
							dataList[i] = data_light.get(i);
						}
						GraphViewSeries gvs_light = new GraphViewSeries(
								dataList);
						createGraph(lightGrpDes, gvs_light, R.id.light_graph,
								!barType, lightGraphCounter);

						lightGraphCounter++;

						// the data being reset after that
						readingLightValue = Double.parseDouble(Float
								.toString(lightV));
						lightDataCounter = 1;

						lastAddedLightData = timeOfMess;
					} else {
						readingLightValue += Double.parseDouble(Float
								.toString(lightV));
						lightDataCounter++;
					}

				}

			} else if (eventType.equals(AccSensorEventKnee.EVENT_TYPE)) {
				AccSensorEventKnee kneeEvt = (AccSensorEventKnee) evt;
				int x = kneeEvt.x_mean;
				int y = kneeEvt.y_mean;
				int z = kneeEvt.z_mean;

				String timeOfMess = kneeEvt.getTimeOfMeasurement();
				if (lastAddedAccData.isEmpty())
					lastAddedAccData = timeOfMess;
				else {
					if (minutesDiff(lastAddedAccData, timeOfMess)) {
						GraphViewData data = new GraphViewData(accGraphCounter,
								readingAccValue / accDataCounter);
						data_acc.add(data);
						GraphViewData[] dataList = new GraphViewData[data_acc
								.size()];
						for (int i = 0; i < data_acc.size(); i++) {
							dataList[i] = data_acc.get(i);
						}
						GraphViewSeries gvs = new GraphViewSeries(
								dataList);
						createGraph(motionGrpDes, gvs, R.id.motion_graph,
								barType, accGraphCounter);

						accGraphCounter++;

						readingAccValue = Math.sqrt(x * x + y * y + z * z); // Euclidean
						// length
						accDataCounter = 1;

						lastAddedAccData = timeOfMess;
					} else {
						readingAccValue += Math.sqrt(x * x + y * y + z * z); // Euclidean
						// length
						accDataCounter++;
					}

				}
			}
		}

	};

	/**
	 * calculate the difference in minutes between two dates in millisecond
	 * 
	 * @param first
	 * @param second
	 * @return true if two dates are difference by ONE minute
	 */
	private boolean minutesDiff(String firstDate, String secondDate) {
		SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd_kk:mm:ss");

		try {
			Date first = dfDate.parse(firstDate);
			Date second = dfDate.parse(secondDate);

			long diff = ((second.getTime() / timespan) - (first.getTime() / timespan));

			return (diff < 1) ? false : true;

		} catch (ParseException e) {
			Log.e(TAG, e.toString());
		}

		return true;
	}

	/**
	 * Returns the current time as "yyyy-MM-dd hh:mm:ss".
	 * 
	 * hh:mm:ss will give you 01:00:00 for 1 PM, use kk:mm:ss to get 13:00:00
	 * 
	 * @return timestamp
	 */
	private String getTimestamp() {
		// return (String) android.text.format.DateFormat.format(
		// "yyyy-MM-dd hh:mm:ss", new java.util.Date());
		return (String) android.text.format.DateFormat.format(
				"yyyy-MM-dd kk:mm:ss", new java.util.Date());
	}
	
	private static String getCurrentDate() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");//dd/MM/yyyy
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}

	@Override
	public void onStop() {
		super.onStop();
		// publish un-subscription
		unsubscriptToEvent(SensorReadingEvent.ACCELEROMETER_KNEE);
		unsubscriptToEvent(SensorReadingEvent.AMBIENT_LIGHT);

		// unregisterReceiver
		if (readingAcc)
			getActivity().unregisterReceiver(myReadingAccReceiver);

		if (readingLight)
			getActivity().unregisterReceiver(myReadingLightReceiver);

	}
}

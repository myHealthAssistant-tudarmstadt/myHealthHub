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

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.AbstractChannel;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.Event;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.Subscription;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.management.Unsubscription;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.SensorReadingEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.environmental.raw.AmbientLightEvent;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.events.sensorreadings.physical.AccSensorEventKnee;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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

	private Double readingLightValue = 0.0d;
	private Double readingAccValue = 0.0d;

	// used for an increasing event ID
	private int eventCounter;

	private int maxDataCount = 100;
	private int dataCounter;
	private GraphViewSeries gvs;
	private GraphViewSeries gvs_acc;

	// for receiving reading events:
	private ReadingEventReceiver myReadingReceiver = new ReadingEventReceiver();

	private int m_interval = 1000; // 1 seconds by default, can be changed later
	private Handler m_handler;
	private Runnable m_Generator;

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

		// generate subscription
		Subscription sub = new Subscription("PubSubExampleEvent"
				+ eventCounter++, getTimestamp(), "PubSubExample",
				getActivity().getPackageName(), AccSensorEventKnee.EVENT_TYPE);

		// publish subscription
		publishManagemntEvent(sub);

		sub = new Subscription("PubSubExampleEvent" + eventCounter++,
				getTimestamp(), "PubSubExample",
				getActivity().getPackageName(), AmbientLightEvent.EVENT_TYPE);

		// publish subscription
		publishManagemntEvent(sub);

		/*
		 * register reading receiver for the desired event types. You can also
		 * register individual receivers for specific event types by having
		 * multiple "myReadingReceivers".
		 */
		IntentFilter inFil = new IntentFilter();
		// inFil.addAction(SensorReadingEvent.BLOOD_PRESSURE);
		// inFil.addAction(SensorReadingEvent.HEART_RATE);
		// inFil.addAction(SensorReadingEvent.WEIGHT_IN_KG);
		// inFil.addAction(SensorReadingEvent.WEIGHT_IN_LBS);
		// inFil.addAction(SensorReadingEvent.BODY_TEMPERATURE_IN_CELSIUS);
		// inFil.addAction(SensorReadingEvent.BODY_TEMPERATURE_IN_FAHRENHEIT);
		// inFil.addAction(SensorReadingEvent.HR_FIDELITY);
		// inFil.addAction(SensorReadingEvent.ACCELEROMETER);
		inFil.addAction(SensorReadingEvent.ACCELEROMETER_KNEE);
		// inFil.addAction(SensorReadingEvent.ACCELEROMETER_ANKLE);
		// inFil.addAction(NotificationEvent.EVENT_TYPE);
		// inFil.addAction(ActivityEventReha.EVENT_TYPE);
		// inFil.addAction(SensorReadingEvent.ACTIVITY_REHA);
		inFil.addAction(SensorReadingEvent.AMBIENT_LIGHT);

		getActivity().registerReceiver(myReadingReceiver, inFil);

		// Graph representation
		dataCounter = maxDataCount;
		GraphViewData[] data = new GraphViewData[maxDataCount];
		for (int i = 0; i < maxDataCount; i++) {
			data[i] = new GraphViewData(i, 0.0d);
		}
		gvs = new GraphViewSeries("Light", new GraphViewSeriesStyle(), data);
		gvs_acc = new GraphViewSeries("Acc", new GraphViewSeriesStyle(Color.rgb(200, 50, 00), 3), data);

		final GraphView graphView = new LineGraphView(getActivity()
				.getApplicationContext(), "GraphViewDemo" // heading
		);
		
		// add data
		graphView.addSeries(gvs);
		graphView.addSeries(gvs_acc);
		// set view port, start=2, size=10
		graphView.setViewPort(2, 10);
		graphView.setScrollable(true);
		// optional - activate scaling / zooming
		// graphView.setScalable(true);
		// optional - legend
		graphView.setShowLegend(true);
		graphView.getGraphViewStyle().setNumHorizontalLabels(1);
		graphView.getGraphViewStyle().setNumVerticalLabels(7);
		graphView.setManualYAxisBounds(300.0d, -150.0d);

//		graphView.setVerticalLabels(new String[] {"high", "middle", "low"});
//		graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
//			  @Override
//			  public String formatLabel(double value, boolean isValueX) {
//			    if (!isValueX) {
//			      if (value < 200) {
//			        return "small";
//			      } else if (value < 400) {
//			        return "middle";
//			      } else {
//			        return "big";
//			      }
//			    }
//			    return null; // let graphview generate X-axis label for us
//			  }
//			});
		
		final LinearLayout layout = (LinearLayout) rootView
				.findViewById(R.id.a_graph);
		layout.addView(graphView);

		m_handler = new Handler();
		m_Generator = new Runnable() {

			@Override
			public void run() {
				m_handler.postDelayed(m_Generator, m_interval / 2);
				gvs.appendData(new GraphViewData(dataCounter++, readingLightValue),
						true, maxDataCount);
				gvs_acc.appendData(new GraphViewData(dataCounter++, readingAccValue),
						true, maxDataCount);
			}
		};

	}

	@Override
	public void onResume() {
		super.onResume();
		m_Generator.run();
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
	 * myHealthAssistant sensor reading events.
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

				readingLightValue = Double.parseDouble(Float.toString(lightV));
//				 Log.e(TAG, "received light reading: " +
//				 String.valueOf(lightV));

			} else if (eventType.equals(AccSensorEventKnee.EVENT_TYPE)) {
				AccSensorEventKnee kneeEvt = (AccSensorEventKnee) evt;
				int x = kneeEvt.x_mean;
				int y = kneeEvt.y_mean;
				int z = kneeEvt.z_mean;
				
				readingAccValue = Double.parseDouble(Integer.toString(x + y + z)); // for no specific reason!
			}
		}
	};

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

	@Override
	public void onStop() {
		super.onStop();
		if (m_handler != null && m_Generator != null) {
			m_handler.removeCallbacks(m_Generator);
		}
		Unsubscription unsub = new Unsubscription("PubSubExampleEvent"
				+ eventCounter++, getTimestamp(), "PubSubExample",
				getActivity().getPackageName(), AmbientLightEvent.EVENT_TYPE);

		// publish un-subscription
		publishManagemntEvent(unsub);
		getActivity().unregisterReceiver(myReadingReceiver);

	}
}

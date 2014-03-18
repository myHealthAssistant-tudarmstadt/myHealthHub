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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.adapter;

import java.util.List;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.fragments.SensorConfigFragment.SensorType;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.TextView;

/**
 * 
 * @author HieuHa
 * 
 */
public class SensorListAdapter extends ArrayAdapter<SensorType> {
	private LayoutInflater mInflater;
	private static final int POS_TAG = 1 + 2 << 24;

	public SensorListAdapter(Activity activity, Context ctx) {
		super(ctx, 0);
		mInflater = (LayoutInflater) LayoutInflater.from(activity);
	}

	static class ViewHolder {
		private TextView mTitle;
		private TextView mStatus;
		private CompoundButton mSwitch;
		private TextView mDevice;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View view;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.list_sensor_config_row, parent,
					false);
		} else {
			view = convertView;
		}

		final SensorType item = getItem(position);

//		if (!item.isClickable()) {
//			view.setClickable(false);
//			return view;
//		}
		// Create the view holder
		final ViewHolder viewHolder = new ViewHolder();

		viewHolder.mTitle = ((TextView) view.findViewById(R.id.sensor_name));
		viewHolder.mStatus = ((TextView) view.findViewById(R.id.connect_type));
		viewHolder.mSwitch = (CompoundButton) view.findViewById(R.id.switch1);
		viewHolder.mDevice = ((TextView) view.findViewById(R.id.device_add));

		view.setTag(POS_TAG, position);
		
		if (!item.getDeviceFamily().isEmpty() && !item.getDeviceFamily().equals(getContext().getResources().getString(R.string.dummy_empty))) 
			viewHolder.mTitle.setText(item.getDeviceFamily());
		else
			viewHolder.mTitle.setText(item.getType());
		
		if (!item.hasDevice().isEmpty())
			viewHolder.mDevice.setText(item.hasDevice());
		else 
			viewHolder.mDevice.setText("");
		
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				item.onSetupSensor();
			}
		});

		viewHolder.mStatus.setTextColor(getContext().getResources()
				.getColor((android.R.color.holo_red_light)));
		
		if (item.isActiveModule() && item.isOn()){
			viewHolder.mStatus.setText(R.string.auto_connect);
			MyRunnable mLongPressed = new MyRunnable(view);
			view.setOnTouchListener(new MOnTouch(mLongPressed));
		}
		else 
			viewHolder.mStatus.setText("");
		
		if (item.isCheckable()) {
			viewHolder.mSwitch.setEnabled(true);
			viewHolder.mSwitch.setChecked(item.isEnabled());
			viewHolder.mSwitch.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					boolean t = viewHolder.mSwitch.isChecked();
					item.onEnableEvent(t);
					if (t && item.isActiveModule()){
						MyRunnable mLongPressed = new MyRunnable(view);
						view.setOnTouchListener(new MOnTouch(mLongPressed));
					}
					else {
						view.setOnTouchListener(null);
					}
				}
			});
		}
		else {
			item.onEnableEvent(false);
			viewHolder.mSwitch.setChecked(false);
			viewHolder.mSwitch.setEnabled(false);
		}
		return view;
	}

	public void setData(List<SensorType> data) {
		clear();
		if (data != null) {
			for (int i = 0; i < data.size(); i++) {
				add(data.get(i));
			}
		}
	}

	private class MOnTouch implements OnTouchListener {
		private MyRunnable mLongPressed;

		public MOnTouch(MyRunnable mLongPressed) {
			this.mLongPressed = mLongPressed;
		}

		@Override
		public boolean onTouch(View v, MotionEvent evt) {
			// to dispatch click / long click event,
			// you must pass the event to it's default callback
			// View.onTouchEvent
			boolean defaultResult = v.onTouchEvent(evt);
			switch (evt.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				handler.postDelayed(mLongPressed, 300);
				break;
			}
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP: {
				handler.removeCallbacks(mLongPressed);

			}
			case MotionEvent.ACTION_OUTSIDE: {
				handler.removeCallbacks(mLongPressed);
				break;
			}
			default:
				return defaultResult;
			}

			// if you reach here, you have consumed the event
			return true;
		}
	}


	final Handler handler = new Handler();

	private class MyRunnable implements Runnable {
		private View v;

		public MyRunnable(View v) {
			this.v = v;
		}

		@Override
		public void run() {
			showPopupMenu(v);
		}

	}

	private void showPopupMenu(View v) {
		int pos = (Integer) v.getTag(POS_TAG);
		final SensorType entry = getItem(pos);
		PopupMenu popupMenu = new PopupMenu(this.getContext(), v);
		popupMenu.getMenuInflater().inflate(R.menu.popupmenu,
				popupMenu.getMenu());
		
		popupMenu
				.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						entry.onConnectEvent();
						return true;
					}
				});
		popupMenu.show();
	}
	
}
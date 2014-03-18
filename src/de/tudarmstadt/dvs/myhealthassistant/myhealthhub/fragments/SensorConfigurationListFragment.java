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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.sensormodules.SensorModule;

/**
 * @author Christian Seeger
 *
 */
public class SensorConfigurationListFragment extends ListFragment {
	
	// for debugging
	private static final String TAG = "SensorConfigurationFragment";
	private static boolean D = true;

	// for enabling Bluetooth
	// private BluetoothAdapter mBluetoothAdapter;

	private View rootView;

    String[] countries = new String[] {
            "India",
            "Pakistan",
            "Sri Lanka",
            "China",
            "Bangladesh",
            "Nepal",
            "Afghanistan",
            "North Korea",
            "South Korea",
            "Japan"
    };
	
    private OnTitleSelectedListener listener; 
    
    public interface OnTitleSelectedListener { 
    	public void onTitleSelected(int index); 
    } 
    
    public void setOnTitleSelectedListener(OnTitleSelectedListener listener) { 
    	this.listener = listener; 
    } 
    
    @Override 
    public void onListItemClick(ListView l, View v, int position, long id) { 
    	Log.i("Test", "selected: "+position);
    	listener.onTitleSelected(position); 
    } 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1,countries);
		//setListAdapter(adapter);
		
		SensorModule[] modules = new SensorModule[] {
				new SensorModule("Heart Rate"),
				new SensorModule("Accelerometer"),
				new SensorModule("Scale"),
				new SensorModule("Blood Pressure")
		};
		
		SensorModuleAdapter adapter = new SensorModuleAdapter(inflater.getContext(), R.layout.list_sensor_config_row,modules);
		setListAdapter(adapter);
		
		
		return super.onCreateView(inflater, container, savedInstanceState);		
	}
	

	public class SensorModuleAdapter extends ArrayAdapter<SensorModule> {
		Context context;
		int layoutResourceId;
		SensorModule data[] = null;

		public SensorModuleAdapter(Context context, int layoutResourceId,
				SensorModule[] modules) {
			super(context, layoutResourceId, modules);
			this.layoutResourceId = layoutResourceId;
			this.context = context;
			this.data = modules;
		}
		
		 @Override
		    public View getView(int position, View convertView, ViewGroup parent) {
		        View row = convertView;
		        WeatherHolder holder = null;
		       
		        if(row == null)
		        {
		            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		            row = inflater.inflate(layoutResourceId, parent, false);
		           
		            holder = new WeatherHolder();
		            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
		           
		            row.setTag(holder);
		        }
		        else
		        {
		            holder = (WeatherHolder)row.getTag();
		        }
		       
		        SensorModule weather = data[position];
		        holder.txtTitle.setText(weather.name);
		        //holder.imgIcon.setImageResource(weather.icon);
		       
		        return row;
		    }
		   
		    class WeatherHolder
		    {
		        TextView txtTitle;
		    }
	}


}
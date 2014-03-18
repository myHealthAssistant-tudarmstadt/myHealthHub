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
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.TransformationManager;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.helper.TransformatorListAdapter;
import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.services.transformationmanager.helper.TransformatorListItem;



/**
 * @author Christian Seeger
 *
 */
public class TransformationManagerActivity extends ListActivity {
	
	// For debugging
	private static String TAG = "TransformationManagerActivity";
	private static boolean D = true;
	
	private ArrayList<TransformatorListItem> items;
	private Map<String, TransformatorListItem> mapItems;
	private TransformatorListAdapter adapter;
	
	private Button update;
	
	private Intent mTransformationManagerIntent;
	private TransformationManager.TransformationManagerBinder mTransformationManager;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transformation_manager_activity);
        
    	items = new ArrayList<TransformatorListItem>();
		mapItems = new HashMap<String, TransformatorListItem>();
		adapter = new TransformatorListAdapter(this, R.layout.transformation_list_item_layout,
				items);

		setListAdapter(adapter);
		registerForContextMenu(getListView());
		
		mTransformationManagerIntent = new Intent(this, TransformationManager.class);
		getApplicationContext().bindService(mTransformationManagerIntent, mTransformationManagerConnection, BIND_AUTO_CREATE);
		
		update = (Button)findViewById(R.id.buttonUpdate);
		update.setOnClickListener(myhandler1);		
		
    }
    
    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {
        	org.osgi.framework.Bundle[] bundles = mTransformationManager.getTransformations();
			
			if(bundles != null) {
				items.clear();
				for(org.osgi.framework.Bundle b : bundles) {
					items.add(new TransformatorListItem(b.getBundleId(), b.getSymbolicName(), getStatusText(b.getState())));
				}
				adapter.notifyDataSetChanged();
			}
			
        }
    };
    
    
    private String getStatusText(int state) {
    	switch(state) {
    	case org.osgi.framework.Bundle.ACTIVE:
    		return "active";
    	case org.osgi.framework.Bundle.INSTALLED:
    		return "installed";
    	case org.osgi.framework.Bundle.RESOLVED:
    		return "resolved";
    	case org.osgi.framework.Bundle.STARTING:
    		return "starting";
    	case org.osgi.framework.Bundle.STOPPING:
    		return "stopping";
    	default:
    		return state+"";
    	}
    }
    
	/**
	 * Sets up a connection to the SensorModuleManager.
	 */
	private ServiceConnection mTransformationManagerConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			if(D)Log.d(TAG, "I am connected.");
			mTransformationManager = (TransformationManager.TransformationManagerBinder) binder;
			
			// fill list
			org.osgi.framework.Bundle[] bundles = mTransformationManager.getTransformations();
			
			if(bundles != null) {
				items.clear();
				for(org.osgi.framework.Bundle b : bundles) {
					items.add(new TransformatorListItem(b.getBundleId(), b.getSymbolicName(), getStatusText(b.getState())));
				}
				adapter.notifyDataSetChanged();
			}

			
		}

		public void onServiceDisconnected(ComponentName className) {
		}
	};
    
    @Override
	public void onResume(){
		super.onResume();
		
		if(mapItems != null){
			
			ArrayList<TransformatorListItem> list = new ArrayList<TransformatorListItem>(
					mapItems.values());
			items.clear();
			items.addAll(list);
			
			if(D)Log.i(TAG, "updating the activityList");
			
			adapter.notifyDataSetChanged();
		}
	}
    
    public void onClickUpdate(View v) {
    	org.osgi.framework.Bundle[] bundles = mTransformationManager.getTransformations();
		
		if(bundles != null) {
			items.clear();
			for(org.osgi.framework.Bundle b : bundles) {
				items.add(new TransformatorListItem(b.getBundleId(), b.getSymbolicName(), b.getState()+""));
			}
			adapter.notifyDataSetChanged();
		}
    }
    
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}
    
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuItem
				.getMenuInfo();

		int position = info.position;
		TransformatorListItem currentRowItem = items.get(position);

		Log.i(TAG, "row selected " + currentRowItem);

		switch (menuItem.getItemId()) {
		case R.id.start:

			Log.i(TAG,
					"trying to start the transformator "
							+ currentRowItem.getTransformatorType());
		
			mTransformationManager.startTransformation(currentRowItem.getID());
			
			return true;
		case R.id.stop:
			Log.i(TAG,
					"trying to stop the transformator "
							+ currentRowItem.getTransformatorType());
			
			mTransformationManager.stopTransformation(currentRowItem.getID());
			
			return true;

		case R.id.delete:
			Log.i(TAG,
					"trying to delete the transformator "
							+ currentRowItem.getTransformatorType());
		
			mTransformationManager.deleteTransformation(currentRowItem.getID());
			
			return true;
		default:
			return super.onContextItemSelected(menuItem);
		}

	}

}
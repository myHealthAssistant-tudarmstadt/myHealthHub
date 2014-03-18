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
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.commontools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AndroidExplorer extends ListActivity {
	
	private List<String> item = null;
	private List<String> path = null;
	//private String root="/";
	private String root="/sdcard";
	private TextView myPath;
	
	public static String FILE_PATH;
	public static String FILE_NAME;
		
    /** Called when the ACTIVITY is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.android_explorer);
        myPath = (TextView)findViewById(R.id.path);
        getDir(root);
    }
    
    private void getDir(String dirPath)
    {
    	myPath.setText("Location: " + dirPath);
    	
    	item = new ArrayList<String>();
    	path = new ArrayList<String>();
    	
    	File f = new File(dirPath);
    	File[] files = f.listFiles();
    	
    	if(!dirPath.equals(root))
    	{

    		item.add(root);
    		path.add(root);
    		
    		item.add("../");
    		path.add(f.getParent());
            
    	}
    	
    	for(int i=0; i < files.length; i++)
    	{
    			File file = files[i];
    			path.add(file.getPath());
    			if(file.isDirectory())
    				item.add(file.getName() + "/");
    			else {
   					item.add(file.getName());
    			}
    	}

    	ArrayAdapter<String> fileList =
    		new ArrayAdapter<String>(this, R.layout.android_explorer_row, item);
    	setListAdapter(fileList);
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		File file = new File(path.get(position));
				
		if (file.isDirectory())
		{
			if(file.canRead()) {
				getDir(path.get(position));
				
			} else
			{
				new AlertDialog.Builder(this)
				.setTitle("[" + file.getName() + "] folder can't be read!")
				.setPositiveButton("OK", 
						new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
			}
		}
		else
		{
			/*new AlertDialog.Builder(this)
				.setTitle("[" + file.getName() + "]")
				.setPositiveButton("OK", 
						new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
							}
						}).show();*/
			final String filename = file.getName();
			final String filepath = file.getAbsolutePath();
			final String path = file.getParent();
			
			Log.d("Android Explorer", "Filepath: "+filepath);
			Log.d("Android Explorer", "Filename: "+filename);
									
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Do you want to load \""+file.getName()+"\"?")
			       .setCancelable(false)
			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			    	   
			    	   
			           public void onClick(DialogInterface dialog, int id) {
			        	   Bundle bundle = new Bundle();
			        	   bundle.putString(FILE_NAME, filename);
			        	   bundle.putString(FILE_PATH, filepath);
			        	   Intent mIntent = new Intent();
			        	   mIntent.putExtras(bundle);
			        	   setResult(RESULT_OK, mIntent);
			        	   finish();
			           }
			       })
			       .setNegativeButton("No", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			                finish();
			           }
			       }).show();
		}
	}
}
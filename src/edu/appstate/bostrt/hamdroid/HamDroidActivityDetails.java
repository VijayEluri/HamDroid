/*
 * HamDroidActivityDetails
 * 
 * Shows details of a single HamDroid Activity.
 * 
 * @author Robert Bost <bostrt at appstate dot edu>
 */

package edu.appstate.bostrt.hamdroid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.appstate.bostrt.hamdroid.cursors.ActivityCursor;

public class HamDroidActivityDetails extends Activity
{

	private HamDroidDBAdapter adapter;
	private ActivityCursor cursor;
	private TextView name;
	private TextView startTime;
	private TextView category;
	private TextView endTime;
	private Button stopTrackingButton;
	private Toast toast;
	private long id;

	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);

		setContentView(R.layout.activity_details);

		this.name = (TextView) findViewById(R.id.activity_name);
		this.category = (TextView) findViewById(R.id.activity_category);
		this.startTime = (TextView) findViewById(R.id.activity_start_time);
		this.endTime = (TextView) findViewById(R.id.activity_end_time);
		this.stopTrackingButton = (Button) findViewById(R.id.stop_tracking);
		this.toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		
		this.adapter = new HamDroidDBAdapter(this);
		this.adapter.open();

		// Get the intent that started this activity
		// Extras should contain an _id
		Intent starter = this.getIntent();
		Bundle extras = starter.getExtras();
		Long id = extras.getLong(DBDefines._ID);
		this.id = id;

		// Get the cursor, should only contain a single row
		this.cursor = this.adapter.getActivityCursorById(id);
		
		this.name.setText(this.cursor.getColName());
		
		this.category.setText(this.cursor.getColCategory());
		
		LinearLayout categoryLayout = (LinearLayout) findViewById(R.id.category);
		categoryLayout.setTag(this.cursor.getColCategory());
		categoryLayout.setOnClickListener(this.categoryListener);
		
		this.dateStuff();
	}
	
	private void dateStuff()
	{
		if(!this.cursor.isActivityOver()){
			this.stopTrackingButton.setOnClickListener(this.stopTrackingListener);
			this.stopTrackingButton.setEnabled(true);
		}
		
		
		String start = DateUtils.formatDateTime(this, cursor.getColStartTime()*1000, DateUtils.FORMAT_SHOW_TIME);
		start += ", ";
		start += DateUtils.formatDateTime(this, cursor.getColStartTime()*1000 , DateUtils.FORMAT_SHOW_DATE);
		this.startTime.setText(start);
		
		if(this.cursor.isActivityOver()){
			String end =  DateUtils.formatDateTime(this, cursor.getColEndTime()*1000, DateUtils.FORMAT_SHOW_TIME);
			end += ", ";
			end += DateUtils.formatDateTime(this, cursor.getColEndTime()*1000, DateUtils.FORMAT_SHOW_DATE);
			this.endTime.setText(end);
		} else {
			this.endTime.setText(getString(R.string.activity_running));
			this.endTime.setTextColor(Color.rgb( 88, 88, 88));
		}
	}

	/**
	 * Close up Database stuff
	 */
	public void onStop()
	{
		super.onStop();
		this.cursor.close();
		this.adapter.close();
	}
	
	public void onRestart()
	{
		super.onRestart();
		this.adapter.open();
		this.cursor = this.adapter.getActivityCursorById(id);
		this.dateStuff();
	}
	
	private View.OnClickListener categoryListener = new View.OnClickListener() 
	{	
		public void onClick(View v) {
			Intent i = new Intent(HamDroidActivityDetails.this, CategoryDetails.class);
			i.putExtra(DBDefines.CATEGORY, (String) v.getTag());
			startActivity(i);
		}
	};
	
	private View.OnClickListener stopTrackingListener = new View.OnClickListener() {
		public void onClick(View v) {
			boolean result = adapter.endActivity(cursor.getColId());
			if (result) {
				Log.i("HamDroid", "Activity ended");
			} else {
				toast.setText(getString(R.string.activity_over_toast));
				toast.show();
			}			
		}
	};

}

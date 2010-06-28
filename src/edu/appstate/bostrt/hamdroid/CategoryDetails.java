package edu.appstate.bostrt.hamdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import edu.appstate.bostrt.hamdroid.cursors.ActivityCursor;

public class CategoryDetails extends Activity
{
	private HamDroidDBAdapter adapter;
	private ActivityCursor cursor;
	private TableLayout activityList;
	private String categoryName;
	
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		setContentView(R.layout.category_details);
		
		Intent starter = this.getIntent();
		Bundle extras = starter.getExtras();
		categoryName = extras.getString(DBDefines.CATEGORY);
		
		// Create adapter and get cursor by category name
		this.adapter = new HamDroidDBAdapter(this);
		this.adapter.open();
		this.cursor = this.adapter.getActivityCursorByCategory(categoryName);

		this.insertList();
	}
	
	private void insertList()
	{
		this.activityList = (TableLayout) findViewById(R.id.activity_list);
		this.activityList.removeAllViews();
		
		// Set header
		((TextView)findViewById(R.id.category_details_header)).setText(categoryName);
		
		LayoutInflater li = this.getLayoutInflater();
		
		// Show listing of ActivityCursor
		while(!this.cursor.isAfterLast()){
			TableRow tr = (TableRow) li.inflate(R.layout.activity_list_item, null);
			
			TextView tvName = (TextView) tr.findViewById(R.id.activity_list_item_name);
			TextView tvDuration = (TextView) tr.findViewById(R.id.activity_list_item_duration);
			
			// Add green light if this is the current activity being tracked
			if(!cursor.isActivityOver()){
				ImageView ivStatus = new ImageView(this);
				ivStatus.setImageResource(R.drawable.available);
				ivStatus.setAdjustViewBounds(true);
				ivStatus.setMaxHeight(30);
				ivStatus.setScaleType(ScaleType.CENTER_INSIDE);
				tr.addView(ivStatus);
			}
			
			tvName.setText(this.cursor.getColName());
			tvDuration.setText(this.cursor.getDuration());
			
			tr.setOnClickListener(this.listRowListener);
			tr.setTag(cursor.getColId());
			
			this.activityList.addView(tr);
			
			this.cursor.moveToNext();
		}		
	}
	
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
		this.cursor = this.adapter.getActivityCursorByCategory(categoryName);
		this.insertList();
	}
	
	/**
	 * This OnClickListener will watch the activity list items If one is clicked
	 * then a new Android Activity is started, showing details for that activity
	 * being tracked.
	 */
	private final OnClickListener listRowListener = new OnClickListener() {
		public void onClick(View v)
		{
			Intent i = new Intent(CategoryDetails.this, HamDroidActivityDetails.class);
			i.putExtra(DBDefines._ID, (Long) v.getTag());
			startActivity(i);
		}
	};
}

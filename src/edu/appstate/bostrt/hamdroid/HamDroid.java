/*
 * HamDroid
 * 
 * HamDroid is a time-tracking application for Android.
 * My goal is to make HamDroid interface-able with another
 * time-tracking applet for the GNOME Desktop called Hamster.
 * 
 * http://projecthamster.wordpress.com/
 * 
 * This is the main Activitiy for HamDroid.  Visible is a TextView
 * for adding a new activity to be tracked.  Below that is a button
 * for submitting your new activity and a list of the activities you
 * have tracked today, both currently tracked activities and ones that 
 * you have ended.
 * 
 * @author Robert Bost <bostrt at appstate dot edu>
 * 
 */

package edu.appstate.bostrt.hamdroid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher.ViewFactory;
import edu.appstate.bostrt.hamdroid.cursors.ActivityCursor;

public class HamDroid extends Activity
{
	private EditText editActivity;
	private EditText editCategory;
	private Button submitActivityButton;

	private HamDroidDBAdapter adapter;
	
	private Toast toast;
	
	/**
	 * Create the view, create the list of activities, assign Listeners and
	 * what-not.
	 */
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		setContentView(R.layout.new_activity);

		// Open the DBAdapter
		this.adapter = new HamDroidDBAdapter(this);
		this.adapter.open();

		// Initialize views
		this.editActivity = (EditText) findViewById(R.id.text_new_activity);
		this.editCategory = (EditText) findViewById(R.id.text_category);
		this.submitActivityButton = (Button) findViewById(R.id.btnConfirmNewActivity);
		
		// Initialize toast
		this.toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

		// Set listeners
		this.editActivity.setOnKeyListener(this.textViewListener);
		this.editCategory.setOnKeyListener(this.textViewListener);
		this.submitActivityButton.setOnClickListener(submitListener);

		// Request focus to text input
		this.editActivity.requestFocus();

		// Show the list of activities
		this.insertActivityList();
	}

	/**
	 * TODO: Do something with these later
	 */
	public void onPause()
	{
		super.onPause();
	}

	public void onResume()
	{
		super.onResume();
		this.adapter.open();
		this.insertActivityList();
	}

	public void onStop()
	{
		super.onStop();
		this.adapter.close();
	}

	/**
	 * Create the pop-up menu at the bottom
	 */
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add("Refresh").setIcon(R.drawable.viewrefresh);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem i)
	{
		if (i.getTitle().equals("Refresh")) {
			this.insertActivityList();
			return true;
		}
		return false;
	}

	/**
	 * Create the Context Menu
	 */
	public void onCreateContextMenu(ContextMenu m, View v,
			ContextMenu.ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(m, v, menuInfo);
		int id = ((Long) v.getTag()).intValue();
		m.add(0, id, 0, getString(R.string.stop_tracking));
	}

	/**
	 * TODO: Make this better. Should be able to handle multiple options.
	 */
	public boolean onContextItemSelected(MenuItem m)
	{
		boolean result = this.adapter.endActivity(m.getItemId());
		this.insertActivityList();
		if (result) {
			Log.i("HamDroid", "Activity ended");
			return true;
		} else {
			toast.setText(getString(R.string.activity_over_toast));
			toast.show();
		}
		this.insertActivityList();
		return false;
	}

	/**
	 * Private method that puts the list of activities in the ListView
	 */
	private void insertActivityList()
	{
		// Get activities from the database
		ActivityCursor cursor = this.adapter.getActivityCursor();

		if (cursor.getCount() < 1) {
			return;
		}

		TableLayout activityList = (TableLayout) findViewById(R.id.activity_list);

		// Unregister from Context menu
		for (int i = 0; i < activityList.getChildCount(); i++) {
			View v = activityList.getChildAt(i);
			unregisterForContextMenu(v);
		}

		activityList.removeAllViews();

		LayoutInflater li = this.getLayoutInflater();

		while (!cursor.isAfterLast()) {
			TableRow tr = (TableRow) li.inflate(R.layout.activity_list_item,
					null);
			TextView tvName = (TextView) tr
					.findViewById(R.id.activity_list_item_name);
			TextView tvDuration = (TextView) tr
					.findViewById(R.id.activity_list_item_duration);
			
			// Add green light if this is the current activity being tracked
			if(!cursor.isActivityOver()){
				ImageView ivStatus = new ImageView(this);
				ivStatus.setImageResource(R.drawable.available);
				ivStatus.setAdjustViewBounds(true);
				ivStatus.setMaxHeight(30);
				ivStatus.setScaleType(ScaleType.CENTER_INSIDE);
				tr.addView(ivStatus);
			}

			tr.setOnClickListener(listRowListener);
			
			tr.setTag(cursor.getColId());

			tvName.setText(cursor.getColName());
			tvDuration.setText(cursor.getDuration());

			activityList.addView(tr);
			cursor.moveToNext();
			registerForContextMenu(tr);
		}
		cursor.close();
	}

	/**
	 * This OnClickListener will watch the activity list items If one is clicked
	 * then a new Android Activity is started, showing details for that activity
	 * being tracked.
	 */
	private final OnClickListener listRowListener = new OnClickListener() {
		public void onClick(View v)
		{
			Intent i = new Intent(HamDroid.this, HamDroidActivityDetails.class);
			i.putExtra(DBDefines._ID, (Long) v.getTag());
			startActivity(i);
		}
	};

	/**
	 * Delete helper text in TextView when user starts typing. Enter key submits
	 * activity.
	 */
	private final OnKeyListener textViewListener = new OnKeyListener() {
		public boolean onKey(View v, int keyCode, KeyEvent event)
		{

			switch (event.getKeyCode())
			{
			case KeyEvent.KEYCODE_BACK:
				return false;
			case KeyEvent.KEYCODE_ENTER:
				submitActivityButton.performClick();
				return true;
			default:
				return false;
			}
		}
	};

	/**
	 * Submit the new activity. Don't accept the string if it is the helper text
	 * though.
	 */
	private final OnClickListener submitListener = new OnClickListener() {
		public void onClick(View v)
		{
			String name = editActivity.getText().toString();
			String category = editCategory.getText().toString();
			// Don't submit the hint text
			if (!name.equals(editActivity.getHint())) {
				// Make sure the TextView isn't emtpy
				if (name.length() > 0) {
					adapter.insertActivity(name, category);
					// Update the list of activities
					insertActivityList();
					
					editActivity.setText("");
					editCategory.setText("");
				}
			}
		}
	};
}

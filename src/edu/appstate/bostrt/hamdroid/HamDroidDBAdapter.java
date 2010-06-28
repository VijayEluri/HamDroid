/*
 * HamDroidDBAdapter
 * 
 * Provides an easier way to access the database.
 * Helps prevent SQL being everywhere.
 * 
 * @author Robert Bost <bostrt at appstate dot edu>
 */

package edu.appstate.bostrt.hamdroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import edu.appstate.bostrt.hamdroid.cursors.ActivityCursor;
import edu.appstate.bostrt.hamdroid.DBDefines;

public class HamDroidDBAdapter 
{

	
	private final Context context;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	
	
	public HamDroidDBAdapter(Context context)
	{
		this.context = context;		
	}
	
	public HamDroidDBAdapter open()
	{
		this.dbHelper = new DatabaseHelper(this.context);
		this.db = dbHelper.getWritableDatabase();
		Log.i("HamDroid", "Database opened");
		return this;
	}
	
	public void close()
	{
		this.dbHelper.close();
		Log.i("HamDroid", "Database closed");
	}
	
	public ActivityCursor getActivityCursorById(long id)
	{				
		ActivityCursor c = (ActivityCursor) this.db.rawQueryWithFactory(
												new HamDroidCursorFactory(), 
												"SELECT * FROM " + DBDefines.ACTIVITIES_TABLE + " WHERE _id = "+id,
												null,
												DBDefines.ACTIVITIES_TABLE);
		c.moveToFirst();
		
		return c;
	}
	
	/**
	 *  Get ActivityCursor. Traverse cursor, adding each 'name'
	 *  column to a String array.   
	 *  
	 * @return String[] - Array of activities
	 */
	public ActivityCursor getActivityCursor()
	{
 		ActivityCursor c = (ActivityCursor) this.db.rawQueryWithFactory(
							new HamDroidCursorFactory(),
							ActivityCursor.QUERY_SPLAT_BY_START_TIME,
							null, DBDefines.ACTIVITIES_TABLE);
 		
 		// Make sure it's at the first position
		c.moveToFirst();
		
		return c;
	}
	
	public ActivityCursor getTodaysActivityCursor()
	{
		return null;
	}
	
	public ActivityCursor getRunningActivityCursor()
	{
		String sql = "SELECT * FROM " + DBDefines.ACTIVITIES_TABLE + " WHERE " + DBDefines.END_TIME + " is NULL";
		ActivityCursor c = (ActivityCursor) this.db.rawQueryWithFactory(new HamDroidCursorFactory(),
																		sql,
																		null,
																		DBDefines.ACTIVITIES_TABLE);
		c.moveToFirst();
		
		return c;
	}
	
	public ActivityCursor getActivityCursorByCategory(String cat)
	{
		String sql = "SELECT * FROM " + DBDefines.ACTIVITIES_TABLE + " WHERE " + DBDefines.CATEGORY + " = '" + cat+"'";
		
		ActivityCursor c = (ActivityCursor) this.db.rawQueryWithFactory(new HamDroidCursorFactory(),
																		sql,
																		null,
																		DBDefines.ACTIVITIES_TABLE);
		c.moveToFirst();
		
		return c;
	}
	
	public void deleteAllActivities()
	{
		try{
			this.db.delete(DBDefines.ACTIVITIES_TABLE, null, null);
			Log.d("HamDroid", "Successfully deleted all entries from "+DBDefines.ACTIVITIES_TABLE);
		} catch(SQLException e)
		{
			Log.e("HamDroid", "Exception thrown while delete all entried from: "+DBDefines.ACTIVITIES_TABLE);
		}
	}
	
	/**
	 * 
	 * @param name - Name of new activity
	 * @param category - String category for activity
	 * @return
	 */
	public long insertActivity(String name, String category)
	{
		ContentValues activityCV = new ContentValues();
		
		activityCV.put(DBDefines.NAME, name);
		activityCV.put(DBDefines.START_TIME, HamDroidUtil.getCurrentTime());
		
		if(category.length() < 1){
			category = "Unsorted";
		}
		activityCV.put(DBDefines.CATEGORY, category);
		
		// Insert new activity into table
		long activityResult = this.db.insert(DBDefines.ACTIVITIES_TABLE, null, activityCV);

		if(activityResult == DBDefines.SQLITE_ERROR){
			Log.e("HamDroid", "Error while inserting new activity");
			throw new SQLException("Error while inserting new activity");
		}
		
		// New activity is being tracked, end last tracked activity
		ActivityCursor c = this.getRunningActivityCursor();
		
		long id = c.getColId();
			
		if(id != activityResult){
			this.endActivity(id);
		}
		
		c.close();
		
		return activityResult;
	}
	
	public boolean endActivity(long id)
	{
		ActivityCursor a = this.getActivityCursorById(id);
		long time = HamDroidUtil.getCurrentTime();
		boolean result = a.setColEndTime(time);

		return result;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DBDefines.DATABASE_NAME, null, DBDefines.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DBDefines.CREATE_ACTIVITIES_TABLE);
			}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
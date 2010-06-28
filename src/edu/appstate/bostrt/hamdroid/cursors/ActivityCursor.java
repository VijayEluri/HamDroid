package edu.appstate.bostrt.hamdroid.cursors;

import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;
import edu.appstate.bostrt.hamdroid.DBDefines;
import edu.appstate.bostrt.hamdroid.HamDroidDBAdapter;
import edu.appstate.bostrt.hamdroid.HamDroidUtil;

public class ActivityCursor extends SQLiteCursor
{

	public static final String QUERY_SPLAT_BY_START_TIME = "SELECT * "
			+ " FROM " + DBDefines.ACTIVITIES_TABLE + " order by "
			+ DBDefines.START_TIME + " desc";

	public ActivityCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
			String editTable, SQLiteQuery query)
	{
		super(db, driver, editTable, query);
	}

	/**
	 * Get the duration of the activity the cursor is looking at.
	 * 
	 * @return String - Time in seconds;
	 */
	public String getDuration()
	{
		// Return duration from start to now if end_time hasn't been set
		if (this.getColEndTime() < 1) {
			return HamDroidUtil.getFormattedDuration(this.getColStartTime());
		}
		// Otherwise, use start_time and end_time
		else {
			return HamDroidUtil.getFormattedDuration(this.getColStartTime(),
					this.getColEndTime());
		}
	}

	/**
	 * Get name column
	 * 
	 * @return String - value of name column for current position of cursor
	 */
	public String getColName()
	{
		return (String) this.getCol(DBDefines.NAME);
	}

	/**
	 * Get _id column
	 * 
	 * @return long - value of _id column for current position of cursor
	 */
	public long getColId()
	{
		return (Long) this.getCol(DBDefines._ID);
	}

	/**
	 * Get start_time column
	 * 
	 * @return long - value of start_time column for current position of cursor
	 */
	public long getColStartTime()
	{
		return (Long) this.getCol(DBDefines.START_TIME);
	}

	/**
	 * Get end_time column
	 * 
	 * @return long - value of end_time column for current position of cursor
	 */
	public long getColEndTime()
	{
		return (Long) this.getCol(DBDefines.END_TIME);
	}
	
	public String getColCategory()
	{
		return (String) this.getCol(DBDefines.CATEGORY);
	}

	/**
	 * Set name column
	 * 
	 * @param time
	 *            - time in seconds
	 * @return boolean - pass/fail, true == pass, false == fail
	 */
	public boolean setColEndTime(long time)
	{
		if (!isActivityOver()) {
			long row = this.mCurrentRowID;

			SQLiteDatabase db = this.getDatabase();

			String query = "UPDATE " + DBDefines.ACTIVITIES_TABLE
					+ " SET " + DBDefines.END_TIME + " = "
					+ time + " WHERE " + DBDefines._ID
					+ " = " + row;
			try {
				db.execSQL(query);
				return true;
			} catch (SQLException e) {
				Log.e("HamDroid", "Exception thrown updating "
						+ DBDefines.ACTIVITIES_TABLE);
				return false;
			}
		} else {
			return false;
		}

	}

	/**
	 * 
	 * @return boolean - true if activity has ended, false if activity is
	 *         running.
	 */
	public boolean isActivityOver()
	{
		if (this.getColEndTime() < 1) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Private method, gets column index and tests if it exists
	 * 
	 * @param col - Column name as string
	 */
	private Object getCol(String col)
	{
		// getColumnIndex() returns -1 if column does not exist
		int index = getColumnIndex(col);

		if (col.equals(DBDefines._ID)) {
			return getLong(index);
		} else if (col.equals(DBDefines.NAME)) {
			return getString(index);
		} else if (col.equals(DBDefines.CATEGORY)){
			return getString(index);
		}else if (col.equals(DBDefines.START_TIME)) {
			return getLong(index);
		} else if (col.equals(DBDefines.END_TIME)) {
			return getLong(index);
		}
		return null;
	}

}// end class

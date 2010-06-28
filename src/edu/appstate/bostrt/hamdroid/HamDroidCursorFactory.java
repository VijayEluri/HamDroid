package edu.appstate.bostrt.hamdroid;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import edu.appstate.bostrt.hamdroid.cursors.ActivityCursor;

public class HamDroidCursorFactory implements SQLiteDatabase.CursorFactory{

	public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
			String editTable, SQLiteQuery query) 
	{
		return new ActivityCursor(db, driver, editTable, query);
	}
}

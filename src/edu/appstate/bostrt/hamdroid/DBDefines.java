package edu.appstate.bostrt.hamdroid;

public class DBDefines {

	// Errors thrown by SQL statements
	public static final int NULL_COLUMN   = -1;
	public static final long SQLITE_ERROR = -1;
	
	// Database info
	public static final String DATABASE_NAME    = "hamdroid.db";
	public static final int DATABASE_VERSION    = 1;

	// Table names
	public static final String ACTIVITIES_TABLE 	 = "activities";
	
	// Activities table columns
	public static final String _ID  		= "_id";
	public static final String NAME 		= "name";
	public static final String START_TIME   = "start_time";
	public static final String END_TIME   	= "end_time";
	public static final String CATEGORY		= "category";

	
	// Table creation statements
	public static final String CREATE_ACTIVITIES_TABLE = "CREATE TABLE activities (" +
														"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
														"name TEXT NOT NULL, " +
														"start_time INTEGER NOT NULL," +
														"end_time INTEGER," +
														"category TEXT);";

}
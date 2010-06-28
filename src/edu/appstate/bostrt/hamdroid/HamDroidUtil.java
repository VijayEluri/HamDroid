/*
 * HamDroidUtil
 * 
 * This class has a bunch of methods that don't really fit anywhere else.
 * Mostly time related methods.
 * 
 * @author Robert Bost <bostrt at appstate dot edu>
 */

package edu.appstate.bostrt.hamdroid;

import android.text.format.Time;

public final class HamDroidUtil {
	
	private static final Time time = new Time();
	
	/**
	 * Get the time difference between a beginning time and an end time
	 * 
	 * @param begin - Start time in seconds
	 * @param end   - Ending time in seconds
	 * @return String - formatted duration
	 */
	public static String getFormattedDuration(long begin, long end)
	{
		long dT = end-begin;
		return HamDroidUtil.formatSecondsToHHMM(dT);
	}
	
	/**
	 * Get get time difference between now and a beginning time
	 * 
	 * @param begin - Start time in seconds
	 * @return String - formatted duration 
	 */
	public static String getFormattedDuration(long begin)
	{
		long now = HamDroidUtil.getCurrentTime();		
		return HamDroidUtil.getFormattedDuration(begin, now);
	}
	
	/**
	 * Converts seconds into hours and minutes. 
	 * 
	 * @param seconds - time in seconds 
	 * @return String - in the form of <hours>h <minutes>min
	 */
	public static String formatSecondsToHHMM(long seconds)
	{
		long m = (seconds/60)%60;
		long h = (seconds/60)/60;
		String hhmm = "";
		
		if( h > 0 )
		{
			hhmm += h+"h";
		}
		if( m > 0 )
		{
			hhmm += " ";
			hhmm += m+"min";
		}
		if(hhmm.length() < 1){
			hhmm += "1min";
		}
		return hhmm;
	}
	
	/**
	 * Get the current time in seconds since January 1, 1970
	 * 
	 * @return long - see description...
	 */
	public static long getCurrentTime()
	{		
		time.setToNow();

		long now = time.toMillis(false)/1000;
		
		return now;
	}
}

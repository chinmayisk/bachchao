package com.example.testproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.widget.Toast;

public class AutoStart extends BroadcastReceiver{
	 private static final int PERIOD=900000; 
	 public static final String PREFS = "BachchaoPrefsFile";
	 SharedPreferences settings;
	 
	@Override
	public void onReceive(Context context, Intent arg1) {
		 
		settings = context.getSharedPreferences(PREFS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("timeelapsed",-1);
		editor.commit();
			 
		AlarmManager alarmmgr=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	    Intent i=new Intent(context, Alarm.class);
	    PendingIntent pi=PendingIntent.getBroadcast(context, 0,
	                                              i, 0);
	    Toast.makeText(context, "On start of phone ", Toast.LENGTH_LONG).show();
	    alarmmgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
	                      SystemClock.elapsedRealtime(),
	                      PERIOD,
	                      pi);
	  
		
	}

	
}

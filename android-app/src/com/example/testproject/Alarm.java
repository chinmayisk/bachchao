package com.example.testproject;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.widget.Toast;

public class Alarm extends BroadcastReceiver 
{    
	SharedPreferences settings;
	public static final String PREFS = "BachchaoPrefsFile";
	private static final int MAX_CNUMBER = 3;
	 int updatetime;
     int timeelapsed;
     String updatemsg;
     @Override
     public void onReceive(Context context, Intent intent) 
     {   
         PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
         PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
         wl.acquire();
         settings = context.getSharedPreferences(PREFS, 0);
	     updatetime =  settings.getInt("locationupdateinterval", 0);
	     timeelapsed = settings.getInt("timeelapsed", -1); 
	      updatemsg = settings.getString("updatemsg", "I am at");
	     if (updatetime != 0)
	     {
	    	 if(timeelapsed == updatetime*6*600000 || timeelapsed == -1)
	    	 {  
	    		 if(timeelapsed!=-1)
	    		 readLocation(context);
	    		 timeelapsed = 900000;
	    		 
	    	 }else
	    	 {
	    		 timeelapsed +=900000 ;
		 
	    	 }
	    	 SharedPreferences.Editor editor= settings.edit();
 			 editor.putInt("timeelapsed",timeelapsed);
 			 editor.commit();
 		
	     }
         wl.release();
     }

 public void SetAlarm(Context context, int updatetime)
 {
     AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
     Intent i = new Intent(context, Alarm.class);
     PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
     am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 10 *6 , pi); // Millisec * Second * Minute
 }

 public void CancelAlarm(Context context)
 {
     Intent intent = new Intent(context, Alarm.class);
     PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
     AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
     alarmManager.cancel(sender);
 }
  
 private void readLocation(Context context)
 {
	  LocationFinder gps = new LocationFinder(context);
      
	     
      // check if GPS enabled
      if(gps.locationavailable()){
          double latitude = gps.getLatitude();
          double longitude = gps.getLongitude();
          for(int i =0 ; i< MAX_CNUMBER ; i++)
  		{
        	  sendsms(settings.getString("contactnumber"+i, null),updatemsg+latitude+","+longitude+".", context);
  		}
  	}
 }
 protected void sendsms(String Phnumber,String message,Context context)
	{
		
	try {
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(Phnumber, null, message, null, null);
		//Toast.makeText( context,"SMS sent to"+Phnumber,Toast.LENGTH_LONG).show();
		
		
	} catch (Exception e) {		
		e.printStackTrace();
	}
	}
}


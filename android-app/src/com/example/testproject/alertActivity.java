package com.example.testproject;



import java.util.Timer;

import com.example.testproject.LocationFinder;
import android.location.Criteria;
import android.location.LocationManager;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Window;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;




public class alertActivity extends Activity {
	SharedPreferences settings;
	int updatetime;
	public static final String PREFS = "BachchaoPrefsFile";
	private static final int MAX_CNUMBER = 3;
	boolean gpsStatus = false;
	boolean networkStatus = false;
	double longitude;
	double latitude;
	String alertmsg;
	private static final int REQUEST_CODE = 0;
	//LocationManager locationManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		gpsSwitch();
		 settings = getSharedPreferences(PREFS, 0);
	       updatetime =  settings.getInt("locationupdateinterval", 0);
	      // Toast.makeText(getApplicationContext(), "The interval for update of location is selected as :"+updatetime + "hours", Toast.LENGTH_LONG).show();
	       LocationFinder gps = new LocationFinder(this);
	       alertmsg = settings.getString("alertmsg", "Help!I am in an emergency at" );
	     
         // check if GPS enabled
         if(gps.locationavailable()){
              latitude = gps.getLatitude();
              longitude = gps.getLongitude();

            // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
         }else{
        	
        	 /*LocationManager service = (LocationManager)getSystemService(LOCATION_SERVICE);
        		boolean enabled = service
        		  .isProviderEnabled(LocationManager.GPS_PROVIDER);

        		// Check if enabled and if not send user to the GPS settings
        		// Better solution would be to display a dialog and suggesting to 
        		// go to the settings
        		if (!enabled) {
        		  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        		  startActivity(intent);
        		  
        		}
        	
        	//startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
        	 */
        	 // this is an exploitation of the bug but an user should be asked before setting the gps on 
        	 
        	final Intent poke = new Intent();
             poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
             poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
             poke.setData(Uri.parse("3")); 
             sendBroadcast(poke);
             latitude = gps.getLatitude();
             longitude = gps.getLongitude();

            // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
              }
         	
         for(int i =0 ; i< MAX_CNUMBER ; i++)
   		{
        	if(settings.getString("contactnumber"+i, null)!=null)
           sendsms(settings.getString("contactnumber"+i, null),alertmsg+" "+latitude+","+longitude+".");
   		}
         
			 Intent myIntent = new Intent(this, digitalwitness.class);
			 Bundle b = new Bundle();
		     b.putDouble("latitude", latitude);
		     b.putDouble("longitude",longitude);
		     myIntent.putExtras(b);
		     myIntent.putExtra("android.intent.extra.durationLimit", 650000);
			this.startActivityForResult(myIntent,0);
			
}
	  private void gpsSwitch()
	   	{
	   		LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
	   		gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	   		networkStatus = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	   			
	   		if (!gpsStatus && !networkStatus) {	
	   			final Intent poke = new Intent();
	               poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
	               poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	               poke.setData(Uri.parse("3")); 
	               sendBroadcast(poke);
	   			
	   		}
	   	}

		protected void sendsms(String Phnumber,String message)
		{
			
		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(Phnumber, null, message, null, null);
			//Toast.makeText(getApplicationContext(),	"SMS sent to"+Phnumber,Toast.LENGTH_LONG).show();
	
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),	"SMS failed, please try again later!",Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		}
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			 
			  finish();
		  }
		}
}

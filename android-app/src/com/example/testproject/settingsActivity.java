package com.example.testproject;

import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class settingsActivity extends Activity {
	private static final int REQUEST_CODE = 0;
	Button contact1, contact2, contact3,incduration,decduration ; 
	TextView durval;
	EditText alertmsg , updatemsg;
	public static final String PREFS = "BachchaoPrefsFile";
	public static int MAX_CNUMBER = 3;
	 SharedPreferences settings;
	 Intent returndata;
	 contactsarray[] contactsinfo;
	  boolean gpsStatus = false;
	  boolean networkStatus = false;
	  int durationvalindex;

	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			
			setContentView(R.layout.settings_view); 
			ActionBar actionBar = getActionBar();
			actionBar.hide();
			    // add the custom view to the action bar
		   actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.settingsheader));
		   actionBar.setIcon(R.drawable.return_icon_settingspage);
		   actionBar.setTitle("");
		   actionBar.show();
		   actionBar.setHomeButtonEnabled(true);
		   contactsinfo= new contactsarray[MAX_CNUMBER];
		   for (int i=0;i< contactsinfo.length;i++)
			 contactsinfo[i] = new contactsarray();  
		   settings = getSharedPreferences(PREFS, 0);
		   setInfo();
	      
	       getElementsById();
	       buttonClickListener();
	       gpsSwitch();	   
	}

	
	private void getElementsById()
	{
		contact1 = (Button)findViewById(R.id.contact1);
		contact2 = (Button)findViewById(R.id.contact2);
		contact3 = (Button)findViewById(R.id.contact3);
		incduration = (Button)findViewById(R.id.increasedur);
		decduration = (Button)findViewById(R.id.decreasedur);
		
		
		if(contactsinfo[0].contactname!=null)
		{
			contact1.setText(contactsinfo[0].contactname);
			//contact1.setBackground(getResources().getDrawable(R.drawable.savedcontactbox));
		}
		if(contactsinfo[1].contactname!=null)
		{
			contact2.setText(contactsinfo[1].contactname);
			//contact2.setBackground(getResources().getDrawable(R.drawable.savedcontactbox));
		}
		if(contactsinfo[2].contactname!=null)
		{
			contact3.setText(contactsinfo[2].contactname);
			//contact3.setBackground(getResources().getDrawable(R.drawable.savedcontactbox));
		}
		
	 
		durationvalindex = settings.getInt("locationupdateindex", 0);
		durval = (TextView)findViewById(R.id.duration);
		Resources res = getResources();
		TypedArray durationarray = res.obtainTypedArray(R.array.updateInterval);
		String durationval = durationarray.getString(durationvalindex);
		durval.setText(durationval);
		
		alertmsg = (EditText)findViewById(R.id.customalertmsg);
		String custommsg = settings.getString("alertmsg", "Help!I am in an emergency at ");
		alertmsg.setText(custommsg);
		String upmsg = settings.getString("updatemsg", "I am at");
		updatemsg =(EditText)findViewById(R.id.trackmsg);
		updatemsg.setText(upmsg);
		// set the particular update
	}
	
	
	private void setInfo()
	{
		for(int i =0 ; i< MAX_CNUMBER ; i++)
		{
			contactsinfo[i].contactname = settings.getString("contactname"+i, null);
			contactsinfo[i].contactnumber = settings.getString("contactnumber"+i, null);
			contactsinfo[i].contactid = settings.getString("contactid"+i, null);
		}
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
	
	
	private void buttonClickListener()
	{
		contact1.setOnClickListener(new OnClickListener()
	     {
			 @Override
			public void onClick(View w)
			 { 
				 
				 loadcontacts(w.getId(), contact1.getText().toString(),1);
				 
			 }
			
	     });
		
		contact2.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View w)
			{
				
				loadcontacts(w.getId(),contact2.getText().toString(),2);
			}
		});
		
		contact3.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View w)
				{
					
					loadcontacts(w.getId(),contact3.getText().toString(),3);
					
				}
		});
		
		incduration.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View w)
			{
				if(durationvalindex< 5 )
				{
					Resources res = getResources();
					SharedPreferences.Editor editor = settings.edit();
					editor.putInt("locationupdateindex",++durationvalindex);
					editor.putInt("timeelapsed",-1);
					TypedArray durationval = res.obtainTypedArray(R.array.updateIntervalValues);
					int durationvalue = durationval.getInt(durationvalindex,0);
					editor.putInt("locationupdateinterval", durationvalue);
					editor.commit();
					
					TypedArray durationarray = res.obtainTypedArray(R.array.updateInterval);
					String durvl = durationarray.getString(durationvalindex);
					durval.setText(durvl);
					
				}
				
			}
		});
		
		decduration.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View w)
			{
				if(durationvalindex > 0 )
				{
					Resources res = getResources();
					SharedPreferences.Editor editor = settings.edit();
					editor.putInt("locationupdateindex",--durationvalindex);
					TypedArray durationval = res.obtainTypedArray(R.array.updateIntervalValues);
					int durationvalue = durationval.getInt(durationvalindex,0);
					editor.putInt("locationupdateinterval", durationvalue);
					editor.putInt("timeelapsed",-1);
					editor.commit();
					
					TypedArray durationarray = res.obtainTypedArray(R.array.updateInterval);
					String durvl = durationarray.getString(durationvalindex);
					durval.setText(durvl);
					
				}
				
			}
		});
				
		
	}
	
	private void rungpsService()
	{
		 
		AlarmManager alarmmgr=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
	    Intent i=new Intent(getApplicationContext(), Alarm.class);
	    PendingIntent pi=PendingIntent.getBroadcast(getApplicationContext(), 0,
	                                              i, 0);
	    
	    alarmmgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
	                      SystemClock.elapsedRealtime(),
	                      900000,
	                      pi);
	}
	
	private void loadcontacts(int id,String contactname, int num)
	{
		Intent myIntent = new Intent(this, contactslistActivity.class);
		Bundle b = new Bundle();
        b.putInt("Triggeritem", id);
        b.putString("contactname", contactname);
        
        for(int i =0 ; i< MAX_CNUMBER ; i++)
		{
        	b.putString("contact"+i, contactsinfo[i].contactid );
		}
        b.putInt("triggernum",num);
        b.putBoolean("contactexists",contactsinfo[num-1].contactid==null);
         myIntent.putExtras(b);
		 this.startActivityForResult(myIntent,REQUEST_CODE);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	  
	   switch (item.getItemId()) 
	   {
	     case android.R.id.home:
	    	 saveSettings();
	    	 finish();
	        return true;
	     default:
	        return super.onOptionsItemSelected(item);
	   }
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
	    if (data.hasExtra("triggeritem")) {
	   
	    	Button contactbutton = (Button)findViewById(data.getExtras().getInt("triggeritem"));
	   
	    	//contactbutton.setBackground(getResources().getDrawable(R.drawable.savedcontactbox));
	    	contactbutton.setText(data.getExtras().getString("contactname"));
	   
	   returndata = data;
	  if (returndata != null)
		{
			
			int i;
			
			if (returndata.getExtras().getInt("triggeritem") == R.id.contact1)
			     i = 0 ;
			 else if(returndata.getExtras().getInt("triggeritem")==R.id.contact2)
				i=1;
			else 
				i= 2; 	
		
			 contactsinfo[i].contactname =  returndata.getExtras().getString("contactname");
			 contactsinfo[i].contactnumber =  returndata.getExtras().getString("contactnumber");
			 contactsinfo[i].contactid =  returndata.getExtras().getString("contactid");
	    
	   
		}
	    }
	  }
	} 
	@Override
	public void onBackPressed() {
		saveSettings();	
    	finish();
    	super.onBackPressed();
    }


	private void saveSettings() {
		SharedPreferences.Editor editor = settings.edit();
		String msg = alertmsg.getText().toString();
		editor.putString("alertmsg",msg );
		msg = updatemsg.getText().toString();
		editor.putString("updatemsg", msg);
		editor.commit();	
		rungpsService();	
	}
	
	
	
}
	
	

package com.example.testproject;

import android.os.Bundle;
import android.app.Activity;
import android.widget.Button;
import android.widget.ImageButton;
import android.view.View.*;
import android.view.View;
import android.view.Window;
import android.content.Intent;

public class MainActivity extends Activity {
	private ImageButton alertButton;
	private ImageButton settingsButton;
	String provider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		getElementsById();
		buttonClickListener();	    	
	    
	}

	/**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
   



private void getElementsById()
{
  alertButton = (ImageButton) findViewById(R.id.button1);
  settingsButton = (ImageButton) findViewById(R.id.menu_settings);
}


private void buttonClickListener()
{
	 alertButton.setOnClickListener(new OnClickListener()
     {
         @Override
         public void onClick(View v)
         {
        	 alertToFriend();
         }
     });
	 
	 settingsButton.setOnClickListener(new OnClickListener()
	 {
		 @Override
		 public void onClick(View w)
		 { 
			 showSettings();
		 }
	 });
}
	 
	 public void alertToFriend()
	 {
		 Intent myIntent = new Intent(this, alertActivity.class);
		 this.startActivity(myIntent);
	 }
	 
	 public void showSettings()
	 {
		 Intent intent = new Intent(this,
			      settingsActivity.class);
			      startActivity(intent);
	 }
}

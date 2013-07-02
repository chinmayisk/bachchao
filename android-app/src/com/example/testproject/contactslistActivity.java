package com.example.testproject;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.Gravity;
//import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;

import java.util.ArrayList;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;

public class contactslistActivity extends Activity
{
	private static final int REQUEST_CODE1 = 0;
    ArrayAdapter<String> adapter;
    ListView listView;
    Button image;
    RelativeLayout rl;
    int itemid;
    String name,contactemail,contactnumber,contactid;
    public static final String PREFS = "BachchaoPrefsFile";
    SharedPreferences settings;
    public static int MAX_CNUMBER = 3;
    Bundle settingsb;
    boolean contactexists = true;
    ActionBar actionBar;
    int num;
    boolean alreadyexists;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.from_contacts);
        setActionBarUI();
      	   
        findViewsById();
        contactnumber="";
        settingsb= getIntent().getExtras();
		itemid = settingsb.getInt("Triggeritem");
	    contactexists = settingsb.getBoolean("contactexists");
	    num = settingsb.getInt("triggernum");
	    
	    
	    if(contactexists==false)
	    {
	    	actionBar.setTitle("REPLACE CONTACT");
	    	addContactlayout();	    	 
	    }
	    
	    
	    
	    
		settings = getSharedPreferences(PREFS, 0);
        ArrayList<contactsresults> contactsResults = getContactsResults();
      
        //adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,contacts);
       
        listView.setAdapter(new contactslistAdapter(this, contactsResults));
        listView.setOnItemClickListener(new OnItemClickListener()
        {
        	@Override
        	  public void onItemClick(AdapterView<?> parent, View view,
        	    int position, long id) {
        		Object selectedItem = listView.getItemAtPosition(position);
        		 contactsresults fullObject = (contactsresults)selectedItem;
        		 
        		 name = fullObject.getName();
        		 contactnumber = fullObject.getPhone();
        		 contactid = fullObject.getId();    		 
        		 view.setSelected(true);
        		 alreadyexists = false;
        		 for(int i =0 ; i< MAX_CNUMBER ; i++)
        			{
        			 if(settings.getString("contactid"+i,null) != null)
        			  if(settings.getString("contactid"+i, null).contentEquals( contactid))
        			  {
        				  alreadyexists = true;  
        				  break;
        			  }
        			 if(settingsb.getString("contact"+i,null) != null)
        				 if(settingsb.getString("contact"+i,null).contentEquals(contactid))
        				 {
        					alreadyexists=true;
        					break;
        				 }
        			}
        		
        		if(((contactnumber!="")&&(contactnumber.length()!=1)&&(contactnumber.length()<=13)&& (alreadyexists != true)) )
        		 {
        			 Intent data = new Intent();
        			 data.putExtra("triggeritem", itemid);
        			 data.putExtra("contactid",contactid );
        			 data.putExtra("contactname",name );
        			 data.putExtra("contactnumber",contactnumber);
        			 setResult(RESULT_OK, data);
        			 if (contactexists==false)
        	   			  image.setText(name);
        	   		  else
        	   		  {
        	   			addContactlayout();
        	   			image.setText(name);
        	   		  }
        			
        		 }
        		 else if(contactnumber.length()==0)
        		 {
        			 Toast.makeText(getApplicationContext(), "Cannot add a contact with no Phone Number and Email Id", Toast.LENGTH_LONG).show();
        		 }else if((contactnumber.length()==1)||(contactnumber.length()>13))
        		 {
        			 Toast.makeText(getApplicationContext(), "Cannot add contact with invalid Phone Number", Toast.LENGTH_LONG).show();
        		 }
        		 else if(alreadyexists)
        		 {
        			 Toast.makeText(getApplicationContext(),"The Contact is already on your list", Toast.LENGTH_LONG).show();
        		 }
        		 
        	}
        });
    }   
    
    private void setActionBarUI() {
    	actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.headersettingspage));
        actionBar.setTitle("ADD CONTACT");
  		actionBar.setIcon(R.drawable.return_icon_settingspage);
  		actionBar.show();
  		actionBar.setHomeButtonEnabled(true);
  		int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
	    TextView yourTextView = (TextView)findViewById(titleId);
	    yourTextView.setTextColor(Color.parseColor("#752620"));
	    yourTextView.setTextSize(18);
	}


	private void addContactlayout() {
    	
   	 rl = (RelativeLayout)findViewById(R.id.chosencontact);
   	 TextView name = new TextView(this);
   	 name.setId(2);
   	 name.setText("ASSIGNED CONTACT:");
   	 name.setTextColor(Color.parseColor("#303030"));
	 name.setTextSize(16);
   	 image = new Button(this);
   	 image.setId(1);
   	 image.setPadding(0, 80, 0, 0);
   	 image.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
   	 image.setTextColor(Color.WHITE);
   	 image.setText(settingsb.getString("contactname"));
   	 image.setTextSize(12);
   	 
   	 LayoutParams params2 = new RelativeLayout.LayoutParams(100,100);
   	    params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
   	    params2.rightMargin = 50;
   	    params2.topMargin = 20;
   	    params2.bottomMargin = 20;
   	 rl.addView(image,params2);
   	 image.setBackgroundResource(R.drawable.savedcontactbox);
   	 LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
   	  	params1.addRule(RelativeLayout.CENTER_VERTICAL);
   	  	params1.addRule(RelativeLayout.CENTER_IN_PARENT);
   	    params1.addRule(RelativeLayout.ALIGN_BASELINE , image.getId());
   	    params1.addRule(RelativeLayout.LEFT_OF, image.getId());
   	    params1.rightMargin = 20; 	    
   	    rl.addView(name, params1);
   	 TextView placeholder = new TextView(this);
   	 	placeholder.setId(3);
   	 	placeholder.setText("CONTACT LIST");
   	 	placeholder.setTextColor(Color.parseColor("#752620"));
   	 	placeholder.setTextSize(18);
	   LayoutParams params3 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	   		params3.addRule(RelativeLayout.BELOW,image.getId());
	   		params3.leftMargin = 40;
	   rl.addView(placeholder,params3);
   	 
		
	}


	private void findViewsById() {
        listView = (ListView) findViewById(android.R.id.list);
    }
    
    private ArrayList<contactsresults> getContactsResults(){
        ArrayList<contactsresults> results = new ArrayList<contactsresults>();
        Cursor curs = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,null,null, null);
        contactsresults cr1 = new contactsresults();
        while (curs.moveToNext())
        {
        	
            String name=curs.getString(curs.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String phoneNumber ="";
            String id = curs.getString(
                    curs.getColumnIndex(BaseColumns._ID));
           
            //getting the phone numbers 
            if (Integer.parseInt(curs.getString(
                    curs.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                 Cursor pCur = getContentResolver().query(
  		    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
  		    null, 
  		    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
  		    new String[]{id}, null);
  	        while (pCur.moveToNext()) {
  	        	phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
  	        } 
  	      pCur.close();
	        
	        
      	    }                
	        cr1 = new contactsresults();
	        cr1.setName(name);
	        cr1.setPhone(phoneNumber);
	        cr1.setId(id);
	        results.add(cr1);
        }
        curs.close();
        
        return results;
    }
    
    
    public boolean onOptionsItemSelected(MenuItem item) 
	{
	   switch (item.getItemId()) 
	   {
	     case android.R.id.home:
	    	 	saveContact();
	    	
		
	    	 finish();
		        return true;
		     default:
		        return super.onOptionsItemSelected(item);
		   }
		}
    private void saveContact() {
    	 SharedPreferences.Editor editor = settings.edit();
    	 if(((contactnumber!="")&&(contactnumber.length()!=1)&&(contactnumber.length()<=13)&& (alreadyexists != true)) )
    	 {
				editor.putString("contactname"+(num-1),name);
				editor.putString("contactnumber"+(num-1),contactnumber);
				editor.putString("contactid"+(num-1),contactid);
		 editor.commit();
    	 }
		
	}

	@Override
	public void onBackPressed() {
    	saveContact();
    	finish();
    	super.onBackPressed();
    }
    
}
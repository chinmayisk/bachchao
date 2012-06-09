package sosapp.csk.rhok;

import android.app.Activity;

import android.content.Context;
import java.io.IOException;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.*;

import android.content.Intent;
import android.media.MediaRecorder;
import android.media.CameraProfile;
import android.media.CamcorderProfile;
import android.hardware.Camera;
import android.provider.MediaStore;

import android.net.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/*import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;*/
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class SosappActivity extends Activity implements LocationListener{
	private TextView latituteField;
	private TextView longitudeField;
	private LocationManager locationManager;
	private String provider;

	/*private Camera mCamera;
    private MediaRecorder mMediaRecorder;*/
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
   
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        latituteField = (TextView) findViewById(R.id.TextView02);
		longitudeField = (TextView) findViewById(R.id.TextView04);
		locationfinder();
		    	
		
		// Working Code :to send alert and info based on availability of internet
	if (isInternetOn())
		{
			sendinfo inf = new sendinfo();
			inf.execute();
		}
	else 
		{
			AlertMessaging();
		}
		VedioRecording();
		
    }
   
   private void VedioRecording()
    {
    	Uri fileUri;
    	Intent intent = new Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA);
        fileUri = getOutputMediaFileUri();// create a file to save the video
        Log.d("myapp","into this"+fileUri.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high
        // start the Video Capture Intent
        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
    	
    }
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       
           if (resultCode == RESULT_OK) {
               // Video captured and saved to fileUri specified in the Intent
               Toast.makeText(this, "Video saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
           } else if (resultCode == RESULT_CANCELED) {
               // User cancelled the video capture
           } else {
               // Video capture failed, advise user
           }
   }
    
 private static Uri getOutputMediaFileUri(){
         	return Uri.fromFile(getOutputMediaFile());
	 	
  }

  /** Create a File for saving an  video */
  private static File getOutputMediaFile(){
    // Create a media file name
	  String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      File mediaFile;
   
      mediaFile = new File(Environment.getExternalStorageDirectory() + File.separator +
      "VID_"+ timeStamp + ".mp4");
      Log.d("myapp",mediaFile.toString());
      return mediaFile;
  }
//Working COde  
    protected void locationfinder()
    {
    	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);

		// Initialize the location fields
		if (location != null) {
			System.out.println("Provider " + provider + " has been selected.");
			int lat = (int) (location.getLatitude());
			int lng = (int) (location.getLongitude());
			
			latituteField.setText(String.valueOf(lat));
			longitudeField.setText(String.valueOf(lng));
		} else {
		
			latituteField.setText("Provider not available");
			longitudeField.setText("Provider not available");
		}
    }
    
    
	///Working code
    private class sendinfo extends AsyncTask<String , Void , String>{
		
	protected String doInBackground (String ... urls) { 
     String r ="";
    HttpClient httpclient = new DefaultHttpClient();
    HttpGet httpget = new HttpGet("http://bachchao.appspot.com/pseudoupload");
    String uploadUrl;
    try{
	   HttpResponse response = httpclient.execute(httpget);
	   Log.d("myapp",response.getEntity().toString());	 
			   uploadUrl= response.getEntity().toString();
   }catch(ClientProtocolException e){
	   e.printStackTrace();
	   uploadUrl = null;
   }catch(IOException e){
	   e.printStackTrace();
	   uploadUrl = null;
   }
   //HttpPost httppost = new HttpPost(uploadUrl);
    /*try {
    	/*List<NameValuePair> value = new ArrayList<NameValuePair>(2);
    	value.add(new BasicNameValuePair("latitude",  latituteField.getText().toString()));
    	value.add(new BasicNameValuePair("longitude", longitudeField.getText().toString()));
    	httppost.setEntity(new UrlEncodedFormEntity(value));
    	Log.d("myapp","works till here ..");
    	HttpResponse response = httpclient.execute(httppost);
    	 Log.d("myapp", "response " + response.getEntity());*/
    /*	MultipartEntity reqEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		reqEntity.addPart("type", new StringBody("photo"));
		reqEntity.addPart("data", new FileBody(image));
		httppost.setEntity(reqEntity);
		HttpResponse response = httpclient.execute(httppost);
    }catch (ClientProtocolException e) {
    	e.printStackTrace();
        // TODO Auto-generated catch block
    } catch (IOException e) {
        // TODO Auto-generated catch block
    	 e.printStackTrace();
    }*/
    return r;
    }
	
	
	protected void onPostExecute(String result) {
		//WIll be done later
	}
	
	}

	protected void sendsms(String Phnumber,String message)
	{
		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(Phnumber, null, message, null, null);
			Toast.makeText(getApplicationContext(), "SMS Sent!",
						Toast.LENGTH_LONG).show();
			Log.d("myapp","hello..");
		  } catch (Exception e) {
			Toast.makeText(getApplicationContext(),
				"SMS faild, please try again later!",
				Toast.LENGTH_LONG).show();
			e.printStackTrace();
		  }
	}
	
	protected void AlertMessaging()
	{
		String line = null; 
		
		File sdcard = Environment.getExternalStorageDirectory();
		
		File file = new File(sdcard,"contacts.json");
	
		StringBuilder text = new StringBuilder();
		
		try {
			
		    
			BufferedReader br = new BufferedReader(new FileReader(file));
		    
		 
		    while ((line = br.readLine()) != null) {
		        text.append(line);
		        text.append('\n');
				}
		      }	   
		catch (IOException e) {
		    //You'll need to add proper error handling here
		}
		   line = text.toString(); 
		Log.d("myapp",line);
		 //working code
		try {
			JSONArray jsonArray = new JSONArray(line);
			Log.d("myapp","helo...");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Log.d("MYAPP", jsonObject.getString("number"));
				sendsms(jsonObject.getString("number"),"hello");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

///working code 
  public final boolean isInternetOn() {
		ConnectivityManager connec =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

		if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
		connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
		connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
		connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED ) {
		// FOR TESTING 
		//Toast.makeText(this, connectionType + ” connected”, Toast.LENGTH_SHORT).show();
		return true;
		} else if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||  connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED  ) {
		
		return false;
		}
		return false;
		}
	
	

	public void onLocationChanged(Location location) {
		// TODO for further development
	}


	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO stub

	}


	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();

	}


	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}
 }

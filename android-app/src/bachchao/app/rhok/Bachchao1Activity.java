package bachchao.app.rhok;



import android.net.*;
import java.io.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.Bundle;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.app.Activity;
import android.location.Criteria;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import android.util.Log;
import android.os.*;
import android.location.LocationManager;

import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.File;
import java.net.URI;
import java.util.Date;

public class Bachchao1Activity extends Activity implements SurfaceHolder.Callback{

	private static final String TAG = "Bachchao App";

	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private Camera camera;
	private boolean previewRunning;
	private MediaRecorder mediaRecorder;
	private final int maxDurationInMs = 600000;
	private final long maxFileSizeInBytes = 500000;
	private final int videoFramesPerSecond = 20;
	LocationFinder locationfinder;
	LocationManager locationManager;
	String provider;


        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.main);
                surfaceView = (SurfaceView) findViewById(R.id.surfacecamera);
                surfaceHolder = surfaceView.getHolder();
                surfaceHolder.addCallback(this);
                mediaRecorder = new MediaRecorder();   
                
                //Location Code 
                locationfinder = new LocationFinder();
               
                locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
    			Criteria criteria = new Criteria();
    			provider = locationManager.getBestProvider(criteria, false);
                locationfinder.setLocation(locationManager.getLastKnownLocation(provider));
                Log.d(TAG,"latitude: "+locationfinder.getLatitude());
                Log.d(TAG,"longitude: "+locationfinder.getLongitude());
        }

    @Override
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open(0);
		if (camera != null){
			Camera.Parameters params = camera.getParameters();
			camera.setParameters(params);
			captureVideo();
			 
		}
		else {
			Toast.makeText(getApplicationContext(), "Camera not available!", Toast.LENGTH_LONG).show();
			finish();
		}
    	
	}
    private void captureVideo(){
    	 if (isInternetOn())
  		{
  			sendinfo inf = new sendinfo();
  			inf.execute();
  		}
          else 
  		{
  			AlertMessaging();
  		}
    	 try{
    		
    		 for(int i = 0; i< 4 ;i++)
    		 {	
    			Log.d(TAG,""+i);
    			 startRecording();
    		 	Thread.sleep(3000);
    		 	mediaRecorder.reset();
    		 }   
    		
    	 }
    	 catch (Exception e) {
    		    
 	    	String message = e.getMessage();
 	    	Log.d(TAG,message);
    	 }
    }
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	if (previewRunning){
			camera.stopPreview();
		}
		
		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
			previewRunning = true;
		}
		catch (IOException e) {
			Log.e(TAG+"..",e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		previewRunning = false;
		camera.release();
		mediaRecorder.release();
	}
	
	public boolean startRecording(){
		try {
			camera.unlock();
			mediaRecorder.setCamera(camera);
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

			mediaRecorder.setMaxDuration(maxDurationInMs);

			String fileUri = getOutputMediaFile();
		    mediaRecorder.setOutputFile(fileUri);

			mediaRecorder.setVideoFrameRate(videoFramesPerSecond);
			mediaRecorder.setVideoSize(surfaceView.getWidth(), surfaceView.getHeight());

			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);

			mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

			mediaRecorder.setMaxFileSize(maxFileSizeInBytes);

            mediaRecorder.prepare();
			mediaRecorder.start();

			return true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static String getOutputMediaFile(){
		    // Create a media file name
			  String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		      String mediaFile;
		   
		      mediaFile = Environment.getExternalStorageDirectory() + File.separator +
		      "VID_"+ timeStamp + ".mp4";
		      return mediaFile;
		  }
	 private class sendinfo extends AsyncTask<String , Void , String>{
			
			protected String doInBackground (String ... urls) { 
		     String r ="";
		    HttpClient httpclient = new DefaultHttpClient();
		  //  HttpGet httpget = new HttpGet("http://bachchao.appspot.com/pseudoupload");
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
		    Log.d(TAG,uploadUrl);
		    doUpload(uploadUrl,"/sdcard/","contacts.json");
		    return r;
		    }
			
			private HttpPost post; 
			public void doUpload(String Server_Name,String filepath,String filename) { 
				HttpClient httpClient = new DefaultHttpClient(); 
				try { 
						httpClient.getParams().setParameter("http.socket.timeout", new 
															Integer(90000)); // 90 second 
						post = new HttpPost(new URI(Server_Name)); 
						File file = new File(filepath); 
						FileEntity entity; 
						if (filepath.substring(filepath.length()-3, filepath.length 
											   ()).equalsIgnoreCase("txt") || 
							filepath.substring(filepath.length()-3, filepath.length 
											   ()).equalsIgnoreCase("log")) { 
							entity = new FileEntity(file,"text/plain; charset=\"UTF-8\""); 
							entity.setChunked(true); 
						}else { 
							entity = new FileEntity(file,"binary/octet-stream"); 
							entity.setChunked(true); 
						} 
						post.setEntity(entity); 
						post.addHeader("filename", filename); 
						HttpResponse response = httpClient.execute(post); 
						if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) { 
							//Log.e("myapp","--------Error--------Response Status line code:"+response.getStatusLine()); 
						}else { 
							// Here every thing is fine. 
						} 
						HttpEntity resEntity = response.getEntity(); 
						if (resEntity == null) { 
							Log.e("myapp","---------Error No Response !!!-----"); 
						} 
					} catch (Exception ex) { 
						Log.e("myapp","---------Error-----"+ex.getMessage()); 
						ex.printStackTrace(); 
					} finally { 
						httpClient.getConnectionManager().shutdown(); 
					} 
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
			
}
package com.example.testproject;

import java.io.File;
import java.io.IOException;
import java.security.Policy.Parameters;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Chronometer;

public class digitalwitness extends Activity implements SurfaceHolder.Callback{
	 private SurfaceHolder surfaceHolder;
	    private SurfaceView surfaceView;
	    public MediaRecorder mrec = new MediaRecorder();
	    public ImageButton stopRecording ;
	    File video;
	    private Camera mCamera;
	    private Chronometer chronometer;
        String fileUri;
        Bundle settingsb;
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	    	
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE); 
	        setContentView(R.layout.digitalwitness);
	        chronometer = (Chronometer) findViewById(R.id.chronometer);
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            settingsb= getIntent().getExtras();
            double latitude = settingsb.getDouble("latitude");
            double longitude = settingsb.getDouble("longitude");
            TextView location = (TextView)findViewById(R.id.textlocation);
            location.setText(String.format("%.4f", latitude)+","+String.format("%.4f", longitude));
            
	        stopRecording = (ImageButton)findViewById(R.id.buttonstop);
	        mCamera = Camera.open();
	        surfaceView = (SurfaceView) findViewById(R.id.surfacecamera);
	        surfaceHolder = surfaceView.getHolder();
	        surfaceHolder.addCallback(this);
	        Handler handler = new Handler();
	        handler.postDelayed(new Runnable(){
	        @Override
	              public void run(){
	        	if(mrec != null)
	   			{
	   				mrec.stop();
	   				mrec.release();
	   				
	   				chronometer.stop();
	   				mrec = null;
	   			}
	        	setResult(RESULT_OK);
	   			finish();
	           }
	        }, 600100);
	        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	       stopRecording.setOnClickListener(new OnClickListener()
	   	 	{
	        @Override
	   		 public void onClick(View w)
	   		 { 
	   			if(mrec != null)
	   			{
	   				mrec.stop();
	   				mrec.release();
	   				
	   				chronometer.stop();
	   				mrec = null;
	   			}
	   		  setResult(RESULT_OK);
	   			finish();
	   		 }
	   	 });
	        
	       
	      
	    }

	

	    protected void startRecording() throws IOException 
	    {
	        mrec = new MediaRecorder();  // Works well
	        mCamera.unlock();

	        mrec.setCamera(mCamera);

	        mrec.setPreviewDisplay(surfaceHolder.getSurface());
	        mrec.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	        mrec.setAudioSource(MediaRecorder.AudioSource.MIC); 
	        mrec.setMaxDuration(600000);
	        fileUri = getOutputMediaFile();
	    	mrec.setOutputFile(fileUri);

	        mrec.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
	        mrec.setPreviewDisplay(surfaceHolder.getSurface());
	       
	        mrec.prepare();
	        mrec.start();
	    }

	    protected void stopRecording() {
	        mrec.stop();
	        mrec.release();
	        mCamera.release();
            chronometer.stop();
	    }

	    private void releaseMediaRecorder(){
	        if (mrec != null) {
	            mrec.reset();   // clear recorder configuration
	            mrec.release(); // release the recorder object
	            mrec = null;
	            mCamera.lock();           // lock camera for later use
	        }
	    }

	    private void releaseCamera(){
	        if (mCamera != null){
	            mCamera.release();        // release the camera for other applications
	            mCamera = null;
	        }
	    }

	    @Override
	    public void surfaceChanged(SurfaceHolder holder, int format, int width,
	            int height) {
	    }

	    @Override
	    public void surfaceCreated(SurfaceHolder holder) {
	    
	    	 
	        if (mCamera != null){
	        	Camera.Parameters params = mCamera.getParameters();
	            mCamera.setParameters(params);
	        }
	        else {
	            Toast.makeText(getApplicationContext(), "Camera not available!", Toast.LENGTH_LONG).show();
	            finish();
	        }
	   	 try {
             startRecording();
         } catch (Exception e) {
             String message = e.getMessage();
             Log.i(null, "Problem Start"+message);
             mrec.release();
         }
	    }

	    @Override
	    public void surfaceDestroyed(SurfaceHolder holder) {
	        mCamera.stopPreview();
	        mCamera.release();
	    }
	    
		private static String getOutputMediaFile(){
			// Create a media file name
			//String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(0));
			Long tsLong = System.currentTimeMillis()/1000;
			String ts = tsLong.toString();
			String mediaFile;
			mediaFile = Environment.getExternalStorageDirectory() + File.separator +
			"VID_"+ ts + ".mp4";
			return mediaFile;
		}
		
		@Override
		public void onBackPressed() {
			if(mrec != null)
   			{
   				mrec.stop();
   				mrec.release();
   				chronometer.stop();
   				mrec = null;
   			}
			  setResult(RESULT_OK);
			finish();
			super.onBackPressed();
		}
		
	}


package bachchao.app.rhok;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationFinder  implements LocationListener {
private int lat ;
private int lon ;
private Location location;
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}
	public int getLatitude()
	{
		if(location!=null){
			lat = (int)(location.getLatitude());
		}else{
			lat = 0;
		}
		return lat;
	}
	
	public int getLongitude()
	{
		if (location != null) {
			lon = (int) (location.getLongitude());
		} else {
			lon = 0;
		}
		return lon;
	}
	
	public void setLocation(Location location)
	{
		this.location = location;
	}
	}


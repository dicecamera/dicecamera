package com.sorasoft.dicecam;

import android.location.Location;
import android.util.Log;

public class LocationCache {

	private static LocationCache instance = null;
	private Location location = null;

	protected LocationCache() {
		// Exists LocationCache to defeat instantiation.
	}
	
	protected void setLocationValue(Location l) {
		this.location = l;
	}
	
	protected Location getLocationValue() {
		return this.location;
	}
	
	public static LocationCache getInstance() {
		if(instance == null) {
			instance = new LocationCache();
		}
		return instance;
	}
	
	public static void setLocation(Location l) {
		getInstance().setLocationValue(l);
	}
	
	public static Location getLocation() {
		return getInstance().getLocationValue();
	}
}
package com.sorasoft.dicecam;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class PublishInfo {
	
	public final static int SOURCE__GOOGLE_PLAY_STORE 			= 1; // 0b00000001
	public final static int SOURCE__INTEL_PROVIDER 				= 3; // 0b00000011
	public final static int SOURCE__AMAZON_APPSTORE_FOR_ANDROID = 5; // 0b00000101
	
	/**
	 * You MUST SET this-'source' value before export apk file.
	 */
	public final static int source = SOURCE__GOOGLE_PLAY_STORE;
	public final static String buildNumber = "371483b0ff704230b2bf82852078d0ed26ad2d8b";

	
	
	
	
	// source string
	public static String getSourceString() {
		return getSourceString(source);
	}
	
	public static String getSourceString(int s) {
		switch (s) {
		case SOURCE__GOOGLE_PLAY_STORE:
			return "google";
		case SOURCE__INTEL_PROVIDER:
			return "intel";
		case SOURCE__AMAZON_APPSTORE_FOR_ANDROID:
			return "amazon";
		}
		return "unknown";
	}
	
	// feedback email address
	public final static String emailAddressForAmazon = "dicecamera8@gmail.com";
	public final static String emailAddressForIntel = "dicecamera8@gmail.com";
	public final static String emailAddress = "dicecamera8@gmail.com";
	
	public static String getFeedbackEmailAddress() {
		switch (source) {
		case SOURCE__AMAZON_APPSTORE_FOR_ANDROID:
			return emailAddressForAmazon;
		case SOURCE__INTEL_PROVIDER:
			return emailAddressForIntel;
		}
		return emailAddress;
	}
	
	// facebookID
	public final static String facebookID = "1422076821443734";
	public static String getFacebookID() {
		return facebookID;
	}
	
	// appID
	public final static String APP_ID = "com.sorasoft.dicecam";
	public static String getAppId() {
		return APP_ID;
	}


	// Exif Metadata - Make
	public static String getExifMake() {
		return android.os.Build.BRAND;
	}
	
	// Exif Metadata - Model
	public static String getExifModel() {
		return android.os.Build.MODEL;
	}

	// Exif Metadata - Software
	public static String getExifSoftware() {
		PackageInfo pInfo = null;
		try {
			Context c = DiceCamera.mContext;
			pInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			return "Dicecam";
		}
		return "Dicecam " + pInfo.versionName;
	}
}

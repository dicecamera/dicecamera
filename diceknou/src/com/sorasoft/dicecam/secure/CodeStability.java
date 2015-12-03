package com.sorasoft.dicecam.secure;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class CodeStability {

	private static WakeLock mWakeLock; // To ensure BroadcastReceiver works.

	public static void gettingWakeLock(Activity m) {
		if(mWakeLock != null && mWakeLock.isHeld()) mWakeLock.release();
		try {
			PowerManager pm = (PowerManager) m.getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EnsureProcessing");
			mWakeLock.acquire();
		} catch (Exception e) {
			;
		}
	}
	
	public static void releaseWakeLock() {
		if(mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}

}

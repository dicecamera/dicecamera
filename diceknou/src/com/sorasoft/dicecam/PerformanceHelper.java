package com.sorasoft.dicecam;

import com.sorasoft.dicecam.engine.NakedFootPreViewCallBack;

import android.os.Build;

public class PerformanceHelper {

	public static long maxMemory = 0;
	public static long freeMemory = 0;

	protected PerformanceHelper() {
	}

	public static void init() {
		Runtime rt = Runtime.getRuntime(); // @see
											// http://stackoverflow.com/a/9428660
		maxMemory = rt.maxMemory();
		freeMemory = rt.freeMemory();
	}

	private static int maxPictureWidth = -1;

	public static int getMaxPictureWidth() {
		if (maxPictureWidth < 0) {
			maxPictureWidth = (maxMemory < 68000000) ? 2500 : 4000; // > 64MB
		}
		return maxPictureWidth;
	}

	public final static int ANIMATION_LEVEL_LOW = 0;
	public final static int ANIMATION_LEVEL_NORMAL = 1;
	public final static int ANIMATION_LEVEL_HIGH = 2;

	private static int animationLevel = -1;

	public static int getAnimationLevel() {
		if (animationLevel < 0) {
			int level = 0;
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				level += 1;
			}
			if (maxMemory > 250000000) { // > 256MB(256*1024*1024)
				level += 1;
			}
			animationLevel = level;
		}
		return animationLevel;
	}

	public static String getAnimationLevelString() {
		switch (getAnimationLevel()) {
		case ANIMATION_LEVEL_LOW:
			return "low";
		case ANIMATION_LEVEL_NORMAL:
			return "normal";
		case ANIMATION_LEVEL_HIGH:
			return "high";
		default:
			return "unknown";
		}
	}

	public static boolean isStillPictureAllowable() {
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			return false;
		if (maxMemory < 120000000)
			return false; // < 128MB(128*1024*1024)
		int maxTextureSize = NakedFootPreViewCallBack.getGlMaxTextureSize();
		if (maxTextureSize > 0 && maxTextureSize < 2050)
			return false;
		return true;
	}

	public static boolean dummySurfaceViewNeeded() {
		if (Build.VERSION.SDK_INT < 11) { // under 3.0
			return true;
		}
		return false;
	}
}

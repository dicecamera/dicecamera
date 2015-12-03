package com.sorasoft.dicecam;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;

public abstract class BaseActivity extends ActionBarActivity {

	public final static String E_TAKE_PICTURE = "Take Picture";
	public final static String E_TAKE_PICTURE_REAR = "Took a Picture with Rear Camera";
	public final static String E_TAKE_PICTURE_FRONT = "Took a Picture with Front Camera";

	public final static String E_SHOW_ALBUM = "Show Album";
	public final static String E_RANDOM_LENS = "Random Lens";
	public final static String E_LENS_SELECTED = "Lens Selected";
	public final static String E_PACK_SELECTED = "Pack Selected";
	public final static String E_LENS_INTENSITY_EDITED = "Lens Intensity Edited";
	public final static String E_QUICK_VIEW_AFTER_CAPTURE = "QuickView after capture";
	public final static String E_QUICK_VIEW_AFTER_NO_EFFECT_CAPTURE = "QuickView after NoEffect capture";
	public final static String E_QUICK_VIEW_FROM_ALBUM = "QuickView from album";
	public final static String E_QUICK_VIEW_DELETE = "QuickView Delete";
	public final static String E_QUICK_VIEW_SHARE = "QuickView Share";
	
	public final static String E_FILTER_SWIPED_LEFT = "Filter Swiped to Left";
	public final static String E_FILTER_SWIPED_RIGHT = "Filter Swiped to Right";
	public final static String E_FILTER_SELECTION_CLOSED = "Filter Selection View Dismissed";

	public final static String E_TAKE_TYPE = "Take Type";
	public final static String E_USE_VIGNETTE = "Use Vignette";
	public final static String E_USE_BLUR = "Use Blur";
	public final static String E_LENS_NAME = "Lens Name";
	public final static String E_BORDER_WIDTH = "Border Width";
	public final static String E_BORDER_COLOR = "Border Color";
	public final static String E_TIMER_DELAY = "Timer Delay";
	public final static String E_TIMER_INTERVAL = "Timer Interval";
	public final static String E_FILTER_INTENSITY = "Intensity";

	public final static String E_LENS_SELECTED_NAME = "Name";
	public final static String E_PACK_SELECTED_NAME = "Name";
	public final static String E_LENS_INTENSITY_VALUE = "Value";

	public static String refineLensIntensityString(float intensity) {
		return String.valueOf(Math.round(intensity * 10.f) * 10);
	}

	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

}

package com.sorasoft.dicecam.view;

public interface DicecamBlurTouchEventListener {
	
	boolean blurGestureEventEnabled();
	
	void centerChanged(float x, float y);
	void radiusChanged(float radius);
	
	void gestureEventStarted();
	void gestureEventFinished();
	
	void blurTouchViewTouchDown();
	void blurTouchViewTouchUp();
	
	void lefeSwiping();
	void rightSwiping();
}

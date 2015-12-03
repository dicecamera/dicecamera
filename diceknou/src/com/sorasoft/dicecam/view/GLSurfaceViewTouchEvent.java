package com.sorasoft.dicecam.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.sorasoft.dicecam.DiceCamera;
import com.sorasoft.dicecam.engine.filter.LensCenter;
import com.sorasoft.dicecam.setting.Settings;
import com.sorasoft.dicecam.util.UserInterfaceUtil;

public class GLSurfaceViewTouchEvent implements GestureDetector.OnGestureListener {
	
	private DicecamBlurTouchEventListener eventListner = null;
	
	GestureDetectorCompat gd;
	
	private GestureDetector mMoveDetector;
	private ScaleGestureDetector mScaleDetector;
	
	private float centerX = 0.5f;
	private float centerY = 0.5f;
	private float radius = 0.3f;
	
	private final float defaultRadius = 0.3f;
	private float mScaleFactor = 1.f;
	
	private int mWidth;
	private int mHeight;
	
	private int mImageWidth = 0;
	private int mImageHeight = 0;
	
	private final float mScaleFactorMin = 0.8f;
	private final float mScaleFactorMax = 1.8f;
	
	private Paint blurGuidePaint = null;
	
	Context mContext;
	
	public GLSurfaceViewTouchEvent(Context context) {
		this.mContext = context;
		gd = new GestureDetectorCompat(mContext, this);
		mMoveDetector = new GestureDetector(context, new MoveListner());
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		
		blurGuidePaint = new Paint();
		blurGuidePaint.setColor(Color.GREEN);
	}

	public void setEventListner(DicecamBlurTouchEventListener listner) {
		eventListner = listner;
	}

	public void setCenter(float x, float y) {
		setCenter(x, y, false);
	}
	
	public void setCenter(float x, float y, boolean update) {
		centerX = Math.max(0.0f, Math.min(1.0f, x)); centerY = Math.max(0.0f, Math.min(1.0f, y));
		if ( eventListner != null ) {
			eventListner.centerChanged(centerX, centerY);
			if ( update ) {
				eventListner.blurTouchViewTouchUp();
			}
		}
	}
	
	public float getCenterX() { return centerX; }
	public float getCenterY() { return centerY; }
	
	public void setRadius(float r) {
		radius = r;
		if ( eventListner != null ) {
			eventListner.radiusChanged(radius);
		}
	}
	
	public float getRadius() { return radius; }
	
	public void startEvent() {
		if ( eventListner == null ) return;
		eventListner.gestureEventStarted();
	}
	
	public void finishEvent() {
		if ( eventListner == null ) return;
		eventListner.gestureEventFinished();
	}

	public boolean onTouchEvent(MotionEvent event) {
		
		if ( eventListner == null ) return true;
		
		if (((DiceCamera)DiceCamera.mContext).mFavoriteView.getVisibility() == View.INVISIBLE) {
			mMoveDetector.onTouchEvent(event);
			mScaleDetector.onTouchEvent(event);
		} else if( ((DiceCamera)DiceCamera.mContext).mFavoriteView.getVisibility() == View.VISIBLE) {
			gd.onTouchEvent(event);
			return true;
		} else {
			//TODO
		}
		
		boolean detectedUp = false;
		
		switch ( event.getAction() ) {
		case MotionEvent.ACTION_DOWN:
			Log.d("hjh","MotionEvent.ACTION_DOWN");
			if(eventListner.blurGestureEventEnabled() == true) eventListner.blurTouchViewTouchDown();
			else ((DiceCamera)DiceCamera.mContext).rollDice(1);
			
			break;
			
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if(eventListner.blurGestureEventEnabled() == true) eventListner.blurTouchViewTouchUp();
			detectedUp = true;
			break;
		}
		
		if ( detectedUp ) {
			finishEvent();
		}
		return true;
	}

	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		mWidth = w; mHeight = h;
	}
	
	public void setImageSize(int w, int h) {
		mImageWidth = w; mImageHeight = h;
	}


	
	public class MoveListner extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			startEvent();
			return super.onDown(e);
		}
		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if ( mImageWidth > 0 && mImageHeight > 0 ) {
				float originX = (mWidth - mImageWidth) / 2.f;
				float originY = (mHeight - mImageHeight) / 2.f;
				float left = e.getX() - originX;
				float top = e.getY() - originY;
				setCenter(left/mImageWidth, top/mImageHeight, true);
			} else {
				setCenter(e.getX()/mWidth, e.getY()/mHeight, true);
			}
			return true; 
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if ( mImageWidth > 0 && mImageHeight > 0 ) {
				setCenter(centerX - distanceX/mImageWidth, centerY - distanceY/mImageHeight);
			} else {
				setCenter(centerX - distanceX/mWidth, centerY - distanceY/mHeight);
			}
			return true; 
		}
	}
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(mScaleFactorMin, Math.min(mScaleFactor, mScaleFactorMax));
            setRadius(mScaleFactor * defaultRadius);
            return false; 
        }
        
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            startEvent();
        	return super.onScaleBegin(detector);
        }
        
    }
	
	public static final int BLUR_GUIDE_CIRCLE_BORDER_WIDTH = 2;
	public static final int BLUR_GUIDE_POINT_RADIUS = 2;
	public void drawBlurGuide(View view, Canvas canvas) {
		final float cx = actualCenterX(centerX);
		final float cy = actualCenterY(centerY);
		
		blurGuidePaint.setStyle(Style.STROKE);
		blurGuidePaint.setStrokeWidth(UserInterfaceUtil.dp2px(BLUR_GUIDE_CIRCLE_BORDER_WIDTH, view));
		canvas.drawCircle(cx, cy, actualRadius(radius), blurGuidePaint);
		
		blurGuidePaint.setStyle(Style.FILL);
		canvas.drawCircle(cx, cy, UserInterfaceUtil.dp2px(BLUR_GUIDE_POINT_RADIUS, view), blurGuidePaint);
	}

	public boolean blurGestureEventEnabled() {
		if ( eventListner != null ) return eventListner.blurGestureEventEnabled();
		return false;
	}
	
	private float actualCenterX(float x) {
		if ( mImageWidth > 0 && mImageHeight > 0 ) return (mWidth - mImageWidth)/2.f + centerX * mImageWidth;
		return centerX * mWidth;
	}
	
	private float actualCenterY(float y) {
		if ( mImageWidth > 0 && mImageHeight > 0 ) return (mHeight - mImageHeight)/2.f + centerY * mImageHeight;
		return centerY * mHeight;
	}
	
	private float actualRadius(float r) {
		if ( mImageWidth > 0 && mImageHeight > 0 ) return r * Math.min(mImageWidth, mImageHeight);
		return r * Math.min(mWidth, mHeight);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		final float sensitvity = 50;
		
		if ((e1.getX() - e2.getX()) > sensitvity ) {
			Log.d("swipe", "Left");
			eventListner.lefeSwiping();
		} else if ((e2.getX() - e1.getX()) > sensitvity) {
			Log.d("swipe", "Right");
			eventListner.rightSwiping();
		}
		return true;
	}
}

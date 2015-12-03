package com.sorasoft.dicecam.view;

import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

public class ArcProgressView extends View {
	
	private Handler mMainHandler = null;
	private Paint mPaint;
	
	private float mStartAngle;
	private float mSweepAngle;
	
	private float mProgress = 0.f;
	
	private long mPlayProgressStartAt = 0;
	private long mPlayProgressDurationMillis = 0;

	public ArcProgressView(Context context) {
		super(context);
		_initialize();
	}

	public ArcProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		_initialize();
	}

	public ArcProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		_initialize();
	}

	
	private void _initialize() {
		mMainHandler = new Handler(Looper.getMainLooper());
		
		mStartAngle = -90.f;
		mSweepAngle = 0.f; 
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.FILL);
		mPaint.setColor(Color.argb(150, 0, 0, 0));
	}
	
	@Override
	public void draw(Canvas canvas) {
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		int length = (int) (Math.max(width, height) * 1.4f);
		int width_g = (width-length)/2;
		int height_g = (height-length)/2;
		RectF oval = new RectF(width_g, height_g, length + width_g, length + height_g);
		
		canvas.drawColor(Color.TRANSPARENT);
		
		if ( mPlayProgressStartAt > 0 && mPlayProgressDurationMillis > 0 ) {
			Date now = new Date();
			long progressMillis = now.getTime() - mPlayProgressStartAt;
			if ( progressMillis > mPlayProgressDurationMillis ) {
				finishProgress();
				mProgress = 1.f;
			} else {
				mProgress = (float)progressMillis / (float)mPlayProgressDurationMillis;
			}
		}
		
		mSweepAngle = 360.f * (1.f - mProgress);
		mStartAngle = 270.f - mSweepAngle;
		
		canvas.drawArc(oval, mStartAngle, mSweepAngle, true, mPaint);
		
		invalidate();
	}

	public void playProgress(long intervalInMillis) {
		mPlayProgressStartAt = new Date().getTime();
		mPlayProgressDurationMillis = intervalInMillis;
		mProgress = 0.f;
		
		mMainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				setVisibility(View.VISIBLE);
			}
		});
	}
	
	public void finishProgress() {
		mPlayProgressStartAt = 0;
		mPlayProgressDurationMillis = 0;
		
		mMainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				setVisibility(View.INVISIBLE);
			}
		});
	}
}

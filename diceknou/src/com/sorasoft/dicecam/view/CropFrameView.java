package com.sorasoft.dicecam.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.sorasoft.dicecam.engine.filter.DicecamLens;

public class CropFrameView extends View {
	
	private Paint mPaint = null;
	private float[] mCropRegion = DicecamLens.NO_CROP_REGION;

	public CropFrameView(Context context) {
		super(context);
		_init();
	}

	public CropFrameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		_init();
	}

	public CropFrameView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		_init();
	}

	private void _init() {
		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);
		mPaint.setTextSize(20.f);
		mPaint.setStyle(Paint.Style.FILL);
		
		setBackgroundColor(Color.TRANSPARENT);
		
		setClickable(false);
	}
	
	public void setCropRegion(float[] c) {
		if ( c == null ) {
			mCropRegion = DicecamLens.NO_CROP_REGION;
		} else {
			mCropRegion = c;
		}
		
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if ( mCropRegion == null || mCropRegion.length < 4 ) return;
		
		final float measuredWidth = (float)getMeasuredWidth();
		final float measuredHeight = (float)getMeasuredHeight();
		
		float left = mCropRegion[0] * measuredWidth;
		float top = mCropRegion[1] * measuredHeight;
		float right = mCropRegion[2] * measuredWidth; 
		float bottom = mCropRegion[3] * measuredHeight;
		
		if ( left > 0.f ) {
			canvas.drawRect(0.f, 0.f, left, measuredHeight, mPaint);
		}
		if ( top > 0.f ) {
			canvas.drawRect(0.f, 0.f, measuredWidth, top, mPaint);
		}
		if ( right < measuredWidth ) {
			canvas.drawRect(right, 0.f, measuredWidth, measuredHeight, mPaint);
		}
		if ( bottom < measuredHeight ) {
			canvas.drawRect(0.f, bottom, measuredWidth, measuredHeight, mPaint);
		}
		
//		if ( BuildConfig.DEBUG ) {
//			mPaint.setStyle(Style.STROKE);
//			
//			mPaint.setColor(Color.MAGENTA);
//			mPaint.setStrokeWidth(10.f);
//			canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
//			
//			mPaint.setColor(Color.CYAN);
//			mPaint.setStrokeWidth(10.f);
//			canvas.drawRect(10.f, 10.f, getWidth() - 20.f, getHeight() - 20.f, mPaint);
//			
//			mPaint.setStyle(Style.FILL);
//			mPaint.setTextSize(28.f);
//			mPaint.setColor(Color.WHITE);
//			String text1 = "" + mCropRegion[0] + ", " + mCropRegion[1] + ", " + mCropRegion[2] + ", " + mCropRegion[3];
//			String text2 = "" + (int)left + "," + (int)top + "," + (int)right + "," + (int)bottom + " - " + measuredWidth + ", " + measuredHeight;
//			canvas.drawText(text1, 80.f, 80.f, mPaint);
//			canvas.drawText(text2, 80.f, 110.f, mPaint);
//			
//			mPaint.setColor(Color.BLACK);
//			mPaint.setStyle(Paint.Style.FILL);
//		}
	}
}

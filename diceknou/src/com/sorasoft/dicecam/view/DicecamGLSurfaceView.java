package com.sorasoft.dicecam.view;

import com.sorasoft.dicecam.BuildConfig;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class DicecamGLSurfaceView extends GLSurfaceView {

	private GLSurfaceViewTouchEvent eventDispatcher = null;
	
	public DicecamGLSurfaceView(Context context) {
		super(context);
		_init(context);
	}

	public DicecamGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		_init(context);
	}

	private void _init(Context context) {
		eventDispatcher = new GLSurfaceViewTouchEvent(context);
		if ( BuildConfig.DEBUG ) {
			setDebugFlags(DEBUG_CHECK_GL_ERROR);
		}
	}
	
	public void setEventListner(DicecamBlurTouchEventListener listner) {
		eventDispatcher.setEventListner(listner);
	}
	
	public GLSurfaceViewTouchEvent eventDispatcher() {
		return eventDispatcher;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		eventDispatcher.onSizeChanged(w, h, oldw, oldh);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return eventDispatcher.onTouchEvent(event);
	}
}

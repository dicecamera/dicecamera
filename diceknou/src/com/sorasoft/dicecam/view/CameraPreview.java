package com.sorasoft.dicecam.view;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements Callback {

	private SurfaceHolder mHolder;
	private Camera mCamera;
	private boolean previewing;

	private final String TAG = "dicecam";

	public CameraPreview(Context context) {
		super(context);
		setup();
	}

	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
	}

	public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setup();
	}

	@SuppressWarnings("deprecation")
	private void setup() {
		previewing = false;

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);

		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void setCamera(Camera c) {
		if (c != null) {
			mCamera = c;
			startPreview();
		} else {
			stopPreview();
			mCamera = null;
		}
	}

	public void startPreview() {
		if (previewing)
			return;

		Log.d(TAG, "CameraPreview.startPreview");

		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
			previewing = true;
			Log.d(TAG, "CameraPreview.startPreview DONE");
		} catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}

	public void stopPreview() {
		Log.d(TAG, "CameraPreview.stopPreview");
		try {
			this.getHolder().removeCallback(this);
		} catch (Exception e) {
			Log.d(TAG, "Error stopping camera preview: " + e.getMessage());
		}
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			previewing = false;
		}
		Log.d(TAG, "CameraPreview.stopPreview DONE");
	}

	// @see
	// http://developer.android.com/reference/android/hardware/Camera.html#setDisplayOrientation(int)
	// @SuppressLint("NewApi")
	// public static void setCameraDisplayOrientation(Activity activity, int
	// cameraId, android.hardware.Camera camera) {
	// if (Build.VERSION.SDK_INT < 9 ) return;
	// android.hardware.Camera.CameraInfo info = new
	// android.hardware.Camera.CameraInfo();
	// android.hardware.Camera.getCameraInfo(cameraId, info);
	// int rotation = activity.getWindowManager().getDefaultDisplay()
	// .getRotation();
	// int degrees = 0;
	// switch (rotation) {
	// case Surface.ROTATION_0: degrees = 0; break;
	// case Surface.ROTATION_90: degrees = 90; break;
	// case Surface.ROTATION_180: degrees = 180; break;
	// case Surface.ROTATION_270: degrees = 270; break;
	// }
	//
	// int result;
	// if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	// result = (info.orientation + degrees) % 360;
	// result = (360 - result) % 360; // compensate the mirror
	// } else { // back-facing
	// result = (info.orientation - degrees + 360) % 360;
	// }
	// camera.setDisplayOrientation(result);
	// }

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		stopPreview();

		// set preview size and make any resize, rotate or
		// reformatting changes here

		// start preview with new settings
		startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the
		// preview.
		startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
		stopPreview();
	}

}

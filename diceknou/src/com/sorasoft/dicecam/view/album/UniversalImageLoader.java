package com.sorasoft.dicecam.view.album;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class UniversalImageLoader extends FrameLayout {

	private Handler mMainHandler = null;
	private Thread mImageLoadThread = null;

	private String mURLString = null;
	private Bitmap mBitmap = null;

	private ImageView mImageView;
	private ProgressBar mImageProgressBar;

	private float[] mCropRegion = { 0.f, 0.f, 1.f, 1.f };

	private int loadedImageWidth = 0;
	private int loadedImageHeight = 0;

	private boolean mLoaded = false;

	public interface URILoadingFinishListner {
		void onURILoadingFinish(Bitmap sourceBitmap);
	}

	public URILoadingFinishListner uriLoadingFinishListner = null;

	public void setOnURILoadingFinishListner(URILoadingFinishListner l) {
		this.uriLoadingFinishListner = l;
	}

	public interface LoadingFinishListner {
		void onLoadingFinish(Bitmap bitmap);
	}

	public LoadingFinishListner loadingFinishListner = null;

	public void setOnLoadingFinishListner(LoadingFinishListner loadingFinishListner) {
		this.loadingFinishListner = loadingFinishListner;
	}

	public UniversalImageLoader(Context context) {
		super(context);
		_initialize(context);
	}

	public UniversalImageLoader(Context context, AttributeSet attrs) {
		super(context, attrs);
		_initialize(context);
	}

	public UniversalImageLoader(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		_initialize(context);
	}

	public void _initialize(Context context) {
		LayoutParams imageViewLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
		mImageView = new ImageView(context);
		this.addView(mImageView, imageViewLayoutParams);

		LayoutParams progressBarLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
		mImageProgressBar = new ProgressBar(context);
		mImageProgressBar.setIndeterminate(true);
		this.addView(mImageProgressBar, progressBarLayoutParams);
	}

	public void setCropRegion(float[] c) {
		mCropRegion = c;
	}

	private BitmapDrawable getProperBitmapDrawable(Uri uri) {
		File file = new File(uri.getPath());
		if (file.exists() == false)
			return null;

		Bitmap bitmap = null;
		bitmap = ImageLoader.getInstance().loadImageSync("file://" + uri.getPath());

		if (this.uriLoadingFinishListner != null) {
			this.uriLoadingFinishListner.onURILoadingFinish(bitmap);
		}

		if (bitmap == null)
			return null;

		// Get orientation
		ExifInterface exif = null;
		int orientation = 1;
		try {
			exif = new ExifInterface(uri.getPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (exif != null) {
			orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);// grab
																					// the
																					// orientation
																					// of
																					// the
																					// image,the
																					// most
																					// important
																					// values
																					// are
																					// 3,
																					// 6
																					// and
																					// 8.
																					// If
																					// the
																					// orientation
																					// is
																					// 6,
																					// for
																					// example,
																					// you
																					// can
																					// rotate
																					// the
																					// image
																					// like
																					// this:
		}
		Log.d("dicecam", "filename: " + uri.getPath() + ", orientation: " + orientation);

		return getProperBitmapDrawable(bitmap, orientation);
	}

	private BitmapDrawable getProperBitmapDrawable(Bitmap bitmap, int orientation) { // orientation:
																						// exif
																						// tag
		int boundingX = this.getWidth();
		int boundingY = this.getHeight();

		int sourceWidth = 100;
		int sourceHeight = 100;

		if (orientation > 4) {
			sourceWidth = bitmap.getHeight();
			sourceHeight = bitmap.getWidth();
		} else {
			sourceWidth = bitmap.getWidth();
			sourceHeight = bitmap.getHeight();
		}

		// set loaded image size
		setLoadedImageWidth(sourceWidth);
		setLoadedImageHeight(sourceHeight);

		int outputWidth = sourceWidth;
		int outputHeight = sourceHeight;

		Bitmap scaledBitmap = null;
		int x = 0;
		int y = 0;
		int width = sourceWidth; // bitmap.getWidth();// sourceWidth;
		int height = sourceHeight; // bitmap.getHeight();// sourceHeight;

		// cropping
		if (mCropRegion != null && (mCropRegion[0] > 0.f || mCropRegion[1] > 0.f || mCropRegion[2] < 1.f || mCropRegion[3] < 1.f)) {
			Log.i("dicecam", "cropRegion: " + mCropRegion[0] + ", " + mCropRegion[1] + ", " + mCropRegion[2] + ", " + mCropRegion[3]);
			boolean flip = (orientation > 4) ? false : true;
			float[] cr = (flip == false) ? mCropRegion : new float[] { mCropRegion[1], mCropRegion[0], mCropRegion[3], mCropRegion[2] };
			x = Math.round(cr[0] * (float) sourceWidth);
			y = Math.round(cr[1] * (float) sourceHeight);
			width = Math.round((cr[2] - cr[0]) * (float) sourceWidth);
			height = Math.round((cr[3] - cr[1]) * (float) sourceHeight);
		}
		Log.i("dicecam", "cr: " + x + ", " + y + ", " + width + ", " + height);

		if (boundingX == 0)
			boundingX = width;
		if (boundingY == 0)
			boundingY = height;

		// Determine how much to scale: the dimension requiring less scaling is
		// closer to the its side. This way the image always stays inside your
		// bounding box AND either x/y axis touches it.
		float xScale = (float) boundingX / (float) width;
		float yScale = (float) boundingY / (float) height;
		float scale = Math.min(xScale, yScale);

		// Create a matrix for the scaling and add the scaling data
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);

		switch (orientation) {
		case 1:
			break;
		case 2:
			matrix.postScale(-1, 1);
			break;
		case 3:
			matrix.postRotate(180);
			break;
		case 4:
			matrix.postRotate(180);
			matrix.postScale(-1, 1);
			break;
		case 5:
			matrix.postRotate(90);
			matrix.postScale(-1, 1);
			break;
		case 6:
			matrix.postRotate(90);
			break;
		case 7:
			matrix.postRotate(-90);
			matrix.postScale(-1, 1);
			break;
		case 8:
			matrix.postRotate(-90);
			break;
		default:
			break;
		}

		//HJH_System.gc();
		// Create a new bitmap and convert it to a format understood by the
		// ImageView
		try {
			if (orientation > 4) {
				Log.i("dicecam", "hjh" + x + ", " + y + ", " + width + ", " + height);
				scaledBitmap = Bitmap.createBitmap(bitmap, y, x, height, width, matrix, true);
			} else {
				Log.i("dicecam", "hjh" + x + ", " + y + ", " + width + ", " + height);
				scaledBitmap = Bitmap.createBitmap(bitmap, x, y, width, height, matrix, true);
			}

			outputWidth = scaledBitmap.getWidth(); // re-use
			outputHeight = scaledBitmap.getHeight(); // re-use

		} catch (Exception e) {
			e.printStackTrace();
			this.loadImage(mURLString);
		}

		// clear bitmap
		// bitmap.recycle();
		bitmap = null;
		//HJH_System.gc();

		BitmapDrawable scaledDrawable = new BitmapDrawable(this.getResources(), scaledBitmap);
		Log.i("dicecam", "scaled : " + Integer.toString(sourceWidth) + " x " + Integer.toString(sourceHeight));
		Log.i("dicecam",
				"scaled : output (ori: " + orientation + ") " + Integer.toString(outputWidth) + " x " + Integer.toString(outputHeight));

		return scaledDrawable;
	}

	public boolean isLoaded() {
		return mLoaded;
	}

	public int getLoadedImageWidth() {
		return loadedImageWidth;
	}

	public void setLoadedImageWidth(int loadedImageWidth) {
		this.loadedImageWidth = loadedImageWidth;
	}

	public int getLoadedImageHeight() {
		return loadedImageHeight;
	}

	public void setLoadedImageHeight(int loadedImageHeight) {
		this.loadedImageHeight = loadedImageHeight;
	}

	private Handler getMainHandler() {
		if (mMainHandler == null) {
			mMainHandler = new Handler(Looper.getMainLooper());
		}
		return mMainHandler;
	}

	public void loadImage(String urlString) {
		mURLString = urlString;
		loadImage();
	}

	public void loadImage(Bitmap bitmap) {
		mBitmap = bitmap;
		loadImage();
	}

	public void loadImage() {
		if (mImageLoadThread != null) {
			cancelLoading();
		}

		// Always zero, Why is this
		Log.d("dicecam", "Universal loader size: " + getWidth() + ", " + getHeight());

		Runnable r = new Runnable() {

			@Override
			public void run() {
				Log.d("dicecam", "load from mURLString: " + mURLString);

				final BitmapDrawable drawable = mBitmap != null ? getProperBitmapDrawable(mBitmap, 1) : getProperBitmapDrawable(Uri
						.parse(mURLString));
				if (drawable != null) {
					getMainHandler().post(new Runnable() {
						@Override
						public void run() {
							Log.d("dicecam", "drawable: " + drawable.getBounds());

							try {
								mImageView.setImageDrawable(drawable);
							} catch (Exception e) {
								e.printStackTrace();
								Log.d("dicecam", "Cannot set Image Drawable: " + drawable);
							}
							hideImageLoadingProgress();
							mLoaded = true;

							if (loadingFinishListner != null) {
								loadingFinishListner.onLoadingFinish(drawable == null ? null : drawable.getBitmap());
							}
						}
					});
				} else {
					getMainHandler().post(new Runnable() {

						@Override
						public void run() {
							hideImageLoadingProgress();
						}
					});
				}
			}
		};

		showImageLoadingProgress();

		mImageLoadThread = new Thread(r);
		mImageLoadThread.start();
	}

	public void cancelLoading() {
		if (mImageLoadThread == null)
			return;
		mImageLoadThread.interrupt();
	}

	public void showImageLoadingProgress() {
		mImageProgressBar.setVisibility(View.VISIBLE);
	}

	public void hideImageLoadingProgress() {
		mImageProgressBar.setVisibility(View.INVISIBLE);
	}

	public void deleteImage() {
		cancelLoading();

		Drawable drawable = mImageView.getDrawable();
		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			Bitmap bitmap = bitmapDrawable.getBitmap();
			bitmap.recycle();
		}
		mImageView.setImageResource(android.R.color.transparent);
	}

	public Bitmap getBitmap() {
		return ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
	}

	public void setBitmap(Bitmap b) {
		mImageView.setImageBitmap(b);
	}
}

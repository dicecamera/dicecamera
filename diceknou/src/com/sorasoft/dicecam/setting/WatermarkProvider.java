package com.sorasoft.dicecam.setting;

import java.io.IOException;
import java.io.InputStream;

import com.sorasoft.dicecam.BuildConfig;
import com.sorasoft.dicecam.ImageComposition;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;


public class WatermarkProvider {
	
	public final static int DICECAM_WATERMARK_NONE = 0;
	
	public final static int DICECAM_WATERMARK_STAMP2 = 1; 
	public final static int DICECAM_WATERMARK_STAMP3 = 2;
	public final static int DICECAM_WATERMARK_RENEWAL2 = 3;
	public final static int DICECAM_WATERMARK_POP2 = 4;
	public final static int DICECAM_WATERMARK_NEW2 = 5;
	public final static int DICECAM_WATERMARK_NEW4 = 6;
	public final static int DICECAM_WATERMARK_NEW6 = 7;
	public final static int DICECAM_WATERMARK_NEW9 = 8;
	public final static int DICECAM_WATERMARK_NEW10 = 9;
	public final static int DICECAM_WATERMARK_STAMPFULL = 10;
	public final static int DICECAM_WATERMARK_RIBONFULL = 11; 
	public final static int DICECAM_WATERMARK_NEW11 = 12;
	public final static int DICECAM_WATERMARK_NEW12 = 13;
	public final static int DICECAM_WATERMARK_VER1 = 14;
	public final static int DICECAM_WATERMARK_SIMPLE = 15;
	public final static int DICECAM_WATERMARK_VINTAGE = 16;
	
	public final static int DICECAM_WATERMARK_FLAG_SINGLE_CAMERA = 0;
	public final static int DICECAM_WATERMARK_FLAG_SINGLE_BUFFER = 1;
	public final static int DICECAM_WATERMARK_FLAG_COLLAGE = 2;
	
	public enum Type {
		PNG, PNG_HALF, PNG_HD, ICON, TITLE
	}
	
	static private String getFilename(int watermark, Type type) {
		String filename = null;
		String idString = null;
		boolean useIconPng = false;
		
		switch (watermark) {
		case WatermarkProvider.DICECAM_WATERMARK_NEW10:				idString = "new10"; break;
		case WatermarkProvider.DICECAM_WATERMARK_NEW11:				idString = "new11"; break;
		case WatermarkProvider.DICECAM_WATERMARK_NEW12:				idString = "new12"; break;
		case WatermarkProvider.DICECAM_WATERMARK_NEW2:				idString = "new2"; break;
		case WatermarkProvider.DICECAM_WATERMARK_NEW4:				idString = "new4"; break;
		case WatermarkProvider.DICECAM_WATERMARK_NEW6:				idString = "new6"; break;
		case WatermarkProvider.DICECAM_WATERMARK_NEW9:				idString = "new9"; break;
		case WatermarkProvider.DICECAM_WATERMARK_POP2:				idString = "pop2"; break;
		case WatermarkProvider.DICECAM_WATERMARK_RENEWAL2:			idString = "renewal2"; break;
		case WatermarkProvider.DICECAM_WATERMARK_RIBONFULL:			idString = "rb"; useIconPng = true; break;
		case WatermarkProvider.DICECAM_WATERMARK_STAMP3:			idString = "stamp3"; break;
		case WatermarkProvider.DICECAM_WATERMARK_STAMPFULL:			idString = "stamp"; break;
		case WatermarkProvider.DICECAM_WATERMARK_VER1:				idString = "ver1"; break;
		case WatermarkProvider.DICECAM_WATERMARK_STAMP2:			idString = "stamp2"; break;
		case WatermarkProvider.DICECAM_WATERMARK_SIMPLE:			idString = "simple"; break; // default
		case WatermarkProvider.DICECAM_WATERMARK_VINTAGE:			idString = "vintage"; break;
		default:
			break;
		}
		
		if ( useIconPng == true && type == Type.ICON ) {
			type = Type.PNG;
		}
		
		if ( filename == null ) {
			switch (type) {
			case PNG:
				filename = "watermarks/png/wm_" + idString + ".png";
				break;
			case PNG_HALF:
				filename = "watermarks/png/wm_HALF" + idString + "_half.png";
				break;
			case PNG_HD:
				filename = "watermarks/png_HD/wm_" + idString + "_2x.png";
				break;
			case ICON:
			case TITLE:
				filename = "watermarks/title/wm_" + idString + "_title.png";
				break;
				
			default:
				break;
			}
		}
		
		return filename;
	}
	
	static public Bitmap getWatermark(Context context, int watermark, Type type) {
		AssetManager assetManager = context.getAssets();
		String filename = getFilename(watermark, type);
		InputStream logo_stream = null;
		try {
			logo_stream = assetManager.open(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if ( logo_stream == null ) return null;
		return BitmapFactory.decodeStream(logo_stream);
	}
	
	static public Bitmap getCurrentWatermarkIcon(Context context) {
		return getCurrentWatermarkIcon(context.getAssets());
	}
	
	static public Bitmap getCurrentWatermarkIcon(AssetManager assetManager) {
		String filename = getFilename(Settings.getInstance().getWatermark(), Type.ICON);
		InputStream logo_stream = null;
		try {
			logo_stream = assetManager.open(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if ( logo_stream == null ) return null;
		return BitmapFactory.decodeStream(logo_stream);
	}
	
	static public Bitmap getCurrentWatermark3200(AssetManager assetManager, boolean isHD) {
		String filename = getFilename(Settings.getInstance().getWatermark(), isHD ? Type.PNG_HD : Type.PNG);
		InputStream logo_stream = null;
		try {
			logo_stream = assetManager.open(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if ( logo_stream == null ) return null;
		return BitmapFactory.decodeStream(logo_stream);
	}
	
	static private int rotationAngelForOrientation(int orientation, int marginFlag, boolean shouldFlipHorizontally) {
		if ( marginFlag == DICECAM_WATERMARK_FLAG_SINGLE_CAMERA ) {
			switch ( orientation ) {
			case ImageComposition.DICECAM_ORIENTATION_PORTRAIT:					return -90;
			case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_LEFT:			return 180;
			case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_RIGHT:			return 0;
			case ImageComposition.DICECAM_ORIENTATION_PORTRAIT_UPSIDE_DOWN:		return 90;

			case ImageComposition.DICECAM_ORIENTATION_NONE:
			default:
				break;
			}
		} else {
			switch ( orientation ) {
			case ImageComposition.DICECAM_ORIENTATION_PORTRAIT:					return 180;
			case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_LEFT:			return shouldFlipHorizontally ? -90 : 90;
			case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_RIGHT:			return shouldFlipHorizontally ? 90 : -90;
			case ImageComposition.DICECAM_ORIENTATION_PORTRAIT_UPSIDE_DOWN:		return 0;

			case ImageComposition.DICECAM_ORIENTATION_NONE:
			default:
				break;
			}
		}
		return 0;
	}
	
	static private float MINIMUM_RESIZE_RATIO = 0.25f;
	
	static public Bitmap applyWatermark(Context context, Bitmap originalImage, int orientation, int marginFlag, boolean shouldFlipHorizontally) {
	
		int minLength = Math.min(originalImage.getWidth(), originalImage.getHeight());
		
		boolean useWatermarkImageHD = minLength > 1600;
		Bitmap watermarkImage = getCurrentWatermark3200(context.getAssets(), useWatermarkImageHD);
		
		float resizeRatio = minLength / 3200.f; 
		if (useWatermarkImageHD == false) {
			resizeRatio = resizeRatio * 2;
		}
		
		if ( resizeRatio < MINIMUM_RESIZE_RATIO ) resizeRatio = MINIMUM_RESIZE_RATIO;
		
		// prepare watermarkImage
		Matrix m = new Matrix();
		m.setRotate(rotationAngelForOrientation(orientation, marginFlag, shouldFlipHorizontally));
		
		int scaleMultiplier = (shouldFlipHorizontally ? -1 : 1);
		m.postScale(resizeRatio * scaleMultiplier, resizeRatio);

		watermarkImage = Bitmap.createBitmap(watermarkImage, 0, 0, watermarkImage.getWidth(), watermarkImage.getHeight(), m, false);
		
		int frameMarginSize = Settings.getInstance().getShouldFramePhoto() ? ImageComposition.marginGapCalculator(originalImage.getWidth(), originalImage.getHeight()) : 0;
		
		if ( BuildConfig.DEBUG ) {
			Log.d("dicecam", "watermark: originalImage.size: " + originalImage.getWidth() + " x " + originalImage.getHeight());
			Log.d("dicecam", "watermark: hd: " + useWatermarkImageHD + " ratio: " + resizeRatio + " -> size: " + watermarkImage.getWidth() + " x " + watermarkImage.getHeight());
			Log.d("dicecam", "watermark: frameMarginSize: " + frameMarginSize);
		}
		
		// prepare marginTop,Left
		int marginTop = 0;
		int marginLeft = 0;

		switch ( marginFlag ) {
		case DICECAM_WATERMARK_FLAG_SINGLE_CAMERA:
		{
			switch ( orientation ) {
			case ImageComposition.DICECAM_ORIENTATION_PORTRAIT:
				marginTop = frameMarginSize;
				marginLeft = originalImage.getWidth() - watermarkImage.getWidth() - frameMarginSize;
				break;
			case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_LEFT:
				marginTop = frameMarginSize;
				marginLeft = frameMarginSize;
				break;
			case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_RIGHT:
				marginTop = originalImage.getHeight() - watermarkImage.getHeight() - frameMarginSize;
				marginLeft = originalImage.getWidth() - watermarkImage.getWidth() - frameMarginSize;
				break;
			case ImageComposition.DICECAM_ORIENTATION_PORTRAIT_UPSIDE_DOWN:
				marginTop = originalImage.getHeight() - watermarkImage.getHeight() - frameMarginSize;
				marginLeft = frameMarginSize;
				break;
			}
		}
		break;
		
		case DICECAM_WATERMARK_FLAG_SINGLE_BUFFER:
		{
			switch ( orientation ) {
			case ImageComposition.DICECAM_ORIENTATION_PORTRAIT:
				if ( shouldFlipHorizontally ) {
					marginTop = frameMarginSize;
					marginLeft = originalImage.getWidth() - watermarkImage.getWidth() - frameMarginSize;
				} else {
					marginTop = frameMarginSize;
					marginLeft = frameMarginSize;
				}
				break;
			case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_LEFT:
				if ( shouldFlipHorizontally ) {
					marginTop = frameMarginSize;
					marginLeft = frameMarginSize;
				} else {
					marginTop = originalImage.getHeight() - watermarkImage.getHeight() - frameMarginSize;
					marginLeft = frameMarginSize;
				}
				break;
			case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_RIGHT:
				if ( shouldFlipHorizontally ) {
					marginTop = originalImage.getHeight() - watermarkImage.getHeight() - frameMarginSize;
					marginLeft = originalImage.getWidth() - watermarkImage.getWidth() - frameMarginSize;
				} else {
					marginTop = frameMarginSize;
					marginLeft = originalImage.getWidth() - watermarkImage.getWidth() - frameMarginSize;
				}
				break;
			case ImageComposition.DICECAM_ORIENTATION_PORTRAIT_UPSIDE_DOWN:
				if ( shouldFlipHorizontally ) {
					marginTop = originalImage.getHeight() - watermarkImage.getHeight() - frameMarginSize;
					marginLeft = frameMarginSize;
				} else {
					marginTop = originalImage.getHeight() - watermarkImage.getHeight() - frameMarginSize;
					marginLeft = originalImage.getWidth() - watermarkImage.getWidth() - frameMarginSize;
				}
				break;
			}
		}
		break;
		
		case DICECAM_WATERMARK_FLAG_COLLAGE:
		{
			marginTop = originalImage.getHeight() - watermarkImage.getHeight() - frameMarginSize;
			marginLeft = originalImage.getWidth() - watermarkImage.getWidth() - frameMarginSize;
		}
		break;
		
		}
		
		Canvas watermarkedImageCanvas = new Canvas(originalImage);
		watermarkedImageCanvas.drawBitmap(watermarkImage, marginLeft, marginTop, null);

		return originalImage;
	}
	
}

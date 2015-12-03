package com.sorasoft.dicecam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;

import com.sorasoft.dicecam.engine.BufferPictureCallback;
import com.sorasoft.dicecam.setting.Settings;
import com.sorasoft.dicecam.setting.WatermarkProvider;

public class ImageComposition {

	public final static int DICECAM_ORIENTATION_NONE = -1;

	public final static int DICECAM_ORIENTATION_PORTRAIT = 0;
	public final static int DICECAM_ORIENTATION_LANDSCAPE_LEFT = 1;
	public final static int DICECAM_ORIENTATION_PORTRAIT_UPSIDE_DOWN = 2;
	public final static int DICECAM_ORIENTATION_LANDSCAPE_RIGHT = 3;

	public final static int MINIMUM_FRAME_MARGIN_GAP = 5;

	public interface AddingCallback {
		void onCollageImageAdded(int imageCount);
	}

	public interface ProcessCallback {
		void onProcess(float process);
	};

	private AddingCallback collageImageAddingCallback = null;
	private ProcessCallback composeProcessCallback = null;
	private ProcessCallback singlePictureProcessCallback = null;

	private DiceCamera mainActivity;
	private Handler taskHandler = null;

	private int orientation = DICECAM_ORIENTATION_PORTRAIT; // outputFile
															// orientation
	private boolean flipHorizontal = false;

	public ImageComposition(DiceCamera ma) {

		this.mainActivity = ma;
		this.collageImageAddingCallback = null;
		this.composeProcessCallback = null;
		this.singlePictureProcessCallback = null;
		taskHandler = new Handler();
	}

	public void setCollageImageAddingCallback(AddingCallback collageImageAddingCallback) {
		this.collageImageAddingCallback = collageImageAddingCallback;
	}

	public void setSinglePictureProcessCallback(ProcessCallback singlePictureProcessCallback) {
		this.singlePictureProcessCallback = singlePictureProcessCallback;
	}

	public void setOrientation(int o) {
		orientation = o;
	}

	public int getOrientatino() {
		return orientation;
	}

	public void setFlipHorizontal(boolean f) {
		flipHorizontal = f;
	}

	public boolean getFlipHorizontal() {
		return flipHorizontal;
	}

	public void delayedTask(Runnable r, long delayMillis) {
		taskHandler.postDelayed(r, delayMillis);
	}

	public BufferPictureCallback pc = new BufferPictureCallback() {

		@Override
		public void callback(ByteBuffer buffer, int width, int height) {

			onSinglePictureProcess(0.1f);

			byte[] bb = new byte[buffer.remaining()];
			buffer.get(bb);

			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(bb, 0, bb.length));

			bb = null;

			onSinglePictureProcess(0.5f);

			if (Settings.getInstance().getShouldFramePhoto()) {

				int marginGap = marginGapCalculator(width, height);

				Bitmap framedImage = Bitmap.createBitmap(width + (marginGap * 2), height + (marginGap * 2), Bitmap.Config.ARGB_8888);

				onSinglePictureProcess(0.6f);

				Canvas framedImageCanvas = new Canvas(framedImage);

				if (Settings.getInstance().getFrameColor() == Settings.FRAME_COLOR_WHITE) {
					framedImageCanvas.drawColor(Color.WHITE);
				}

				onSinglePictureProcess(0.7f);

				framedImageCanvas.drawBitmap(bitmap, marginGap, marginGap, null);

				bitmap = framedImage;

				onSinglePictureProcess(0.9f);
			}

			onSinglePictureProcess(1.0f);

			// apply watermark (single buffer)
			if (Settings.getInstance().isWatermarkEnabled()) {
				boolean flipHorizontally = (mainActivity.isFrontCameraActivated() && Settings.getInstance().getSaveAsPreview() == false) ? false
						: true;
				bitmap = WatermarkProvider.applyWatermark(mainActivity, bitmap, mainActivity.ORIENTATION,
						WatermarkProvider.DICECAM_WATERMARK_FLAG_SINGLE_BUFFER, flipHorizontally);
			}

			// flip horizontal (save as preview, when front camera activated)
			boolean flipHorizontal =mainActivity.isFrontCameraActivated() && (Settings.getInstance().getSaveAsPreview() == false);
					
//			boolean flipHorizontal = false;

			FileManagement fm = new FileManagement(mainActivity);
			Uri savedUri = fm.saveBitmap(bitmap, mainActivity.ORIENTATION, mainActivity.shouldStoreEXIF(), false, flipHorizontal,
					mainActivity.getLastKnownLocationForPictureExif());
			
			
//			bitmap = rotateWithOrientation(bitmap, orientation, flipHorizontal);

			if (savedUri == null) {
				mainActivity.showSavingError();
			} else {
				Log.i("Dicecam", "hjh ImageComposition savedUri" + savedUri);
				mainActivity.showQuickView(savedUri, "RenderedPicture", 1);
			}
		}

	};

	public BufferPictureCallback rect_pc = new BufferPictureCallback() {

		@Override
		public void callback(ByteBuffer buffer, int width, int height) {

			byte[] bb = new byte[buffer.remaining()];
			buffer.get(bb);

			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(bb, 0, bb.length));

			bb = null;

			Log.d("dicecam", "Activity.Orientation: " + mainActivity.ORIENTATION + " , set Orientation: " + orientation + ", set FlipHori: "
					+ flipHorizontal);

			bitmap = rotateWithOrientation(bitmap, orientation, flipHorizontal);

			mainActivity.collagePictureBitmaps.add(bitmap);

			if (collageImageAddingCallback != null) {
				taskHandler.post(new Runnable() {
					@Override
					public void run() {
						try {
							collageImageAddingCallback.onCollageImageAdded(mainActivity.collagePictureBitmaps.size());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
	};

	public void no_effect_tmp_file_pc(final File tmpFile, final byte[] data, final int pictureOrientation, final boolean isFrontCamera,
			final boolean saveAsPreview) {
		final Uri imageUri = FileManagement.saveBitmapData(tmpFile, data, pictureOrientation, isFrontCamera, saveAsPreview, null);
		Log.d("take", "imageUri: ori(" + pictureOrientation + ") f: " + flipHorizontal + " - " + imageUri);

		mainActivity.collagePictureURIs.add(imageUri);

		if (collageImageAddingCallback != null) {
			taskHandler.post(new Runnable() {
				@Override
				public void run() {
					collageImageAddingCallback.onCollageImageAdded(mainActivity.collagePictureURIs.size());
				}
			});
		}
	}

	public static Bitmap rotateWithOrientation(Bitmap b, int orientation, boolean flipHorizontal) {
		switch (orientation) {
		case DICECAM_ORIENTATION_PORTRAIT_UPSIDE_DOWN:
			return rotateWithDegrees(b, 180, flipHorizontal);
		case DICECAM_ORIENTATION_LANDSCAPE_LEFT:
			return rotateWithDegrees(b, 90, flipHorizontal);
		case DICECAM_ORIENTATION_LANDSCAPE_RIGHT:
			return rotateWithDegrees(b, 270, flipHorizontal);
		default:
		case DICECAM_ORIENTATION_PORTRAIT:
			return rotateWithDegrees(b, 0, flipHorizontal);
		}
	}

	public static Bitmap rotateWithDegrees(Bitmap b, int degrees, boolean flipHorizontal) {
		if (b == null)
			return null;
		if (degrees == 0 && flipHorizontal == false)
			return b;

		Matrix m = new Matrix();
		m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);

		// flipHorizontal
		if (flipHorizontal) {
			m.postScale(-1, 1);
			m.postTranslate(b.getWidth(), 0);
		}

		try {
			Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
			if (b != b2) {
				b.recycle();
				b = b2;
			}
		} catch (OutOfMemoryError ex) {
			// We have no memory to rotate. Return the original bitmap.
		}

		return b;
	}

	public Bitmap composeMultipleTakeTape(int collageStatus, List<Bitmap> renderedPictures, int takes, ProcessCallback processCallback) {
		this.composeProcessCallback = processCallback;
		return composeMultipleTakeTape(collageStatus, renderedPictures, takes);
	}

	public Bitmap composeMultipleTakeTape(int collageStatus, List<?> items, int takes) {

		Bitmap firstTake = getBitmap(items.get(0), MAX_DIMENSION_FOR_BITMAP_TAPE);

		int pictureWidth = firstTake.getWidth();
		int pictureHeight = firstTake.getHeight();

		firstTake = null;

		onComposeProcess(0.f);

		int combinedWidth = 0;
		int combinedHeight = 0;

		Bitmap combinedImage = null;

		switch (collageStatus) {
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_DOUBLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_DOUBLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_VERTICAL:

		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_TRIPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_TRIPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_VERTICAL:

		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_VERTICAL: {

			switch (collageStatus) {

			case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_VERTICAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_TRIPLE_VERTICAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_DOUBLE_VERTICAL: {
				combinedWidth = pictureWidth * takes;
				combinedHeight = pictureHeight;
				break;
			}

			case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_VERTICAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_TRIPLE_VERTICAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_DOUBLE_VERTICAL: {

				int length = (pictureWidth > pictureHeight ? pictureHeight : pictureWidth);

				combinedWidth = length * takes;
				combinedHeight = length;
				break;
			}

			case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_VERTICAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_VERTICAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_VERTICAL: {

				int length = (pictureWidth > pictureHeight ? pictureWidth : pictureHeight);
				combinedWidth = length;
				combinedHeight = length;

				break;
			}
			}

			onComposeProcess(0.1f);

			int marginGap = 0;
			int positionMarker = combinedWidth / takes;

			if (Settings.getInstance().getShouldFramePhoto()) {
				marginGap = marginGapCalculator(pictureWidth, pictureHeight);

				combinedWidth += (takes + 1) * marginGap;
				combinedHeight += 2 * marginGap;
			}

			combinedImage = Bitmap.createBitmap(combinedWidth, combinedHeight, Bitmap.Config.ARGB_8888);

			onComposeProcess(0.3f);

			Canvas combinedImageCanvas = new Canvas(combinedImage);

			if (Settings.getInstance().getFrameColor() == Settings.FRAME_COLOR_WHITE) {
				combinedImageCanvas.drawColor(Color.WHITE);
			}

			combinedImageCanvas.drawBitmap(getBitmap(items.get(0), MAX_DIMENSION_FOR_BITMAP_TAPE), marginGap * 1, marginGap, null);
			combinedImageCanvas.drawBitmap(getBitmap(items.get(1), MAX_DIMENSION_FOR_BITMAP_TAPE), (marginGap * 2) + positionMarker, marginGap, null);

			onComposeProcess(0.5f);

			switch (takes) {
			case 3: {
				combinedImageCanvas.drawBitmap(getBitmap(items.get(2), MAX_DIMENSION_FOR_BITMAP_TAPE), (marginGap * 3) + (positionMarker * 2),
						marginGap, null);

				break;
			}

			case 4: {
				combinedImageCanvas.drawBitmap(getBitmap(items.get(2), MAX_DIMENSION_FOR_BITMAP_TAPE), (marginGap * 3) + (positionMarker * 2),
						marginGap, null);
				combinedImageCanvas.drawBitmap(getBitmap(items.get(3), MAX_DIMENSION_FOR_BITMAP_TAPE), (marginGap * 4) + (positionMarker * 3),
						marginGap, null);

				break;
			}
			}

			combinedImageCanvas.save();
			combinedImageCanvas = null;

			onComposeProcess(0.7f);

			break;
		}

		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_DOUBLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_DOUBLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_HORIZONTAL:

		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_TRIPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_TRIPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_HORIZONTAL:

		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_HORIZONTAL: {

			switch (collageStatus) {

			case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_HORIZONTAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_TRIPLE_HORIZONTAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_DOUBLE_HORIZONTAL: {
				combinedWidth = pictureWidth;
				combinedHeight = pictureHeight * takes;
				break;
			}

			case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_HORIZONTAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_TRIPLE_HORIZONTAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_DOUBLE_HORIZONTAL: {

				int length = (pictureWidth > pictureHeight ? pictureHeight : pictureWidth);

				combinedWidth = length;
				combinedHeight = length * takes;
				break;
			}

			case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_HORIZONTAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_HORIZONTAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_HORIZONTAL: {

				int length = (pictureWidth > pictureHeight ? pictureWidth : pictureHeight);
				combinedWidth = length;
				combinedHeight = length;

				break;
			}

			}

			onComposeProcess(0.1f);

			int marginGap = 0;
			int positionMarker = combinedHeight / takes;

			if (Settings.getInstance().getShouldFramePhoto()) {
				marginGap = marginGapCalculator(pictureWidth, pictureHeight);

				combinedHeight += (takes + 1) * marginGap;
				combinedWidth += 2 * marginGap;
			}

			combinedImage = Bitmap.createBitmap(combinedWidth, combinedHeight, Bitmap.Config.ARGB_8888);

			onComposeProcess(0.3f);

			Canvas combinedImageCanvas = new Canvas(combinedImage);

			if (Settings.getInstance().getFrameColor() == Settings.FRAME_COLOR_WHITE) {
				combinedImageCanvas.drawColor(Color.WHITE);
			}

			combinedImageCanvas.drawBitmap(getBitmap(items.get(items.size() - 1), MAX_DIMENSION_FOR_BITMAP_TAPE), marginGap, marginGap, null);
			combinedImageCanvas.drawBitmap(getBitmap(items.get(items.size() - 2), MAX_DIMENSION_FOR_BITMAP_TAPE), marginGap, (marginGap * 2)
					+ positionMarker, null);

			onComposeProcess(0.5f);

			switch (takes) {
			case 3: {
				combinedImageCanvas.drawBitmap(getBitmap(items.get(items.size() - 3), MAX_DIMENSION_FOR_BITMAP_TAPE), marginGap, (marginGap * 3)
						+ (positionMarker * 2), null);

				break;
			}

			case 4: {
				combinedImageCanvas.drawBitmap(getBitmap(items.get(items.size() - 3), MAX_DIMENSION_FOR_BITMAP_TAPE), marginGap, (marginGap * 3)
						+ (positionMarker * 2), null);
				combinedImageCanvas.drawBitmap(getBitmap(items.get(items.size() - 4), MAX_DIMENSION_FOR_BITMAP_TAPE), marginGap, (marginGap * 4)
						+ (positionMarker * 3), null);

				break;
			}
			}

			combinedImageCanvas.save();

			combinedImageCanvas = null;

			onComposeProcess(0.7f);

			break;
		}

		}

		onComposeProcess(0.8f);

		// items.clear();
		// items = null;

		Matrix m = new Matrix();
		m.preScale(1, -1);
		Bitmap flippedImage = Bitmap.createBitmap(combinedImage, 0, 0, combinedWidth, combinedHeight, m, false);
		combinedImage = null;

		onComposeProcess(0.9f);

		if (Settings.getInstance().isWatermarkEnabled()) {
			flippedImage = WatermarkProvider.applyWatermark(mainActivity, flippedImage, DICECAM_ORIENTATION_NONE,
					WatermarkProvider.DICECAM_WATERMARK_FLAG_COLLAGE, false);
		}

		onComposeProcess(1.f);
		composeProcessCallback = null;

		return flippedImage;

	}

	public Bitmap composeQuads(List<?> items) {

		onComposeProcess(0.f);

		Bitmap firstTakeBitmap = getBitmap(items.get(0), MAX_DIMENSION_FOR_BITMAP_QUAD);

		int pictureWidth = firstTakeBitmap.getWidth();
		int pictureHeight = firstTakeBitmap.getHeight();

		int marginGap = 0;

		if (Settings.getInstance().getShouldFramePhoto()) {
			marginGap = marginGapCalculator(pictureWidth, pictureHeight);
		}

		int combinedWidth = (pictureWidth * 2) + (marginGap * 3);
		int combinedHeight = (pictureHeight * 2) + (marginGap * 3);

		onComposeProcess(0.2f);

		Bitmap combinedImage = Bitmap.createBitmap(combinedWidth, combinedHeight, Bitmap.Config.ARGB_8888);

		onComposeProcess(0.4f);

		Canvas combinedImageCanvas = new Canvas(combinedImage);

		if (Settings.getInstance().getFrameColor() == Settings.FRAME_COLOR_WHITE) {
			combinedImageCanvas.drawColor(Color.WHITE);
		}

		combinedImageCanvas.drawBitmap(getBitmap(items.get(2), MAX_DIMENSION_FOR_BITMAP_QUAD), marginGap, marginGap, null);

		combinedImageCanvas.drawBitmap(getBitmap(items.get(3), MAX_DIMENSION_FOR_BITMAP_QUAD), (marginGap * 2) + pictureWidth, marginGap, null);

		onComposeProcess(0.6f);

		combinedImageCanvas.drawBitmap(firstTakeBitmap, marginGap, (marginGap * 2) + pictureHeight, null);
		firstTakeBitmap = null;
		combinedImageCanvas.drawBitmap(getBitmap(items.get(1), MAX_DIMENSION_FOR_BITMAP_QUAD), (marginGap * 2) + pictureWidth, (marginGap * 2)
				+ pictureHeight, null);

		onComposeProcess(0.8f);

		// items.clear();
		// items = null;

		combinedImageCanvas = null;

		Matrix m = new Matrix();
		m.preScale(1, -1);
		Bitmap flippedImage = Bitmap.createBitmap(combinedImage, 0, 0, combinedWidth, combinedHeight, m, false);
		combinedImage = null;

		onComposeProcess(0.9f);

		if (Settings.getInstance().isWatermarkEnabled()) {
			flippedImage = WatermarkProvider.applyWatermark(mainActivity, flippedImage, DICECAM_ORIENTATION_NONE,
					WatermarkProvider.DICECAM_WATERMARK_FLAG_COLLAGE, false);
		}

		onComposeProcess(1.f);
		composeProcessCallback = null;

		return flippedImage;
	}

	/**
	 * 
	 * @param items
	 *            can be a list of Bitmap or Uri
	 * @return
	 */
	public Bitmap composeNonuple(List<?> items) {

		onComposeProcess(0.f);

		Bitmap firstTakeBitmap = getBitmap(items.get(0), MAX_DIMENSION_FOR_BITMAP_NONUPLE);

		int pictureWidth = firstTakeBitmap.getWidth();
		int pictureHeight = firstTakeBitmap.getHeight();

		int marginGap = 0;

		if (Settings.getInstance().getShouldFramePhoto()) {
			marginGap = marginGapCalculator(pictureWidth, pictureHeight);
		}

		int combinedWidth = (pictureWidth * 3) + (marginGap * 4);
		int combinedHeight = (pictureHeight * 3) + (marginGap * 4);

		onComposeProcess(0.1f);

		Bitmap combinedImage = null;
		
//		try {
		combinedImage = Bitmap.createBitmap(combinedWidth, combinedHeight, Bitmap.Config.ARGB_8888);
//		} catch(OutOfMemoryError e) {
//			e.printStackTrace();
//			combinedImage = Bitmap.createBitmap(combinedWidth/2, combinedHeight/2, Bitmap.Config.ARGB_8888);
//		}
		
		Canvas combinedImageCanvas = null;
		if(combinedImage != null) combinedImageCanvas = new Canvas(combinedImage);
		else {
			((DiceCamera)DiceCamera.mContext).onResume();
		}

		if (Settings.getInstance().getFrameColor() == Settings.FRAME_COLOR_WHITE) {
			combinedImageCanvas.drawColor(Color.WHITE);
		}

		onComposeProcess(0.2f);
		firstTakeBitmap = null;

		combinedImageCanvas.drawBitmap(getBitmap(items.get(6), MAX_DIMENSION_FOR_BITMAP_NONUPLE), marginGap, marginGap, null);
		combinedImageCanvas.drawBitmap(getBitmap(items.get(3), MAX_DIMENSION_FOR_BITMAP_NONUPLE), marginGap, (marginGap * 2) + pictureHeight, null);
		combinedImageCanvas.drawBitmap(getBitmap(items.get(0), MAX_DIMENSION_FOR_BITMAP_NONUPLE), marginGap, (marginGap * 3) + (pictureHeight * 2),
				null);
		onComposeProcess(0.4f);

		combinedImageCanvas.drawBitmap(getBitmap(items.get(7), MAX_DIMENSION_FOR_BITMAP_NONUPLE), (marginGap * 2) + pictureWidth, marginGap, null);
		combinedImageCanvas.drawBitmap(getBitmap(items.get(4), MAX_DIMENSION_FOR_BITMAP_NONUPLE), (marginGap * 2) + pictureWidth, (marginGap * 2)
				+ pictureHeight, null);
		combinedImageCanvas.drawBitmap(getBitmap(items.get(1), MAX_DIMENSION_FOR_BITMAP_NONUPLE), (marginGap * 2) + pictureWidth, (marginGap * 3)
				+ (pictureHeight * 2), null);
		onComposeProcess(0.6f);

		combinedImageCanvas.drawBitmap(getBitmap(items.get(8), MAX_DIMENSION_FOR_BITMAP_NONUPLE), (marginGap * 3) + (pictureWidth * 2), marginGap,
				null);
		combinedImageCanvas.drawBitmap(getBitmap(items.get(5), MAX_DIMENSION_FOR_BITMAP_NONUPLE), (marginGap * 3) + (pictureWidth * 2),
				(marginGap * 2) + pictureHeight, null);
		combinedImageCanvas.drawBitmap(getBitmap(items.get(2), MAX_DIMENSION_FOR_BITMAP_NONUPLE), (marginGap * 3) + (pictureWidth * 2),
				(marginGap * 3) + (pictureHeight * 2), null);
		onComposeProcess(0.8f);

		// items.clear();
		// items = null;

		combinedImageCanvas = null;

		Matrix m = new Matrix();
		m.preScale(1, -1);
		//OutofMemoryErrorPoint
		Bitmap flippedImage = null;
		try {
		flippedImage = Bitmap.createBitmap(combinedImage, 0, 0, combinedWidth, combinedHeight, m, false);
		} catch(OutOfMemoryError e) {
			e.printStackTrace();
			//HJH_System.gc();
			//Out of Memory Point(hjh)
			flippedImage = Bitmap.createBitmap(combinedImage, 0, 0, combinedWidth, combinedHeight, m, false);
		}
		combinedImage = null;

		onComposeProcess(0.9f);

		if (Settings.getInstance().isWatermarkEnabled()) {
			flippedImage = WatermarkProvider.applyWatermark(mainActivity, flippedImage, DICECAM_ORIENTATION_NONE,
					WatermarkProvider.DICECAM_WATERMARK_FLAG_COLLAGE, false);
		}

		onComposeProcess(1.f);
		composeProcessCallback = null;

		return flippedImage;
	}

	private static int MAX_DIMENSION_FOR_BITMAP_TAPE = 1024;
	private static int MAX_DIMENSION_FOR_BITMAP_QUAD = 1024;
	private static int MAX_DIMENSION_FOR_BITMAP_NONUPLE = 768;

	public static int getMaxDimensionForCollageIndividualPicture(final int collageStatus) {
		switch (collageStatus) {
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_DOUBLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_DOUBLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_DOUBLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_DOUBLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_HORIZONTAL:

		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_TRIPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_TRIPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_TRIPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_TRIPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_HORIZONTAL:

		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_VERTICAL:
			return MAX_DIMENSION_FOR_BITMAP_TAPE;

		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_SQUARE:
			return MAX_DIMENSION_FOR_BITMAP_QUAD;

		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_NONUPLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_NONUPLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_NONUPLE:
			return MAX_DIMENSION_FOR_BITMAP_NONUPLE;

		default:
			break;
		}
		return 0;
	}

	private Bitmap getBitmap(Object item, int maxDimension) {
		if (item instanceof Bitmap)
			return (Bitmap) item;

		Uri uriItem = null;
		if (item instanceof String) {
			uriItem = Uri.parse((String) item);
		}
		if (item instanceof Uri) {
			uriItem = (Uri) item;
		}

		if (uriItem != null) {
			try {
				Bitmap b = MediaStore.Images.Media.getBitmap(mainActivity.getContentResolver(), uriItem);
				int angle = 0;
				boolean flipHorizontally = true;
				switch (orientation) {
				case ImageComposition.DICECAM_ORIENTATION_PORTRAIT:
					angle = -90;
					break;
				case ImageComposition.DICECAM_ORIENTATION_PORTRAIT_UPSIDE_DOWN:
					angle = 90;
					break;
				case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_LEFT:
					angle = 0;
					break;
				case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_RIGHT:
					angle = 180;
					break;
				default:
					break;
				}
				// int o =
				// FileManagement.getExifOrientationForBitmapData(orientation,
				// flipHorizontal);
				return FileManagement.scaleAndRotate(b, maxDimension, angle, flipHorizontally);
				// return FileManagement.rotateBitmap(b, o);
				// return
				// MediaStore.Images.Media.getBitmap(mainActivity.getContentResolver(),
				// (Uri)item);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Log.d("dicecam", "ERROR - cannot getBitmap: " + item);
		return null;
	}

	private void onComposeProcess(float process) {
		if (composeProcessCallback != null) {
			composeProcessCallback.onProcess(process);
		}
	}

	private void onSinglePictureProcess(float process) {
		if (singlePictureProcessCallback != null) {
			singlePictureProcessCallback.onProcess(process);
		}
	}

	public static int marginGapCalculator(int imageWidth, int imageHeight) {

		int imageLength = (imageWidth > imageHeight ? imageWidth : imageHeight);

		int gap = (int) (imageLength * (Settings.getInstance().getFrameWidth() == Settings.FRAME_WIDTH_LIGHT ? 0.01 : 0.02));

		if (gap < MINIMUM_FRAME_MARGIN_GAP)
			return MINIMUM_FRAME_MARGIN_GAP;
		else
			return gap;
	}

	public static Bitmap generateFramedBitmap(Bitmap bitmap, int outputWidth, int outputHeight) {
		int marginGap = ImageComposition.marginGapCalculator(outputWidth, outputHeight);
		Bitmap framedImage = Bitmap.createBitmap(outputWidth + (marginGap * 2), outputHeight + (marginGap * 2), Bitmap.Config.ARGB_8888);
		Canvas framedImageCanvas = new Canvas(framedImage);
		if (Settings.getInstance().getFrameColor() == Settings.FRAME_COLOR_WHITE) {
			framedImageCanvas.drawColor(Color.WHITE);
		}
		framedImageCanvas.drawBitmap(bitmap, marginGap, marginGap, null);
		return framedImage;
	}

	public static String orientationString(int orientation) {
		switch (orientation) {
		case DICECAM_ORIENTATION_PORTRAIT:
			return "Portrait";
		case DICECAM_ORIENTATION_LANDSCAPE_LEFT:
			return "Landscape(Left)";
		case DICECAM_ORIENTATION_PORTRAIT_UPSIDE_DOWN:
			return "Portrait(Upside-down)";
		case DICECAM_ORIENTATION_LANDSCAPE_RIGHT:
			return "Landscape(Right)";
		case DICECAM_ORIENTATION_NONE:
			return "None";
		default:
			return "Unknown";
		}
	}

	public static String displayRotationString(int rotation) {
		switch (rotation) {
		case Surface.ROTATION_0:
			return "No Rotation";
		case Surface.ROTATION_90:
			return "90 Rotated";
		case Surface.ROTATION_180:
			return "180 Rotated";
		case Surface.ROTATION_270:
			return "270 Rotated";
		default:
			return "Unknown";
		}
	}

	public static int displayRotationAngle(int rotation) {
		switch (rotation) {
		case Surface.ROTATION_0:
			return 0;
		case Surface.ROTATION_90:
			return 90;
		case Surface.ROTATION_180:
			return 180;
		case Surface.ROTATION_270:
			return 270;
		default:
			return 0;
		}
	}

	public Bitmap compose(final int collageStatus, final List<?> collageItems, ProcessCallback processCallback) {

		this.composeProcessCallback = processCallback;

		switch (collageStatus) {
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_NONUPLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_NONUPLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_NONUPLE:
			return composeNonuple(collageItems);

		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_SQUARE:
			return composeQuads(collageItems);

		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_VERTICAL:
			return composeMultipleTakeTape(collageStatus, collageItems, 4);

		default:
			return composeMultipleTakeTape(collageStatus, collageItems, collageItems.size());
		}
	}
}
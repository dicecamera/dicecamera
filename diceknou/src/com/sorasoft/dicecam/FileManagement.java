package com.sorasoft.dicecam;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.util.DisplayMetrics;
import android.util.Log;

import com.sorasoft.dicecam.setting.Settings;
import com.sorasoft.dicecam.util.FileComparator;
import com.sorasoft.dicecam.util.UserInterfaceUtil;

public class FileManagement {

	private Activity activityContext;
	private SQLiteOpenHelper dbHelper;
	private int mThumbnailWidthInPx;
	
	private DiceCamera mainActivity;
	
	private static String cachedRemovableExternalStoragePath;
	private static File fallbackInternalDirectory;
	
	public static final int THUMBNAIL_HEIGHT_IN_DP__NORMAL = 100;
	public static final int THUMBNAIL_HEIGHT_IN_DP__LARGE = 150;
	public static final int THUMBNAIL_HEIGHT_IN_DP__XLARGE = 200;
	
	public static final int MINIMUM_THUMBNAIL_HEIGHT_IN_PX = 50;
	
	private boolean isTablet = false;
    
	public static int getThumbnailWidthInDP() {
		switch (DiceCamera.screenLayoutSize) {
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			return THUMBNAIL_HEIGHT_IN_DP__XLARGE;
		case Configuration.SCREENLAYOUT_SIZE_LARGE:
			return THUMBNAIL_HEIGHT_IN_DP__LARGE;
		default:
			return THUMBNAIL_HEIGHT_IN_DP__NORMAL;
		}
	}

	public FileManagement(Activity context) {
		activityContext = context;
		
		isTablet = activityContext.getResources().getBoolean(R.bool.isTablet);
		
		dbHelper = new SQLiteOpenHelper(context, "DICECAM_THUMBNAIL.sqlite", null, 1) {

			@Override
			public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			}

			@Override
			public void onCreate(SQLiteDatabase db) {
				try {
					db.execSQL("CREATE TABLE THUMBNAIL (uri VARCHAR(512) NOT NULL, thumbnail_image BLOB);");
					db.execSQL("CREATE UNIQUE INDEX `id_uri` ON `THUMBNAIL` (`uri` ASC);");
				} catch (Exception e) {
					Log.d("DiceCamera", "DB " + e.getMessage());
				}
			}
		};

		DisplayMetrics displaymetrics = new DisplayMetrics();
		activityContext.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

		mThumbnailWidthInPx = UserInterfaceUtil.dp2px(getThumbnailWidthInDP(), displaymetrics.density);
		if (mThumbnailWidthInPx < MINIMUM_THUMBNAIL_HEIGHT_IN_PX) mThumbnailWidthInPx = MINIMUM_THUMBNAIL_HEIGHT_IN_PX;

		if ( BuildConfig.DEBUG ) {
			Log.d("thumb", "mThumbnailWidthInPx: " + mThumbnailWidthInPx + " (" + displaymetrics.widthPixels + ", " + displaymetrics.heightPixels + ")");
		}

		mainActivity = null;
	}
	
	public static void printTemporaryFiles(Context context) {
		File outputDir = context.getCacheDir(); // context being the Activity pointer
		File[] files = outputDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			Log.d("tmp", "tmp: " + file.getPath());
		}
	}
	
	public static void clearTemporaryFiles(Context context) {
		File outputDir = context.getCacheDir(); // context being the Activity pointer
		File[] files = outputDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			boolean result = file.delete();
			Log.d("tmp", "remove.tmp: (" + result + ") " + file.getPath());
		}
	}
	
	public static void clearTemoporaryJPEGFiles(Context context) {
		File outputDir = context.getCacheDir(); // context being the Activity pointer
		FilenameFilter f = new FilenameFilter() {
			
			@SuppressLint("DefaultLocale")
			@Override
			public boolean accept(File dir, String filename) {
				if ( filename != null && filename.toLowerCase().endsWith(".jpg") ) return true;
				return false;
			}
		};
		File[] files = outputDir.listFiles(f);
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			boolean result = file.delete();
			Log.d("tmp", "remove.tmp.jpg: (" + result + ") " + file.getPath());
		}
	}
	
	public File getTemporaryJPEGFile(Context context) {

		if (((DiceCamera) DiceCamera.mContext).mImage_capture_intent_uri != null) return new File(((DiceCamera)DiceCamera.mContext).mImage_capture_intent_uri.toString());

		File outputDir = context.getCacheDir(); // context being the Activity
												// pointer
		try {
			return File.createTempFile(generateRandomImageFilename(), ".jpg", outputDir);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressLint("NewApi")
	private static String removableExternalStoragePath(Context context) {
		
		try {
			if (cachedRemovableExternalStoragePath != null) {
				return cachedRemovableExternalStoragePath;
			}
			
			if (Build.VERSION.SDK_INT < 19) {
				
				if (Build.VERSION.SDK_INT < 9) {
					return "";
				}
				
				if (Environment.isExternalStorageRemovable()) {
					return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
				} else {
					
					List<String> devices = new ArrayList<String>(20);
					List<String> vold = new ArrayList<String>(20);

					try {
						File mountFile = new File("/proc/mounts");

						if (mountFile.exists()) {

							Scanner scanner = new Scanner(mountFile);

							while (scanner.hasNext()) {
								String line = scanner.nextLine();

								if (line.startsWith("/dev/block/vold/")) {
									String[] lineElements = line.split(" ");
									String element = lineElements[1];

									if (element.equals(Environment.getExternalStorageDirectory().getAbsolutePath()) == false) {
										devices.add(element);
									}
								}
							}

							scanner.close();
						}
					} catch (Exception e) {
						
						if (BuildConfig.DEBUG) {
							e.printStackTrace();
						}
					}

					try {
						File voldFile = new File("/system/etc/vold.fstab");

						if (voldFile.exists()) {
							Scanner scanner = new Scanner(voldFile);

							while (scanner.hasNext()) {
								String line = scanner.nextLine();
								if (line.startsWith("dev_mount")) {
									String[] lineElements = line.split(" ");
									String element = lineElements[2];

									if (element.contains(":")) {
										element = element.substring(0, element.indexOf(":"));
									}

									if (!element.equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
										vold.add(element);
									}
								}
							}

							scanner.close();
						}
					} catch (Exception e) {
						
						if (BuildConfig.DEBUG) {
							e.printStackTrace();
						}
					}

					for (int i = 0; i < devices.size(); i++) {

						String mount = devices.get(i);

						if (!vold.contains(mount)) {
							devices.remove(i--);
						}
					}

					vold.clear();

					if (devices.size() > 0) {

						cachedRemovableExternalStoragePath = devices.get(0);
						
						return devices.get(0);
						
					} else {
						
						return "";
					}
				}
			}
			
			else {
				
				if (Environment.isExternalStorageRemovable()) {
					
					return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
					
				} else {
					
					 if (context.getExternalFilesDirs(Environment.DIRECTORY_PICTURES).length > 1) {
							return context.getExternalFilesDirs(Environment.DIRECTORY_PICTURES)[1].getAbsolutePath();
					 }
					 
					 else {
						 return ""; 
					 }
				} 
			}

		} catch (Exception e) {
			return "";
		}
	}
	
	public static boolean hasRemovableExternalStorage(Context context) {
		
		return removableExternalStoragePath(context).length() > 0;

	}
	
	private static File mediaStorageDir(Context context) {
		
		if (fallbackInternalDirectory != null) {
			return fallbackInternalDirectory;
		}
		
		String externalStorageState = Environment.getExternalStorageState();
		
		if (externalStorageState.equals(Environment.MEDIA_MOUNTED) && externalStorageState.equals(Environment.MEDIA_MOUNTED_READ_ONLY) == false) {			
			try {
				if (hasRemovableExternalStorage(context)) {
					
					if (removableExternalStoragePath(context).equals(Environment.getExternalStorageDirectory()) == false) {
						
						// Both an emulated external storage and removable storage exist on the device, but the removable storage is not the primary external storage of the device.
						// Return the primary external storage path, if the user opted to save photos to non-removable storage.
						
						return (Settings.getInstance().getSaveToRemovableStorage() ? new File(new File(removableExternalStoragePath(context)), "DiceCamera") : new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DiceCamera"));
					
					} else {
						
						// Removable storage is the primary external storage present on the device.
						// Return the internal storage path, if the user opted to save photos to non-removable storage.
						return (Settings.getInstance().getSaveToRemovableStorage() ? new File(new File(removableExternalStoragePath(context)), "DiceCamera") : new File(context.getFilesDir(), "DiceCamera"));
					}
				} else {
					
					// Some sort of external storage exists, though no removable storage was found on the device.
					
					return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DiceCamera");
				}
			
			} catch (Exception e) {
				return new File(context.getFilesDir(), "DiceCamera");
			}
		} else {
			
			// Even an emulated external storage is not present on the device.
			// Return internal storage path. 
			return new File(context.getFilesDir(), "DiceCamera");
		}

	}
	
	public File getOutputMediaFile() {
		
		File mediaStorageDir = null;
		
		try {
			mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DiceCamera");
			
		} catch (Exception e) {
			
			mediaStorageDir = new File(mainActivity.getFilesDir(), "DiceCamera");
		}
		
		if (mediaStorageDir != null && !mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				
				if ( BuildConfig.DEBUG ) {
					Log.d("DiceCamera", "Cannot execute mkdirs(): " + mediaStorageDir.getAbsolutePath());
				}
				
				if (!mediaStorageDir.mkdir()) {
					if ( BuildConfig.DEBUG ) {
						Log.d("DiceCamera", "Cannot execute mkdir(): " + mediaStorageDir.getAbsolutePath());
					}
					
					File fallbackInternalDirectoryTemporary = new File(mainActivity.getFilesDir(), "DiceCamera");
					
					if (!fallbackInternalDirectoryTemporary.exists()) {
						if (!fallbackInternalDirectoryTemporary.mkdirs()) {
							return null;
						}
					}
					
					mediaStorageDir = fallbackInternalDirectoryTemporary;
					fallbackInternalDirectory = fallbackInternalDirectoryTemporary;
				}
			}
		}

		String path = mediaStorageDir.getPath() + File.separator + generateRandomImageFilename() + ".jpg";

		return new File(path);
	}
	
	private String generateRandomImageFilename() {
		String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		return "IMG_" + ts;
	}

	public Uri addImageToGallery(String filepath, String title, String description) {
		Log.d("DiceCamera", "addImageToGallery: " + filepath);
		ContentValues values = new ContentValues();
		values.put(Media.TITLE, title);
		values.put(Media.DESCRIPTION, description); 
		values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
		values.put(Images.Media.MIME_TYPE, "image/jpeg");
		values.put(MediaStore.MediaColumns.DATA, filepath);

		return activityContext.getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
	}

	//	public Uri saveBitmap(Bitmap bitmap, final int orientation, boolean shouldStoreEXIF) {
	//		return saveBitmap(bitmap, orientation, shouldStoreEXIF, false, false);
	//	}

	public static File[] getMediaFiles(Context context) {
		
		return mediaStorageDir(context).listFiles();
	}

	public static File[] getMediaFiles(boolean shouldSort, Context context) {

//2.1.3		File mediaStorageDir = mediaStorageDir(context);
	File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DiceCamera");

		if (shouldSort) {
			File[] files = mediaStorageDir.listFiles();

			ArrayList<File> directoryListing = new ArrayList<File>();
			directoryListing.addAll(Arrays.asList(files));
			Collections.sort(directoryListing, new FileComparator());

			File[] sortedFiles = directoryListing.toArray(new File[files.length]);
			return sortedFiles;
		}

		else {
			return mediaStorageDir.listFiles();
		}
		
	}

	public void setMainActivity(DiceCamera mActivity) {
		mainActivity = mActivity;
	}

	private static String latitudeRef(double latitude) {
		return latitude < 0.0d ? "S" : "N";
	}

	private static String longitudeRef(double longitude) {
		return longitude < 0.0d ? "W" : "E";
	}

	synchronized public static final String convertCoordinate(double coordinate) {

		coordinate = Math.abs(coordinate);
		int degree = (int) coordinate;
		coordinate *= 60;
		coordinate -= (degree * 60.0d);
		int minute = (int) coordinate;
		coordinate *= 60;
		coordinate -= (minute * 60.0d);
		int second = (int) (coordinate * 1000.0d);

		StringBuilder sb = new StringBuilder(20);
		sb.setLength(0);
		sb.append(degree);
		sb.append("/1,");
		sb.append(minute);
		sb.append("/1,");
		sb.append(second);
		sb.append("/1000,");

		return sb.toString();
	}

	/**
	 * 
	 * @param bitmap
	 * @param orientation DiceCamera Orientation (e.g. ImageComposition.DICECAM_ORIENTATION_PORTRAIT)
	 * @param shouldStoreEXIF
	 * @param takenByCamera
	 * @param flipHorizontal
	 * @param location
	 * @return
	 */
	public Uri saveBitmap(Bitmap bitmap, final int orientation, 
			boolean shouldStoreEXIF, boolean takenByCamera, boolean flipHorizontal, Location location) {
		return saveBitmap(bitmap, orientation, shouldStoreEXIF, takenByCamera, flipHorizontal, location, null);
	}
	public Uri saveBitmap(Bitmap bitmap, final int orientation, 
			boolean shouldStoreEXIF, boolean takenByCamera, boolean flipHorizontal, Location location, ExifInterface locationExifInterface) {
		return saveBitmap(getOutputMediaFile(), bitmap, orientation, shouldStoreEXIF, takenByCamera, flipHorizontal, location, locationExifInterface);
	}
	public Uri saveBitmap(File targetFile, Bitmap bitmap, final int orientation, 
			boolean shouldStoreEXIF, boolean takenByCamera, boolean flipHorizontal, Location location, ExifInterface locationExifInterface) {
		return saveBitmap(targetFile, bitmap, orientation, shouldStoreEXIF, takenByCamera, flipHorizontal, location, locationExifInterface, true);
	}
	public Uri saveBitmap(File targetFile, Bitmap bitmap, final int orientation, 
			boolean shouldStoreEXIF, boolean takenByCamera, boolean flipHorizontal, Location location, ExifInterface locationExifInterface,
			boolean broadcastScanFile) {

		if(((DiceCamera)DiceCamera.mContext).mImage_capture_intent_uri != null ) 	targetFile = new File(((DiceCamera)DiceCamera.mContext).mImage_capture_intent_uri.getPath()); 
		
		if (targetFile == null) return null;

		int exifOrientationValue = getExifOrientation(takenByCamera, orientation, flipHorizontal);

		boolean exifAllowed = exifOrientationIsAllowable(exifOrientationValue);
		Log.d("DiceCamera", "exifAllowed: " + exifAllowed);

		if ( exifAllowed == false ) {
			bitmap = rotateBitmap(bitmap, exifOrientationValue);
			exifOrientationValue = ExifInterface.ORIENTATION_NORMAL;
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(targetFile);

			// store bitmap
			bitmap.compress(Bitmap.CompressFormat.JPEG, 98, fos);

			try {
				if ( fos != null ) {
					fos.flush();
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// clear bitmap
			bitmap.recycle(); bitmap = null;
		} catch (Exception e) {
			e.printStackTrace();
			try { if ( bitmap != null ) { bitmap.recycle(); bitmap = null; } } catch (Exception e2) { bitmap = null; } // clear bitmap
			return null;
		}

		try {
			if (shouldStoreEXIF == true) {
				Log.d("DiceCamera", "pictureFile.getAbsolutePath(): " + targetFile.getAbsolutePath());

				// set exif orientation
				ExifInterface exifInterface = new ExifInterface(targetFile.getAbsolutePath());
				writeDefaultMetadata(exifInterface);
				exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "" + exifOrientationValue);

				if ( locationExifInterface != null ) {
					if ( BuildConfig.DEBUG ) {
						Log.d("location", "location-1: " + locationExifInterface);
						Log.d("location", "lll: " + locationExifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
					}
					try {
						if ( locationExifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE) != null ) {
							exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, locationExifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
						}
						if ( locationExifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF) != null ) {
							exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, locationExifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF));
						}
						if ( locationExifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) != null ) {
							exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, locationExifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
						}
						if ( locationExifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF) != null ) {
							exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, locationExifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if ( location != null ) {
					if ( BuildConfig.DEBUG ) {
						Log.d("location", "location1: " + location);
					}
					exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, convertCoordinate(location.getLatitude()));
					exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latitudeRef(location.getLatitude()));
					exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, convertCoordinate(location.getLongitude()));
					exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, longitudeRef(location.getLongitude()));
				}
				
				try {
					exifInterface.saveAttributes();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if ( locationExifInterface != null ) {
				if ( BuildConfig.DEBUG ) {
					Log.d("location", "location-2: " + location);
				}
				ExifInterface exifInterface = new ExifInterface(targetFile.getAbsolutePath());
				writeDefaultMetadata(exifInterface);

				try {
					if ( locationExifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE) != null ) {
						exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, locationExifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
					}
					if ( locationExifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF) != null ) {
						exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, locationExifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF));
					}
					if ( locationExifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) != null ) {
						exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, locationExifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
					}
					if ( locationExifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF) != null ) {
						exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, locationExifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					exifInterface.saveAttributes();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if ( location != null ) {
				if ( BuildConfig.DEBUG ) {
					Log.d("location", "location2: " + location);
				}
				ExifInterface exifInterface = new ExifInterface(targetFile.getAbsolutePath());
				writeDefaultMetadata(exifInterface);

				exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, convertCoordinate(location.getLatitude()));
				exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latitudeRef(location.getLatitude()));
				exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, convertCoordinate(location.getLongitude()));
				exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, longitudeRef(location.getLongitude()));

				exifInterface.saveAttributes();
			} else {
				ExifInterface exifInterface = new ExifInterface(targetFile.getAbsolutePath());
				writeDefaultMetadata(exifInterface);
				exifInterface.saveAttributes();
			}

			Uri uri = Uri.fromFile(targetFile);
			
			if ( broadcastScanFile ) {
								
				activityContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
				
				if (targetFile.exists() == true && targetFile.length() > 0) {
					createThumnailForFile(targetFile);
				}
			}

			return uri;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	private static void writeDefaultMetadata(ExifInterface exifInterface) {
		exifInterface.setAttribute(ExifInterface.TAG_MAKE, PublishInfo.getExifMake());
		exifInterface.setAttribute(ExifInterface.TAG_MODEL, PublishInfo.getExifModel());
		try {
			exifInterface.setAttribute("Software", PublishInfo.getExifSoftware());
		} catch (Exception e) {
			Log.d("exif", "Cannot set 'Software' attribute : " + e.getMessage());
		}
	}

	public Uri saveBitmapData(final byte[] data, final int orientation, boolean isFrontCamera, boolean flipHorizontal, Location location) {
		File outputFile = getOutputMediaFile();
		if ( outputFile == null ) return null;
		return saveBitmapData(outputFile, data, orientation, isFrontCamera, flipHorizontal, location);
	}
	
	public static Uri saveBitmapData(final File outputFile, final byte[] data, final int orientation, boolean isFrontCamera, boolean flipHorizontal, Location location) {
		return saveBitmapData(outputFile, data, getExifOrientationForBitmapData(orientation, isFrontCamera, flipHorizontal), location);
	}
	
	public static Uri saveBitmapData(final File outputFile, final byte[] data, final int exifOrientationValue, final Location location) {
		// write data
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outputFile);

			// store bitmap
			fos.write(data);

			try {
				if ( fos != null ) {
					fos.flush();
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		// write exif
		try {
			ExifInterface exifInterface = new ExifInterface(outputFile.getAbsolutePath());
			writeDefaultMetadata(exifInterface);
			exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "" + exifOrientationValue);

			if ( location != null ) {
				exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, convertCoordinate(location.getLatitude()));
				exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latitudeRef(location.getLatitude()));
				exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, convertCoordinate(location.getLongitude()));
				exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, longitudeRef(location.getLongitude()));
			}

			exifInterface.saveAttributes();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return Uri.fromFile(outputFile);
	}



	public static int getExifOrientationForBitmapData(int orientation, boolean isFrontCamera, boolean flipHorizontal) {
		if ( isFrontCamera ) {
			switch (orientation) {
			case ImageComposition.DICECAM_ORIENTATION_PORTRAIT:
				return flipHorizontal ? ExifInterface.ORIENTATION_TRANSVERSE : ExifInterface.ORIENTATION_ROTATE_270; // 5 : 8

			case ImageComposition.DICECAM_ORIENTATION_PORTRAIT_UPSIDE_DOWN:
				return flipHorizontal ? ExifInterface.ORIENTATION_TRANSPOSE : ExifInterface.ORIENTATION_ROTATE_90; // 7 : 6

			case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_LEFT:
				return flipHorizontal ? ExifInterface.ORIENTATION_FLIP_HORIZONTAL : ExifInterface.ORIENTATION_NORMAL; // 2 : 1

			case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_RIGHT:
				return flipHorizontal ? ExifInterface.ORIENTATION_FLIP_VERTICAL : ExifInterface.ORIENTATION_ROTATE_180; // 4 : 3

			default:
				break;
			}
		} else {
			switch (orientation) {
			case ImageComposition.DICECAM_ORIENTATION_PORTRAIT:
				return ExifInterface.ORIENTATION_ROTATE_90; // 6

			case ImageComposition.DICECAM_ORIENTATION_PORTRAIT_UPSIDE_DOWN:
				return ExifInterface.ORIENTATION_ROTATE_270; // 8

			case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_LEFT:
				return ExifInterface.ORIENTATION_NORMAL; // 1

			case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_RIGHT:
				return ExifInterface.ORIENTATION_ROTATE_180; // 3

			default:
				break;
			}
		}
		return ExifInterface.ORIENTATION_NORMAL;
	}
	
//	public static int getExifOrientationForBitmapData(int orientation, boolean flipHorizontal) {
//		switch (orientation) {
//		case ImageComposition.DICECAM_ORIENTATION_PORTRAIT:
//			return ExifInterface.ORIENTATION_ROTATE_90; // 6
//
//		case ImageComposition.DICECAM_ORIENTATION_PORTRAIT_UPSIDE_DOWN:
//			return ExifInterface.ORIENTATION_ROTATE_270; // 8
//
//		case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_LEFT:
//			return ExifInterface.ORIENTATION_NORMAL; // 1
//
//		case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_RIGHT:
//			return ExifInterface.ORIENTATION_ROTATE_180; // 3
//
//		default:
//			break;
//		}
////		switch (orientation) {
////		case ImageComposition.DICECAM_ORIENTATION_PORTRAIT:
////			return flipHorizontal ? ExifInterface.ORIENTATION_TRANSVERSE : ExifInterface.ORIENTATION_ROTATE_270; // 5 : 8
////
////		case ImageComposition.DICECAM_ORIENTATION_PORTRAIT_UPSIDE_DOWN:
////			return flipHorizontal ? ExifInterface.ORIENTATION_TRANSPOSE : ExifInterface.ORIENTATION_ROTATE_90; // 7 : 6
////
////		case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_LEFT:
////			return flipHorizontal ? ExifInterface.ORIENTATION_FLIP_HORIZONTAL : ExifInterface.ORIENTATION_NORMAL; // 2 : 1
////
////		case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_RIGHT:
////			return flipHorizontal ? ExifInterface.ORIENTATION_FLIP_VERTICAL : ExifInterface.ORIENTATION_ROTATE_180; // 4 : 3
////			
////		default:
////			break;
////		}
//		return ExifInterface.ORIENTATION_NORMAL;
//	}
	
	
	
	

	/**
	 *   1        2       3      4         5            6           7          8
	 *
	 * 888888  888888      88  88      8888888888  88                  88  8888888888
	 * 88          88      88  88      88  88      88  88          88  88      88  88
	 * 8888      8888    8888  8888    88          8888888888  8888888888          88
	 * 88          88      88  88
	 * 88          88  888888  888888
	 * 
	 * @see http://sylvana.net/jpegcrop/exif_orientation.html
	 * @return
	 */
	public int getExifOrientation(boolean isStillCamera, int orientation, boolean flipHorizontal) {
		if ( isStillCamera ) {
			switch (orientation) {
			case ImageComposition.DICECAM_ORIENTATION_PORTRAIT:
				return ExifInterface.ORIENTATION_ROTATE_90; // 6

			case ImageComposition.DICECAM_ORIENTATION_PORTRAIT_UPSIDE_DOWN:
				return ExifInterface.ORIENTATION_ROTATE_270; // 8

			case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_LEFT:
				return ExifInterface.ORIENTATION_ROTATE_180; // 3

			case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_RIGHT:
				return ExifInterface.ORIENTATION_NORMAL; // 1

			case ImageComposition.DICECAM_ORIENTATION_NONE:
				return ExifInterface.ORIENTATION_NORMAL; // 1

			default:
				break;
			}
		} else {
			switch (orientation) {
			case ImageComposition.DICECAM_ORIENTATION_PORTRAIT:
				if ( flipHorizontal ) {
					return ExifInterface.ORIENTATION_ROTATE_180; // 3
				} else {
					return ExifInterface.ORIENTATION_FLIP_VERTICAL; // 4 (-)
				}

			case ImageComposition.DICECAM_ORIENTATION_PORTRAIT_UPSIDE_DOWN:
				if ( flipHorizontal ) {
					return ExifInterface.ORIENTATION_NORMAL; // 1
				} else {
					return ExifInterface.ORIENTATION_FLIP_HORIZONTAL; // 2 (-)
				}

			case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_LEFT:
				if ( flipHorizontal ) {
					return ExifInterface.ORIENTATION_ROTATE_270; // 8
				} else {
					return ExifInterface.ORIENTATION_TRANSVERSE; // 7 (-)
				}

			case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_RIGHT:
				if ( flipHorizontal ) {
					return ExifInterface.ORIENTATION_ROTATE_90; // 6
				} else {
					return ExifInterface.ORIENTATION_TRANSPOSE; // 5 (-)
				}

			case ImageComposition.DICECAM_ORIENTATION_NONE:
				return ExifInterface.ORIENTATION_NORMAL; // 1

			default:
				break;
			}
		}

		return ExifInterface.ORIENTATION_UNDEFINED; // 0
	}

	public boolean exifOrientationIsAllowable(int exifOrientation) {
		switch (exifOrientation) {
		case ExifInterface.ORIENTATION_FLIP_VERTICAL: // 4 (-)
		case ExifInterface.ORIENTATION_FLIP_HORIZONTAL: // 2 (-)
		case ExifInterface.ORIENTATION_TRANSVERSE: // 7(-)
		case ExifInterface.ORIENTATION_TRANSPOSE: // 5 (-)
			return false;
		}
		return true;
	}

	/*
	 *   1        2       3      4         5            6           7          8
	 *
	 * 888888  888888      88  88      8888888888  88                  88  8888888888
	 * 88          88      88  88      88  88      88  88          88  88      88  88
	 * 8888      8888    8888  8888    88          8888888888  8888888888          88
	 * 88          88      88  88
	 * 88          88  888888  888888
	 * 
	 * @see http://sylvana.net/jpegcrop/exif_orientation.html
	 */
	public Bitmap rotatedBitmap(Bitmap bitmap, int exifOrientation) {
		if ( exifOrientation < 2 || exifOrientation > 8 ) return bitmap;

		float degrees = 0.f;
		boolean flipHorizontally = false;

		switch ( exifOrientation ) {
		case 1:
			degrees = 0.f;
			break;

		case 2:
			degrees = 0.f;
			flipHorizontally = true;
			break;

		case 3:
			degrees = 180.f;
			break;

		case 4:
			degrees = 180.f;
			flipHorizontally = true;
			break;

		case 5:
			degrees = 270.f;
			flipHorizontally = true;
			break;

		case 6:
			degrees = 90.f;
			break;

		case 7:
			degrees = 90.f;
			flipHorizontally = true;
			break;

		case 8:
			degrees = 270.f;
			break;
		}

		Log.d("thumb", "ori: " + exifOrientation + ", rotate: " + degrees + ", flipHorizon: " + flipHorizontally);

		Matrix matrix = new Matrix();
		if ( flipHorizontally ) {
			matrix.setScale(-1,1);
			matrix.postTranslate(bitmap.getWidth(),0);
		}
		matrix.postRotate(degrees);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

		try{
			Matrix matrix = new Matrix();
			switch (orientation) {
			case ExifInterface.ORIENTATION_NORMAL:
				return bitmap;
			case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
				matrix.setScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				matrix.setRotate(180);
				break;
			case ExifInterface.ORIENTATION_FLIP_VERTICAL:
				matrix.setRotate(180);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_TRANSPOSE:
				matrix.setRotate(90);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				matrix.setRotate(90);
				break;
			case ExifInterface.ORIENTATION_TRANSVERSE:
				matrix.setRotate(-90);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				matrix.setRotate(-90);
				break;
			default:
				return bitmap;
			}
			try {
				Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				bitmap.recycle();
				return bmRotated;
			}
			catch (OutOfMemoryError e) {
				e.printStackTrace();
				return bitmap;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return bitmap;
		}
	}
	
	
	
	public static Bitmap loadFromCamera(Context context, Uri photoUri) {
        Bitmap photo = null;
		try {
			photo = MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
        if (photo != null) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int maxDim = Math.max(metrics.widthPixels, metrics.heightPixels);
            int rotation = getRotationFor(context, photoUri);
            photo = scaleAndRotate(photo, maxDim, rotation);
            return photo;
        }
        return null;
    }
    
    public static int getRotationFor(Context context, Uri photoUri) {
        try {
            String[] orientationColumn = { MediaStore.Images.Media.ORIENTATION };
            Cursor cur = context.getContentResolver().query(photoUri, orientationColumn, null, null, null);
            if (cur != null && cur.moveToFirst()) {
                int orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                return orientation;
            }
        } catch (Exception e) {
            // couldn't parse
        }
        return 0;
    }

    public static Bitmap scaleAndRotate(Bitmap bitmap, int maxDim, int rotation) {
    	return scaleAndRotate(bitmap, maxDim, rotation, false);
    }
    
    public static Bitmap scaleAndRotate(Bitmap bitmap, int maxDim, int rotation, boolean flipHorizontally) {
        float scale = 1;
        if (bitmap.getWidth() > maxDim && bitmap.getWidth() > bitmap.getHeight())
            scale = maxDim * 1.0f / bitmap.getWidth();
        else if (bitmap.getHeight() > maxDim)
            scale = maxDim * 1.0f / bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        matrix.postRotate(rotation);
        matrix.postScale(-1, 1);

        Bitmap scaled = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return scaled;
    }


	public void createThumnailForFile(File originalFile) {

		String filename = originalFile.getPath();

		BitmapFactory.Options bfo = new BitmapFactory.Options();
		bfo.inJustDecodeBounds = false;
		bfo.inSampleSize = 4;
		Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(filename, bfo), mThumbnailWidthInPx, mThumbnailWidthInPx, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

		try {
			ExifInterface exif = new ExifInterface(filename);
			int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
			thumbnail = rotatedBitmap(thumbnail, exifOrientation);
		} catch (IOException e1) {
			e1.printStackTrace();
		}


		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		thumbnail.compress(Bitmap.CompressFormat.JPEG, 80, stream);

		try {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			String sqlInsert = "INSERT INTO THUMBNAIL (uri, thumbnail_image) VALUES (?, ?)";
			SQLiteStatement insertStatement = db.compileStatement(sqlInsert);
			insertStatement.clearBindings();
			insertStatement.bindString(1, Uri.fromFile(originalFile).toString());
			insertStatement.bindBlob(2, stream.toByteArray());
			if ( BuildConfig.DEBUG ) {
				Log.d("thumb", "created thumb: " + thumbnail.getWidth() + ", " + thumbnail.getHeight() + " - " + (stream.size()/1024) + "KB");
			}
			insertStatement.executeInsert();
			db.close();

		} catch (Exception e) {
			Log.d("DiceCamera", e.getMessage());
		} 

		thumbnail = null;

	}

	public boolean removeFileAt(final String path) {
		Log.d("DiceCamera", "removeFileAt: " + path);
		URI uriUpper = null;
		try {
			uriUpper = new URI(path);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}

		File fileToDelete = new File(uriUpper);
		if ( fileToDelete.exists() == false ) {
			Log.d("DiceCamera", "file not exists.");
			return false;
		}

		Uri fileUri = Uri.fromFile(fileToDelete);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete("THUMBNAIL", "uri= '" + fileUri.toString() + "'", null);
		db.close();

		boolean fileDeleted =  fileToDelete.delete();
		Log.d("DiceCamera", "fileDeleted: " + fileDeleted);

		fileToDelete = null;
		//HJH_System.gc();

		//		Uri uri = Uri.parse(path);
		//		int nod = activityContext.getContentResolver().delete(uri, null, null);
		//		Log.d("DiceCamera", "the number of removed rows : " + nod);
		//		File fileToDelete = new File(uri);
		//		boolean fileDeleted =  fileToDelete.delete();
		//		if ( fileDeleted == false ) return false;

		//		activityContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_REMOVED, Uri.parse(path)));
		activityContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(path)));
		//		activityContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" +  Environment.getExternalStorageDirectory())));
		return true;
	}
	
	public static boolean removeFileAt(final Activity context, final String path) {
		FileManagement fm = new FileManagement(context);
		return fm.removeFileAt(path);
	}
}

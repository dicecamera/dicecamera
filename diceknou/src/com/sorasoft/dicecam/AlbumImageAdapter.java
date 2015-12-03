package com.sorasoft.dicecam;

import java.io.File;
import java.util.HashMap;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.sorasoft.dicecam.util.UserInterfaceUtil;

public class AlbumImageAdapter extends BaseAdapter {

    private Activity mActivity;
    public File[] mediaFiles;
    private int length;
    
	private SQLiteOpenHelper dbHelper;
	private FileManagement mFileManagement;
	
	private Handler mHandler;
	private HashMap<ImageView, LazyLoader> mLoaderMap;
	
    public AlbumImageAdapter(Activity c) {
    	mActivity = c;

    	try {
    		mediaFiles = FileManagement.getMediaFiles(true, mActivity);
		} catch (Exception e) {
			e.printStackTrace();
			mediaFiles = null;
		}
        
		DisplayMetrics displaymetrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		
		length = displaymetrics.widthPixels / 3;
		
		if (length < 50) length = 50;
		
		dbHelper = new SQLiteOpenHelper(c, "DICECAM_THUMBNAIL.sqlite", null, 1) {
			
			@Override
			public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			}
			
			@Override
			public void onCreate(SQLiteDatabase db) {
				try {
					db.execSQL("CREATE TABLE THUMBNAIL (uri TEXT, thumbnail_image BLOB);");
				} catch (Exception e) {	
					Log.d("dicecam", "DB " + e.getMessage());
				}
			}
		};
		
		mFileManagement = new FileManagement(c);

		HandlerThread bgThread = new HandlerThread("mm");
		bgThread.start();
		mHandler = new Handler(bgThread.getLooper());
		
		mLoaderMap = new HashMap<ImageView, AlbumImageAdapter.LazyLoader>();
    }
    
    public void refreshData() {
    	try {
    		mediaFiles = FileManagement.getMediaFiles(true, mActivity);
    		Log.d("dicecam", "refreshData: " + mediaFiles.length + " files");
		} catch (Exception e) {
			e.printStackTrace();
			mediaFiles = null;
    		Log.d("dicecam", "refreshData: No files");
		}
    }
    
    private Uri getUri(int position) {
    	File file = getFile(position);
    	return file == null ? null : Uri.fromFile(file);
    }
    
    private File getFile(int position) {
		if ( mediaFiles == null || position >= mediaFiles.length ) return null;
		return mediaFiles[position];
    }

	@Override
	public int getCount() {
		return mediaFiles == null ? 0 : mediaFiles.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mActivity);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, UserInterfaceUtil.dp2px(getThumbnailViewHeightInDP(), imageView)));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            int paddingInPx = UserInterfaceUtil.dp2px(4, imageView);
            imageView.setPadding(0, paddingInPx, 0, paddingInPx);
        } else {
            imageView = (ImageView) convertView;
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, UserInterfaceUtil.dp2px(getThumbnailViewHeightInDP(), imageView)));
        }
		UserInterfaceUtil.setImageViewAlpha(imageView, 0, false);
		imageView.setImageResource(android.R.color.transparent);
        
		final File file = getFile(position);
		if ( file == null ) return imageView;
        final Uri uri = Uri.fromFile(file);
        
        mHandler.post(new LazyLoader(imageView, file, uri));
        
		return imageView;
	}

	private int getThumbnailViewHeightInDP() {
		return FileManagement.getThumbnailWidthInDP();
	}

	private class LazyLoader implements Runnable {
		protected ImageView mImageView;
		protected File mFile;
		protected Uri mUri;
		
		public LazyLoader(final ImageView imageView, final File file, final Uri uri) {
			mImageView = imageView;
			mFile = file;
			mUri = uri;
			
	        mLoaderMap.put(imageView, this);
		}

		@Override
		public void run() {
			LazyLoader loader = mLoaderMap.get(mImageView);
			if ( loader != this ) {
				return;
			}
			
			try {
				SQLiteDatabase db = dbHelper.getReadableDatabase();
				
		        String sqlQuery = "SELECT thumbnail_image FROM THUMBNAIL WHERE uri = '" + mUri.toString() + "'";
		        
		        Cursor cursor = db.rawQuery(sqlQuery, new String[] {});
		        byte[] jpegByteArray = null;
		        
		        if (cursor.moveToFirst()) {
		        	jpegByteArray = cursor.getBlob(0);
		        }
		        
		        if (cursor != null && !cursor.isClosed()) {
		            cursor.close();
		        }
		        
//		        db.close();
		        
		        Bitmap thumbnail = null;
		        
		        if (cursor.getCount() > 0 && jpegByteArray != null) {
		        	thumbnail = BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.length);
		        } else {
		        	mFileManagement.createThumnailForFile(mFile);
		        	
			        cursor = db.rawQuery(sqlQuery, new String[] {});
			        jpegByteArray = null;
			        
			        if (cursor.moveToFirst()) {
			        	jpegByteArray = cursor.getBlob(0);
			        }
			        
			        if (cursor != null && !cursor.isClosed()) {
			            cursor.close();
			        }
			        
//			        db.close();

			        if (cursor.getCount() > 0 && jpegByteArray != null) {
			        	thumbnail = BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.length);
			        }
		        }
		        
		        final LazyLoader mThis = this;
		        final Bitmap thumbnailPassed = thumbnail;
		        mActivity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						LazyLoader loader = mLoaderMap.get(mImageView);
						if ( loader != mThis ) {
							return;
						}
						
						mImageView.setImageBitmap(thumbnailPassed);
						UserInterfaceUtil.setImageViewAlpha(mImageView, 255);
						
						mLoaderMap.remove(mImageView);
					}
				});
		        //imageViewForThread.setImageBitmap(thumbnail);
		        
			} catch (Exception e) {
				mLoaderMap.remove(mImageView);
			}
		}

	}
}

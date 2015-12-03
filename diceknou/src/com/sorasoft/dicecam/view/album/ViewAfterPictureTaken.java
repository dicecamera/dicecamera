package com.sorasoft.dicecam.view.album;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sorasoft.dicecam.BuildConfig;
import com.sorasoft.dicecam.R;
import com.sorasoft.dicecam.BaseActivity;
import com.sorasoft.dicecam.BaseActivityWithoutActionBar;
import com.sorasoft.dicecam.DiceCamera;
import com.sorasoft.dicecam.FileManagement;
import com.sorasoft.dicecam.setting.Settings;
import com.sorasoft.dicecam.util.ControlUserInput;
import com.sorasoft.dicecam.util.UserInterfaceUtil;

public class ViewAfterPictureTaken extends BaseActivityWithoutActionBar implements OnTouchListener, UniversalImageLoader.LoadingFinishListner {

	Activity mActivity;

	String mURIString = null;
	String mDebugText = null;
	String mFrom = null;
	int mPosition = 0;

	UniversalImageLoader mImageLoader;
	ImageView mImageView;

	ImageButton mShareButton;
	ImageButton mInstagramButton;
	ImageButton mSaveButton;
	ImageButton mTrashButton;
	Button mRemoveAdButton;

	boolean mDisplayAd = false;
	boolean mAdAlreadyDisplayed = false;
	// DICE int mAdDisplayType = SPAd.DISPLAY_TYPE_ALL;

	private ViewPager mPager;
	private int mPagerMaxPages = 3;
	private File[] mMediaFiles;

	public static final String EXTRA_KEY_FROM = "From";
	public static final String EXTRA_KEY_DISPLAY_AD = "DisplayAd";
	public static final String EXTRA_KEY_AD_DISPLAY_TYPE = "AdDisplayType";
	public static final String EXTRA_KEY_URI = "URI";
	public static final String EXTRA_KEY_POSITION = "Position";
	public static final String EXTRA_KEY_DEBUG_TEXT = "DebugText";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActivity = this;

		mFrom = setupUI();

		// mWaterMarkSelector = (WatermarkSelector)
		// findViewById(R.id.watermark_selector);
		// mWaterMarkSelector.show();

	}

	private String setupUI() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_quickview);

		Intent intent = getIntent();
		String from = intent.getStringExtra(EXTRA_KEY_FROM);
		mDisplayAd = intent.getBooleanExtra(EXTRA_KEY_DISPLAY_AD, false);
		// DICE mAdDisplayType = intent.getIntExtra(EXTRA_KEY_AD_DISPLAY_TYPE,
		// SPAd.DISPLAY_TYPE_ALL);
		mURIString = intent.getStringExtra(EXTRA_KEY_URI);
		mDebugText = intent.getStringExtra(EXTRA_KEY_DEBUG_TEXT);
		mPosition = intent.getIntExtra(EXTRA_KEY_POSITION, -1);

		mPager = (ViewPager) findViewById(R.id.pager_image_view);
		if (from.equals(BaseActivity.E_QUICK_VIEW_AFTER_CAPTURE)) {
			mPagerMaxPages = 1;
		} else {
			mMediaFiles = FileManagement.getMediaFiles(true, mActivity);
		}

		mPager.setAdapter(new QuickViewPagerAdapter(getApplicationContext()));

		mPager.setCurrentItem(mPosition);

		mShareButton = (ImageButton) findViewById(R.id.share_button);
		mTrashButton = (ImageButton) findViewById(R.id.trash_button);
		mRemoveAdButton = (Button) findViewById(R.id.remove_ad_button);

		mShareButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doShare();
			}
		});

		mTrashButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doTrash();

			}
		});

		mRemoveAdButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((DiceCamera) DiceCamera.mContext).callPurchaseActivity();
			}
		});
		return from;
	}

	private void displayDebugInfoButton(final String text) {
		ViewGroup root = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
		Button showInfoButton = new Button(this);
		showInfoButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		if (text == null) {
			showInfoButton.setText(mDebugText == null ? "Info" : ("Info\n" + mDebugText));
			root.addView(showInfoButton, dp2px(80), dp2px(70));
		} else {
			showInfoButton.setText(mDebugText == null ? text : (text + "\n" + mDebugText));
			root.addView(showInfoButton, dp2px(180), dp2px(90));
		}
		showInfoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showInfo();
			}
		});
	}

	private void showInfo() {
		StringBuffer texts = new StringBuffer();
		texts.append("" + mImageLoader.getLoadedImageWidth() + " x " + mImageLoader.getLoadedImageHeight() + "\n");

		ExifInterface exif = null;
		try {
			String uriPath = Uri.parse(mURIString).getPath();
			exif = new ExifInterface(uriPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (exif != null) {
			String orientationString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
			if (orientationString != null) {
				int orientation = Integer.parseInt(orientationString);
				texts.append("orientation: " + orientationString + " (" + UserInterfaceUtil.getExifString(orientation) + ")" + "\n");
			}

			String lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
			String lat_ref = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
			String lon = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
			String lon_ref = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
			if (lat != null) {
				texts.append("lat: " + lat + " (" + lat_ref + ")\n");
			}
			if (lon != null) {
				texts.append("lon: " + lon + " (" + lon_ref + ")\n");
			}
		}

		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setTitle("Info");
		dlg.setMessage(texts);
		dlg.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dlg.show();

	}

	@Override
	protected void onResume() {
		super.onResume();

		mRemoveAdButton.setVisibility(View.GONE);
		// } else {
		// mRemoveAdButton.setVisibility(View.VISIBLE);
		// mRemoveAdButton.bringToFront();
		// mRemoveAdButton.invalidate();
		// }

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		// if (hasFocus) {
		// if (mImageLoader.isLoaded() == false) {
		// mImageLoader.loadImage(mURIString);
		// }
		// }

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// DICE if (adspace() == true && mAdAlreadyDisplayed == true) {
		// // Checks the orientation of the screen
		// if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		// SPAdController.sharedController().reloadCurrentAD(this,
		// getAdTargetLayout(SPAdGoogleAd.isAlt));
		// } else if (newConfig.orientation ==
		// Configuration.ORIENTATION_PORTRAIT) {
		// SPAdController.sharedController().reloadCurrentAD(this,
		// getAdTargetLayout(SPAdGoogleAd.isAlt));
		// }
		// }
	}

	private FrameLayout getAdTargetLayout(boolean isAlt) {
		if (isAlt)
			return (FrameLayout) findViewById(R.id.ad_target_layout_2);
		return (FrameLayout) findViewById(R.id.ad_target_layout_1);
	}

	protected void doShare() {
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		if (mFrom.equals(BaseActivity.E_QUICK_VIEW_AFTER_CAPTURE)) {
			shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(mURIString));
		} else {
			shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(mMediaFiles[mPager.getCurrentItem()]));
		}
		shareIntent.setType("image/jpeg");
		startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_image)));
	}

	protected void doTrash() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.confirm_delete);
		builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				removeFileAndCloseActivity();
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	protected void removeFile() {
		FileManagement fm = new FileManagement(this);
		if (mFrom.equals(BaseActivity.E_QUICK_VIEW_AFTER_CAPTURE)) {
			fm.removeFileAt(mURIString.toString());
		} else {
			fm.removeFileAt(Uri.fromFile(mMediaFiles[mPager.getCurrentItem()]).toString());
		}
	}

	protected void removeFileAndCloseActivity() {

		if (mImageLoader != null)
			mImageLoader.deleteImage();

		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				removeFile();
				closeActivity();
			}
		}, 100);
	}

	protected void closeActivity() {
		final Activity mThis = this;

		new Handler(getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				mThis.finish();
			}
		});
	}

	private int dp2px(final int dp) {
		final float scale = getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return new ControlUserInput(this).onTouchEvent(event);
	}

	@Override
	public void onLoadingFinish(Bitmap bitmap) {
		if (BuildConfig.DEBUG) {
			if (bitmap == null) {
				displayDebugInfoButton(null);
			} else {
				displayDebugInfoButton("preview: " + bitmap.getWidth() + " x " + bitmap.getHeight() + "\n"
						+ mImageLoader.getLoadedImageWidth() + " x " + mImageLoader.getLoadedImageHeight() + "\n"
						+ (Settings.getInstance().getSaveAsPreview() ? "SaveAsPreview" : ""));
			}
		}
	}

	public View getHandler() {
		return getWindow().getDecorView();
	}

	private class QuickViewPagerAdapter extends PagerAdapter {

		public QuickViewPagerAdapter(Context c) {
			super();
		}

		@Override
		public int getCount() {
			return mMediaFiles == null ? 1 : mMediaFiles.length;
		}

		@Override
		public Object instantiateItem(final View pager, int position) {

			if (mPagerMaxPages == 1 || mPosition == -1) {

				mImageLoader = new UniversalImageLoader(getApplicationContext());

				mImageLoader.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
				Bitmap bm = null;
				try {
					bm = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), Uri.parse(mURIString));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(bm != null) {
				Matrix matrix = new Matrix();
							
				if(((DiceCamera)DiceCamera.mContext).mSingleRotationBugFix) matrix.setRotate(180);
				else matrix.setRotate(0);
				
				Bitmap bmpBowRotated = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, false);

				mImageLoader.loadImage(bmpBowRotated);
				
				Log.i("dicecam", "hjh " + mURIString);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						((ViewPager) pager).addView(mImageLoader, 0);
					}
				});

				return mImageLoader;
				} else 	return null;
			}

			mImageView = new ImageView(getApplicationContext());

			mImageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});

			try {
				Bitmap bp = ImageLoader.getInstance().loadImageSync("file://" + mMediaFiles[position].getPath());
				mImageView.setImageBitmap(bp);

			} catch (Exception e) {
				e.printStackTrace();
			}

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					((ViewPager) pager).addView(mImageView, 0);
				}
			});

			return mImageView;
		}

		@Override
		public void destroyItem(View pager, int position, Object view) {
			((ViewPager) pager).removeView((View) view);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

	@Override
	public void advertisement() {
		// TODO Auto-generated method stub

	}
}

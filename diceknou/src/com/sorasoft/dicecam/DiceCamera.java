package com.sorasoft.dicecam;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.sorasoft.dicecam.engine.NakedFootPreViewCallBack;
import com.sorasoft.dicecam.engine.filter.DicecamLens;
import com.sorasoft.dicecam.engine.filter.LensCenter;
import com.sorasoft.dicecam.secure.CodeStability;
import com.sorasoft.dicecam.setting.PurchaseActivity;
import com.sorasoft.dicecam.setting.SettingActivity;
import com.sorasoft.dicecam.setting.Settings;
import com.sorasoft.dicecam.setting.WatermarkProvider;
import com.sorasoft.dicecam.util.CommonUtil;
import com.sorasoft.dicecam.util.TinyDB;
import com.sorasoft.dicecam.util.UserInterfaceUtil;
import com.sorasoft.dicecam.view.ArcProgressView;
import com.sorasoft.dicecam.view.DicecamBlurTouchEventListener;
import com.sorasoft.dicecam.view.DicecamGLSurfaceView;
import com.sorasoft.dicecam.view.MainIconArea;
import com.sorasoft.dicecam.view.LensIntensityControlView;
import com.sorasoft.dicecam.view.FavoritesMenu;
import com.sorasoft.dicecam.view.FavoritesMenu.FavoritesListener;
import com.sorasoft.dicecam.view.album.ViewAfterPictureTaken;

public class DiceCamera extends BaseActivityWithoutActionBar implements DicecamBlurTouchEventListener, ImageComposition.AddingCallback,
		FavoritesListener {

	private static final int OPTION_TXT_COLOR = Color.argb(255, 242, 57, 124);
	private static final int OPTION_TXT_COLOR_SELECTED = Color.argb(255, 255, 255, 255);
	public boolean mSingleRotationBugFix = false;
	public static Context mContext;
	public static int irandom = 0;
	public static boolean mAlready = true;
	
	public TinyDB tinyDB;

	private static final long SWITCH_ON_SAVE_AS_PREVIEW_THRESHOLD = 86400000;
	private boolean firstRunAfterInitialInstall = false;
	private long elapsedTimeFromFirstInstall = 0;
	
	public ArrayList<DicecamLens> mDataFavorites;

	private Camera mCamera;
	private boolean isOneCamera = false;
	private boolean isFrontCameraDefaultWhenIsOneCameraFlagTrue = false;

	private boolean firstInitializing = true;

	public static boolean isTablet = false;
	public static int screenLayoutSize = 0;
	private String action;
	public Uri mImage_capture_intent_uri = null;

	private FrameLayout mCameraPreviewFrame;

	private DicecamGLSurfaceView mGLSurfaceView; // for live preview
	private SurfaceView mDummySurfaceView = null;

	private TextView mFilterTitleTextView;
	private TextView mTimerInfoTextView;
	private TextView mTimerHUDTextView;
	private ArcProgressView mArcProgressView;
	private View mShutterView;
	private ProgressBar mProgressBar;
	private ProgressBar mProgressCircle;

	private Handler mMainHandler;

	private ImageButton mSettingButton;
	private ImageButton mFlipCameraButton;

	private ImageButton mCollageButton;
	private ImageButton mVignetteButton;
	private ImageButton mBlurButton;
	private ImageButton mFrameButton;
	private ImageButton mTimerButton;

	private ImageButton mAlbumButton;
	private ImageButton mCaptureButton;
	private ImageButton mBtnRollDice;
	private ImageButton mBtnFavorites;

	private TextView mRandomFilterButtonTextView;
	private TextView mFilterButtonTextView;

	private SeekBar mTimerSeekbar;
	private SeekBar mIntervalSeekbar;

	private RelativeLayout mToolbarHeadLayout;
	private MainIconArea mToolbarBodyLayout;

	private LinearLayout mFramingOption;
	private LinearLayout mTimingOption;

	public FavoritesMenu mFavoriteView = null;

//	private OrientationEventListener mOrientationListener;

	private int numberOfCameras = 0; // 0: not calculated, -1: unknown, 1 ~

	public int collageStatus;

	public int ORIENTATION = ImageComposition.DICECAM_ORIENTATION_PORTRAIT;
	private float orientationDegree = 0.f;

	public int chosenPreviewWidth = 0;
	public int chosenPreviewHeight = 0;

	public int chosenPictureWidth = 0;
	public int chosenPictureHeight = 0;

	public List<Bitmap> collagePictureBitmaps;
	public List<Uri> collagePictureURIs;

	private ImageComposition mImageComposition;
	private FileManagement mFileManagement;
	private CollageProvider mCollageProvider;

	private CountDownTimer mCountDownTimer;
	private int mCurrentTimerValue = 0;
	private boolean mCountDownBegan;

	private CollageSelector mFrameSelection;

	public NakedFootPreViewCallBack mEngine;
	private boolean mCurrentLensIsLocked = false;

	private boolean canTakeStillPicture = true;

	public LocationManager locationManager;

	public String locationProvider;

	public boolean locationEnabled;

	private final int timerInfoTextSizeLarge_inSP = 200;
	private final int timerInfoTextSizeSmall_inSP = 80;

	// private TextView infoTextView = null; // for debug
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
		   if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			   takingShot();
		       return true;
		   } else {
		       return super.onKeyDown(keyCode, event); 
		   }
		   
		}

	private void takeSinglePicture() {
//		if (PerformanceHelper.isStillPictureAllowable() == false) {
//			takeRenderedSinglePicture();
			// } else if (canTakeStillPicture) {
			// takeStillSinglePicture();
//		} else {
			takeRenderedPicture();
//		}
	}

	private void takeRenderedPicture() {
		if (CollageProvider.hasSinglePicture(collageStatus)) {
			mEngine.getCurrentBuffer(mImageComposition.pc);
			flickerShutterView();
		} else {
//			mCollageProvider.setCollageButton(mCollageButton, collageStatus, getCollageCountTaken() + 1);
			showShutterView();
//			SoundProvider.sharedProvider().playShutterSound();
			mEngine.getCurrentBuffer(mImageComposition.rect_pc);
		}
	}

	private void takeRenderedSinglePicture() {
		stopCameraPreview();
		showProgressBar();
		flickerShutterView();
		mImageComposition.setSinglePictureProcessCallback(new ImageComposition.ProcessCallback() {
			@Override
			public void onProcess(float process) {
				setProgressBar((int) (100.f * process));
			}
		});
		mEngine.getCurrentBuffer(mImageComposition.pc);
	}

	private void loadUnivImgLoader() {
		File cacheDir = StorageUtils.getCacheDirectory(mContext);
		DisplayImageOptions Gallery = new DisplayImageOptions.Builder().considerExifParams(true).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).memoryCacheExtraOptions(1000, 800)
				// default = device screen dimensions
				.diskCacheExtraOptions(1000, 800, null).threadPoolSize(10)
				// default
				.threadPriority(Thread.NORM_PRIORITY - 2)
				// default
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				// default
				.denyCacheImageMultipleSizesInMemory().memoryCache(new LruMemoryCache(2 * 1024 * 1024)).memoryCacheSize(2 * 1024 * 1024)
				.memoryCacheSizePercentage(13) // default
				.diskCache(new UnlimitedDiscCache(cacheDir)) // default
				.diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(100).diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
				.imageDownloader(new BaseImageDownloader(mContext)) // default
				.imageDecoder(new BaseImageDecoder(false)) // default
				.defaultDisplayImageOptions(Gallery) // default
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}

	private void takeStillSinglePicture() {
		final int pictureOrientation = ORIENTATION;
		final Location pictureLocation = getLastKnownLocationForPictureExif();

		// HJH_System.gc();

		showShutterView();

		mCamera.takePicture(null, null, new Camera.PictureCallback() {
			@Override
			public void onPictureTaken(final byte[] data, Camera camera) {

				disableButtonsForTakingPhoto();
				stopCameraPreview();
				hideShutterView();
				showProgressBar();
				// HJH_System.gc();

				// Thread pThread ...
				// pThread.start(); Replaced by ThreadOfTakeSinglePicture
				new ThreadOfTakeStillSinglePicture().execute(pictureOrientation, pictureLocation, data);
			}
		});

	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (CommonUtil.isNotSupportOpenGLES2(this))
			finish();

		mContext = this;

		loadUnivImgLoader();

		setupUI();
		
//		mDataFavorites = Settings.getInstance().loadFavories();
		mDataFavorites = new ArrayList<DicecamLens>();
		
		tinyDB = new TinyDB(this);
		
		ArrayList<String> strArr = 	tinyDB.getListString("favorites");
		DicecamLens dl = null;
		for(String str : strArr) {
			try {
				Integer i = Integer.parseInt(str);
				dl = LensCenter.defaultCenter().getLensAt(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(dl != null) mDataFavorites.add(dl);
		}
		// DICE setupSpad();
		setInitialSaveAsPreview();

		// if (BuildConfig.DEBUG) {
		// addDebugInfoTextView();
		// } else {
		// infoTextView = null;
		// }
		if (Settings.getInstance().getFirstTimeExecution()) {
			Settings.getInstance().setCameraSelection(Camera.CameraInfo.CAMERA_FACING_FRONT);
			Settings.getInstance().setCurrentLensID("0225");
			Settings.getInstance().setFirstTimeExecution(false);
		}

	}

	/**
	 * Setting up UI
	 */
	private void setupUI() {

		// Initializing///////////////////////////////////////////////
		PerformanceHelper.init();
		firstInitializing = true;
		mMainHandler = new Handler(Looper.getMainLooper());

		canTakeStillPicture = true;

		LensCenter.setDefaultContext(this);

		isTablet = getResources().getBoolean(R.bool.isTablet);
		screenLayoutSize = this.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

		// if (BuildConfig.DEBUG) {
		// Log.d("dicecam", "temporaryFiles..");
		// FileManagement.printTemporaryFiles(this);
		// // FileManagement.clearTemporaryFiles(this);
		// }
		// FileManagement.clearTemoporaryJPEGFiles(this);
		// TODO: clearTemporaryFiles (This is needed when Live Effect is
		// disabled) !important

//		if (mOrientationListener == null) {
//			mOrientationListener = new OrientationEventListener(this) {
//				@SuppressLint("NewApi")
//				@Override
//				public void onOrientationChanged(int angle) {
//					if (angle == ORIENTATION_UNKNOWN)
//						return;
//
//					int newOrientation = ImageComposition.DICECAM_ORIENTATION_PORTRAIT;
//
//					Display display = getWindowManager().getDefaultDisplay();
//					int rotation = display.getRotation();
//					int adjustedAngle = (angle + ImageComposition.displayRotationAngle(rotation)) % 360;
//
//					switch (adjustedAngle / 45) {
//					case 0:
//						newOrientation = ImageComposition.DICECAM_ORIENTATION_PORTRAIT;
//						break;
//					case 1:
//						newOrientation = ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_RIGHT;
//						break;
//					case 2:
//						newOrientation = ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_RIGHT;
//						break;
//					case 3:
//						newOrientation = ImageComposition.DICECAM_ORIENTATION_PORTRAIT_UPSIDE_DOWN;
//						break;
//					case 4:
//						newOrientation = ImageComposition.DICECAM_ORIENTATION_PORTRAIT_UPSIDE_DOWN;
//						break;
//					case 5:
//						newOrientation = ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_LEFT;
//						break;
//					case 6:
//						newOrientation = ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_LEFT;
//						break;
//					case 7:
//						newOrientation = ImageComposition.DICECAM_ORIENTATION_PORTRAIT;
//						break;
//					}
//
//					// if ( BuildConfig.DEBUG ) {
//					// Log.d("dicecam", "angle: " + angle + "(rotated: " +
//					// rotation + " - " +
//					// ImageComposition.displayRotationString(rotation) + ")" +
//					// " -> newOri: " +
//					// ImageComposition.orientationString(newOrientation) + " ("
//					// + newOrientation + ")");
//					// }
//
//					if (newOrientation == ORIENTATION)
//						return;
//					ORIENTATION = newOrientation;
//
//					updateButtonOrientations();
//				}
//			};
//		}

		// if ( Build.VERSION.SDK_INT >= 11) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getSupportActionBar().hide();
		// }

		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dicecamera);

		mCameraPreviewFrame = (FrameLayout) findViewById(R.id.camera_preview);

		mFilterTitleTextView = (TextView) findViewById(R.id.filter_title_textview);
		mTimerInfoTextView = (TextView) findViewById(R.id.timer_info_textview);
		mTimerHUDTextView = (TextView) findViewById(R.id.timer_hud_textview);
		mArcProgressView = (ArcProgressView) findViewById(R.id.arc_progressview);
		mShutterView = findViewById(R.id.shutter_view);
		mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
		mProgressCircle = (ProgressBar) findViewById(R.id.progress_circle);

		mFramingOption = (LinearLayout) findViewById(R.id.framing_option);
		mTimingOption = (LinearLayout) findViewById(R.id.timer_option);

		mTimerSeekbar = (SeekBar) findViewById(R.id.timer_seek_bar);
		mIntervalSeekbar = (SeekBar) findViewById(R.id.interval_seek_bar);

		mProgressBar.setProgressDrawable(new ColorDrawable(Color.rgb(Integer.valueOf("F2", 16), Integer.valueOf("39", 16), Integer.valueOf("7C", 16))));
//		mIntervalSeekbar.setProgressDrawable(new ColorDrawable(Color.rgb(Integer.valueOf("F2", 16), Integer.valueOf("39", 16), Integer.valueOf("7C", 16))));
		
		mImageComposition = new ImageComposition(this);
		mImageComposition.setCollageImageAddingCallback(this);
		mFileManagement = new FileManagement(this);
		mFileManagement.setMainActivity(this);
		mCollageProvider = new CollageProvider(this);
		mFrameSelection = new CollageSelector(this);
		collageStatus = mFrameSelection.getSelectedFrame();

		// capture button
		mCaptureButton = (ImageButton) findViewById(R.id.capture_button);
		mCaptureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onCaptureButtonClick(view);
			}
		});

		// settings button
		mSettingButton = (ImageButton) findViewById(R.id.settings_button);
		mSettingButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent settingActivity = new Intent(mContext, SettingActivity.class);
				settingActivity.putExtra("adjustedPreviewWidth", adjustedPreviewWidth);
				settingActivity.putExtra("adjustedPreviewHeight", adjustedPreviewHeight);
				settingActivity.putExtra("chosenPreviewWidth", chosenPreviewWidth);
				settingActivity.putExtra("chosenPreviewHeight", chosenPreviewHeight);
				settingActivity.putExtra("chosenPictureWidth", chosenPictureWidth);
				settingActivity.putExtra("chosenPictureHeight", chosenPictureHeight);
				startActivity(settingActivity);
			}
		});

		// flip_button
		mFlipCameraButton = (ImageButton) findViewById(R.id.camera_flip_button);
		mFlipCameraButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				flipCamera();
			}
		});
		updateFlipButtonVisible();

		// collage button
		mCollageButton = (ImageButton) findViewById(R.id.collage_button);
		mCollageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleCollageSelector();
			}
		});

		// vignette_button
		mVignetteButton = (ImageButton) findViewById(R.id.vignette_button);
		mVignetteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleVignette();
			}
		});

		// blur_button
		mBlurButton = (ImageButton) findViewById(R.id.blur_button);
		mBlurButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				toggleBlur();
			}
		});

		// frame_button
		mFrameButton = (ImageButton) findViewById(R.id.edge_button);
		mFrameButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				insertEdgeFrame();
			}
		});

		// timer_button
		mTimerButton = (ImageButton) findViewById(R.id.timer_button);
		mTimerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleTimerOptionView();
			}
		});

		// album_button
		mAlbumButton = (ImageButton) findViewById(R.id.album_button);
		mAlbumButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showAlbum();
			}
		});

		mBtnFavorites = (ImageButton) findViewById(R.id.filter_button);
		mBtnFavorites.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
			if(mDataFavorites.size() > 5) {
					mDataFavorites.remove(0);
					if(mAlready) {
						Toast.makeText(mContext, "Limits to save 6 filters. First one has been deleted.", Toast.LENGTH_LONG).show();
						mAlready = false;
					}
				}
				mDataFavorites.add(currentLens);
				Toast.makeText(mContext, "Added.", Toast.LENGTH_LONG).show();
//				Settings.getInstance().saveFavories(mDataFavorites);
				return true;
			}
		});
		
		mBtnFavorites.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("hjh", "favorite Button Clicked.");
				if(mDataFavorites.size() == 0) Toast.makeText(mContext, "Long~click to Add", Toast.LENGTH_LONG).show();
				else favoritesVisiable(true);
			}
		});
		// filter_button_textview
		mFilterButtonTextView = (TextView) findViewById(R.id.filter_button_textview);

		mBtnRollDice = (ImageButton) findViewById(R.id.random_filter_button);
		mBtnRollDice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rollDice(1);
			}
		});
		
		mBtnRollDice.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				rollDice(0);
				return true;
			}
		});

		// lens selector
		mFavoriteView = (FavoritesMenu) findViewById(R.id.lens_selector);
		mFavoriteView.setLensSelectorListener(this);

		// toolbar_head
		mToolbarHeadLayout = (RelativeLayout) findViewById(R.id.toolbox);

		// toolbar_body
		mToolbarBodyLayout = (MainIconArea) findViewById(R.id.toolbar_body);
		mToolbarBodyLayout.captureButton = mCaptureButton;
		mToolbarBodyLayout.albumButton = mAlbumButton;
		mToolbarBodyLayout.randomFilterButton = mBtnRollDice;
		mToolbarBodyLayout.filterButton = mBtnFavorites;
		mToolbarBodyLayout.randomFilterButtonTextView = mRandomFilterButtonTextView;
		mToolbarBodyLayout.filterButtonTextView = mFilterButtonTextView;

//		int previousToolbarBodyHeight = Settings.getInstance().getToolbarBodyHeight();
//		if (previousToolbarBodyHeight > 0) {
//			setToolbarBodyHeight(previousToolbarBodyHeight);
//		}

		// timer option (timer + interval)
		mTimerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				mTimerInfoTextView.setVisibility(View.INVISIBLE);

				updateTimerHUDTextView();
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				mTimerInfoTextView.setText("");
				mTimerInfoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, timerInfoTextSizeLarge_inSP);
				mTimerInfoTextView.setVisibility(View.VISIBLE);

				updateTimerHUDTextView();
			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				int delay = arg1 / 10;
				Settings.getInstance().setTimer(delay);

				String timerString = (delay == 0 ? "" : String.format("%d", delay));

				mTimerInfoTextView.setText(timerString);
			}
		});

		mIntervalSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				mTimerInfoTextView.setVisibility(View.INVISIBLE);

				updateTimerHUDTextView();
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				mTimerInfoTextView.setText("");
				mTimerInfoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, timerInfoTextSizeSmall_inSP);
				mTimerInfoTextView.setVisibility(View.VISIBLE);

				updateTimerHUDTextView();
			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				int interval = arg1 * 20;
				Settings.getInstance().setInterval(interval);

				float interval_ms = ((float) interval) / 1000;
				String intervalString = String.format("%.2f secs", interval_ms);

				if (interval == 0) {
					intervalString = "MANUAL";
				}

				mTimerInfoTextView.setText(intervalString);

			}
		});
	}

	/**
	 * Advertisement
	 */
	// DICE private void setupSpad() {
	// if (Settings.defaultSettings().getProUpgrade() == false) {
	// mMainHandler.postDelayed(new Runnable() {
	//
	// @Override
	// public void run() {
	// // RevMob
	//
	// // AdColony
	// /**
	// * @see https
	// * ://github.com/AdColony/AdColony-Android-SDK/wiki
	// * /API-Details#configure-activity-activity-string-
	// * client_options-string-app_id-string-zone_ids-
	// * client_options - A String containing your app
	// * version, the origin store, and an optional
	// * "skippable" String that will enable users to cancel
	// * the ad experience using the back button. Publisher
	// * earnings will not occur for ads that are cancelled
	// * using this method (examples:
	// * �ersion:1.1,store:google�or
	// * "version:2.23,store:amazon,skippable"). Please note
	// * that if you are integrating into an Amazon app you
	// * will need to replace 'google' with 'amazon' in the
	// * client_options String.
	// */
	// String storeString = null;
	// switch (PublishInfo.source) {
	// case PublishInfo.SOURCE__AMAZON_APPSTORE_FOR_ANDROID:
	// storeString = "amazon";
	// break;
	// case PublishInfo.SOURCE__INTEL_PROVIDER:
	// case PublishInfo.SOURCE__GOOGLE_PLAY_STORE:
	// default:
	// storeString = "google";
	// break;
	// }
	// SPAdAdColonyVideo.clientOptions = "version:" +
	// CommonUtil.getAppVersion(mContext) + ",store:" + storeString;
	// SPAdAdColonyVideo.appId = Settings.ADCOLONY_APP_ID;
	// SPAdAdColonyVideo.zoneId = Settings.ADCOLONY_ZONE_ID;
	//
	// // GoogleAd
	// SPAdGoogleAd.unitID = Settings.ADMOB_UNIT_ID;
	// SPAdGoogleAd.gender = AdRequest.GENDER_FEMALE;
	// // SPAdGoogleAd.birthday = new GregorianCalendar(1987, 1,
	// // 1).getTime();
	//
	// // setSource
	// SPAdController spad = SPAdController.sharedController();
	// spad.setSource(PublishInfo.source);
	//
	// if (BuildConfig.DEBUG) {
	// Log.d("spad", "Settings.Source: " + PublishInfo.getSourceString() + " - "
	// + PublishInfo.source);
	// Log.d("spad", "SPAD.Source: " + spad.getSource());
	// Log.d("spad", "SPAdAdColonyVideo.clientOptions: " +
	// SPAdAdColonyVideo.clientOptions);
	// }
	//
	// spad.setLocationReader(new SPAdController.LocationReader() {
	//
	// @Override
	// public Location locationForSPAd() {
	// return LocationCache.getLocation();
	// }
	// });
	//
	// spad.updateAsync(mContext, new SPAdController.UpdateCallback() {
	// @Override
	// public void onUpdateComplete() {
	// Log.d("spad", "onUpdateComplete");
	//
	// if
	// (SPAdController.sharedController().hasAdInQueue(SPAd.SPAD_GoogleAD_Alt))
	// {
	// SPAdGoogleAd.unitID = Settings.ADMOB_ALT_UNIT_ID;
	// Log.d("spad", "alt unitID set");
	// }
	//
	// SPAdController.sharedController().configureAds((Activity) mContext);
	// }
	// });
	// }
	// }, 1000);
	// }
	// }

	@SuppressLint("NewApi")
	private void setInitialSaveAsPreview() {
		if (Settings.getInstance().getFirstLaunchTime() > 0) {
			return;
		}

		long currentTime = System.currentTimeMillis();
		Settings.getInstance().setFirstLaunchTime(currentTime);

		if (Settings.getInstance().getSaveAsPreview() == true) {
			return;
		}

		if (Build.VERSION.SDK_INT < 9) {
			AlbumImageAdapter adapter = new AlbumImageAdapter(this);

			if (adapter.getCount() < 1) {
				Settings.getInstance().setSaveAsPreview(true);
				firstRunAfterInitialInstall = true;
			}
		} else {
			try {
				elapsedTimeFromFirstInstall = currentTime - getPackageManager().getPackageInfo(getPackageName(), 0).firstInstallTime;
				if (elapsedTimeFromFirstInstall < SWITCH_ON_SAVE_AS_PREVIEW_THRESHOLD) {
					Settings.getInstance().setSaveAsPreview(true);
					firstRunAfterInitialInstall = true;
				}
			} catch (NameNotFoundException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void addDebugInfoTextView() {
		// FrameLayout layout = (FrameLayout)
		// this.getWindow().getDecorView().findViewById(android.R.id.content);
		// infoTextView = new TextView(this);
		// infoTextView.setVisibility(View.INVISIBLE);
		// infoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
		// infoTextView.setBackgroundColor(Color.argb(128, 0, 0, 0));
		// UserInterfaceUtil.setAlpha(infoTextView, 0.5f);
		// FrameLayout.LayoutParams params = new
		// FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
		// FrameLayout.LayoutParams.WRAP_CONTENT,
		// Gravity.CENTER_HORIZONTAL | Gravity.TOP);
		// layout.addView(infoTextView, params);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void updateDebugInfoTextView() {
		ArrayList<String> infoStrings = new ArrayList<String>();

		Runtime rt = Runtime.getRuntime(); // @see
		// http://stackoverflow.com/a/9428660
		long maxMemory = rt.maxMemory();
		long freeMemory = rt.freeMemory();
		infoStrings.add("maxMemory: " + Long.toString(maxMemory) + " " + (maxMemory / 1024 / 1024) + "MB");
		infoStrings.add("freeMemory: " + Long.toString(freeMemory) + " " + (freeMemory / 1024 / 1024) + "MB");

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		float dpWidth = displaymetrics.widthPixels / displaymetrics.density;
		float dpHeight = displaymetrics.heightPixels / displaymetrics.density;
		infoStrings.add("dpWidth: " + dpWidth + ", dpHeight: " + dpHeight);

		infoStrings.add("layout: " + getResources().getString(R.string.layout));
		infoStrings.add("DPI: " + UserInterfaceUtil.getDPIString(this));

		infoStrings.add("isTable: " + getResources().getBoolean(R.bool.isTablet));

		int screenLayoutSize = this.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		String screenLayoutSizeString = null;
		switch (screenLayoutSize) {
		case 1:
			screenLayoutSizeString = "SMALL";
			break;
		case 2:
			screenLayoutSizeString = "NORMAL";
			break;
		case 3:
			screenLayoutSizeString = "LARGE";
			break;
		case 4:
			screenLayoutSizeString = "XLARGE";
			break;
		default:
			screenLayoutSizeString = "UNKNOWN";
			break;
		}
		infoStrings.add("screenLayoutSize: " + screenLayoutSize + " - " + screenLayoutSizeString);

		int width, height;
		if (android.os.Build.VERSION.SDK_INT < 13) {
			Display display = getWindowManager().getDefaultDisplay();
			width = display.getWidth();
			height = display.getHeight();
		} else {
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			width = size.x;
			height = size.y;
		}
		infoStrings.add("window size: " + width + " x " + height + " pixels");
		infoStrings.add("OS ver: " + android.os.Build.VERSION.RELEASE + " - " + android.os.Build.VERSION.SDK_INT);
		infoStrings.add("animation level: " + PerformanceHelper.getAnimationLevelString());
		infoStrings.add("still picture allowed: " + (PerformanceHelper.isStillPictureAllowable() ? "YES" : "NO"));

		// int maxTextureSize = NakedFootPreViewCallBack.getGlMaxTextureSize();
		// Log.i("dicecam", "[i] maxTextureSize: " + maxTextureSize);

		infoStrings.add("firstRunAfterInitialInstall: " + String.valueOf(firstRunAfterInitialInstall));
		long currentTime = System.currentTimeMillis();
		infoStrings.add("firstLaunchTime: " + Math.round((currentTime - Settings.getInstance().getFirstLaunchTime()) / 1000.f)
				+ " sec ago");
		if (Build.VERSION.SDK_INT > 8) {
			try {
				PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
				long firstInstalledTime = currentTime - pi.firstInstallTime;
				infoStrings.add("firstInstalledTime: " + Math.round(firstInstalledTime / 1000.f) + " sec ago");
				long lastUpdatedTime = currentTime - pi.lastUpdateTime;
				infoStrings.add("lastUpdatedTime: " + Math.round(lastUpdatedTime / 1000.f) + " sec ago");
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		PackageManager pm = getPackageManager();
		ApplicationInfo appInfo;
		try {
			appInfo = pm.getApplicationInfo("com.sorasoft.dicecam", 0);
			String appFile = appInfo.sourceDir;
			long installed = new File(appFile).lastModified(); // Epoch Time
			infoStrings.add("installed: " + Math.round(installed / 1000.f) + " sec ago");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		infoStrings.add("frontCam Available: " + String.valueOf(isFrontCameraAvailable()));
		infoStrings.add("frontCam Activated: " + String.valueOf(isFrontCameraActivated()));
		infoStrings.add("saveAsPreview: " + String.valueOf(Settings.getInstance().getSaveAsPreview()));
		infoStrings.add("SaveToRemovableStorage: " + String.valueOf(Settings.getInstance().getSaveToRemovableStorage()));
		infoStrings.add("elapsedTimeFromFirstInstall: " + elapsedTimeFromFirstInstall);
		// infoStrings.add("SWITCH_ON_SAVE_AS_PREVIEW_THRESHOLD: ")

		infoStrings.add("source: " + PublishInfo.source + " - " + PublishInfo.getSourceString());

		StringBuilder infoString = new StringBuilder();
		for (int i = 0; i < infoStrings.size(); i++) {
			infoString.append(infoStrings.get(i) + "\n");
		}

		// infoTextView.setText(infoString);
	}

	// @Override
	// public boolean dispatchKeyEvent(KeyEvent event) {
	// int action = event.getAction();
	// int keyCode = event.getKeyCode();
	//
	// switch (keyCode) {
	// case KeyEvent.KEYCODE_VOLUME_DOWN:
	// case KeyEvent.KEYCODE_VOLUME_UP: {
	//
	// if (action == KeyEvent.ACTION_DOWN) {
	// onCaptureButtonClick(null);
	// }
	//
	// return true;
	// }
	//
	// default:
	// return super.dispatchKeyEvent(event);
	// }
	// }

	private RotateAnimation getTimerHUDTextViewRotationAnimation() {
		RotateAnimation animation = new RotateAnimation(orientationDegree, orientationDegree, mTimerHUDTextView.getWidth() / 2,
				mTimerHUDTextView.getHeight() / 2);
		animation.setFillAfter(true);
		animation.setDuration(0);
		return animation;
	}

	private void updateTimerHUDTextViewRotation() {
		mTimerHUDTextView.startAnimation(getTimerHUDTextViewRotationAnimation());
	}

	private void updateTimerHUDTextView() {
		mTimerHUDTextView.clearAnimation();

		if (mTimerInfoTextView.getVisibility() == View.VISIBLE || mFilterTitleTextView.getVisibility() == View.VISIBLE) {
			mTimerHUDTextView.setVisibility(View.INVISIBLE);
		} else {
			int delay = Settings.getInstance().getTimer();
			if (delay > 0) {
				mTimerHUDTextView.setText(String.format("%d", delay));
				mTimerHUDTextView.setVisibility(View.VISIBLE);

				if (orientationDegree != 0.f) {
					updateTimerHUDTextViewRotation();
				}
			} else {
				mTimerHUDTextView.setVisibility(View.INVISIBLE);
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void updateFlipButtonVisible() {
		if (numberOfCameras == 0) {
			// calculate number of cameras
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				numberOfCameras = Camera.getNumberOfCameras();
			} else {
				numberOfCameras = -1;
			}
		}

		if (numberOfCameras == 1) {
			mFlipCameraButton.setVisibility(View.INVISIBLE);
		} else {
			mFlipCameraButton.setVisibility(View.VISIBLE);
		}
	}

	private LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d("location", "onStatusChanged(" + status + "): " + provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.d("location", "onProviderEnabled: " + provider);
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.d("location", "onProviderDisabled: " + provider);
		}

		@Override
		public void onLocationChanged(Location location) {
			Log.d("location", "onLocationChanged: " + location);
			LocationCache.setLocation(location);
		}
	};

	private void resumeLocationManager() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setCostAllowed(false);

		locationProvider = locationManager.getBestProvider(criteria, true);

		locationEnabled = false;

		// If no suitable provider is found, null is returned.
		Log.d("location", "best providerName: " + locationProvider);

		locationEnabled = false;
		if (locationProvider != null) {
			if (locationManager.isProviderEnabled(locationProvider)) {
				long minTime = 10 * 60 * 1000; // 10 minutes
				float minDistance = 10.f; // 10 meters
				locationManager.requestLocationUpdates(locationProvider, minTime, minDistance, mLocationListener);
				LocationCache.setLocation(locationManager.getLastKnownLocation(locationProvider));
				locationEnabled = true;
				Log.d("location", "location Enabled: " + locationManager);
			}
		}
	}

	private void pauseLocationManager() {
		locationManager.removeUpdates(mLocationListener);
		locationEnabled = false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d("dicecam", "DiceCamera.onStart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("dicecam", "DiceCamera.onResume");
		Settings.getInstance().setLensIntensity(1.0f);
		// IntentFilter imageCaptureFilter = new
		// IntentFilter("android.media.action.IMAGE_CAPTURE");
		// imageCaptureFilter.addAction("android.provider.MediaStore.ACTION_IMAGE_CAPTURE"
		// );
		// imageCaptureFilter.addAction("android.media.action.STILL_IMAGE_CAMERA");
		// imageCaptureFilter.addAction("android.intent.action.CAMERA_BUTTON" );

		action = this.getIntent().getAction();
		mImage_capture_intent_uri = null;

		if (MediaStore.ACTION_IMAGE_CAPTURE.equals(action)) {
			Bundle myExtras = this.getIntent().getExtras();
			if (myExtras != null) {
				mImage_capture_intent_uri = (Uri) myExtras.getParcelable(MediaStore.EXTRA_OUTPUT);
				Log.d("dicecam", "save to: " + mImage_capture_intent_uri);
			}
		}

		CodeStability.gettingWakeLock(this);

		resumeLocationManager();

		// updateCropRegion();
		updateCaptureButtonLocked();
		updateHeadButtons();
		releaseCollageTakingMode();
		updateTimerHUDTextView();

		enableButtonsAfterTakingPhoto();

		if (firstInitializing == false) {
			startPreview();
		}

//		mOrientationListener.enable();

		if (mProgressBar.getVisibility() == View.VISIBLE) {
			hideProgressBar();
		}

		if (BuildConfig.DEBUG) {
			updateDebugInfoTextView();
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			if (firstInitializing == true) {
				firstInitializing = false;
				// mMainHandler.postDelayed(new Runnable() {
				//
				// @Override
				// public void run() {
				// stopCameraPreview();
				// startCameraPreview();
				// }
				// }, 5000);
				startPreview();
			}
			// showCurrentLensTitleAfter(100);
		} else {
			// stopCameraPreview();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d("dicecam", "DiceCamera.onPause");
		CodeStability.releaseWakeLock();

		// releaseCameraPreview();

		stopCameraPreview();

//		mOrientationListener.disable();

		if (mFavoriteView.getVisibility() == View.VISIBLE) {
			favoritesVisiable(false);
		}
		if (mFramingOption.getVisibility() == View.VISIBLE) {
			closeEdgeOptionView();
		}

		pauseLocationManager();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d("dicecam", "DiceCamera.onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ArrayList<String> arrStr = new ArrayList<String>();
		for(DicecamLens lens:mDataFavorites) {
			arrStr.add(lens.getID());
		}
		tinyDB.putListString("favorites", arrStr);
	}

	@Override
	public void onLowMemory() {
		Log.d("dicecam", "LOW MEMORY");
		if (Build.VERSION.SDK_INT < 14) {
			clearUnneccesaryMemory();
		}
		super.onLowMemory();
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onTrimMemory(int level) {
		Log.d("dicecam", "TRIM MEMORY: " + level);
		clearUnneccesaryMemory();
		super.onTrimMemory(level);
	}

	private void clearUnneccesaryMemory() {
		try {
			LensCenter.defaultCenter().clearMemory(mEngine != null ? mEngine.getCurrentLens() : null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized void stopCameraPreview() {
		_stopCameraPreview();
	}

	// 2.1-> private void loadDefaultPreview() {
	// // remove mGLSurfaceView
	// if (mGLSurfaceView != null) {
	// if (mEngine != null)
	// mEngine.setGLSurfaceView(null);
	// mCameraPreviewFrame.removeView(mGLSurfaceView);
	// mGLSurfaceView = null;
	// }
	//
	// // remove mDummySurfaceView
	// if (mDummySurfaceView != null) {
	// if (mCamera != null) {
	// try {
	// mCamera.setPreviewDisplay(null);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// mCameraPreviewFrame.removeView(mDummySurfaceView);
	// mDummySurfaceView = null;
	// }
	//
	// load mNoEffectCameraPreview
	// if (mNoEffectCameraPreview == null) {
	// mNoEffectCameraPreview = new CameraPreview(this);
	// FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
	// adjustedPreviewWidth > 0 ? adjustedPreviewWidth : chosenPreviewWidth,
	// adjustedPreviewHeight > 0 ? adjustedPreviewHeight
	// : chosenPreviewHeight);
	// layoutParams.gravity = Gravity.CENTER;
	// mCameraPreviewFrame.addView(mNoEffectCameraPreview, 0, layoutParams);
	// mCameraPreviewFrame.setOnClickListener(new View.OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// if (mCamera != null) {
	// mCamera.autoFocus(null);
	// }
	// }
	// });
	//
	// // add mNoEffectCropFrameView
	// if (mNoEffectCropFrameView == null) {
	// mNoEffectCropFrameView = new CropFrameView(this);
	// mCameraPreviewFrame.addView(mNoEffectCropFrameView, 1, new
	// FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
	// LayoutParams.MATCH_PARENT));
	// }
	// }
	// 2.1<- }

	private void loadLivePreview() {
		// 2.1-> // remove mSurfaceView
		// if (mNoEffectCameraPreview != null) {
		// if (mCamera != null) {
		// try {
		// mCamera.setPreviewDisplay(null);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		// mCameraPreviewFrame.removeView(mNoEffectCameraPreview);
		// mNoEffectCameraPreview = null;
		//
		// if (mNoEffectCropFrameView != null) {
		// mCameraPreviewFrame.removeView(mNoEffectCropFrameView);
		// mNoEffectCropFrameView = null;
		// }
		// 2.1<- }

		// load mGLSurfaceView
		if (mGLSurfaceView == null) {
			mGLSurfaceView = new DicecamGLSurfaceView(this);
			// if ( Build.VERSION.SDK_INT > 10 ) {
			// mGLSurfaceView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
			// }
			mGLSurfaceView.setEventListner(this);
			mGLSurfaceView.setVisibility(View.INVISIBLE);
			FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(adjustedPreviewWidth > 0 ? adjustedPreviewWidth
					: chosenPreviewWidth, adjustedPreviewHeight > 0 ? adjustedPreviewHeight : chosenPreviewHeight);
			layoutParams.gravity = Gravity.CENTER;
			mCameraPreviewFrame.addView(mGLSurfaceView, 0, layoutParams);
		}

		// load mDummySurfaceView
		if (PerformanceHelper.dummySurfaceViewNeeded()) { // under 3.0
			loadDummySurfaceView();
		}
	}

	@SuppressWarnings("deprecation")
	private void loadDummySurfaceView() {
		if (mDummySurfaceView == null) {
			mDummySurfaceView = new SurfaceView(this);
			SurfaceHolder holder = mDummySurfaceView.getHolder();
			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			holder.addCallback(new SurfaceHolder.Callback() {

				@Override
				public void surfaceDestroyed(SurfaceHolder holder) {
					Log.d("dicecam", "mDummySurfaceView surfaceDestroyed: " + holder);
					try {
						if (mCamera != null) {
							mCamera.setPreviewDisplay(null);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void surfaceCreated(SurfaceHolder holder) {
					Log.d("dicecam", "mDummySurfaceView surfaceCreated: " + holder);
					try {
						if (mCamera != null) {
							mCamera.setPreviewDisplay(holder);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
					Log.d("dicecam", "mDummySurfaceView surfaceChanged: " + holder);
				}
			});
			mCameraPreviewFrame.addView(mDummySurfaceView, new LayoutParams(1, 1));
		} else {
			mDummySurfaceView.setLayoutParams(new LayoutParams(1, 1));
		}
	}

	private void loadEngine() {
		if (mEngine == null) {
			String lensID = Settings.getInstance().getCurrentLensID();
			DicecamLens initialLens = null;
			try {
				if (lensID != null) {
					initialLens = LensCenter.defaultCenter().getLensWithID(lensID);
				}
				if (initialLens == null) {
					initialLens = LensCenter.defaultCenter().firstLens();
				}
				updateLensAttributes(initialLens);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d("dicecam", "saved lensID: " + (lensID != null ? lensID : "null"));
			Log.d("dicecam", "initialLens: " + initialLens.toString());
			mEngine = new NakedFootPreViewCallBack(this, initialLens);
			mEngine.setLens(initialLens);
			updateCaptureButtonLocked(initialLens);
		} else {
			DicecamLens lens = mEngine.getCurrentLens();
			updateLensAttributes(lens);
			try {
				mEngine.setLens(lens);
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateCaptureButtonLocked(lens);
		}
	}

	private synchronized void startPreview() {

		if (mCamera == null) {
			mCamera = getCameraInstance();
			if (mCamera == null) {
				Log.d("dicecam", "mCamera is NULL!!!");
				return;
			}
		}

		// load Engine
		loadEngine();

		// load Preview
		// 2.1-> if (Settings.defaultSettings().getLivePreviewDisabled() ==
		// true) {
		// loadDefaultPreview();
		// updateCameraPreviewFrame();
		// mNoEffectCameraPreview.setCamera(mCamera);
		// } else {
		loadLivePreview();
		updateCameraPreviewFrame();
		mEngine.setGLSurfaceView(mGLSurfaceView);
		mEngine.setUpCamera(mCamera, isFrontCameraActivated());
		mEngine.runOnRendererThread(new Runnable() {
			@Override
			public void run() {
				if (mGLSurfaceView != null) {
					mCamera.setPreviewCallback(mEngine.getRenderer());
				}
				mCamera.startPreview();

				Log.d("dicecam", "camera startPreview: " + mCamera);
			}
		});

		if (BuildConfig.DEBUG) {
			updateDebugInfoTextView();
		}
	}

	// 2.1<- }

	private void updateCameraPreviewFrame() {
		if (mCameraPreviewFrame.getMeasuredWidth() > 0 && mCameraPreviewFrame.getMeasuredHeight() > 0) {
			updateCropRegion();
		} else {
			ViewTreeObserver cameraPreviewFrameObserver = mCameraPreviewFrame.getViewTreeObserver();
			cameraPreviewFrameObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@SuppressLint("NewApi")
				@SuppressWarnings("deprecation")
				@Override
				public void onGlobalLayout() {
					ViewTreeObserver obs = mCameraPreviewFrame.getViewTreeObserver();
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						obs.removeOnGlobalLayoutListener(this);
					} else {
						obs.removeGlobalOnLayoutListener(this);
					}
					Log.d("dicecam",
							"onGlobalLayout - " + mCameraPreviewFrame.getMeasuredWidth() + " x " + mCameraPreviewFrame.getMeasuredHeight());
					updateCropRegion();
				}
			});
		}
	}

	private synchronized void _stopCameraPreview() {

		if (mCamera != null) {

			// 2.1-> if (mNoEffectCameraPreview != null) {
			// mNoEffectCameraPreview.setCamera(null);
			// mNoEffectCameraPreview.getHolder().removeCallback(mNoEffectCameraPreview);
			// try {
			// mCamera.setPreviewDisplay(null);
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// 2.1<- }

			if (mDummySurfaceView != null) {
				try {
					mCamera.stopPreview();
					mCamera.setPreviewDisplay(null);
				} catch (IOException e) {
					e.printStackTrace();
				}
				mCameraPreviewFrame.removeView(mDummySurfaceView);
				mDummySurfaceView = null;
			}

			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}

		if (mEngine != null) {
			mEngine.clearDrawQueue();
		}
	}

	// private synchronized void releaseCameraPreview() {
	// if ( mGLSurfaceView != null ) {
	// mGLSurfaceView.onPause();
	// }
	//
	// if ( mCamera != null ) {
	// mCamera.stopPreview();
	// }
	//
	// if ( mEngine != null ) {
	// if ( mCamera != null ) {
	// mEngine.releaseCamera(mCamera);
	// }
	// mEngine.release();
	// mEngine = null;
	// }
	//
	// if ( mCamera != null ) {
	// mCamera.setPreviewCallback(null);
	// mCamera.release();
	// mCamera = null;
	// }
	// }

	@SuppressWarnings("unused")
	private synchronized void pauseCameraPreview() {
		if (mCamera != null) {
			mCamera.stopPreview();
		}
	}

	private synchronized void resumeCameraPreview() {
		if (mCamera == null) {
			startPreview();
		} else {
			mCamera.startPreview();
		}
	}

	private synchronized Camera getCameraInstance() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		// int availableWidth = mCameraPreviewFrame.getMeasuredWidth();
		// int availableHeight = mCameraPreviewFrame.getMeasuredHeight();
		// if ( availableWidth < 1 || availableHeight < 1 ) {
		// availableWidth = displaymetrics.widthPixels;
		// availableHeight = displaymetrics.heightPixels;
		// Log.d("dicecam", "mCameraPreviewFrame is NOT meatured!!!");
		// }

		int availableWidth = displaymetrics.widthPixels;
		int availableHeight = displaymetrics.heightPixels - getInitialBottomToolbarHeight();
		Log.d("dicecam", "initial bottomToolbarHeight: " + getInitialBottomToolbarHeight());

		Log.d("dicecam", "getting camera instance for availableSize: " + availableWidth + ", " + availableHeight);
		return getCameraInstance(availableWidth, availableHeight, this);
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private static Camera getDefaultCamera(int position) {

		int mNumberOfCameras = Camera.getNumberOfCameras();

		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		for (int i = 0; i < mNumberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == position) {
				return Camera.open(i);
			}
		}
		return null;
	}

	public static Camera getRearCameraInstance() {
		return getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
	}

	public static Camera getFrontCameraInstance() {
		return getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public Camera getCameraInstance(int perferredMaximumWidth, int perferredMaximumHeight, final Activity context) {
		@SuppressWarnings("unused")
		boolean isFront = false;
		Camera c = null;
		int mNumberOfCameras = 1;
		if (Build.VERSION.SDK_INT >= 9)
			mNumberOfCameras = Camera.getNumberOfCameras();

		PackageManager pm = mContext.getPackageManager();

		isFrontCameraDefaultWhenIsOneCameraFlagTrue = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);

		try {
			if (Build.VERSION.SDK_INT == 8) {
				c = Camera.open();
			} else if (Build.VERSION.SDK_INT >= 9 && mNumberOfCameras == 1) {
				isOneCamera = true;
				Camera.CameraInfo info = new Camera.CameraInfo();
				Settings.getInstance().setCameraSelection(info.facing);
				c = Camera.open(info.facing);
			} else {
				try {
					int cameraId = Settings.getInstance().getCameraSelection();
					if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
						isFront = true;
					if (isFront)
						c = getFrontCameraInstance();
					else
						c = getRearCameraInstance();
				} catch (Exception e) {
					e.printStackTrace();
					c = Camera.open();
				}
			}

			Camera.Parameters parameters = c.getParameters();

			// DEBUG START
			if (BuildConfig.DEBUG) {
				List<Integer> possiblePreviewFormats = parameters.getSupportedPreviewFormats();
				for (Integer format : possiblePreviewFormats) {
					Log.d("dicecam", "*Preview Format possibles: " + format + " (" + ImageFormat.NV21 + ")");
				}

				List<Integer> possiblePictureFormats = parameters.getSupportedPictureFormats();
				for (Integer format : possiblePictureFormats) {
					Log.d("dicecam", "*Picture Format possibles: " + format + " (" + ImageFormat.JPEG + ")");
				}

				List<Camera.Size> possiblePreviewSizes = parameters.getSupportedPreviewSizes();
				for (Camera.Size size : possiblePreviewSizes) {
					Log.d("dicecam", "-Preview possibles: " + size.width + ", " + size.height);
				}

				List<Camera.Size> possiblePictureSizes = parameters.getSupportedPictureSizes();
				for (Camera.Size size : possiblePictureSizes) {
					Log.d("dicecam", "+Picture possibles: " + size.width + ", " + size.height);
				}
				List<String> possibleFocusModes = parameters.getSupportedFocusModes();
				if (possibleFocusModes != null && possibleFocusModes.size() > 0) {
					for (String focusMode : possibleFocusModes) {
						Log.d("dicecam", "%Focus Mode: " + focusMode);
					}
				}
			}
			// DEBUG FINISH

			// set focus mode
			List<String> possibleFocusModes = parameters.getSupportedFocusModes();
			if (possibleFocusModes != null && possibleFocusModes.size() > 0) {
				for (String focusMode : possibleFocusModes) {
					if (focusMode.equals(Parameters.FOCUS_MODE_AUTO)) {
						parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
						Log.d("dicecam", "% SET Focus Mode: " + focusMode);
					}
				}
			}

			// set auto exposure lock
			if (Build.VERSION.SDK_INT >= 14) {
				Log.d("dicecam", "isAutoExposureLockSupported: " + parameters.isAutoExposureLockSupported());
				if (parameters.isAutoExposureLockSupported()) {
					parameters.setAutoExposureLock(false);
				}
			}

			// exposure area
			if (Build.VERSION.SDK_INT >= 14) {
				try {
					List<Camera.Area> possibleMeteringAreas = parameters.getMeteringAreas();
					if (possibleMeteringAreas != null && possibleMeteringAreas.size() > 0) {
						for (Area area : possibleMeteringAreas) {
							Log.d("dicecam", "possibleMeteringArea: " + area);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// set picture format
			parameters.setPictureFormat(ImageFormat.JPEG);

			// set preview format
			Log.d("dicecam", "skipYUV: " + NakedFootPreViewCallBack.skipYUV());
			if (NakedFootPreViewCallBack.skipYUV()) {
				NakedFootPreViewCallBack.setPreviewFormat(ImageFormat.NV21); // YCbCr_420_SP
			} else {
				NakedFootPreViewCallBack.setPreviewFormat(ImageFormat.NV21); // YCbCr_420_SP
			}
			parameters.setPreviewFormat(NakedFootPreViewCallBack.getPreviewFormat());
			Log.d("dicecam", "chosen Format: " + NakedFootPreViewCallBack.getPreviewFormat());

			// choose previewSize
			int[] chosenPreviewSize = chooseOptimumSize(parameters.getSupportedPreviewSizes(), perferredMaximumWidth,
					perferredMaximumHeight);
			choosePreviewSize(chosenPreviewSize[0], chosenPreviewSize[1]);

			// choose pictureSize
			int[] chosenPictureSize = chooseOptimumSize(parameters.getSupportedPictureSizes(), perferredMaximumWidth,
					perferredMaximumHeight);
			chosenPictureWidth = chosenPictureSize[0];
			chosenPictureHeight = chosenPictureSize[1];

			if (chosenPreviewWidth > 0 && chosenPreviewHeight > 0) {
				parameters.setPreviewSize(chosenPreviewHeight, chosenPreviewWidth);
			}

			if (chosenPictureWidth > 0 && chosenPictureHeight > 0) {
				parameters.setPictureSize(chosenPictureHeight, chosenPictureWidth);
			}

			// @see
			// http://stackoverflow.com/questions/5788993/use-android-camera-without-surface-view
			// SurfaceView view = new SurfaceView(this);
			// c.setPreviewDisplay(view.getHolder());
			// c.setPreviewDisplay(mGLSurfaceView.getHolder());
			// int bufferSize = chosenPreviewSize[0]* chosenPreviewSize[1] *
			// ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8;
			// final int NUM_CAMERA_PREVIEW_BUFFERS = 4;
			// for (int i = 0; i < NUM_CAMERA_PREVIEW_BUFFERS; i++) {
			// byte [] cameraBuffer = new byte[bufferSize];
			// c.addCallbackBuffer(cameraBuffer);
			// }
			//

			// Rotate Camera
			setCameraOrientation(c, parameters);

			Log.d("dicecam", "chosen Preview size: " + chosenPreviewHeight + ", " + chosenPreviewWidth);
			Log.d("dicecam", "chosen Picture size: " + chosenPictureHeight + ", " + chosenPictureWidth);
			Log.d("dicecam", "maxPictureWidth: " + PerformanceHelper.getMaxPictureWidth());

			// DEBUG - print fps
			if (BuildConfig.DEBUG) {
				debugPrintFPS(parameters);
			}
			// DEBUG - print fps FINISH

			c.setParameters(parameters);

		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
			e.printStackTrace();
			// this.recreate();
			try {
				c = Camera.open();
				Camera.CameraInfo info = new Camera.CameraInfo();
				Settings.getInstance().setCameraSelection(info.facing);
			} catch (Exception e2) {
				e.printStackTrace();
				try {
					c = Camera.open(0);
					Settings.getInstance().setCameraSelection(0);
				} catch (Exception e3) {
					e.printStackTrace();
					try {
						c = Camera.open(1);
						Settings.getInstance().setCameraSelection(1);
					} catch (Exception e4) {
						e.printStackTrace();
						// TODO: handle exception
						UserInterfaceUtil.displayError(this, getResources().getString(R.string.camera_error));
						finish();
					}
				}
			}
		}
		return c; // returns null if camera is unavailable
	}

	private static final String CAMERA_PARAM_ORIENTATION = "orientation";
	@SuppressWarnings("unused")
	private static final String CAMERA_PARAM_LANDSCAPE = "landscape";
	private static final String CAMERA_PARAM_PORTRAIT = "portrait";

	private void setCameraOrientation(Camera c, Camera.Parameters cameraParams) {

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) { // for 2.1 and
																	// before
			cameraParams.set(CAMERA_PARAM_ORIENTATION, CAMERA_PARAM_PORTRAIT);
		} else { // for 2.2 and later
			c.setDisplayOrientation(90);
		}

	}

	public int getDeviceDefaultOrientation() {

		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		Configuration config = getResources().getConfiguration();

		int rotation = windowManager.getDefaultDisplay().getRotation();
		String rotationString = "Unknown";
		switch (rotation) {
		case Surface.ROTATION_0:
			rotationString = "ROTATION_0";
			break;
		case Surface.ROTATION_180:
			rotationString = "ROTATION_180";
			break;
		case Surface.ROTATION_90:
			rotationString = "ROTATION_90";
			break;
		case Surface.ROTATION_270:
			rotationString = "ROTATION_270";
			break;
		}

		String orientationString = "Unknown";
		switch (config.orientation) {
		case Configuration.ORIENTATION_LANDSCAPE:
			orientationString = "LANDSCAPE";
			break;
		case Configuration.ORIENTATION_PORTRAIT:
			orientationString = "PORTRAIT";
			break;
		case Configuration.ORIENTATION_SQUARE:
			orientationString = "SQUARE";
			break;
		}

		if (((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && config.orientation == Configuration.ORIENTATION_LANDSCAPE)
				|| ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) && config.orientation == Configuration.ORIENTATION_PORTRAIT)) {
			return Configuration.ORIENTATION_LANDSCAPE;
		} else {
			return Configuration.ORIENTATION_PORTRAIT;
		}
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void debugPrintFPS(Camera.Parameters parameters) {
		if (android.os.Build.VERSION.SDK_INT < 9) {
			List<Integer> fpsrates = parameters.getSupportedPreviewFrameRates();
			for (int i = 0; i < fpsrates.size(); i++) {
				Integer fps = fpsrates.get(i);
				Log.d("dicecam", i + " fps= " + fps.toString());
			}
			int frate = parameters.getPreviewFrameRate();
			Log.d("dicecam", "frame rate: " + frate);
		} else {
			List<int[]> fpslist = parameters.getSupportedPreviewFpsRange();
			for (int i = 0; i < fpslist.size(); i++) {
				Log.d("dicecam", i + " fps= " + fpslist.get(i)[Camera.Parameters.PREVIEW_FPS_MIN_INDEX] + " ~ "
						+ fpslist.get(i)[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
			}
			int[] fpsrange = new int[2];
			parameters.getPreviewFpsRange(fpsrange);
			Log.d("dicecam", "fps: " + fpsrange[0] + " ~ " + fpsrange[1]);
		}
	}

	private int[] chooseOptimumSize(List<Size> supportedSizes, int preferredMaximumWidth, int preferredMaximumHeight) {
		int chosenWidth = 0;
		int chosenHeight = 0;
		float chosenRatio = 0.f;

		int longerLength = 1;
		int shorterLength = 1;

		if (preferredMaximumWidth > preferredMaximumHeight) {
			longerLength = preferredMaximumWidth;
			shorterLength = preferredMaximumHeight;
		} else {
			longerLength = preferredMaximumHeight;
			shorterLength = preferredMaximumWidth;
		}

		float resolutionRatio = (float) longerLength / (float) shorterLength;
		resolutionRatio = 1.333333f;

		Log.d("dicecam", "prefer Max: " + preferredMaximumWidth + ", " + preferredMaximumHeight);
		Log.d("dicecam", "resolutionRatio: " + resolutionRatio + " - (" + longerLength + ", " + shorterLength + ")");

		if (supportedSizes == null || supportedSizes.size() < 1)
			return new int[] { 0, 0 };

		for (Camera.Size possibleSize : supportedSizes) {
			int possibleLongerLength = 1;
			int possibleShorterLength = 1;

			if (possibleSize.width > PerformanceHelper.getMaxPictureWidth() || possibleSize.height > PerformanceHelper.getMaxPictureWidth())
				continue;

			if (possibleSize.width > possibleSize.height) {
				possibleLongerLength = possibleSize.width;
				possibleShorterLength = possibleSize.height;
			} else {
				possibleLongerLength = possibleSize.height;
				possibleShorterLength = possibleSize.width;
			}

			float possibleResolutionRatio = (float) possibleLongerLength / (float) possibleShorterLength;

			Log.d("dicecam", "possibleResolutionRatio: " + possibleResolutionRatio);
			Log.d("dicecam", "possibleSize: " + possibleSize.width + ", " + possibleSize.height + "(" + possibleResolutionRatio + ")"
					+ " - chosen: " + chosenWidth + ", " + chosenHeight + "("
					+ (chosenHeight > 0 ? (float) chosenWidth / (float) chosenHeight : 0) + ")");

			// if ((possibleResolutionRatio - resolutionRatio) < 0.1 &&
			// (possibleSize.width > chosenWidth || possibleSize.height >
			// chosenHeight)) {
			if (Math.abs(possibleResolutionRatio - resolutionRatio) < 0.1
					&& (possibleSize.width > chosenWidth || possibleSize.height > chosenHeight)) {
				if (chosenRatio > 0.f) {
					if (Math.abs(chosenRatio - resolutionRatio) < Math.abs(possibleResolutionRatio - resolutionRatio))
						continue;
				}
				chosenWidth = possibleSize.width;
				chosenHeight = possibleSize.height;
				chosenRatio = possibleResolutionRatio;
			}
		}

		if (chosenWidth == 0 || chosenHeight == 0) {
			// If everything did not match 4:3 ratio

			for (Camera.Size possibleSize : supportedSizes) {
				if (chosenWidth < possibleSize.width || chosenHeight < possibleSize.height) {
					chosenWidth = possibleSize.width;
					chosenHeight = possibleSize.height;
				}
			}
		}

		int[] result = new int[2];
		result[0] = chosenHeight;
		result[1] = chosenWidth;
		return result;
	}

	@SuppressLint("InlinedApi")
	private void flipCamera() {
		if (isFrontCameraAvailable() == false)
			return;

		stopCameraPreview();

		if (Settings.getInstance().getCameraSelection() == Camera.CameraInfo.CAMERA_FACING_BACK) {
			Settings.getInstance().setCameraSelection(Camera.CameraInfo.CAMERA_FACING_FRONT);
		} else {
			Settings.getInstance().setCameraSelection(Camera.CameraInfo.CAMERA_FACING_BACK);
		}

		startPreview();

		// if ( mEngine != null ) {
		// updateLensAttributes(mEngine.getCurrentLens());
		// }
	}

	private void stopCollageButtonTouched() {
		releaseCollageTakingMode();
		enableButtonsAfterTakingPhoto();
	}

	private int getCollageCountTaken() {
		return (collagePictureBitmaps == null) ? 0 : collagePictureBitmaps.size();
	}

	private int getCollageCountTotal() {
		return CollageProvider.getTotalNumberOfCollagePictures(collageStatus);
	}

	@Override
	public void onCollageImageAdded(int imageCount) {
		hideShutterView();

		if (mCaptureButton.isSelected() == false && Settings.getInstance().getInterval() > 0)
			return;
		if (imageCount < getCollageCountTotal()) {

			int interval = Settings.getInstance().getInterval();
			if (interval > 0) {
				mArcProgressView.playProgress(interval);
				mImageComposition.delayedTask(new Runnable() {
					@Override
					public void run() {
						if (mCaptureButton.isSelected() == false)
							return;

						takeRenderedPicture();
					}
				}, interval);
			} else {
				mCaptureButton.setSelected(false);
				mCaptureButton.setEnabled(true);
			}

		} else {
			finishCollage();
		}
	}

	private void onCaptureButtonClick(View view) {
		if (mCurrentLensIsLocked == true) {
			callPurchaseActivity();
			return;
		}

		if (mCaptureButton.isSelected() == true) {

			if (mCountDownBegan) {
				// Canceling the timer
				mCountDownTimer.cancel();
				mCountDownBegan = false;
				mCurrentTimerValue = 0;
				updateTimerHUDTextView();

				mCaptureButton.setSelected(false);
				hideShutterView();
				enableButtonsAfterTakingPhoto();

			} else {
				// Canceling collage task
				stopCollageButtonTouched();
			}

		} else {
			takingShot();
		}
	}

	private void takingShot() {
		closeAllPanels();

		if (Settings.getInstance().getTimer() > 0) {
			disableButtonsForTakingPhotoExceptCaptureButton();
			mCaptureButton.setSelected(true);
		} else {
			disableButtonsForTakingPhoto();
		}

		final long delayInMillis = Settings.getInstance().getTimer() * 1000;

		if (delayInMillis < 1) {
			if (collageStatusIsForSinglePicture()) {
				takeSinglePicture();
			} else {
				// collage
				if (collagePictureBitmaps != null && collagePictureBitmaps.size() > 0) {
					takeRenderedPicture();
				} else {
					startCollage();
				}
			}

			mCountDownBegan = false;
		} else {
			mCountDownTimer = new CountDownTimer(delayInMillis, 250) {

				@Override
				public void onTick(long arg0) {
					Log.d("timer", "onTick: " + arg0);
					int newValue = (int) (1 + arg0 / 1000);
					displayCurrentTimerValue(newValue);
				}

				@Override
				public void onFinish() {
					Log.d("timer", "onFinish: ");
					mTimerHUDTextView.clearAnimation();
					mTimerHUDTextView.setVisibility(View.INVISIBLE);

					if (collageStatusIsForSinglePicture()) {
						takeSinglePicture();
					} else {
						// collage
						if (collagePictureBitmaps != null && collagePictureBitmaps.size() > 0) {
							takeRenderedPicture();
						} else {
							startCollage();
						}
					}

					mCountDownBegan = false;
				}
			};

			Log.d("timer", "start: " + delayInMillis);
			displayCurrentTimerValue((int) (delayInMillis / 1000));
			mCountDownTimer.start();
			mCountDownBegan = true;
		}
	}

	private synchronized void displayCurrentTimerValue(int newValue) {
		if (newValue == mCurrentTimerValue) {
			return;
		}
		mCurrentTimerValue = newValue;
		Log.d("timer", "display value: " + mCurrentTimerValue);

		mTimerHUDTextView.clearAnimation();
		mTimerHUDTextView.setText(String.format("%d", mCurrentTimerValue));
		mTimerHUDTextView.setVisibility(View.VISIBLE);

		AnimationSet animations = new AnimationSet(true);
		animations.setFillBefore(true);
		animations.setFillAfter(true);

		if (orientationDegree != 0.f) {
			RotateAnimation rotation = getTimerHUDTextViewRotationAnimation();
			rotation.setFillEnabled(true);
			animations.addAnimation(rotation);
		}

		ScaleAnimation scaleDown = new ScaleAnimation(2.f, 1.f, 2.f, 1.f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT,
				0.5f);
		scaleDown.setDuration(100);

		AlphaAnimation alphaUp = new AlphaAnimation(0.f, 1.f);
		alphaUp.setDuration(100);

		animations.addAnimation(scaleDown);
		animations.addAnimation(alphaUp);

		mTimerHUDTextView.startAnimation(animations);
	}

	private boolean collageStatusIsForSinglePicture() {
		switch (collageStatus) {
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_SINGLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_SINGLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_SINGLE:
			return true;

		default:
			return false;
		}
	}

	protected void toggleCollageSelector() {
		if (mFrameSelection.isVisible() == false) {
			closeAllPanels();
		}

		mFrameSelection.onFrameSelectionButtonClick(mFrameButton);
	}

	protected void toggleVignette() {
		DicecamLens lens = mEngine.getCurrentLens();
		boolean newValue = !lens.isUseVignette();

		lens.setUseVignette(newValue);
		lens.setNeedsReload();

		Settings.getInstance().setUseVignette(newValue);
		updateHeadButtons();
	}

	protected void toggleBlur() {
		DicecamLens lens = mEngine.getCurrentLens();
		boolean newValue = !lens.isUseBlur();

		lens.setUseBlur(newValue);

		lens.setNeedsHighlightBlurGuide();
		lens.setNeedsReload();

		Settings.getInstance().setUseBlur(newValue);
		updateHeadButtons();
	}

	public void insertEdgeFrame() {
		if (mFramingOption.getVisibility() != View.VISIBLE) {
			closeAllPanels();
			showEdgeOption();
		} else {
			closeEdgeOptionView();
		}

		updateHeadButtons();
	}

	protected void closeEdgeOptionView() {
		mFramingOption.setVisibility(View.INVISIBLE);
		mShutterView.setVisibility(View.INVISIBLE);
		mFrameButton.setSelected(false);
	}

	protected void showEdgeOption() {

		toggleFrameWidthButton(Settings.getInstance().getFrameWidth());

		Button frame_width_none = (Button) findViewById(R.id.frame_width_none);
		Button frame_width_light = (Button) findViewById(R.id.frame_width_light);
		Button frame_width_bold = (Button) findViewById(R.id.frame_width_bold);

		if (isTablet) {
			LinearLayout frame_width_1 = (LinearLayout) findViewById(R.id.fram_width_1);
			LinearLayout frame_width_2 = (LinearLayout) findViewById(R.id.fram_width_2);
			LinearLayout frame_color_1 = (LinearLayout) findViewById(R.id.frame_color_1);
			LinearLayout frame_color_2 = (LinearLayout) findViewById(R.id.frame_color_2);
			int tallerHeight_1 = UserInterfaceUtil.dp2px(55, frame_color_1);
			int tallerHeight_2 = UserInterfaceUtil.dp2px(45, frame_color_2);

			ViewGroup.LayoutParams params = frame_width_1.getLayoutParams();
			params.height = tallerHeight_1;
			frame_width_1.setLayoutParams(params);
			params = frame_width_1.getLayoutParams();
			params.height = tallerHeight_2;
			frame_width_2.setLayoutParams(params);

			params = frame_color_1.getLayoutParams();
			params.height = tallerHeight_1;
			frame_color_1.setLayoutParams(params);
			params = frame_color_2.getLayoutParams();
			params.height = tallerHeight_2;
			frame_color_2.setLayoutParams(params);
		}

		frame_width_none.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleFrameWidthButton(Settings.FRAME_WIDTH_NONE);
			}
		});

		frame_width_light.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleFrameWidthButton(Settings.FRAME_WIDTH_LIGHT);
			}
		});

		frame_width_bold.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleFrameWidthButton(Settings.FRAME_WIDTH_BOLD);
			}
		});

		toggleFrameColorButton(Settings.getInstance().getFrameColor());

		Button frame_color_white = (Button) findViewById(R.id.frame_color_white);
		Button frame_color_black = (Button) findViewById(R.id.frame_color_black);

		frame_color_white.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleFrameColorButton(Settings.FRAME_COLOR_WHITE);
			}
		});

		frame_color_black.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleFrameColorButton(Settings.FRAME_COLOR_BLACK);

			}
		});

		int marginInPx = getBottomToolbarHeight();

		FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mFramingOption.getLayoutParams();

		if (isTablet) { // height is fixed. later on, It will be calculated by
						// screen size
			layoutParams.height = UserInterfaceUtil.dp2px(115, mShutterView);
		}

		layoutParams.setMargins(0, 0, 0, marginInPx);

		mFramingOption.setLayoutParams(layoutParams);
		mFramingOption.setVisibility(View.VISIBLE);

		setViewBackgroundDrawable(mShutterView, getResources().getDrawable(R.color.shutter_close_color));
		mShutterView.setVisibility(View.VISIBLE);

		mFrameButton.setSelected(true);
	}

	void toggleFrameWidthButton(int frameWidthType) {

		Button frame_width_none = (Button) findViewById(R.id.frame_width_none);
		Button frame_width_light = (Button) findViewById(R.id.frame_width_light);
		Button frame_width_bold = (Button) findViewById(R.id.frame_width_bold);

		frame_width_none.setTextColor(OPTION_TXT_COLOR);
		frame_width_none.setBackgroundColor(Color.TRANSPARENT);

		frame_width_light.setTextColor(OPTION_TXT_COLOR);
		frame_width_light.setBackgroundColor(Color.TRANSPARENT);

		frame_width_bold.setTextColor(OPTION_TXT_COLOR);
		frame_width_bold.setBackgroundColor(Color.TRANSPARENT);

		Settings.getInstance().setFrameWidth(frameWidthType);

		switch (frameWidthType) {
		case Settings.FRAME_WIDTH_NONE: {
			frame_width_none.setTextColor(OPTION_TXT_COLOR_SELECTED);
			UserInterfaceUtil.setBackground(frame_width_none, getResources().getDrawable(R.drawable.left_rounded_square_white));

			break;
		}

		case Settings.FRAME_WIDTH_LIGHT: {
			frame_width_light.setTextColor(OPTION_TXT_COLOR_SELECTED);
			UserInterfaceUtil.setBackground(frame_width_light, getResources().getDrawable(R.drawable.square_border));

			break;
		}

		case Settings.FRAME_WIDTH_BOLD: {
			frame_width_bold.setTextColor(OPTION_TXT_COLOR_SELECTED);
			UserInterfaceUtil.setBackground(frame_width_bold, getResources().getDrawable(R.drawable.right_rounded_square_white));

			break;
		}
		}

	}

	void toggleFrameColorButton(int frameColorType) {
		Button frame_color_white = (Button) findViewById(R.id.frame_color_white);
		Button frame_color_black = (Button) findViewById(R.id.frame_color_black);

		frame_color_white.setTextColor(OPTION_TXT_COLOR);
		frame_color_white.setBackgroundColor(Color.TRANSPARENT);

		frame_color_black.setTextColor(OPTION_TXT_COLOR);
		frame_color_black.setBackgroundColor(Color.TRANSPARENT);

		Settings.getInstance().setFrameColor(frameColorType);

		switch (frameColorType) {

		case Settings.FRAME_COLOR_BLACK: {
			frame_color_black.setTextColor(OPTION_TXT_COLOR_SELECTED);
			UserInterfaceUtil.setBackground(frame_color_black, getResources().getDrawable(R.drawable.left_rounded_square_white));

			break;
		}

		case Settings.FRAME_COLOR_WHITE: {
			frame_color_white.setTextColor(OPTION_TXT_COLOR_SELECTED);
			UserInterfaceUtil.setBackground(frame_color_white, getResources().getDrawable(R.drawable.right_rounded_square_white));

			break;
		}

		}
	}

	protected void toggleTimerOptionView() {

		if (mTimingOption.getVisibility() != View.VISIBLE) {
			closeAllPanels();
			openTimerOptionView();
		}

		else {
			closeTimerOptionView();
		}

		updateHeadButtons();
	}

	protected void closeTimerOptionView() {
		mTimingOption.setVisibility(View.INVISIBLE);
		mShutterView.setVisibility(View.INVISIBLE);
		mTimerButton.setSelected(false);
	}

	protected void openTimerOptionView() {
		mTimerSeekbar.setProgress(Settings.getInstance().getTimer() * 10);
		mIntervalSeekbar.setProgress(Settings.getInstance().getInterval() / 20);

		int marginInPx = getBottomToolbarHeight();

		FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mTimingOption.getLayoutParams();
		layoutParams.setMargins(0, 0, 0, marginInPx);
		if (isTablet) { // height is fixed. later on, It will be calculated by
						// screen size
			layoutParams.height = UserInterfaceUtil.dp2px(115, mShutterView);

			LinearLayout timerOpt1 = (LinearLayout) findViewById(R.id.timer_option_1);
			LinearLayout timerOpt2 = (LinearLayout) findViewById(R.id.timer_option_2);
			int tallerHeight_1 = UserInterfaceUtil.dp2px(52.5f, timerOpt1);

			ViewGroup.LayoutParams params = timerOpt1.getLayoutParams();
			params.height = tallerHeight_1;
			timerOpt1.setLayoutParams(params);

			params = timerOpt2.getLayoutParams();
			params.height = tallerHeight_1;
			timerOpt2.setLayoutParams(params);
		}
		mTimingOption.setLayoutParams(layoutParams);
		mTimingOption.setVisibility(View.VISIBLE);

		setViewBackgroundDrawable(mShutterView, getResources().getDrawable(R.color.shutter_close_color));
		mShutterView.setVisibility(View.VISIBLE);

		mTimerButton.setSelected(true);
	}

	protected void showAlbum() {
		Intent albumActivity = new Intent(mContext, AlbumActivity.class);
		startActivity(albumActivity);
	}

	public void rollDice(int i) {
		closeAllPanels();

		Settings.getInstance().setUseVignette(false);
		Settings.getInstance().setUseBlur(false);

		try {
			setLens(LensCenter.defaultCenter().rollDiceFilter(i));
		} catch (Exception e) {
			e.printStackTrace();
		}

		updateHeadButtons();
	}

	protected void favoritesVisiable(boolean tf) {
		if(tf) {
		closeAllPanels();
		mFavoriteView.show();
		} else {
			mFavoriteView.hide();
		}
	}

	/**
	 * @return true : Front facing camera is activated. false : others
	 */
	public boolean isFrontCameraActivated() {
		if (isOneCamera && isFrontCameraDefaultWhenIsOneCameraFlagTrue)
			return true;
		if (isFrontCameraAvailable() == false)
			return false;

		if (Settings.getInstance().getCameraSelection() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			return true;
		}
		return false;
	}

	/**
	 * @return true : Front facing camera is available. false : Front facing
	 *         camera is not available.
	 */
	@SuppressLint("NewApi")
	public boolean isFrontCameraAvailable() {
		if (isOneCamera && isFrontCameraDefaultWhenIsOneCameraFlagTrue)
			return true;
		if (Build.VERSION.SDK_INT < 9 || Camera.getNumberOfCameras() < 2)
			return false;

		int cameraCount = 0;
		boolean isFrontCameraAvailable = false;
		cameraCount = Camera.getNumberOfCameras();

		while (cameraCount > 0) {
			CameraInfo cameraInfo = new CameraInfo();
			cameraCount--;
			Camera.getCameraInfo(cameraCount, cameraInfo);

			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
				isFrontCameraAvailable = true;
				break;
			}

		}

		return isFrontCameraAvailable;
	}

	public boolean shouldStoreEXIF() {
		return (collageStatus == CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_SINGLE
				|| collageStatus == CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_SINGLE || collageStatus == CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_SINGLE);
	}

	public synchronized void setLens(DicecamLens lens) {
		updateLensAttributes(lens);
		showCurrentLensTitleAfter(10);
		
		mEngine.setLens(lens);

		updateCaptureButtonLocked(lens);
		

		Settings.getInstance().setCurrentLensID(lens.getID());
	}

	private void updateLensAttributes(DicecamLens lens) {
		if (lens == null) {
			return;
		}

		lens.setFilterIntensity(Settings.getInstance().getLensIntensity());
		lens.setUseVignette(isUseVignette());
		lens.setUseBlur(isUseBlur());
		if (mGLSurfaceView != null) {
			setLensCenter(lens, mGLSurfaceView.eventDispatcher().getCenterX(), mGLSurfaceView.eventDispatcher().getCenterY());
			setLensRadius(lens, mGLSurfaceView.eventDispatcher().getRadius());
		}
		if (isUseBlur()) {
			lens.setNeedsHighlightBlurGuide();
		}

		lens.setCropRegion(mFrameSelection.getCropRegion());
	}

	private Runnable delayedShowCurrentLensTitle = null;

	private synchronized void showCurrentLensTitleAfter(long afterMillis) {
		if (delayedShowCurrentLensTitle != null) {
			mMainHandler.removeCallbacks(delayedShowCurrentLensTitle);
		}
		delayedShowCurrentLensTitle = new Runnable() {
			@Override
			public void run() {
				showCurrentLensTitle((PerformanceHelper.getAnimationLevel() > PerformanceHelper.ANIMATION_LEVEL_LOW));
				delayedShowCurrentLensTitle = null;
			}
		};
		mMainHandler.postDelayed(delayedShowCurrentLensTitle, afterMillis);
	}

	// DICE
	private synchronized void showCurrentLensTitle(final boolean animate) {

		if (mEngine == null)
			return;
		DicecamLens lens = mEngine.getCurrentLens();
		if (lens == null)
			return;

		UserInterfaceUtil.updateTextViewForLensTitle(mFilterTitleTextView);
		mFilterTitleTextView.setText(lens.getDisplayTitle());
		mFilterTitleTextView.clearAnimation();
		mFilterTitleTextView.setVisibility(View.VISIBLE);

		updateTimerHUDTextView();

		if (animate) {
			ScaleAnimation scaleDown = new ScaleAnimation(2.f, 1.f, 2.f, 1.f, Animation.RELATIVE_TO_PARENT, 0.5f,
					Animation.RELATIVE_TO_PARENT, 0.5f);
			scaleDown.setDuration(100);

			AlphaAnimation alphaUp = new AlphaAnimation(0.f, 1.f);
			alphaUp.setDuration(100);

			AnimationSet animations = new AnimationSet(false);
			animations.addAnimation(scaleDown);
			animations.addAnimation(alphaUp);
			animations.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					mFilterTitleTextView.setVisibility(View.VISIBLE);
				}
			});
			mFilterTitleTextView.startAnimation(animations);

			hideCurrentLensTitleAfter(1000);
		} else {

			hideCurrentLensTitleAfter(1000 - 100);
		}

	}

	private Runnable delayedHideCurrentLensTitle = null;
	

	private synchronized void hideCurrentLensTitleAfter(long afterMillis) {

		if (delayedHideCurrentLensTitle != null) {
			mMainHandler.removeCallbacks(delayedHideCurrentLensTitle);
		}

		delayedHideCurrentLensTitle = new Runnable() {
			@Override
			public void run() {
				hideCurrentLensTitle((PerformanceHelper.getAnimationLevel() > PerformanceHelper.ANIMATION_LEVEL_NORMAL));
				delayedHideCurrentLensTitle = null;
			}
		};
		mMainHandler.postDelayed(delayedHideCurrentLensTitle, afterMillis);
	}

	private synchronized void hideCurrentLensTitle(final boolean animate) {
		mFilterTitleTextView.clearAnimation();
		if (animate) {
			ScaleAnimation scaleUp = new ScaleAnimation(1.f, 1.25f, 1.f, 1.25f, Animation.RELATIVE_TO_PARENT, 0.5f,
					Animation.RELATIVE_TO_PARENT, 0.5f);
			scaleUp.setDuration(100);

			AlphaAnimation alphaDown = new AlphaAnimation(1.f, 0.f);
			alphaDown.setDuration(100);

			AnimationSet animations = new AnimationSet(false);
			animations.addAnimation(scaleUp);
			animations.addAnimation(alphaDown);
			animations.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					mFilterTitleTextView.setVisibility(View.INVISIBLE);
					updateTimerHUDTextView();
				}
			});
			mFilterTitleTextView.startAnimation(animations);
		} else {
			mFilterTitleTextView.setVisibility(View.INVISIBLE);
			updateTimerHUDTextView();
		}
	}

	@SuppressLint("NewApi")
	private synchronized void showLensIntensityText() {
		if (mFavoriteView == null || mFavoriteView.lensIntensityControlView == null)
			return;

		if (delayedShowCurrentLensTitle != null) {
			mMainHandler.removeCallbacks(delayedShowCurrentLensTitle);
			delayedShowCurrentLensTitle = null;
		}

		if (delayedHideCurrentLensTitle != null) {
			mMainHandler.removeCallbacks(delayedHideCurrentLensTitle);
		}

		mFilterTitleTextView.clearAnimation();
		final float i = mFavoriteView.lensIntensityControlView.getIntensity();
		UserInterfaceUtil.updateTextViewForLensIntensity(mFilterTitleTextView);
		mFilterTitleTextView.setText(String.format("%2.0f%%", i * 100.f));
		mFilterTitleTextView.setVisibility(View.VISIBLE);

		UserInterfaceUtil.setAlpha(mFilterTitleTextView, 1.f);

		updateTimerHUDTextView();
	}

	private synchronized void hideLensIntensityText(final boolean animate) {
		if (mFavoriteView == null || mFavoriteView.lensIntensityControlView == null)
			return;

		mFilterTitleTextView.clearAnimation();
		final float i = mFavoriteView.lensIntensityControlView.getIntensity();
		mFilterTitleTextView.setText(String.format("%2.0f%%", i * 100.f));

		UserInterfaceUtil.setAlpha(mFilterTitleTextView, 0.f, animate, new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mFilterTitleTextView.setVisibility(View.INVISIBLE);
				updateTimerHUDTextView();
			}
		});
	}

	private void flickerShutterView() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				setViewBackgroundDrawable(mShutterView, getResources().getDrawable(R.color.shutter_taking_color));
				mShutterView.setVisibility(View.VISIBLE);

				mMainHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mShutterView.setVisibility(View.INVISIBLE);
					}
				}, 100);
			}
		});
	}

	private void showShutterView() {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			setViewBackgroundDrawable(mShutterView, getResources().getDrawable(R.color.shutter_taking_color));
			mShutterView.setVisibility(View.VISIBLE);
		} else {
			mMainHandler.post(new Runnable() {
				@Override
				public void run() {
					setViewBackgroundDrawable(mShutterView, getResources().getDrawable(R.color.shutter_taking_color));
					mShutterView.setVisibility(View.VISIBLE);
				}
			});
		}
	}

	private void hideShutterView() {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			mShutterView.setVisibility(View.INVISIBLE);
		} else {
			mMainHandler.post(new Runnable() {
				@Override
				public void run() {
					mShutterView.setVisibility(View.INVISIBLE);
				}
			});
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void setViewBackgroundDrawable(View targetView, Drawable drawable) {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			targetView.setBackground(drawable);
		} else {
			targetView.setBackgroundDrawable(drawable);
		}
	}

	public void setCollageStatus(int sFrame) {
		this.collageStatus = sFrame;
		updateCropRegion();
		updateHeadButtons();
	};

	private void updateCropRegion() {
		if (mEngine == null)
			return;
		updateCropRegion(mEngine, mEngine.getCurrentLens());
	}

	private void updateCropRegion(NakedFootPreViewCallBack engine, DicecamLens lens) {
		if (mCameraPreviewFrame.getWidth() < 1 || mCameraPreviewFrame.getHeight() < 1)
			return;

		mFrameSelection.calculateCropInformation(collageStatus, chosenPreviewWidth, chosenPreviewHeight);

		final float[] cropRegion = mFrameSelection.getCropRegion();
		lens.setCropRegion(cropRegion);

		final int layoutWidth = mFrameSelection.getCropWidth();
		final int layoutHeight = mFrameSelection.getCropHeight();

		mMainHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mGLSurfaceView != null) {
					FrameLayout.LayoutParams layoutParams = (LayoutParams) mGLSurfaceView.getLayoutParams();

					Log.d("dicecam", "cropSize(layoutSize): " + layoutWidth + ", " + layoutHeight);
					int maxWidth = adjustedPreviewWidth; // mCameraPreviewFrame.getWidth();
					int maxHeight = adjustedPreviewHeight; // mCameraPreviewFrame.getHeight();
					// if ( (maxWidth > 0 && maxWidth < layoutWidth) ||
					// (maxHeight > 0 && maxHeight < layoutHeight) ) {
					if (maxWidth > 0 && maxHeight > 0) {
						// fit
						float maxRatio = (float) maxWidth / (float) maxHeight;
						float layoutRatio = (float) layoutWidth / (float) layoutHeight;
						if (maxRatio > layoutRatio) {
							// l.w : l.h = n.w : n.h
							// l.w * n.h / l.h
							layoutParams.height = maxHeight;
							layoutParams.width = (int) ((float) layoutParams.height * layoutRatio);
						} else {
							layoutParams.width = maxWidth;
							layoutParams.height = (int) ((float) layoutParams.width / layoutRatio);
						}
					} else {
						layoutParams.width = layoutWidth;
						layoutParams.height = layoutHeight;
					}
					layoutParams.gravity = Gravity.CENTER;
					mGLSurfaceView.setLayoutParams(layoutParams);
					mGLSurfaceView.setVisibility(View.VISIBLE);
					Log.d("dicecam", "glSurfaceView resized: " + layoutParams.width + " x " + layoutParams.height + " (max: " + maxWidth
							+ " x " + maxHeight + ")");
				}

				// 2.1 -> if (mNoEffectCropFrameView != null) {
				// mNoEffectCropFrameView.setCropRegion(cropRegion);
				// 2.1 <- }
			}
		});

		// ViewGroup.LayoutParams layoutParams =
		// mGLSurfaceView.getLayoutParams();
		// layoutParams.width = engine.getPreferOutputWidth();
		// layoutParams.height = engine.getPreferOutputHeight();
		// mGLSurfaceView.setLayoutParams(layoutParams);
	}

	private void setLensCenter(DicecamLens lens, float x, float y) {
		lens.setBlurCenter(x, y);
	}

	private void setLensRadius(DicecamLens lens, float r) {
		lens.setBlurRadius(r);
	}

	@Override
	public void onBackPressed() {

		if (mFavoriteView.getVisibility() == View.VISIBLE) {
			mFavoriteView.onBackPressed();
		} else if (mFrameSelection.isVisible() == true) {
			mFrameSelection.makeInvisible();
		} else if (mFrameButton.isSelected() == true) {
			closeEdgeOptionView();
		} else if (mTimerButton.isSelected() == true) {
			closeTimerOptionView();
		} else {
			super.onBackPressed();
		}
	}

	private boolean isUseVignette() {
		return Settings.getInstance().getUseVignette();
	}

	private boolean isUseBlur() {
		return Settings.getInstance().getUseBlur();
	}

	private boolean isUseFrameBorder() {
		return (Settings.getInstance().getFrameWidth() != Settings.FRAME_WIDTH_NONE);
	}

	private boolean isUseTimer() {
		if (Settings.getInstance().getTimer() > 0)
			return true;
		int numberOfCollagePictures = CollageProvider.getTotalNumberOfCollagePictures(this.collageStatus);
		if (numberOfCollagePictures > 1) {
			return Settings.getInstance().getInterval() > 0 ? true : false;
		}
		return false;
	}

	@Override
	public boolean blurGestureEventEnabled() {
		return isUseBlur();
	}

	@Override
	public void centerChanged(float x, float y) {
		if (isUseBlur() == false)
			return;
		setLensCenter(mEngine.getCurrentLens(), x, y);
	}

	@Override
	public void radiusChanged(float r) {
		if (isUseBlur() == false)
			return;
		setLensRadius(mEngine.getCurrentLens(), r);
	}

	@Override
	public void gestureEventStarted() {
		if (isUseBlur() == false)
			return;
		DicecamLens lens = mEngine.getCurrentLens();
		lens.startBlurGesture();
	}

	@Override
	public void gestureEventFinished() {
		if (isUseBlur() == false)
			return;
		DicecamLens lens = mEngine.getCurrentLens();
		lens.finishBlurGesture();
	}

	@Override
	public void blurTouchViewTouchDown() {
	}

	@Override
	public void blurTouchViewTouchUp() {
		if (mCamera != null) {
			try {
				mCamera.autoFocus(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void lefeSwiping() {
		Log.i("hjh", "leftSwiping");
		if (mFavoriteView.selectNextLens() == false)
			return;
	}

	@Override
	public void rightSwiping() {
		Log.i("hjh", "rightSwiping");
		if (mFavoriteView.selectPreviousLens() == false)
			return;
	}

	public synchronized void showProgressBar() {
		mMainHandler.post(new Runnable() {
			@Override
			public void run() {
				mProgressBar.setProgress(0);
				mProgressBar.setVisibility(View.VISIBLE);
				mProgressCircle.setVisibility(View.VISIBLE);
			}
		});
	}

	public synchronized void setProgressBar(final int value) {
		mMainHandler.post(new Runnable() {
			@Override
			public void run() {
				mProgressBar.setProgress(value);
			}
		});
	}

	public void hideProgressBar() {
		mMainHandler.post(new Runnable() {
			@Override
			public void run() {
				mProgressBar.setVisibility(View.INVISIBLE);
				mProgressCircle.setVisibility(View.INVISIBLE);
			}
		});
	}

	public void showQuickView(final Uri imageUri, final String debugText, int i) {
		stopCameraPreview();
		
		this.mSingleRotationBugFix = false;
		if(i == 1 && !Settings.getInstance().getSaveAsPreview() && isFrontCameraActivated())	this.mSingleRotationBugFix = true;
		Log.i("Dicecam", "hjh : showQuickView() Uri" + imageUri);

		if (MediaStore.ACTION_IMAGE_CAPTURE.equals(action)) {
			Intent intent = new Intent();
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mImage_capture_intent_uri);
			intent.putExtra(Camera.ACTION_NEW_PICTURE, mImage_capture_intent_uri);
			intent.putExtra("com.android.camera.NEW_PICTURE", mImage_capture_intent_uri);
			this.setResult(Activity.RESULT_OK, intent);
			this.finish();
		} else {
			Intent quickViewActivity = new Intent(mContext, ViewAfterPictureTaken.class);
			quickViewActivity.putExtra(ViewAfterPictureTaken.EXTRA_KEY_FROM, BaseActivity.E_QUICK_VIEW_AFTER_CAPTURE);
			quickViewActivity.putExtra(ViewAfterPictureTaken.EXTRA_KEY_DISPLAY_AD, true);
			quickViewActivity.putExtra(ViewAfterPictureTaken.EXTRA_KEY_URI, imageUri.toString());
			if (debugText != null) {
				quickViewActivity.putExtra(ViewAfterPictureTaken.EXTRA_KEY_DEBUG_TEXT, debugText);
			}
			startActivity(quickViewActivity);
		}
	}

	public void showSavingError() {

		UserInterfaceUtil.displayError(this, this.getResources().getString(R.string.saving_error));

		mMainHandler.post(new Runnable() {
			@Override
			public void run() {

				releaseCollageTakingMode();

				hideProgressBar();
				startPreview();
				enableButtonsAfterTakingPhoto();
			}
		});
	}

	private void releaseCollageTakingMode() {
//		mOrientationListener.enable();
		clearCollageItems();
//		mCollageProvider.setCollageButton(mCollageButton, collageStatus);
		mCaptureButton.setSelected(false);
		hideShutterView();
	}

	private void startCollage() {
//		mOrientationListener.disable();

		disableButtonsForTakingPhotoExceptCaptureButton();

		mCaptureButton.setSelected(true);

		collagePictureBitmaps = new ArrayList<Bitmap>(CollageProvider.getTotalNumberOfCollagePictures(collageStatus));

		mImageComposition.setFlipHorizontal(isFrontCameraActivated() && (Settings.getInstance().getSaveAsPreview() == false));

		if (CollageProvider.individualImageShouldSupportOrientation(collageStatus)) {
			mImageComposition.setOrientation(ORIENTATION);
		} else {
			mImageComposition.setOrientation(ImageComposition.DICECAM_ORIENTATION_PORTRAIT);
		}

		setViewBackgroundDrawable(mShutterView, getResources().getDrawable(R.color.shutter_taking_color));

		takeRenderedPicture();
	}

	private void finishCollage() {

		disableButtonsForTakingPhoto();
		stopCameraPreview();
		showProgressBar();

		Thread pThread = new Thread(new Runnable() {

			private ImageComposition.ProcessCallback processCallback = new ImageComposition.ProcessCallback() {
				@Override
				public void onProcess(float process) {
					setProgressBar(0 + (int) (70.f * process));
				}
			};

			@Override
			public void run() {
				Bitmap combinedImage = null;

				combinedImage = mImageComposition.compose(collageStatus, collagePictureBitmaps, processCallback);

				clearCollageItems();

				setProgressBar(70);

				Uri savedUri = mFileManagement.saveBitmap(combinedImage, ImageComposition.DICECAM_ORIENTATION_NONE, shouldStoreEXIF(),
						false, false, getLastKnownLocationForPictureExif());

				setProgressBar(100);

				if (savedUri != null) {
					Log.i("Dicecam", "hjh DiceCamera showQuickView(), Collage" + savedUri);
					showQuickView(savedUri, null, 0);
				} else {
					showSavingError();
				}
			}
		});
		pThread.start();

	}

	protected void clearCollageItems() {
		if (collagePictureBitmaps != null) {
			for (int i = 0; i < collagePictureBitmaps.size(); i++) {
				Bitmap b = collagePictureBitmaps.get(i);
				b.recycle();
			}
			collagePictureBitmaps = null;
		}

		if (collagePictureURIs != null) {
			for (int i = 0; i < collagePictureURIs.size(); i++) {
				Uri u = collagePictureURIs.get(i);
				boolean isRemoved = FileManagement.removeFileAt(this, u.toString());
				Log.d("dicecam", "collage image file removed: " + isRemoved + " - " + u);
			}
			collagePictureURIs = null;
		}
	}

	private static final int HEAD_BUTTON_ENABLED_ALPHA = 255;
	private static final int HEAD_BUTTON_DISABLED_ALPHA = 91;

	private void updateHeadButtons() {
		setImageButtonAlpha(mVignetteButton, isUseVignette() ? HEAD_BUTTON_ENABLED_ALPHA : HEAD_BUTTON_DISABLED_ALPHA);
		setImageButtonAlpha(mBlurButton, isUseBlur() ? HEAD_BUTTON_ENABLED_ALPHA : HEAD_BUTTON_DISABLED_ALPHA);
		setImageButtonAlpha(mFrameButton, isUseFrameBorder() ? HEAD_BUTTON_ENABLED_ALPHA : HEAD_BUTTON_DISABLED_ALPHA);
		setImageButtonAlpha(mTimerButton, isUseTimer() ? HEAD_BUTTON_ENABLED_ALPHA : HEAD_BUTTON_DISABLED_ALPHA);
	}

	private void updateCaptureButtonLocked() {
		if (mEngine == null)
			return;
		DicecamLens currentLens = mEngine.getCurrentLens();
		if (currentLens == null)
			return;
		updateCaptureButtonLocked(currentLens);
	}

	private void updateCaptureButtonLocked(final DicecamLens lens) {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			mMainHandler.post(new Runnable() {
				@Override
				public void run() {
					updateCaptureButtonLocked(lens);
				}
			});
			return;
		}

		try {
			mCurrentLensIsLocked = LensCenter.defaultCenter().lensIsLocked(lens);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (mCurrentLensIsLocked) {
			int resID = getResources().getIdentifier("ico_locker", "drawable", "com.sorasoft.dicecam");
			mCaptureButton.setImageResource(resID);
		} else {
			mCaptureButton.setImageResource(0);
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void setImageButtonAlpha(ImageButton imageButton, final int alpha) {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			imageButton.setImageAlpha(alpha);
		} else {
			imageButton.setAlpha(alpha);
		}
	}

	private void disableButtonsForTakingPhoto() {
		disableButtonsForTakingPhoto(false);
	}

	private void disableButtonsForTakingPhotoExceptCaptureButton() {
		disableButtonsForTakingPhoto(true);
	}

	private void disableButtonsForTakingPhoto(final boolean exceptCaptureButton) {
		mMainHandler.post(new Runnable() {
			@Override
			public void run() {
				mSettingButton.setEnabled(false);
				mFlipCameraButton.setEnabled(false);

				mCollageButton.setEnabled(false);
				mVignetteButton.setEnabled(false);
				mBlurButton.setEnabled(false);
				mFrameButton.setEnabled(false);
				mTimerButton.setEnabled(false);

				mAlbumButton.setEnabled(false);
				mCaptureButton.setEnabled(exceptCaptureButton);

				mBtnRollDice.setEnabled(false);
				mBtnFavorites.setEnabled(false);
			}
		});
	}

	private void enableButtonsAfterTakingPhoto() {
		mMainHandler.post(new Runnable() {
			@Override
			public void run() {
				mSettingButton.setEnabled(true);
				mFlipCameraButton.setEnabled(true);

				mCollageButton.setEnabled(true);
				mVignetteButton.setEnabled(true);
				mBlurButton.setEnabled(true);
				mFrameButton.setEnabled(true);
				mTimerButton.setEnabled(true);

				mAlbumButton.setEnabled(true);
				mCaptureButton.setEnabled(true);

				mBtnRollDice.setEnabled(true);
				mBtnFavorites.setEnabled(true);

				mBtnRollDice.setVisibility(View.VISIBLE);
				mBtnFavorites.setVisibility(View.VISIBLE);
				mToolbarBodyLayout.setHideFilterButton(false);
				mToolbarBodyLayout.setHideRandomFilterButton(false);
				mToolbarBodyLayout.updateTextViewVisibles();
			}
		});
	}

	private void closeAllPanels() {
		mFrameSelection.makeInvisible();

		closeEdgeOptionView();
		closeTimerOptionView();

		favoritesVisiable(false);
	}

	private synchronized void updateButtonOrientations() {
		float fromDegree = orientationDegree;
		float degree = 0.f;
		switch (ORIENTATION) {
		case ImageComposition.DICECAM_ORIENTATION_PORTRAIT:
			degree = 0.f;
			break;
		case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_LEFT:
			degree = 90.f;
			break;
		case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_RIGHT:
			degree = -90.f;
			break;
		case ImageComposition.DICECAM_ORIENTATION_PORTRAIT_UPSIDE_DOWN:
			degree = 180.f;
			break;
		}

		orientationDegree = degree;

		// startImageButtonRotateAnimation(mSettingButton, fromDegree, degree);

		if (numberOfCameras == 1) {
			mFlipCameraButton.setVisibility(View.INVISIBLE);
		} else {
			startImageButtonRotateAnimation(mFlipCameraButton, fromDegree, degree);
		}

		startImageButtonRotateAnimation(mVignetteButton, fromDegree, degree);
		startImageButtonRotateAnimation(mBlurButton, fromDegree, degree);
		startImageButtonRotateAnimation(mFrameButton, fromDegree, degree);
		startImageButtonRotateAnimation(mTimerButton, fromDegree, degree);

		if (Settings.getInstance().getTimer() > 0) {
			startImageButtonRotateAnimation(mTimerHUDTextView, fromDegree, degree);
		}
	}

	// private void setImageButtonRotation(ImageButton button, float degree) {
	// Rect imageBounds = button.getDrawable().getBounds();
	// Matrix matrix = new Matrix();
	// matrix.postRotate(degree, imageBounds.width()/2, imageBounds.height()/2);
	// matrix.postTranslate(button.getWidth()/2 - imageBounds.width()/2,
	// button.getHeight()/2 - imageBounds.height()/2);
	// button.setScaleType(ScaleType.MATRIX);
	// button.setImageMatrix(matrix);
	// }

	private void startImageButtonRotateAnimation(final View view, final float fromDegree, final float toDegree) {
		RotateAnimation animation = new RotateAnimation(fromDegree, toDegree, view.getWidth() / 2, view.getHeight() / 2);
		animation.setFillAfter(true);
		animation.setDuration(250);

		view.clearAnimation();
		view.startAnimation(animation);

		// animation.setAnimationListener(new Animation.AnimationListener() {
		//
		// @Override
		// public void onAnimationStart(Animation animation) {
		// }
		//
		// @Override
		// public void onAnimationRepeat(Animation animation) {
		// }
		//
		// @Override
		// public void onAnimationEnd(Animation animation) {
		// setImageButtonRotation(button, toDegree);
		// }
		// });
	}

	private int initialBottomToolbarHeight = 0;

	public int getBottomToolbarHeight() {
		int heightInPx = mToolbarHeadLayout.getHeight();
//		int heightInPx = mToolbarHeadLayout.getHeight() + mToolbarBodyLayout.getHeight();

		if (initialBottomToolbarHeight == 0) {
			initialBottomToolbarHeight = heightInPx;
		}
		
		return heightInPx;
	}

	private int getInitialBottomToolbarHeight() {
		if (initialBottomToolbarHeight > 0)
			return initialBottomToolbarHeight;
		return getBottomToolbarHeight();
	}

	private void choosePreviewSize(int w, int h) {
		if (adjustedPreviewWidth == 0)
			adjustedPreviewWidth = w;
		if (adjustedPreviewHeight == 0)
			adjustedPreviewHeight = w;
		if (chosenPreviewWidth == w && chosenPreviewHeight == h) {
			showBottomToolbars();
			return;
		}
		chosenPreviewWidth = w;
		chosenPreviewHeight = h;
		
		adjustBottomToolbarHeight();
	}

	private int adjustedPreviewWidth = 0;
	private int adjustedPreviewHeight = 0;

	public static DicecamLens currentLens;

	private void adjustBottomToolbarHeight() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int fullWidth = displaymetrics.widthPixels;
		int fullHeight = displaymetrics.heightPixels;
	
//		final int toolbarBodyHeight = mToolbarBodyLayout.getHeight() - mToolbarHeadLayout.getHeight();
		
		int[] fitSize = { fullWidth, mCameraPreviewFrame.getHeight() };

		adjustedPreviewWidth = fitSize[0];
		adjustedPreviewHeight = fitSize[1];
		
//		Settings.getInstance().setToolbarBodyHeight(toolbarBodyHeight);
//		setToolbarBodyHeight(toolbarBodyHeight);
	}


//	private void setToolbarBodyHeight(final int toolbarBodyHeight) {
//		if (Looper.myLooper() == Looper.getMainLooper()) {
//			LinearLayout.LayoutParams bodyParam = (LinearLayout.LayoutParams) mToolbarBodyLayout.getLayoutParams();
//			bodyParam.height = toolbarBodyHeight;
//			mToolbarBodyLayout.setLayoutParams(bodyParam);
//			mToolbarBodyLayout.updateCaptureButtonSize(toolbarBodyHeight);
//			showBottomToolbars();
//			mFavoriteView.setPreferredHeightInPixel(mToolbarHeadLayout.getHeight() + toolbarBodyHeight);
//		} else {
//			mMainHandler.post(new Runnable() {
//				@Override
//				public void run() {
//					setToolbarBodyHeight(toolbarBodyHeight);
//				}
//			});
//		}
//	}

	private void showBottomToolbars() {
		if (mToolbarBodyLayout.getVisibility() == View.VISIBLE)
			return;

		if (Looper.myLooper() == Looper.getMainLooper()) {
			mToolbarHeadLayout.setVisibility(View.VISIBLE);
			mToolbarBodyLayout.setVisibility(View.VISIBLE);
		} else {
			mMainHandler.post(new Runnable() {
				@Override
				public void run() {
					mToolbarHeadLayout.setVisibility(View.VISIBLE);
					mToolbarBodyLayout.setVisibility(View.VISIBLE);
				}
			});
		}
	}

	public Location getLastKnownLocation() {
		if (locationEnabled == false)
			return null;
		try {
			Location loc = locationManager.getLastKnownLocation(locationProvider);
			return loc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Location getLastKnownLocationForPictureExif() {
		if (Settings.getInstance().getGeoTag())
			return getLastKnownLocation();
		return null;
	}

	@Override
	public void onSelectLens(DicecamLens lens, FavoritesMenu favoritesMenu) {
		if (lens != null)
			setLens(lens);
		else
			return;

		// log event - lens selected
		String name = lens.getTitle();
		HashMap<String, String> eventParam = new HashMap<String, String>();
		eventParam.put(BaseActivity.E_LENS_SELECTED_NAME, name);
	}

	@Override
	public void onCloseButtonTouched() {
		favoritesVisiable(false);
	}

	@Override
	public void onShown(FavoritesMenu favoritesMenu) {
		UserInterfaceUtil.setAlpha(mToolbarHeadLayout, 0.5f);
		UserInterfaceUtil.setAlpha(mToolbarBodyLayout, 0.5f);
	}

	@Override
	public void onHidden(FavoritesMenu favoritesMenu) {
		UserInterfaceUtil.setAlpha(mToolbarHeadLayout, 1.f);
		UserInterfaceUtil.setAlpha(mToolbarBodyLayout, 1.f);
	}

	@Override
	public DicecamLens lensSelectorSelectedLens() {
		if (mEngine == null)
			return null;
		return mEngine.getCurrentLens();
	}

	@Override
	public void onIntensityValueChanged(LensIntensityControlView controlView, float intensity) {
		final float i = intensity;

		showLensIntensityText();

		mEngine.runOnRendererThread(new Runnable() {

			@Override
			public void run() {
				DicecamLens lens = mEngine.getCurrentLens();
				lens.setFilterIntensity(i);
			}
		});
	}

	@Override
	public void onIntensityEditModeChanged(LensIntensityControlView controlView, boolean editMode) {
		if (editMode) {
			showLensIntensityText();
		} else {
			hideLensIntensityText(true);

			// log event - lens intensity edited
			HashMap<String, String> eventParam = new HashMap<String, String>();
			eventParam.put(BaseActivity.E_LENS_INTENSITY_VALUE, BaseActivity.refineLensIntensityString(controlView.getIntensity()));
		}
	}

	private class ThreadOfTakeStillSinglePicture extends AsyncTask<Object, Integer, Boolean> {

		Uri savedImageUri = null;

		protected void onProgressUpdate(Integer... params) {
			mProgressBar.setProgress(params[0]);
		}

		@Override
		// private Thread savePicture(final int pictureOrientation, final
		protected Boolean doInBackground(Object... params) {
			// Location pictureLocation, final byte[] data) {
			int pictureOrientation = Integer.parseInt("" + params[0]);

			boolean previewRestarted = false;

			publishProgress(10);

			try {
				Bitmap sourceBitmap = BitmapFactory.decodeByteArray((byte[]) params[2], 0, ((byte[]) params[2]).length);

				publishProgress(30);

				NakedFootPreViewCallBack engine = new NakedFootPreViewCallBack(mContext, mEngine.getCurrentLens().duplicate(mContext), true);
				DicecamLens lens = engine.getCurrentLens();
				lens.setSkipYUV(true);
				engine.setOrientation(pictureOrientation, isFrontCameraActivated(), Settings.getInstance().getSaveAsPreview());
				engine.setBitmapApplyProgressCallback(new NakedFootPreViewCallBack.BitmapApplyProgressCallback() {
					@Override
					public void onProcess(float process) {
						publishProgress(40 + (int) (process * 40.f));
					}
				});

				mFrameSelection.calculateCropInformation(collageStatus, sourceBitmap.getWidth(), sourceBitmap.getHeight());
				lens.setCropRegion(mFrameSelection.getCropRegion());

				int outputWidth = mFrameSelection.getCropWidth();
				int outputHeight = mFrameSelection.getCropHeight();

				Log.d("dicecam", "sourceBitmap: " + sourceBitmap.getWidth() + ", " + sourceBitmap.getHeight());
				Log.d("dicecam", "orientation: " + pictureOrientation + " (" + CollageProvider.getOrientationToString(pictureOrientation)
						+ ")" + " isFront: " + isFrontCameraActivated() + " saveAsPreview: "
						+ Settings.getInstance().getSaveAsPreview());
				Log.d("dicecam", "calculated outputSize: " + outputWidth + ", " + outputHeight);
				// lens.setCropRegion(mEngine.getCurrentLens().getCropRegion());
				lens.setFlipAspectRatio(true);
				lens.setBlurCenterAndRadiusForStillPicture(pictureOrientation, isFrontCameraActivated(), Settings.getInstance()
						.getSaveAsPreview(), mGLSurfaceView.eventDispatcher().getCenterX(), mGLSurfaceView.eventDispatcher().getCenterY(),
						mGLSurfaceView.eventDispatcher().getRadius());

				publishProgress(40);

				try {
					// int maxTextureSize =
					// NakedFootPreViewCallBack.getGlMaxTextureSize();
					// Log.d("dicecam", "11maxTextureSize: " + maxTextureSize);
					// if (maxTextureSize > 1024 && (outputWidth >
					// maxTextureSize || outputHeight > maxTextureSize)) {
					// int[] newOutputSize =
					// UserInterfaceUtil.getFitSize(outputWidth, outputHeight,
					// maxTextureSize, maxTextureSize);
					// outputWidth = newOutputSize[0];
					// outputHeight = newOutputSize[1];
					// Log.d("dicecam", "resize outputSize(maxTextureSize: " +
					// maxTextureSize + ") -> " + outputWidth + ", " +
					// outputHeight);
					// }
					Bitmap bitmap = engine.getBitmapWithFilterApplied(sourceBitmap, outputWidth, outputHeight, true);
					sourceBitmap.recycle();
					sourceBitmap = null;
					lens.setFlipAspectRatio(true);

					publishProgress(60);

					// frame photo
					if (Settings.getInstance().getShouldFramePhoto()) {
						bitmap = ImageComposition.generateFramedBitmap(bitmap, outputWidth, outputHeight);
					}

					// //HJH_System.gc();

					publishProgress(80);
					Bitmap watermarkedBitmap = null;
					// print watermark
					if (Settings.getInstance().isWatermarkEnabled()) {
						watermarkedBitmap = WatermarkProvider.applyWatermark(mContext, bitmap, pictureOrientation,
								WatermarkProvider.DICECAM_WATERMARK_FLAG_SINGLE_CAMERA, false);
						bitmap = watermarkedBitmap;
					}

					publishProgress(90);

					// //HJH_System.gc();

					// save bitmap
					savedImageUri = mFileManagement.saveBitmap(bitmap, pictureOrientation, true, true, false, (Location) params[1]);

					bitmap.recycle();
					bitmap = null;
					if (watermarkedBitmap != null) {
						watermarkedBitmap.recycle();
						watermarkedBitmap = null;
					}

					// //HJH_System.gc();

					publishProgress(100);

				} catch (OutOfMemoryError e) {
					Log.i("Dicecam", "hjh OutOfMemory in DiceCamera 3708");

					e.printStackTrace();

					sourceBitmap = null;

					publishProgress(60);

					if (previewRestarted == false) {
						resumeCameraPreview();
						previewRestarted = true;
					}

					publishProgress(70);

					canTakeStillPicture = false;
					takeRenderedPicture();

					publishProgress(100);
					return Boolean.TRUE;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (previewRestarted == false) {
				// resumeCameraPreview();
				previewRestarted = true;
			}
			// return pThread;
			return null;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if (savedImageUri != null) {
				Log.i("Dicecam", "hjh DiceCamera showQuickView() " + savedImageUri);
				showQuickView(savedImageUri, "StillPicture", 1);
			} else {
				showSavingError();
			}
		}
	}

	public void callPurchaseActivity() {
		Intent activity;
		switch (PublishInfo.source) {
		case PublishInfo.SOURCE__INTEL_PROVIDER:
		case PublishInfo.SOURCE__GOOGLE_PLAY_STORE:
			activity = new Intent(this, PurchaseActivity.class);
			break;
		default:
			activity = new Intent(this, PurchaseActivity.class);
			break;
		}
		startActivity(activity);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mImage_capture_intent_uri != null) {
			outState.putString("cameraImageUri", mImage_capture_intent_uri.toString());
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey("cameraImageUri")) {
			mImage_capture_intent_uri = Uri.parse(savedInstanceState.getString("cameraImageUri"));
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			takingShot();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	@Override
	public void advertisement() {
	}

	public void setCurrentLens(DicecamLens lens) {
		this.currentLens = lens;
	}
}

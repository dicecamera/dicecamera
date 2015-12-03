package com.sorasoft.dicecam.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.sorasoft.dicecam.R;
import com.sorasoft.dicecam.BaseActivity;
import com.sorasoft.dicecam.DiceCamera;
import com.sorasoft.dicecam.PublishInfo;
import com.sorasoft.dicecam.util.CommonUtil;

public class SettingActivity extends BaseActivity {

	private static Activity mActivity;

	private int screenWidth;
	private int screenHeight;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = this;

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_setting);
		ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setIcon(R.drawable.ico_title_fit);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

		screenWidth = displaymetrics.widthPixels;
		screenHeight = displaymetrics.heightPixels;

		final ToggleButton preview_toggle = (ToggleButton) findViewById(R.id.preview_toggle);
		preview_toggle.setChecked(Settings.getInstance().getSaveAsPreview());
		preview_toggle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Settings.getInstance().setSaveAsPreview(preview_toggle.isChecked());
			}
		});

		final ToggleButton watermark_toggle = (ToggleButton) findViewById(R.id.watermark_toggle);
		watermark_toggle.setChecked(Settings.getInstance().isWatermarkEnabled());
		watermark_toggle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Settings.getInstance().setWatermarkEnabled(watermark_toggle.isChecked());
			}
		});

		View.OnClickListener row_pro_upgrade_clicker = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				((DiceCamera) DiceCamera.mContext).callPurchaseActivity();
			}
		};

		FrameLayout row_pro_upgrade = (FrameLayout) findViewById(R.id.row_pro_upgrade);
		row_pro_upgrade.setClickable(true);
		row_pro_upgrade.setOnClickListener(row_pro_upgrade_clicker);

		View.OnClickListener row_select_logo_clicker = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent watermarkActivity = new Intent(mActivity, WatermarkActivity.class);
				startActivity(watermarkActivity);
			}
		};
		FrameLayout row_select_logo = (FrameLayout) findViewById(R.id.row_select_logo);
		row_select_logo.setClickable(true);
		row_select_logo.setOnClickListener(row_select_logo_clicker);

		View.OnClickListener row_rate_this_app_clicker = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// rate this app
				openReview();
			}
		};

		final ToggleButton geo_toggle = (ToggleButton) findViewById(R.id.geo_toggle);
		geo_toggle.setChecked(Settings.getInstance().getGeoTag());
		geo_toggle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Settings.getInstance().setGeoTag(geo_toggle.isChecked());
			}
		});
		
//		if (FileManagement.hasRemovableExternalStorage(mActivity)) {	
//		if (false) {
//			final ToggleButton save_to_removable_storage_toggle = (ToggleButton) findViewById(R.id.save_to_removable_storage_toggle);
//			save_to_removable_storage_toggle.setChecked(Settings.defaultSettings().getSaveToRemovableStorage());
//			save_to_removable_storage_toggle.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					Settings.defaultSettings().setSaveToRemovableStorage(save_to_removable_storage_toggle.isChecked());
//				}
//			});
//
//		}
//		
//		else {
			final FrameLayout save_to_external_storage_separator = (FrameLayout) findViewById(R.id.save_to_removable_storage_separator);
			save_to_external_storage_separator.setVisibility(View.GONE);
			
			final FrameLayout save_to_external_storage_framelayout = (FrameLayout) findViewById(R.id.save_to_removable_storage_framelayout);
			save_to_external_storage_framelayout.setVisibility(View.GONE);
//		}
		

		FrameLayout row_rate_this_app = (FrameLayout) findViewById(R.id.row_rate_this_app);
		row_rate_this_app.setClickable(true);
		row_rate_this_app.setOnClickListener(row_rate_this_app_clicker);

		View.OnClickListener row_tell_friend_clicker = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// tell a friend
				openTellAFriend();
			}
		};

		FrameLayout row_tell_friend = (FrameLayout) findViewById(R.id.row_tell_friend);
		row_tell_friend.setClickable(true);
		row_tell_friend.setOnClickListener(row_tell_friend_clicker);

		View.OnClickListener row_facebook_clicker = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// facebook
				openFacebookPage();
			}
		};

		FrameLayout row_facebook = (FrameLayout) findViewById(R.id.row_facebook);
		row_facebook.setClickable(true);
		row_facebook.setOnClickListener(row_facebook_clicker);
		
		View.OnClickListener row_faq_clicker = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openFAQ();
			}
		};
		
		FrameLayout row_faq = (FrameLayout) findViewById(R.id.row_faq);
		row_faq.setClickable(true);
		row_faq.setOnClickListener(row_faq_clicker);

		View.OnClickListener row_feedback_clicker = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// feedback
				openEmail();
			}
		};

		FrameLayout row_feedback = (FrameLayout) findViewById(R.id.row_feedback);
		row_feedback.setClickable(true);
		row_feedback.setOnClickListener(row_feedback_clicker);

		// Button closeButton = (Button) findViewById(R.id.close_button);
		// closeButton.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // close
		// mActivity.finish();
		// }
		// });

		TextView version_text = (TextView) findViewById(R.id.version_text);
		version_text.setText(CommonUtil.getAppVersion(this));
		
		FrameLayout row_version = (FrameLayout) findViewById(R.id.row_version);
		row_version.setClickable(false);
//		row_version.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				showVersionDetail();
//			}
//		});
	}

	protected void openTellAFriend() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.tell_a_friend_subject));
		intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_to_friend));

		try {
			startActivity(Intent.createChooser(intent, getResources().getString(R.string.tell_a_friend)));
		} catch (android.content.ActivityNotFoundException ex) {
		}
	}

	protected void openEmail() {

		PackageInfo pInfo = null;

		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
		}

		Intent thisIntent = getIntent();

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { PublishInfo.getFeedbackEmailAddress() });
		intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.feedback_title));
		intent.putExtra(Intent.EXTRA_TEXT, "\n\n" 
		+ " [" + screenWidth + "x" + screenHeight + "]"+ android.os.Build.BRAND + " " + android.os.Build.MODEL + " (" + android.os.Build.VERSION.SDK_INT + " - " + pInfo.versionCode + " @ [" + pInfo.versionName + "] "
				+ ")"	);

		try {
			startActivity(Intent.createChooser(intent, getResources().getString(R.string.email_the_developer)));
		} catch (android.content.ActivityNotFoundException ex) {
		}
	}

	protected void openFacebookPage() {
		try {
			final String url = "fb://page/" + PublishInfo.getFacebookID();
			Intent facebookAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			facebookAppIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			startActivity(facebookAppIntent);
		} catch (Exception e) {
			final String url = "https://www.facebook.com/" + PublishInfo.getFacebookID();
			Intent facebookPageIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(facebookPageIntent);
		}
	}

	protected void openReview() {
		String urlString = null;
		
		switch (PublishInfo.source) {
		case PublishInfo.SOURCE__AMAZON_APPSTORE_FOR_ANDROID:
			urlString = "amzn://apps/android?p=" + PublishInfo.getAppId();
			break;
			
		default:
			urlString = "market://details?id=" + PublishInfo.getAppId();
			break;
		}
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(urlString));
		startActivity(intent);
	}
	
	protected void openFAQ() {
		final String url = "http://sorasoft0213.cafe24.com/open-source-license/";
		Intent webPageIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(webPageIntent);
	}

	protected void showVersionDetail() {
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setTitle(CommonUtil.getAppVersion(this));
		dlg.setMessage("" + PublishInfo.getSourceString() + "(" + PublishInfo.source + ")\n" + PublishInfo.buildNumber);
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

		updateLogoPreview();
	}

	private void updateLogoPreview() {

		ImageView select_logo_imageview = (ImageView) findViewById(R.id.select_logo_imageview);
		select_logo_imageview.setImageBitmap(WatermarkProvider.getCurrentWatermarkIcon(mActivity));

	}
}

package com.sorasoft.dicecam;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.sorasoft.dicecam.R;
import com.sorasoft.dicecam.setting.Settings;
import com.sorasoft.dicecam.util.UserInterfaceUtil;
import com.sorasoft.dicecam.view.album.ViewAfterPictureTaken;

public class AlbumActivity extends BaseActivity {

	private Activity mActivity;
	private GridView mGridview;
	private AlbumImageAdapter mAlbumImageAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActivity = this;
		
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_album);
		ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setIcon(R.drawable.ico_title_fit);

		mGridview = (GridView) findViewById(R.id.gridview);
		mAlbumImageAdapter = new AlbumImageAdapter(this);
		mGridview.setColumnWidth(UserInterfaceUtil.dp2px(getThumbnailViewWidthInDP(), mGridview));
		mGridview.setAdapter(mAlbumImageAdapter);

		mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Uri imageUri = Uri.fromFile(mAlbumImageAdapter.mediaFiles[position]);

				Log.d("dicecam", "Y: " + mGridview.getScrollY());

				Intent quickViewActivity = new Intent(mActivity, ViewAfterPictureTaken.class);
				quickViewActivity.putExtra(ViewAfterPictureTaken.EXTRA_KEY_FROM, BaseActivity.E_QUICK_VIEW_FROM_ALBUM);
				quickViewActivity.putExtra(ViewAfterPictureTaken.EXTRA_KEY_DISPLAY_AD, false);
				quickViewActivity.putExtra(ViewAfterPictureTaken.EXTRA_KEY_URI, imageUri.toString());
				quickViewActivity.putExtra(ViewAfterPictureTaken.EXTRA_KEY_POSITION, position);
				
				mActivity.startActivity(quickViewActivity);
			}
		});

		// Button close_button = (Button) findViewById(R.id.close_button);
		// close_button.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// finish();
		// }
		// });

//DICE		if (adspace()) {
//			final Activity activity = this;
//			new Handler().postDelayed(new Runnable() {
//
//				@Override
//				public void run() {
//					if (SPAdController.sharedController().displayBannerAD(activity, getAdTargetLayout()) == true) {
//					}
//				}
//			}, 100);
//		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mAlbumImageAdapter.refreshData();
		mAlbumImageAdapter.notifyDataSetChanged();
		mGridview.invalidateViews();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

//DICE		if ( adspace() == true ) {
//			// Checks the orientation of the screen
//			if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//				SPAdController.sharedController().reloadCurrentAD(this, getAdTargetLayout());
//			} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//				SPAdController.sharedController().reloadCurrentAD(this, getAdTargetLayout());
//			}
//		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public FrameLayout getAdTargetLayout() {
		return (FrameLayout) findViewById(R.id.album_ad_target_layout);
	}
	
	private int getThumbnailViewWidthInDP() {
		return FileManagement.getThumbnailWidthInDP();
	}

}

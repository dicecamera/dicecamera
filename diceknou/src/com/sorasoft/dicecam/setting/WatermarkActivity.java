package com.sorasoft.dicecam.setting;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableRow;

import com.sorasoft.dicecam.R;
import com.sorasoft.dicecam.BaseActivity;

public class WatermarkActivity extends BaseActivity {
	
	private static Activity mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this;
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_watermark);
		ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setIcon(R.drawable.ico_title_fit);
		
//		Button back_button = (Button) findViewById(R.id.back_button);
//		back_button.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				mContext.finish();
//			}
//		});

		ImageView wm_simple_image = (ImageView) findViewById(R.id.wm_simple_image);
		wm_simple_image.setImageBitmap(WatermarkProvider.getWatermark(this, WatermarkProvider.DICECAM_WATERMARK_SIMPLE, WatermarkProvider.Type.TITLE));
		TableRow wm_simple = (TableRow) findViewById(R.id.wm_simple);
		wm_simple.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Settings.getInstance().setWatermark(WatermarkProvider.DICECAM_WATERMARK_SIMPLE);
				mContext.finish();
			}
		});
		
		ImageView wm_stamp2_image = (ImageView) findViewById(R.id.wm_stamp2_image);
		wm_stamp2_image.setImageBitmap(WatermarkProvider.getWatermark(this, WatermarkProvider.DICECAM_WATERMARK_STAMP2, WatermarkProvider.Type.TITLE));
		TableRow wm_stamp2 = (TableRow) findViewById(R.id.wm_stamp2);
		wm_stamp2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Settings.getInstance().setWatermark(WatermarkProvider.DICECAM_WATERMARK_STAMP2);
				mContext.finish();
			}
		});
		
		ImageView wm_stamp3_image = (ImageView) findViewById(R.id.wm_stamp3_image);
		wm_stamp3_image.setImageBitmap(WatermarkProvider.getWatermark(this, WatermarkProvider.DICECAM_WATERMARK_STAMP3, WatermarkProvider.Type.TITLE));
		TableRow wm_stamp3 = (TableRow) findViewById(R.id.wm_stamp3);
		wm_stamp3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Settings.getInstance().setWatermark(WatermarkProvider.DICECAM_WATERMARK_STAMP3);
				mContext.finish();

			}
		});
		
		ImageView wm_renewal2_image = (ImageView) findViewById(R.id.wm_renewal2_image);
		wm_renewal2_image.setImageBitmap(WatermarkProvider.getWatermark(this, WatermarkProvider.DICECAM_WATERMARK_RENEWAL2, WatermarkProvider.Type.TITLE));
		TableRow wm_renewal2 = (TableRow) findViewById(R.id.wm_renewal2);
		wm_renewal2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Settings.getInstance().setWatermark(WatermarkProvider.DICECAM_WATERMARK_RENEWAL2);
				mContext.finish();
				
			}
		});
		
		ImageView wm_pop2_image = (ImageView) findViewById(R.id.wm_pop2_image);
		wm_pop2_image.setImageBitmap(WatermarkProvider.getWatermark(this, WatermarkProvider.DICECAM_WATERMARK_POP2, WatermarkProvider.Type.TITLE));
		TableRow wm_pop2 = (TableRow) findViewById(R.id.wm_pop2);
		wm_pop2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Settings.getInstance().setWatermark(WatermarkProvider.DICECAM_WATERMARK_POP2);
				mContext.finish();
				
			}
		});
		
		ImageView wm_vintage_image = (ImageView) findViewById(R.id.wm_vintage_image);
		wm_vintage_image.setImageBitmap(WatermarkProvider.getWatermark(this, WatermarkProvider.DICECAM_WATERMARK_VINTAGE, WatermarkProvider.Type.TITLE));
		TableRow wm_vintage = (TableRow) findViewById(R.id.wm_vintage);
		wm_vintage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Settings.getInstance().setWatermark(WatermarkProvider.DICECAM_WATERMARK_VINTAGE);
				mContext.finish();
				
			}
		});
		
		ImageView wm_new2_image = (ImageView) findViewById(R.id.wm_new2_image);
		wm_new2_image.setImageBitmap(WatermarkProvider.getWatermark(this, WatermarkProvider.DICECAM_WATERMARK_NEW2, WatermarkProvider.Type.TITLE));
		TableRow wm_new2 = (TableRow) findViewById(R.id.wm_new2);
		wm_new2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Settings.getInstance().setWatermark(WatermarkProvider.DICECAM_WATERMARK_NEW2);
				mContext.finish();
				
			}
		});
		
		ImageView wm_new4_image = (ImageView) findViewById(R.id.wm_new4_image);
		wm_new4_image.setImageBitmap(WatermarkProvider.getWatermark(this, WatermarkProvider.DICECAM_WATERMARK_NEW4, WatermarkProvider.Type.TITLE));
		TableRow wm_new4 = (TableRow) findViewById(R.id.wm_new4);
		wm_new4.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Settings.getInstance().setWatermark(WatermarkProvider.DICECAM_WATERMARK_NEW4);
				mContext.finish();
				
			}
		});
				
		ImageView wm_new6_image = (ImageView) findViewById(R.id.wm_new6_image);
		wm_new6_image.setImageBitmap(WatermarkProvider.getWatermark(this, WatermarkProvider.DICECAM_WATERMARK_NEW6, WatermarkProvider.Type.TITLE));
		TableRow wm_new6 = (TableRow) findViewById(R.id.wm_new6);
		wm_new6.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Settings.getInstance().setWatermark(WatermarkProvider.DICECAM_WATERMARK_NEW6);
				mContext.finish();
				
			}
		});
		
		ImageView wm_new9_image = (ImageView) findViewById(R.id.wm_new9_image);
		wm_new9_image.setImageBitmap(WatermarkProvider.getWatermark(this, WatermarkProvider.DICECAM_WATERMARK_NEW9, WatermarkProvider.Type.TITLE));
		TableRow wm_new9 = (TableRow) findViewById(R.id.wm_new9);
		wm_new9.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Settings.getInstance().setWatermark(WatermarkProvider.DICECAM_WATERMARK_NEW9);
				mContext.finish();
				
			}
		});
		
		ImageView wm_new10_image = (ImageView) findViewById(R.id.wm_new10_image);
		wm_new10_image.setImageBitmap(WatermarkProvider.getWatermark(this, WatermarkProvider.DICECAM_WATERMARK_NEW10, WatermarkProvider.Type.TITLE));
		TableRow wm_new10 = (TableRow) findViewById(R.id.wm_new10);
		wm_new10.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Settings.getInstance().setWatermark(WatermarkProvider.DICECAM_WATERMARK_NEW10);
				mContext.finish();
				
			}
		});
		
		ImageView wm_stamp_image = (ImageView) findViewById(R.id.wm_stamp_image);
		wm_stamp_image.setImageBitmap(WatermarkProvider.getWatermark(this, WatermarkProvider.DICECAM_WATERMARK_STAMPFULL, WatermarkProvider.Type.TITLE));
		TableRow wm_stamp_full = (TableRow) findViewById(R.id.wm_stamp_full);
		wm_stamp_full.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Settings.getInstance().setWatermark(WatermarkProvider.DICECAM_WATERMARK_STAMPFULL);
				mContext.finish();
				
			}
		});
		
		ImageView wm_rb_image = (ImageView) findViewById(R.id.wm_rb_image);
		wm_rb_image.setImageBitmap(WatermarkProvider.getWatermark(this, WatermarkProvider.DICECAM_WATERMARK_RIBONFULL, WatermarkProvider.Type.TITLE));
		TableRow wm_rb_full = (TableRow) findViewById(R.id.wm_rb_full);
		wm_rb_full.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Settings.getInstance().setWatermark(WatermarkProvider.DICECAM_WATERMARK_RIBONFULL);
				mContext.finish();
				
			}
		});
		
		ImageView wm_new11_image = (ImageView) findViewById(R.id.wm_new11_image);
		wm_new11_image.setImageBitmap(WatermarkProvider.getWatermark(this, WatermarkProvider.DICECAM_WATERMARK_NEW11, WatermarkProvider.Type.TITLE));
		TableRow wm_new11 = (TableRow) findViewById(R.id.wm_new11);
		wm_new11.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Settings.getInstance().setWatermark(WatermarkProvider.DICECAM_WATERMARK_NEW11);
				mContext.finish();
				
			}
		});
		
		ImageView wm_new12_image = (ImageView) findViewById(R.id.wm_new12_image);
		wm_new12_image.setImageBitmap(WatermarkProvider.getWatermark(this, WatermarkProvider.DICECAM_WATERMARK_NEW12, WatermarkProvider.Type.TITLE));
		TableRow wm_new12 = (TableRow) findViewById(R.id.wm_new12);
		wm_new12.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Settings.getInstance().setWatermark(WatermarkProvider.DICECAM_WATERMARK_NEW12);
				mContext.finish();
				
			}
		});
		
		ImageView wm_ver1_image = (ImageView) findViewById(R.id.wm_ver1_image);
		wm_ver1_image.setImageBitmap(WatermarkProvider.getWatermark(this, WatermarkProvider.DICECAM_WATERMARK_VER1, WatermarkProvider.Type.TITLE));
		TableRow wm_ver1 = (TableRow) findViewById(R.id.wm_ver1);
		wm_ver1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Settings.getInstance().setWatermark(WatermarkProvider.DICECAM_WATERMARK_VER1);
				mContext.finish();
				
			}
		});
	}
}

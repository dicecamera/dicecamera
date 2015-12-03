package com.sorasoft.dicecam.view.watermark;

import android.content.Context;
import android.widget.TextView;

import com.sorasoft.dicecam.R;
import com.sorasoft.dicecam.engine.filter.LensPack;


/**
 * Copied from LensSelectionLargeButton
 */

public class WatermarkSelectorLargeButton extends WatermarkSelectorButton {
	
	public WatermarkSelectorLargeButton(Context context, LensPack pack, int i) {
		super(context, pack, i);
	}
	
	protected int getPackWidthInDP() {
		return 114;
	}
	
	public int getPackHeightInDP() {
		return 114;
	}

	protected int getLensWidthInDP() {
		return 72;
	}
	
	protected int getLensHeightInDP() {
		return 114;
	}

	protected int getLensTextHeightInDP() {
		return 42;
	}
	
	protected int getBackButtonWidthInDP() {
		return 75;
	}
	
	protected int getBackButtonHeightInDP() {
		return 75;
	}
	
	protected int getBackButtonMarginYInDP() {
		return 19;
	}
	
	protected int getLockerTopMarginInDP() {
		return 12;
	}
	
	protected void setPackTitleTextViewAppearance(Context ctx, TextView tv) {
		tv.setTextAppearance(ctx, R.style.LensSelectorPackLargeButtonTextView);
	}
	
	protected void setLensTitleTextViewAppearance(Context ctx, TextView tv) {
		tv.setTextAppearance(ctx, R.style.LensSelectorLensLargeButtonTextView);
	}
}
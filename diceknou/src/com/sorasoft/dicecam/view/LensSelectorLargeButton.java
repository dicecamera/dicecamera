package com.sorasoft.dicecam.view;

import android.content.Context;
import android.widget.TextView;

import com.sorasoft.dicecam.R;
import com.sorasoft.dicecam.engine.filter.DicecamLens;
import com.sorasoft.dicecam.engine.filter.LensPack;

public class LensSelectorLargeButton extends CategoriesButton {
	
	public LensSelectorLargeButton(Context context) {
		super(context);
	}
	
	public LensSelectorLargeButton(Context context, LensSelectorButtonType type) {
		super(context, type);
	}
	
	public LensSelectorLargeButton(Context context, LensPack pack, int i) {
		super(context, pack, i);
	}
	
	public LensSelectorLargeButton(Context context, DicecamLens lens, int lensIndex, int packIndex) {
		super(context, lens, lensIndex, packIndex);
	}

	protected int getPackWidthInDP() {
		return 114;
	}
	
	protected int getPackHeightInDP() {
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
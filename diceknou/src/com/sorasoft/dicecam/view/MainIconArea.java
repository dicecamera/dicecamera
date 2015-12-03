package com.sorasoft.dicecam.view;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorasoft.dicecam.util.UserInterfaceUtil;

public class MainIconArea extends RelativeLayout {

	public ImageButton captureButton;

	public ImageButton albumButton;
	public ImageButton filterButton;
	public ImageButton randomFilterButton;

	public TextView albumButtonTextView;
	public TextView filterButtonTextView;
	public TextView randomFilterButtonTextView;

	private boolean hideFilterButton = false;
	private boolean hideRandomFilterButton = false;

	private final int MAX_CAPTURE_BUTTON_WIDTH_IN_DP = 74;
	private int MAX_CAPTURE_BUTTON_WIDTH_IN_PX = 0;

	private final int CAPTURE_BUTTON_MARGIN_IN_DP = 8;
	private int CAPTURE_BUTTON_MARGIN_IN_PX = 0;

	private final int MIN_HEIGHT_IN_DP = 60;
	private int MIN_HEIGHT_IN_PX = 0;

	private Paint overlayPaint;

	public MainIconArea(Context context) {
		super(context);
		_init(context);
	}

	public MainIconArea(Context context, AttributeSet attrs) {
		super(context, attrs);
		_init(context);
	}

	public MainIconArea(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		_init(context);
	}

	private void _init(Context context) {
		MAX_CAPTURE_BUTTON_WIDTH_IN_PX = UserInterfaceUtil.dp2px(
				MAX_CAPTURE_BUTTON_WIDTH_IN_DP, this);
		CAPTURE_BUTTON_MARGIN_IN_PX = UserInterfaceUtil.dp2px(
				CAPTURE_BUTTON_MARGIN_IN_DP, this);
		MIN_HEIGHT_IN_PX = UserInterfaceUtil.dp2px(MIN_HEIGHT_IN_DP, this);
	}

	public int getMinHeightInPx() {
		return MIN_HEIGHT_IN_PX;
	}

	public boolean isHideFilterButton() {
		return hideFilterButton;
	}

	public void setHideFilterButton(boolean hideFilterButton) {
		this.hideFilterButton = hideFilterButton;
	}

	public boolean isHideRandomFilterButton() {
		return hideRandomFilterButton;
	}

	public void setHideRandomFilterButton(boolean hideRandomFilterButton) {
		this.hideRandomFilterButton = hideRandomFilterButton;
	}

	public void updateTextViewVisibles() {
		if (albumButton == null || albumButtonTextView == null)
			return;
		int actualHeight = albumButton.getMeasuredHeight()
				+ albumButtonTextView.getMeasuredHeight();
		if (actualHeight < 1)
			return;
		if (actualHeight > getMeasuredHeight()) {
			albumButtonTextView.setVisibility(View.GONE);
			filterButtonTextView.setVisibility(View.GONE);
			randomFilterButtonTextView.setVisibility(View.GONE);
		} else {
			albumButtonTextView.setVisibility(View.VISIBLE);
			filterButtonTextView
					.setVisibility(isHideFilterButton() ? View.INVISIBLE
							: View.INVISIBLE);
		}
	}

	public void updateCaptureButtonSize(final int toolbarBodyHeight) {
		// int actualHeight = getMeasuredHeight() - CAPTURE_BUTTON_MARGIN_IN_PX;
		int actualHeight = toolbarBodyHeight - CAPTURE_BUTTON_MARGIN_IN_PX;
		int preferWidth = actualHeight;
		Log.d("dicecam", "toolbar.body - toolbarBodyHeight: "
				+ toolbarBodyHeight);
		Log.d("dicecam", "toolbar.body - MAX_CAPTURE_BUTTON_WIDTH_IN_PX: "
				+ MAX_CAPTURE_BUTTON_WIDTH_IN_PX);
		Log.d("dicecam", "toolbar.body - prefer button height: " + preferWidth
				+ " (actual: " + actualHeight + ", max: "
				+ MAX_CAPTURE_BUTTON_WIDTH_IN_PX + ")");
		int cal_max_in_px = toolbarBodyHeight * 2 / 3;
		int actual_max_in_px = Math.max(cal_max_in_px,
				MAX_CAPTURE_BUTTON_WIDTH_IN_PX);
		Log.d("dicecam", "toolbar.body - cal_max_in_px: " + cal_max_in_px);
		Log.d("dicecam", "toolbar.body - actual_max_in_px: " + actual_max_in_px);
		if (preferWidth > actual_max_in_px) {
			preferWidth = actual_max_in_px;
		}

		LayoutParams params = (LayoutParams) captureButton.getLayoutParams();
		params.width = preferWidth;
		params.height = preferWidth;
		captureButton.setLayoutParams(params);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Log.d("dicecam", "toolbar.body - measured: " + widthMeasureSpec + ", "
				+ heightMeasureSpec);
		Log.d("dicecam", "toolbar.body - size: " + getMeasuredWidth() + ", "
				+ getMeasuredHeight());

		updateTextViewVisibles();
		// updateCaptureButtonSize();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		Log.d("dicecam", "toolbar.body - layout: " + l + ", " + t + ", " + r
				+ ", " + b);
	}
}

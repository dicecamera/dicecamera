package com.sorasoft.dicecam.view.watermark;

import android.content.Context;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sorasoft.dicecam.R;
import com.sorasoft.dicecam.engine.filter.LensPack;

/**
 * Copied from LensSelectionButton
 */

public class WatermarkSelectorButton extends FrameLayout implements View.OnTouchListener {

	private ImageView sampleImageView = null;
	private TextView titleTextView = null;
	private ImageView lockerImageView = null;

	private WaterMarkType type = WaterMarkType.BUNDLE_WATERMARK;

	private int mIndex = -1;

	private final int MARGIN_X_IN_DP = 5;
	private final int MARGIN_Y_IN_DP = 0;

	private int PACK_WIDTH_IN_DP = 76;
	private int PACK_HEIGHT_IN_DP = 76;

	private final int BORDER_TYPE_NORMAL = 0;
	private final int BORDER_TYPE_HIGHLIGHTED = 1;
	private final int BORDER_TYPE_SELECTED = 2;

	private boolean mHighlighted = false;
	private boolean mSelected = false;

	private int dp2px(final int dp) {
		final float scale = getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	public WatermarkSelectorButton(Context context, WaterMarkType type) {
		super(context);

		setOnTouchListener(this);

		setBackgroundColor(Color.WHITE);

		int content_width_in_dp = 0;
		int content_height_in_dp = 0;
		int margin_x_in_dp = MARGIN_X_IN_DP;
		int margin_y_in_dp = MARGIN_Y_IN_DP;

		switch (type) {
		case BUNDLE_WATERMARK:
			content_width_in_dp = PACK_WIDTH_IN_DP;
			content_height_in_dp = PACK_HEIGHT_IN_DP;
			break;
		// case another watermark
		}

		int width_in_dp = content_width_in_dp + margin_x_in_dp * 2;
		int height_in_dp = content_height_in_dp + margin_y_in_dp * 2;

		int width_in_px = dp2px(width_in_dp);
		int height_in_px = dp2px(height_in_dp);

		LayoutParams layoutParams = new LayoutParams(width_in_px, height_in_px);
		layoutParams.gravity = Gravity.CENTER;
		setLayoutParams(layoutParams);
		setForegroundGravity(Gravity.CENTER);

		ViewGroup parentView = this;

		LayoutParams imageViewParams = null;
		sampleImageView = new ImageView(context);
		sampleImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		switch (type) {
		case BUNDLE_WATERMARK:
			imageViewParams = new LayoutParams(dp2px(content_width_in_dp), dp2px(content_height_in_dp));
			break;
		// case for another type
		}
		imageViewParams.gravity = Gravity.CENTER;
		sampleImageView.setLayoutParams(imageViewParams);
		parentView.addView(sampleImageView);

		titleTextView = new TextView(context);
		titleTextView.setGravity(Gravity.CENTER);
		LayoutParams params = null;
		switch (type) {
		case BUNDLE_WATERMARK:
			params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			setPackTitleTextViewAppearance(context, titleTextView);
			break;
		// case for another type
		}
		titleTextView.setLayoutParams(params);
		parentView.addView(titleTextView);
	}

	public WatermarkSelectorButton(Context context, LensPack pack, int i) {
		this(context, WaterMarkType.BUNDLE_WATERMARK);

		setDisplay(pack);
		setPackIndex(i);

		updateBorderColor();
	}

	public void setImageBitmap(Bitmap bitmap) {
		sampleImageView.setImageBitmap(bitmap);
	}

	public void setDisplay(LensPack pack) {
		type = WaterMarkType.BUNDLE_WATERMARK;

		titleTextView.setText(pack.getTitle());

		int resID = getResources().getIdentifier(pack.getSampleImageFilename(), "drawable", "com.sorasoft.dicecam");
		Bitmap image = BitmapFactory.decodeResource(getResources(), resID);
		setImageBitmap(image);
	}

	public WaterMarkType getType() {
		return type;
	}

	public void setPackIndex(int i) {
		mIndex = i;
	}

	public int getPackIndex() {
		return mIndex;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void setBorderColor(View targetView, int borderType) {
		int border = 0;
		switch (borderType) {
		case BORDER_TYPE_HIGHLIGHTED:
			border = R.drawable.highlighted_border;
			break;

		case BORDER_TYPE_SELECTED:
			border = R.drawable.selected_border;
			break;

		case BORDER_TYPE_NORMAL:
		default:
			border = R.drawable.normal_border;
			break;
		}
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			targetView.setBackground(getResources().getDrawable(border));
		} else {
			targetView.setBackgroundDrawable(getResources().getDrawable(border));
		}
	}

	private void updateBorderColor() {
		int border_type = BORDER_TYPE_NORMAL;
		if (mHighlighted) {
			border_type = BORDER_TYPE_HIGHLIGHTED;
		} else if (mSelected) {
			border_type = BORDER_TYPE_SELECTED;
		}

		switch (type) {
		case BUNDLE_WATERMARK:
			setBorderColor(sampleImageView, border_type);
			break;

		// case for other type

		default:
			break;
		}
	}

	public void setHighlighted(final boolean highlighted) {
		mHighlighted = highlighted;
		// Log.d("dicecam", "setHighlighted: " + highlighted);
		updateBorderColor();
	}

	public void setSelected(final boolean selected) {
		mSelected = selected;
		// Log.d("dicecam", "setSelected: " + selected);
		updateBorderColor();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			setHighlighted(true);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			setHighlighted(false);
			break;
		default:
			// Log.d("dicecam", "onTouch: " + event.getAction());
			break;
		}
		return false;
	}

	public void setLocked(boolean locked) {
		if (locked) {
			if (lockerImageView == null) {
				lockerImageView = new ImageView(getContext());
				int resID = getResources().getIdentifier("ico_locker", "drawable", "com.sorasoft.dicecam");
				lockerImageView.setImageResource(resID);
				lockerImageView.setBackgroundColor(Color.TRANSPARENT);
				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.TOP);
				params.setMargins(0, dp2px(getLockerTopMarginInDP()), 0, 0);
				addView(lockerImageView, params);
			}
		} else {
			if (lockerImageView != null) {
				lockerImageView.setImageResource(0);
				removeView(lockerImageView);
				lockerImageView = null;
			}
		}
	}

	protected int getLockerTopMarginInDP() {
		return 0;
	}

	protected void setPackTitleTextViewAppearance(Context ctx, TextView tv) {
		tv.setTextAppearance(ctx, R.style.LensSelectorPackButtonTextView);
	}

	protected void setLensTitleTextViewAppearance(Context ctx, TextView tv) {
		tv.setTextAppearance(ctx, R.style.LensSelectorLensButtonTextView);
	}

	public int getPackHeightInDP() {
		// TODO Auto-generated method stub
		return PACK_HEIGHT_IN_DP;
	}
}

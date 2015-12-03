package com.sorasoft.dicecam.view.watermark;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sorasoft.dicecam.R;
import com.sorasoft.dicecam.DiceCamera;
import com.sorasoft.dicecam.engine.filter.DicecamLens;
import com.sorasoft.dicecam.engine.filter.LensCenter;
import com.sorasoft.dicecam.engine.filter.LensPack;
import com.sorasoft.dicecam.util.UserInterfaceUtil;

/**
 * Copied from WatermarkSelector
 */

public class WatermarkSelector extends FrameLayout implements View.OnClickListener {

	public WatermarkSelectorButton mCurrentSelectedButton;

	public interface WaterMarkSelectorListner {
		void onSelectLens(DicecamLens lens, WatermarkSelector lensSelector);

		void onShown(WatermarkSelector lensSelector);

		void onHidden(WatermarkSelector lensSelector);

		DicecamLens lensSelectorSelectedLens();

		// void onIntensityValueChanged(LensIntensityControlView controlView,
		// float intensity);
		//
		// void onIntensityEditModeChanged(LensIntensityControlView controlView,
		// boolean editMode);
	}

	private WaterMarkSelectorListner mListner = null;

	private Context mContext = null;
	private HorizontalScrollView scrollView = null;
	private LinearLayout scrollLinearLayout = null;
	private ImageButton closeButton = null;

	private boolean initialDisplay = true;

	private static final int NOT_SELECTED = -1;

	private int selectedPackIndex = NOT_SELECTED;
	private int selectedPackScrollX = NOT_SELECTED;

	private int scrollLayoutHeightInPixel = 0;
	private int preferredHeightInPixel = 0;
	private final int CLOSEBUTTON_MINIMUM_HEIGHT_IN_DP = 50;
	private final int CLOSEBUTTON_MAXIMUM_HEIGHT_IN_DP = 200;

	public WatermarkSelector(Context context) {
		super(context);
		mContext = context;
		// initButtons(context);
	}

	public WatermarkSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		// initButtons(context);
	}

	public WatermarkSelector(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		// initButtons(context);
	}

	@Override
	protected void onFinishInflate() {
		initViews();
		super.onFinishInflate();
	}

	private void initViews() {

		if (scrollView == null) {
			scrollView = (HorizontalScrollView) findViewById(R.id.watermark_selector_scroll_view);
		}

		if (scrollLinearLayout == null) {
			scrollLinearLayout = (LinearLayout) findViewById(R.id.watermark_selector_linear_layout);
		}

		closeButton = (ImageButton) findViewById(R.id.watermark_selector_save_button);
		closeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hide();
			}
		});
	}

	public void show() {
		if (initialDisplay) {
			showPackButtons();
			adjustCloseButtonHeight();
			initialDisplay = false;
		}

		setVisibility(VISIBLE);

		if (this.mListner != null) {
			this.mListner.onShown(this);
		}
	}

	public void hide() {
		setVisibility(INVISIBLE);

		if (this.mListner != null) {
			this.mListner.onHidden(this);
		}
	}

	private void showPackButtons() {

		scrollLinearLayout.removeAllViews();
		selectedPackIndex = NOT_SELECTED;

		int initialPackSelectIndex = NOT_SELECTED;
		View initialPackSelectButton = null;

		try {
			LensCenter center = LensCenter.defaultCenter();
			DicecamLens currentLens = mListner == null ? null : mListner.lensSelectorSelectedLens();

			// Draw Watermark buttons
			for (int i = 0; i < center.sizeOfPacks(); i++) {
				LensPack pack = center.getPackAt(i);

				WatermarkSelectorButton button = createPackButton(mContext, pack, i);
				if (scrollLayoutHeightInPixel < 1) {
					scrollLayoutHeightInPixel = UserInterfaceUtil.dp2px(12 + 12 + button.getPackHeightInDP(), button);
				}
				button.setOnClickListener(this);
				scrollLinearLayout.addView(button);

				if (initialDisplay && selectedPackScrollX == NOT_SELECTED && currentLens != null && pack.has(currentLens)) {
					initialPackSelectIndex = i;
					initialPackSelectButton = button;
				}
			}

			// scroll to
			if (selectedPackScrollX != NOT_SELECTED) {
				final int x = selectedPackScrollX;
				scrollLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						scrollLinearLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						scrollView.scrollTo(x, 0);
					}
				});
			} else if (initialPackSelectIndex != NOT_SELECTED) {
				final View btn = initialPackSelectButton;
				scrollLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						scrollLinearLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						int x = btn.getLeft() + btn.getWidth() / 2 - scrollView.getWidth() / 2;
						scrollView.scrollTo(x, 0);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private WatermarkSelectorButton createPackButton(Context ctx, LensPack pack, int index) {
		switch (DiceCamera.screenLayoutSize) {
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
		case Configuration.SCREENLAYOUT_SIZE_LARGE:
			return new WatermarkSelectorLargeButton(ctx, pack, index);
		default:
			return new WatermarkSelectorButton(ctx, pack, index);
		}
	}

	public WaterMarkSelectorListner getLensSelectorListner() {
		return mListner;
	}

	public void setLensSelectorListener(WaterMarkSelectorListner mListner) {
		this.mListner = mListner;
	}

	@Override
	public void onClick(View v) {
		mCurrentSelectedButton = (WatermarkSelectorButton) v;
		Toast.makeText(mContext, "adfasdfsadf", 1).show();
	}

	public int getPreferredHeightInPixel() {
		return preferredHeightInPixel;
	}

	public void setPreferredHeightInPixel(int preferredHeightInPixel) {
		this.preferredHeightInPixel = preferredHeightInPixel;
	}

	private void adjustCloseButtonHeight() {
		if (preferredHeightInPixel < 1) return;
		final int buttonScrollHeight = scrollLayoutHeightInPixel; // scrollView.getHeight();
		int adjustedHeight = preferredHeightInPixel - buttonScrollHeight;
		Log.d("dicecam", "adj_CBH buttonScrollHeight: " + buttonScrollHeight);
		Log.d("dicecam", "adj_CBH preferredHeightInPixel: " + preferredHeightInPixel);
		Log.d("dicecam", "adj_CBH adjustedHeight: " + adjustedHeight);

		final float density = UserInterfaceUtil.densityOfView(closeButton);
		final int maxHeight = UserInterfaceUtil.dp2px(CLOSEBUTTON_MAXIMUM_HEIGHT_IN_DP, density);
		final int minHeight = UserInterfaceUtil.dp2px(CLOSEBUTTON_MINIMUM_HEIGHT_IN_DP, density);
		Log.d("dicecam", "adj_CBH height range: " + minHeight + " ~ " + maxHeight);

		adjustedHeight = Math.min(maxHeight, Math.max(minHeight, adjustedHeight));
		Log.d("dicecam", "adj_CBH rangedAdjustedHeight: " + adjustedHeight);

		// set adjusted height
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) closeButton.getLayoutParams();
		params.height = adjustedHeight;
		closeButton.setLayoutParams(params);
	}
}

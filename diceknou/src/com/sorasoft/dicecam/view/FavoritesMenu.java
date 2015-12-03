package com.sorasoft.dicecam.view;

import java.util.HashMap;
import java.util.List;

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

import com.sorasoft.dicecam.R;
import com.sorasoft.dicecam.BaseActivity;
import com.sorasoft.dicecam.DiceCamera;
import com.sorasoft.dicecam.engine.filter.DicecamLens;
import com.sorasoft.dicecam.engine.filter.LensCenter;
import com.sorasoft.dicecam.engine.filter.LensPack;
import com.sorasoft.dicecam.setting.Settings;
import com.sorasoft.dicecam.util.UserInterfaceUtil;
import com.sorasoft.dicecam.view.CategoriesButton.LensSelectorButtonType;

public class FavoritesMenu extends FrameLayout implements View.OnClickListener {

	public CategoriesButton mSelectedButton;

	public interface FavoritesListener {
		void onSelectLens(DicecamLens lens, FavoritesMenu favoritesMenu);

		void onCloseButtonTouched();

		void onShown(FavoritesMenu favoritesMenu);

		void onHidden(FavoritesMenu favoritesMenu);

		void onIntensityValueChanged(LensIntensityControlView controlView, float intensity);

		void onIntensityEditModeChanged(LensIntensityControlView controlView, boolean editMode);

		Object lensSelectorSelectedLens();
	}

	private FavoritesListener mListner = null;

	private Context mContext = null;
	private HorizontalScrollView scrollView = null;
	private LinearLayout scrollLinearLayout = null;
	public LensIntensityControlView lensIntensityControlView = null;
	private ImageButton closeButton = null;

	private static final int NOT_SELECTED = -1;

	private int selectedLensIndex = NOT_SELECTED;
	private int selectedPackIndex = NOT_SELECTED;
	private int selectedPackScrollX = NOT_SELECTED;

	private int scrollLayoutHeightInPixel = 0;
	private int preferredHeightInPixel = 0;
	private final int CLOSEBUTTON_MINIMUM_HEIGHT_IN_DP = 50;
	private final int CLOSEBUTTON_MAXIMUM_HEIGHT_IN_DP = 200;

	public FavoritesMenu(Context context) {
		super(context);
		mContext = context;
		// initButtons(context);
	}

	public FavoritesMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		// initButtons(context);
	}

	public FavoritesMenu(Context context, AttributeSet attrs, int defStyleAttr) {
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
			scrollView = (HorizontalScrollView) findViewById(R.id.lens_selector_scroll_view);
		}

		if (scrollLinearLayout == null) {
			scrollLinearLayout = (LinearLayout) findViewById(R.id.lens_buttons_linear_layout);
		}

		if (lensIntensityControlView == null) {
			lensIntensityControlView = (LensIntensityControlView) findViewById(R.id.lens_intensity_control_view);
			lensIntensityControlView.setOnValueChangeListener(new LensIntensityControlView.OnValueChangeListener() {

				@Override
				public void onValueChanged(LensIntensityControlView controlView, float intensity) {
					if (mListner == null)
						return;
					mListner.onIntensityValueChanged(controlView, intensity);
				}
			});
			lensIntensityControlView.setOnEditModeChangeListener(new LensIntensityControlView.OnEditModeChangeListener() {

				@Override
				public void onEditModeChanged(LensIntensityControlView controlView, boolean editMode) {
					if (mListner == null)
						return;
					mListner.onIntensityEditModeChanged(controlView, editMode);
				}
			});
		}

		lensIntensityControlView.setEnabled(false); // joe_later
		lensIntensityControlView.setVisibility(View.INVISIBLE); // joe_later

		closeButton = (ImageButton) findViewById(R.id.lens_selector_close_button);
		
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeButtonTouched();
			}
		});
		
	}

	public void show() {
			favoriteCategories();

		DicecamLens currentLens = (DicecamLens) (mListner == null ? null : mListner.lensSelectorSelectedLens());
		
		if (currentLens != null) {
			lensIntensityControlView.setColor(currentLens.getSampleColor());
		}
		lensIntensityControlView.setIntensity(Settings.getInstance().getLensIntensity());

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

	private void closeButtonTouched() {
		if (this.mListner != null) {
			this.mListner.onCloseButtonTouched();
		} else {
			hide();
		}
	}

	private void favoriteCategories() {

		try {
			scrollLinearLayout.removeAllViews();
		} catch (Exception e) {
			e.printStackTrace();
		}

		selectedPackIndex = NOT_SELECTED;

		int initialPackSelectIndex = NOT_SELECTED;
		View initialPackSelectButton = null;

		try {

			// categories DicecamLens currentLens = mListner == null ? null :
			// mListner.lensSelectorSelectedLens();
			//
			// for (int i = 0; i < center.sizeOfPacks(); i++) {
			// LensPack pack = center.getPackAt(i);
			//
			// CategoriesButton button = createPackButton(mContext, pack, i);
			// if (scrollLayoutHeightInPixel < 1) {
			// scrollLayoutHeightInPixel = UserInterfaceUtil.dp2px(12 + 12 +
			// button.getPackHeightInDP(), button);
			// }
			// button.setOnClickListener(this);
			// scrollLinearLayout.addView(button);
			//
			// if (initialDisplay && selectedPackScrollX == NOT_SELECTED &&
			// currentLens != null && pack.has(currentLens)) {
			// initialPackSelectIndex = i;
			// initialPackSelectButton = button;
			// }
			// }

			showFavoritesButton(1, 1);

			// categories if (selectedPackScrollX != NOT_SELECTED) {
			// final int x = selectedPackScrollX;
			// scrollLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new
			// ViewTreeObserver.OnGlobalLayoutListener() {
			// @SuppressWarnings("deprecation")
			// @Override
			// public void onGlobalLayout() {
			// scrollLinearLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			// scrollView.scrollTo(x, 0);
			// }
			// });
			// } else if (initialPackSelectIndex != NOT_SELECTED) {
			// final View btn = initialPackSelectButton;
			// scrollLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new
			// ViewTreeObserver.OnGlobalLayoutListener() {
			// @SuppressWarnings("deprecation")
			// @Override
			// public void onGlobalLayout() {
			// scrollLinearLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			// int x = btn.getLeft() + btn.getWidth() / 2 -
			// scrollView.getWidth() / 2;
			// scrollView.scrollTo(x, 0);
			// }
			// });
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showFavoritesButton(int packIndex, int indexOfScroll) {

		CategoriesButton button = null;
		CategoriesButton backButton = null;
		selectedPackIndex = packIndex;
		selectedPackScrollX = scrollView.getScrollX();
		int buttonGetWidth = 0;

		scrollLinearLayout.removeAllViews();

		try {
			// later // add back button
			// backButton = createBackButton(mContext);
			// backButton.setAsBackButtonWithPackIndex(packIndex);
			// backButton.setOnClickListener(this);
			// scrollLinearLayout.addView(backButton);

			// add lens buttons
//			DicecamLens lens = mListner == null ? null : mListner.lensSelectorSelectedLens();
			
			List<DicecamLens> lst = ((DiceCamera)DiceCamera.mContext).mDataFavorites;
			
//			for (int i = 0; i < pack.size(); i++) {
//				DicecamLens lens = pack.get(i);

			for(DicecamLens lens : lst) {
				boolean locked = LensCenter.defaultCenter().lensIsLocked(lens);

				button = createLensButton(mContext, lens, 1, 1);
				if (buttonGetWidth == 0)
					buttonGetWidth = UserInterfaceUtil.dp2px(button.getLensWidthInDP(), scrollLinearLayout);
				button.setLocked(locked);
				button.setOnClickListener(this);
				button.setLensID(lens.getID());
				scrollLinearLayout.addView(button);
				mSelectedButton = button;
			}
			
//soon				if (currentLens != null && currentLens == lens) {
//					button.setSelected(true);
//				}
				
				
//			}

			// scroll
			if (indexOfScroll == 0) {
				scrollView.scrollTo(0, 0);
			} else if (button != null) {
				int scrollX = 0;
				if (indexOfScroll > 1) {
					final int centerDiffInPx = UserInterfaceUtil.dp2px(CategoriesButton.LENS_WIDTH_IN_DP + CategoriesButton.MARGIN_X_IN_DP,
							this) / 2;
					final int stepInPx = UserInterfaceUtil.dp2px(CategoriesButton.LENS_WIDTH_IN_DP + CategoriesButton.MARGIN_X_IN_DP * 2,
							this);
					scrollX = backButton.getWidth() + UserInterfaceUtil.dp2px(CategoriesButton.MARGIN_X_IN_DP, button) + stepInPx
							* (indexOfScroll - 2) + centerDiffInPx;
				}
				scrollView.smoothScrollTo(scrollX, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private CategoriesButton createLensButton(Context ctx, DicecamLens lens, int i, int packIndex) {
		switch (DiceCamera.screenLayoutSize) {
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
		case Configuration.SCREENLAYOUT_SIZE_LARGE:
			return new LensSelectorLargeButton(ctx, lens, i, packIndex);
		default:
			return new CategoriesButton(ctx, lens, i, packIndex);
		}
	}

	private CategoriesButton createBackButton(Context ctx) {
		switch (DiceCamera.screenLayoutSize) {
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
		case Configuration.SCREENLAYOUT_SIZE_LARGE:
			return new LensSelectorLargeButton(ctx);
		default:
			return new CategoriesButton(ctx);
		}
	}

	private CategoriesButton createPackButton(Context ctx, LensPack pack, int index) {
		switch (DiceCamera.screenLayoutSize) {
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
		case Configuration.SCREENLAYOUT_SIZE_LARGE:
			return new LensSelectorLargeButton(ctx, pack, index);
		default:
			return new CategoriesButton(ctx, pack, index);
		}
	}

	public FavoritesListener getLensSelectorListner() {
		return mListner;
	}

	public void setLensSelectorListener(FavoritesListener mListner) {
		this.mListner = mListner;
	}

	public void onBackPressed() {
		if (selectedPackIndex == NOT_SELECTED) {
			closeButtonTouched();
		} else {
			favoriteCategories();
		}
	}

	@Override
	public void onClick(View v) {
		CategoriesButton b = (CategoriesButton)v;
		int vS = Integer.parseInt(b.getLensID());
		Log.d("hjh", "getID" + ""+vS);
		try {
			((DiceCamera)DiceCamera.mContext).setLens(LensCenter.defaultCenter().getLensAt(vS));
		} catch (Exception e) {
			Log.e("hjh", "Favorites onClick ERROR");
			e.printStackTrace();
		}
	}

	public int getPreferredHeightInPixel() {
		return preferredHeightInPixel;
	}

	public void setPreferredHeightInPixel(int preferredHeightInPixel) {
		this.preferredHeightInPixel = preferredHeightInPixel;
	}

	private void updateSelectedIndexes() {
		if (mSelectedButton != null && mSelectedButton.getType() == LensSelectorButtonType.Lens) {
			selectedLensIndex = mSelectedButton.getLensIndex();
			selectedPackIndex = mSelectedButton.getPackIndex();
		} else {
			try {
				selectedLensIndex = LensCenter.defaultCenter().getLensIndex(Settings.getInstance().getCurrentLensID());
				selectedPackIndex = LensCenter.defaultCenter().getPackIndex(Settings.getInstance().getCurrentLensID());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean selectNextLens() {
		CategoriesButton vLensSelectionButton = mSelectedButton;
		if (vLensSelectionButton == null)
			return false;

		updateSelectedIndexes();
		int vCurrentIndex = selectedLensIndex;
		int vCurrentPackIndex = selectedPackIndex;

		try {
			if (++vCurrentIndex > LensCenter.defaultCenter().getPackAt(vCurrentPackIndex).size() - 1) {
				if (++vCurrentPackIndex >= LensCenter.defaultCenter().packs().size() - 1) {
					vCurrentPackIndex = 0;
					vCurrentIndex = 0;
				} else
					vCurrentIndex = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		vLensSelectionButton.setLensIndex(vCurrentIndex);
		vLensSelectionButton.setPackIndex(vCurrentPackIndex);
		showFavoritesButton(vLensSelectionButton.getPackIndex(), vCurrentIndex);
		return true;
	}

	public boolean selectPreviousLens() {
		CategoriesButton vLensSelectionButton = mSelectedButton;
		if (vLensSelectionButton == null)
			return false;

		updateSelectedIndexes();
		int vCurrentIndex = selectedLensIndex;
		int vCurrentPackIndex = selectedPackIndex;

		if (vCurrentIndex == 0) {
			try {
				if (--vCurrentPackIndex < 0)
					vCurrentPackIndex = LensCenter.defaultCenter().packs().size() - 1;
				vCurrentIndex = LensCenter.defaultCenter().getPackAt(vCurrentPackIndex).size();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			vLensSelectionButton.setPackIndex(vCurrentPackIndex);
		}
		vLensSelectionButton.setLensIndex(vCurrentIndex - 1);
		showFavoritesButton(vCurrentPackIndex, vCurrentIndex - 1);
		return true;
	}
}

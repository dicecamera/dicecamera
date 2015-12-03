package com.sorasoft.dicecam;

import com.sorasoft.dicecam.R;
import com.sorasoft.dicecam.setting.Settings;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class CollageSelector {

	private DiceCamera mainActivity;
	private CollageProvider mCollageProvider;

	private float[] mCropInfoRegion = { 0.f, 0.f, 1.f, 1.f };
	private int mCropInfoWidth = 0;
	private int mCropInfoHeight = 0;

	public CollageSelector() {
		mainActivity = null;
		mCollageProvider = null;
	}

	public CollageSelector(DiceCamera ma) {
		mainActivity = ma;
		mCollageProvider = new CollageProvider(mainActivity);

		initiateFrameCategories();

		ImageButton frame_selection_34_single = (ImageButton) mainActivity.findViewById(R.id.frame_selection_34_single);
		frame_selection_34_single.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_SINGLE);
			}
		});

		ImageButton frame_selection_34_double_vert = (ImageButton) mainActivity.findViewById(R.id.frame_selection_34_double_vert);
		frame_selection_34_double_vert.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_DOUBLE_VERTICAL);
			}
		});

		ImageButton frame_selection_34_double_hori = (ImageButton) mainActivity.findViewById(R.id.frame_selection_34_double_hori);
		frame_selection_34_double_hori.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_DOUBLE_HORIZONTAL);
			}
		});

		ImageButton frame_selection_34_triple_vert = (ImageButton) mainActivity.findViewById(R.id.frame_selection_34_triple_vert);
		frame_selection_34_triple_vert.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_TRIPLE_VERTICAL);
			}
		});

		ImageButton frame_selection_34_triple_hori = (ImageButton) mainActivity.findViewById(R.id.frame_selection_34_triple_hori);
		frame_selection_34_triple_hori.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_TRIPLE_HORIZONTAL);
			}
		});

		ImageButton frame_selection_34_quad = (ImageButton) mainActivity.findViewById(R.id.frame_selection_34_quad);
		frame_selection_34_quad.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE);
			}
		});

		ImageButton frame_selection_34_nonu = (ImageButton) mainActivity.findViewById(R.id.frame_selection_34_nonu);
		frame_selection_34_nonu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_NONUPLE);
			}
		});

		ImageButton frame_selection_34_quad_hori = (ImageButton) mainActivity.findViewById(R.id.frame_selection_34_quad_hori);
		frame_selection_34_quad_hori.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_HORIZONTAL);
			}
		});

		ImageButton frame_selection_34_quad_vert = (ImageButton) mainActivity.findViewById(R.id.frame_selection_34_quad_vert);
		frame_selection_34_quad_vert.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_VERTICAL);
			}
		});

		ImageButton frame_selection_11_single = (ImageButton) mainActivity.findViewById(R.id.frame_selection_11_single);
		frame_selection_11_single.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_SINGLE);
			}
		});

		ImageButton frame_selection_11_double_vert = (ImageButton) mainActivity.findViewById(R.id.frame_selection_11_double_vert);
		frame_selection_11_double_vert.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_DOUBLE_VERTICAL);
			}
		});

		ImageButton frame_selection_11_double_hori = (ImageButton) mainActivity.findViewById(R.id.frame_selection_11_double_hori);
		frame_selection_11_double_hori.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_DOUBLE_HORIZONTAL);
			}
		});

		ImageButton frame_selection_11_triple_vert = (ImageButton) mainActivity.findViewById(R.id.frame_selection_11_triple_vert);
		frame_selection_11_triple_vert.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_TRIPLE_VERTICAL);
			}
		});

		ImageButton frame_selection_11_triple_hori = (ImageButton) mainActivity.findViewById(R.id.frame_selection_11_triple_hori);
		frame_selection_11_triple_hori.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_TRIPLE_HORIZONTAL);
			}
		});

		ImageButton frame_selection_11_quad = (ImageButton) mainActivity.findViewById(R.id.frame_selection_11_quad);
		frame_selection_11_quad.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE);
			}
		});

		ImageButton frame_selection_11_nonu = (ImageButton) mainActivity.findViewById(R.id.frame_selection_11_nonu);
		frame_selection_11_nonu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_NONUPLE);
			}
		});

		ImageButton frame_selection_11_quad_hori = (ImageButton) mainActivity.findViewById(R.id.frame_selection_11_quad_hori);
		frame_selection_11_quad_hori.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_HORIZONTAL);
			}
		});

		ImageButton frame_selection_11_quad_vert = (ImageButton) mainActivity.findViewById(R.id.frame_selection_11_quad_vert);
		frame_selection_11_quad_vert.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_VERTICAL);
			}
		});

		// division

		ImageButton frame_selection_div_single = (ImageButton) mainActivity.findViewById(R.id.frame_selection_div_single);
		frame_selection_div_single.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_SINGLE);
			}
		});

		ImageButton frame_selection_div_double_vert = (ImageButton) mainActivity.findViewById(R.id.frame_selection_div_double_vert);
		frame_selection_div_double_vert.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_VERTICAL);
			}
		});

		ImageButton frame_selection_div_double_hori = (ImageButton) mainActivity.findViewById(R.id.frame_selection_div_double_hori);
		frame_selection_div_double_hori.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_HORIZONTAL);
			}
		});

		ImageButton frame_selection_div_triple_vert = (ImageButton) mainActivity.findViewById(R.id.frame_selection_div_triple_vert);
		frame_selection_div_triple_vert.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_VERTICAL);
			}
		});

		ImageButton frame_selection_div_triple_hori = (ImageButton) mainActivity.findViewById(R.id.frame_selection_div_triple_hori);
		frame_selection_div_triple_hori.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_HORIZONTAL);
			}
		});

		ImageButton frame_selection_div_quad = (ImageButton) mainActivity.findViewById(R.id.frame_selection_div_quad);
		frame_selection_div_quad.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_SQUARE);
			}
		});

		ImageButton frame_selection_div_nonu = (ImageButton) mainActivity.findViewById(R.id.frame_selection_div_nonu);
		frame_selection_div_nonu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_NONUPLE);
			}
		});

		ImageButton frame_selection_div_quad_hori = (ImageButton) mainActivity.findViewById(R.id.frame_selection_div_quad_hori);
		frame_selection_div_quad_hori.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_HORIZONTAL);
			}
		});

		ImageButton frame_selection_div_quad_vert = (ImageButton) mainActivity.findViewById(R.id.frame_selection_div_quad_vert);
		frame_selection_div_quad_vert.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFrame(CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_VERTICAL);
			}
		});

	}

	public boolean isVisible() {
		final LinearLayout frameSelectionFrame = (LinearLayout) mainActivity.findViewById(R.id.frame_selection);
		return (frameSelectionFrame.getVisibility() == View.VISIBLE) ? true : false;
	}

	public void onFrameSelectionButtonClick(ImageButton frameSelectionButton) {

		final LinearLayout frameSelectionFrame = (LinearLayout) mainActivity.findViewById(R.id.frame_selection);
		final FrameLayout frameSelection = (FrameLayout) mainActivity.findViewById(R.id.frame_selector);
		final LinearLayout frame34SelectionFrame = (LinearLayout) mainActivity.findViewById(R.id.frame_selection_34);
		final LinearLayout frame11SelectionFrame = (LinearLayout) mainActivity.findViewById(R.id.frame_selection_11);
		final LinearLayout framedivSelectionFrame = (LinearLayout) mainActivity.findViewById(R.id.frame_selection_div);

		if (isVisible()) {
			makeInvisible();
		} else {
			adjustBottomMargins();

			frameSelectionFrame.setVisibility(View.VISIBLE);
			frameSelection.setVisibility(View.VISIBLE);

			switch (getSelectedFrame()) {
			case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_SINGLE:
			case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_DOUBLE_HORIZONTAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_DOUBLE_VERTICAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_TRIPLE_HORIZONTAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_TRIPLE_VERTICAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE:
			case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_NONUPLE:
			case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_VERTICAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_HORIZONTAL: {

				frame34SelectionFrame.setVisibility(View.INVISIBLE);

				break;
			}

			case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_SINGLE:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_DOUBLE_HORIZONTAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_DOUBLE_VERTICAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_TRIPLE_HORIZONTAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_TRIPLE_VERTICAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_NONUPLE:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_VERTICAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_HORIZONTAL: {

				frame11SelectionFrame.setVisibility(View.VISIBLE);

				break;
			}

			case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_SINGLE:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_VERTICAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_HORIZONTAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_VERTICAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_HORIZONTAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_SQUARE:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_NONUPLE:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_HORIZONTAL:
			case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_VERTICAL: {

				framedivSelectionFrame.setVisibility(View.VISIBLE);
				break;
			}
			}
		}
	}

	private void adjustBottomMargins() {
		int marginInPx = mainActivity.getBottomToolbarHeight();

		final FrameLayout frameSelection = (FrameLayout) mainActivity.findViewById(R.id.frame_selector);
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) frameSelection.getLayoutParams();
		params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, marginInPx);
		frameSelection.setLayoutParams(params);
	}

	private void initiateFrameCategories() {

		final LinearLayout frame34icons = (LinearLayout) mainActivity.findViewById(R.id.frame_selection_34);
		final LinearLayout frame11icons = (LinearLayout) mainActivity.findViewById(R.id.frame_selection_11);
		final LinearLayout frameEtcIcons = (LinearLayout) mainActivity.findViewById(R.id.frame_selection_div);

		final ImageButton frame34Button = (ImageButton) mainActivity.findViewById(R.id.frame_cat34);
		final ImageButton frame11Button = (ImageButton) mainActivity.findViewById(R.id.frame_cat11);
		final ImageButton framedivButton = (ImageButton) mainActivity.findViewById(R.id.frame_catdiv);

		frame34Button.setBackgroundColor(Color.TRANSPARENT);
		frame11Button.setBackgroundColor(Color.TRANSPARENT);
		framedivButton.setBackgroundColor(Color.TRANSPARENT);

		final Animation transparentAnim = new AlphaAnimation(0.0f, 1.0f);
		transparentAnim.setRepeatCount(0);
		transparentAnim.setDuration(500);

		frame34Button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				frame11icons.setVisibility(View.INVISIBLE);
				frameEtcIcons.setVisibility(View.INVISIBLE);
				frame34icons.setAnimation(transparentAnim);
				frame34icons.setVisibility(View.VISIBLE);
				transparentAnim.start();
			}
		});

		frame11Button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				frame34icons.setVisibility(View.INVISIBLE);
				frameEtcIcons.setVisibility(View.INVISIBLE);
				frame11icons.setAnimation(transparentAnim);
				frame11icons.setVisibility(View.VISIBLE);
				transparentAnim.start();
			}
		});

		framedivButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				frame11icons.setVisibility(View.INVISIBLE);
				frame34icons.setVisibility(View.INVISIBLE);
				frameEtcIcons.setAnimation(transparentAnim);
				frameEtcIcons.setVisibility(View.VISIBLE);
				transparentAnim.start();
			}
		});

	}

	public void makeInvisible() {
		LinearLayout frameSelectionFrame = (LinearLayout) mainActivity.findViewById(R.id.frame_selection);
		FrameLayout frameSelection = (FrameLayout) mainActivity.findViewById(R.id.frame_selector);
		LinearLayout frame34SelectionFrame = (LinearLayout) mainActivity.findViewById(R.id.frame_selection_34);
		LinearLayout frame11SelectionFrame = (LinearLayout) mainActivity.findViewById(R.id.frame_selection_11);
		LinearLayout framedivSelectionFrame = (LinearLayout) mainActivity.findViewById(R.id.frame_selection_div);

		frameSelectionFrame.setVisibility(View.INVISIBLE);
		frameSelection.setVisibility(View.INVISIBLE);
		frame34SelectionFrame.setVisibility(View.INVISIBLE);
		frame11SelectionFrame.setVisibility(View.INVISIBLE);
		framedivSelectionFrame.setVisibility(View.INVISIBLE);
	}

	public void setSelectedFrame(int sFrame) {

		mainActivity.setCollageStatus(sFrame);

		Settings.getInstance().setFrameSelection(mainActivity.collageStatus);


		makeInvisible();
	}

	public int getSelectedFrame() {
		return Settings.getInstance().getFrameSelection();
	}

	private void calculateCropRegion(int cols, int rows, int sourceWidth, int sourceHeight) {
		float preferRatioWidth = 1.f / (float) cols;
		float preferRatioHeight = 1.f / (float) rows;
		float preferRatio = preferRatioWidth / preferRatioHeight;
		float sourceRatio = (float) sourceWidth / (float) sourceHeight;
		float cropWidth = 0.f;
		float cropHeight = 0.f;
		if (sourceRatio > preferRatio) {
			cropHeight = (float) sourceHeight;
			cropWidth = cropHeight * preferRatio;
		} else {
			cropWidth = (float) sourceWidth;
			cropHeight = cropWidth / preferRatio;
		}
		float left = ((float) sourceWidth - cropWidth) / (float) sourceWidth / 2.f;
		float top = ((float) sourceHeight - cropHeight) / (float) sourceHeight / 2.f;
		float[] cropRegion = { left, top, 1.f - left, 1.f - top };
		mCropInfoRegion = cropRegion;
		mCropInfoWidth = (int) cropWidth;
		mCropInfoHeight = (int) cropHeight;
		Log.d("dicecam", "cropSize: " + cropWidth + ", " + cropHeight + " (" + sourceWidth + ", " + sourceHeight + ")");
	}

	public void calculateCropInformation(int collageStatus, int sourceWidth, int sourceHeight) {
		switch (collageStatus) {
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_SINGLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_DOUBLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_DOUBLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_TRIPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_TRIPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_NONUPLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_SINGLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_SQUARE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_NONUPLE:
			int shortLength = Math.min(sourceWidth, sourceHeight);

			float left = ((float) sourceWidth - (float) shortLength) / (float) sourceWidth / 2.f;
			float top = ((float) sourceHeight - (float) shortLength) / (float) sourceHeight / 2.f;
			float[] squareRegion = { left, top, 1.f - left, 1.f - top };
			mCropInfoRegion = squareRegion;
			mCropInfoWidth = shortLength;
			mCropInfoHeight = shortLength;
			break;

		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_VERTICAL:
			calculateCropRegion(2, 1, sourceWidth, sourceHeight);
			break;
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_VERTICAL:
			calculateCropRegion(3, 1, sourceWidth, sourceHeight);
			break;
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_VERTICAL:
			calculateCropRegion(4, 1, sourceWidth, sourceHeight);
			break;

		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_HORIZONTAL:
			calculateCropRegion(1, 2, sourceWidth, sourceHeight);
			break;
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_HORIZONTAL:
			calculateCropRegion(1, 3, sourceWidth, sourceHeight);
			break;
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_HORIZONTAL:
			calculateCropRegion(1, 4, sourceWidth, sourceHeight);
			break;

		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_SINGLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_DOUBLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_DOUBLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_TRIPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_TRIPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_NONUPLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_HORIZONTAL:
		default:
			float[] cropRegion = { 0.f, 0.f, 1.f, 1.f };
			mCropInfoRegion = cropRegion;
			mCropInfoWidth = sourceWidth;
			mCropInfoHeight = sourceHeight;
			break;
		}
		Log.d("dicecam", "CropInfo: " + mCropInfoWidth + ", " + mCropInfoHeight);
		Log.d("dicecam", "CropRegion: " + mCropInfoRegion[0] + ", " + mCropInfoRegion[1] + ", " + mCropInfoRegion[2] + ", "
				+ mCropInfoRegion[3]);
	}

	public float[] getCropRegion() {
		return mCropInfoRegion;
	}

	public int getCropWidth() {
		return mCropInfoWidth;
	}

	public int getCropHeight() {
		return mCropInfoHeight;
	}
}

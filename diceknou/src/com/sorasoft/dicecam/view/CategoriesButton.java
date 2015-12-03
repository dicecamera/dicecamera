package com.sorasoft.dicecam.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sorasoft.dicecam.R;
import com.sorasoft.dicecam.engine.filter.DicecamLens;
import com.sorasoft.dicecam.engine.filter.LensPack;


public class CategoriesButton extends FrameLayout implements View.OnTouchListener {
	public static enum LensSelectorButtonType {
		Pack,
		Lens,
		BackToPack
	}
	private ImageView sampleImageView = null;
	private TextView titleTextView = null;
	private LinearLayout linearLayout = null;
	private ImageView lockerImageView = null;

	private LensSelectorButtonType type = LensSelectorButtonType.Pack;
	private int packIndex = -1;
	private int lensIndex = -1;
	
	private String lensID = null;
	
	public final static int MARGIN_X_IN_DP = 5;
	public final static int MARGIN_Y_IN_DP = 0;
	
	public final static int PACK_WIDTH_IN_DP = 76;
	public final static int PACK_HEIGHT_IN_DP = 76;
	
	public final static int LENS_WIDTH_IN_DP = 48;
	public final static int LENS_HEIGHT_IN_DP = 76;
	public final static int LENS_TEXT_HEIGHT_IN_DP = 28;
	
	public final static int BACK_BUTTON_WIDTH_IN_DP = 50;
	public final static int BACK_BUTTON_HEIGHT_IN_DP = 50;
	public final static int BACK_BUTTON_MARGIN_Y_IN_DP = 13;
	
	private final int BORDER_TYPE_NORMAL = 0;
	private final int BORDER_TYPE_HIGHLIGHTED = 1;
	private final int BORDER_TYPE_SELECTED = 2;
	
	private boolean mHighlighted = false;
	private boolean mSelected = false;
	
	private int dp2px(final int dp) {
		final float scale = getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
	
	public CategoriesButton(Context context, LensSelectorButtonType type) {
		super(context);
		
		setOnTouchListener(this);
		
		setBackgroundColor(Color.WHITE);
		
//		if ( type == LensSelectorButtonType.BackToPack ) {
//			setBackgroundColor(Color.GREEN);
//		} else {
//			setBackgroundColor(Color.RED);
//		}
		
		int content_width_in_dp = 0;
		int content_height_in_dp = 0;
		int margin_x_in_dp = MARGIN_X_IN_DP;
		int margin_y_in_dp = MARGIN_Y_IN_DP;
		
		switch (type) {
		case Pack:
			content_width_in_dp = getPackWidthInDP();
			content_height_in_dp = getPackHeightInDP();
			break;
		case Lens:
			content_width_in_dp = getLensWidthInDP();
			content_height_in_dp = getLensHeightInDP();
			break;
		case BackToPack:
			content_width_in_dp = getBackButtonWidthInDP();
			content_height_in_dp = getBackButtonHeightInDP();
			margin_y_in_dp = getBackButtonMarginYInDP();
			break;
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
	    if (type == LensSelectorButtonType.Lens) {
	    	// add linearLayout
	    	LayoutParams linearParams = new LayoutParams(dp2px(content_width_in_dp), dp2px(content_height_in_dp));
	    	linearParams.gravity = Gravity.CENTER;
	    	linearLayout = new LinearLayout(context);
	    	linearLayout.setOrientation(LinearLayout.VERTICAL);
	    	linearLayout.setLayoutParams(linearParams);
	    	addView(linearLayout);
	    	parentView = linearLayout;
	    }
	    
	    // add sampleImageView
	    LayoutParams imageViewParams = null;
	    sampleImageView = new ImageView(context);
//	    sampleImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	    sampleImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
	    switch (type) {
	    case Pack:
	    	imageViewParams = new LayoutParams(dp2px(content_width_in_dp), dp2px(content_height_in_dp));
	    	break;
	    case Lens:
	    	imageViewParams = new LayoutParams(dp2px(content_width_in_dp), dp2px(content_height_in_dp - getLensTextHeightInDP()));
	    	break;
	    case BackToPack:
	    	imageViewParams = new LayoutParams(dp2px(content_width_in_dp), dp2px(content_height_in_dp));
//	    	imageViewParams.setMargins(0, dp2px(LENS_HEIGHT_IN_DP - BACK_BUTTON_HEIGHT_IN_DP), 0, 0);
	    	break;
	    }
		imageViewParams.gravity = Gravity.CENTER;
		sampleImageView.setLayoutParams(imageViewParams);
		parentView.addView(sampleImageView);
		
		// add titleTextView
		if ( type == LensSelectorButtonType.BackToPack ){
			titleTextView = null;
		} else {
			titleTextView = new TextView(context);
			titleTextView.setGravity(Gravity.CENTER);
			LayoutParams params = null;
			switch (type) {
			case Pack:
				params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				setPackTitleTextViewAppearance(context, titleTextView);
				break;
			case Lens:
			case BackToPack:
				params = new LayoutParams(LayoutParams.MATCH_PARENT, dp2px(getLensTextHeightInDP()));
				params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
				setLensTitleTextViewAppearance(context, titleTextView);
				break;
			}
			titleTextView.setLayoutParams(params);
			parentView.addView(titleTextView);
		}
	}

	public CategoriesButton(Context context) {
		this(context, LensSelectorButtonType.BackToPack);
	}
	
	public CategoriesButton(Context context, LensPack pack, int i) {
		this(context, LensSelectorButtonType.Pack);
		
		setDisplay(pack);
		setPackIndex(i);
		setLensIndex(-1);
		
		updateBorderColor();
	}
	
	public CategoriesButton(Context context, DicecamLens lens, int lensIndex, int packIndex) {
		this(context, LensSelectorButtonType.Lens);
		
		setDisplay(lens);
		setPackIndex(packIndex);
		setLensIndex(lensIndex);
		
		updateBorderColor();
	}

	public void setImageBitmap(Bitmap bitmap) {
		sampleImageView.setImageBitmap(bitmap);
	}
	
	public void setDisplay(LensPack pack) {
		type = LensSelectorButtonType.Pack;
		
		titleTextView.setText(pack.getTitle());
		
		int resID = getResources().getIdentifier(pack.getSampleImageFilename(), "drawable", "com.sorasoft.dicecam");
		Bitmap image = BitmapFactory.decodeResource(getResources(), resID);
		setImageBitmap(image);
	}

	public void setDisplay(DicecamLens lens) {
		type = LensSelectorButtonType.Lens;
		
		titleTextView.setText(lens.getDisplayTitle());
		titleTextView.setBackgroundColor(lens.getSampleColor());
		
		int resID = getResources().getIdentifier(lens.getSampleImageFilename(), "drawable", "com.sorasoft.dicecam");
		Bitmap image = BitmapFactory.decodeResource(getResources(), resID);
		setImageBitmap(image);
	}

	public void setAsBackButtonWithPackIndex(int packIndex) {
		setPackIndex(packIndex);
		setLensIndex(-1);

		type = LensSelectorButtonType.BackToPack;
		
		int resID = getResources().getIdentifier("btn_back_packs", "drawable", "com.sorasoft.dicecam");
		Bitmap image = BitmapFactory.decodeResource(getResources(), resID);
		setImageBitmap(image);
		
		updateBorderColor();
	}

//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		float density = getResources().getDisplayMetrics().density;
//		int width = 80; // unit: dp
//		int height = 80; // unit: dp
//		if ( type == LensSelectorButtonType.Pack ) {
//			width = 120;
//		} else {
//			width = 80;
//		}
//		setMeasuredDimension((int)(density * width), (int)(density * height));
//	}

	public void setLensID(String lensID) {
		this.lensID = lensID;
	}
	
	public String getLensID() {
		return lensID;
	}
	
	public LensSelectorButtonType getType() {
		return type;
	}

	public void setPackIndex(int i) {
		packIndex = i;
	}

	public void setLensIndex(int i) {
		lensIndex = i;
	}

	public int getPackIndex() {
		return packIndex;
	}

	public int getLensIndex() {
		return lensIndex;
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
		if ( currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN ){
			targetView.setBackground(getResources().getDrawable(border));
		} else{
			targetView.setBackgroundDrawable(getResources().getDrawable(border));
		}
	}
	
	private void updateBorderColor() {
		int border_type = BORDER_TYPE_NORMAL;
		if ( mHighlighted ) {
			border_type = BORDER_TYPE_HIGHLIGHTED;
		} else if ( mSelected ) {
			border_type = BORDER_TYPE_SELECTED;
		}
		
		switch (type) {
		case Pack:
	    	setBorderColor(sampleImageView, border_type);
			break;
			
		case Lens:
	    	setBorderColor(linearLayout, border_type);
			break;
			
		case BackToPack:
			setBorderColor(sampleImageView, border_type);
			break;

		default:
			break;
		}
	}
	
	public void setHighlighted(final boolean highlighted) {
		mHighlighted = highlighted;
//		Log.d("dicecam", "setHighlighted: " + highlighted);
		updateBorderColor();
	}
	
	public void setSelected(final boolean selected) {
		mSelected = selected;
//		Log.d("dicecam", "setSelected: " + selected);
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
//			Log.d("dicecam", "onTouch: " + event.getAction());
			break;
		}
		return false;
	}

	public void setLocked(boolean locked) {
		if ( locked ) {
			if ( lockerImageView == null ) {
				lockerImageView = new ImageView(getContext());
				int resID = getResources().getIdentifier("ico_locker", "drawable", "com.sorasoft.dicecam");
				lockerImageView.setImageResource(resID);
				lockerImageView.setBackgroundColor(Color.TRANSPARENT);
				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.TOP);
				params.setMargins(0, dp2px(getLockerTopMarginInDP()), 0, 0);
				addView(lockerImageView, params);
			}
		} else {
			if ( lockerImageView != null ) {
				lockerImageView.setImageResource(0);
				removeView(lockerImageView);
				lockerImageView = null;
			}
		}
	}

	
	protected int getPackWidthInDP() {
		return PACK_WIDTH_IN_DP;
	}
	
	protected int getPackHeightInDP() {
		return PACK_HEIGHT_IN_DP;
	}

	protected int getLensWidthInDP() {
		return LENS_WIDTH_IN_DP;
	}
	
	protected int getLensHeightInDP() {
		return LENS_HEIGHT_IN_DP;
	}

	protected int getLensTextHeightInDP() {
		return LENS_TEXT_HEIGHT_IN_DP;
	}
	
	protected int getBackButtonWidthInDP() {
		return BACK_BUTTON_WIDTH_IN_DP;
	}
	
	protected int getBackButtonHeightInDP() {
		return BACK_BUTTON_HEIGHT_IN_DP;
	}
	
	protected int getBackButtonMarginYInDP() {
		return BACK_BUTTON_MARGIN_Y_IN_DP;
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
}
package com.sorasoft.dicecam.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sorasoft.dicecam.util.UserInterfaceUtil;

public class LensIntensityControlView extends View {

	private final int HEIGHT_IN_DP = 40;
	private final int MARGIN_IN_DP = 18;
	private final int LINE_HEIGHT_IN_DP = 4;
	private final int LEFT_BASE_IN_DP = 1;

	private float height_in_px;
	private float margin_in_px;
	private float line_height_in_px;
	private float left_base_in_px;

	private Paint paint;

	private float intensity;
	private boolean editMode;

	public interface OnValueChangeListener {
		void onValueChanged(LensIntensityControlView controlView, float intensity);
	}

	public interface OnEditModeChangeListener {
		void onEditModeChanged(LensIntensityControlView controlView, boolean editMode);
	}

	private OnValueChangeListener onValueChangeListner = null;
	private OnEditModeChangeListener onEditModeChangeListener = null;

	public LensIntensityControlView(Context context) {
		super(context);
		init(context);
	}

	public LensIntensityControlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public LensIntensityControlView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	@SuppressLint("ResourceAsColor")
	private void init(Context context) {
		height_in_px = (float) UserInterfaceUtil.dp2px(HEIGHT_IN_DP, this);
		margin_in_px = (float) UserInterfaceUtil.dp2px(MARGIN_IN_DP, this);
		line_height_in_px = (float) UserInterfaceUtil.dp2px(LINE_HEIGHT_IN_DP, this);
		left_base_in_px = (float) UserInterfaceUtil.dp2px(LEFT_BASE_IN_DP, this);

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.FILL);

		intensity = 1.f;
		editMode = false;
	}

	public void setIntensity(float i) {
		if (intensity == i)
			return;
		intensity = i;
		invalidate();
	}

	public float getIntensity() {
		return intensity;
	}

	public void setColor(int c) {
		paint.setColor(c);
	}

	public void setOnValueChangeListener(OnValueChangeListener l) {
		this.onValueChangeListner = l;
	}

	public void setOnEditModeChangeListener(OnEditModeChangeListener l) {
		this.onEditModeChangeListener = l;
	}

	private void didIntensityChanged() {
		if (this.onValueChangeListner != null) {
			this.onValueChangeListner.onValueChanged(this, intensity);
		}
	}

	private void enterEditMode() {
		editMode = true;
		if (this.onEditModeChangeListener != null) {
			this.onEditModeChangeListener.onEditModeChanged(this, editMode);
		}
	}

	private void exitEditMode() {
		editMode = false;
		if (this.onEditModeChangeListener != null) {
			this.onEditModeChangeListener.onEditModeChanged(this, editMode);
		}
	}

	private float getDrawingWidth(float intentisy) {
		float width = (float) getWidth();
		return left_base_in_px + (width - left_base_in_px) * intentisy;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (editMode) {
			canvas.drawRect(0.f, 0.f, getDrawingWidth(intensity), height_in_px, paint);
		} else {
			canvas.drawRect(0.f, margin_in_px, getDrawingWidth(intensity), margin_in_px + line_height_in_px, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		float new_i = 0.5f + (e.getX() - getWidth() / 2.f) / getWidth() * 1.28f;
		new_i = Math.max(0.f, Math.min(1.f, new_i));

		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			intensity = new_i;
			didIntensityChanged();
			enterEditMode();
			invalidate();
			break;

		case MotionEvent.ACTION_UP:
			intensity = new_i;
			didIntensityChanged();
			exitEditMode();
			invalidate();
			break;

		default:
			setIntensity(new_i);
			didIntensityChanged();
			break;
		}

		return true;
	}
}

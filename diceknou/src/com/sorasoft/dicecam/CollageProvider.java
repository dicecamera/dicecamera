package com.sorasoft.dicecam;

import com.sorasoft.dicecam.R;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.widget.ImageButton;

public class CollageProvider {
	
	public final static int DICECAM_COLLAGE_STATUS_RECTANGLE_SINGLE = 0;
	public final static int DICECAM_COLLAGE_STATUS_RECTANGLE_DOUBLE_HORIZONTAL = 1;
	public final static int DICECAM_COLLAGE_STATUS_RECTANGLE_DOUBLE_VERTICAL = 2;
	public final static int DICECAM_COLLAGE_STATUS_RECTANGLE_TRIPLE_HORIZONTAL = 3;
	public final static int DICECAM_COLLAGE_STATUS_RECTANGLE_TRIPLE_VERTICAL = 4;
	public final static int DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE = 5;
	public final static int DICECAM_COLLAGE_STATUS_RECTANGLE_NONUPLE = 6;
	public final static int DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_VERTICAL = 7;
	public final static int DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_HORIZONTAL = 8;

	public final static int DICECAM_COLLAGE_STATUS_SQUARE_SINGLE = 10;
	public final static int DICECAM_COLLAGE_STATUS_SQUARE_DOUBLE_HORIZONTAL = 11;
	public final static int DICECAM_COLLAGE_STATUS_SQUARE_DOUBLE_VERTICAL = 12;
	public final static int DICECAM_COLLAGE_STATUS_SQUARE_TRIPLE_HORIZONTAL = 13;
	public final static int DICECAM_COLLAGE_STATUS_SQUARE_TRIPLE_VERTICAL = 14;
	public final static int DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE = 15;
	public final static int DICECAM_COLLAGE_STATUS_SQUARE_NONUPLE = 16;
	public final static int DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_VERTICAL = 17;
	public final static int DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_HORIZONTAL = 18;

	public final static int DICECAM_COLLAGE_STATUS_SECTIONAL_SINGLE = 20;
	public final static int DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_VERTICAL = 21;
	public final static int DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_HORIZONTAL = 22;
	public final static int DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_VERTICAL = 23;
	public final static int DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_HORIZONTAL = 24;
	public final static int DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_SQUARE = 25;
	public final static int DICECAM_COLLAGE_STATUS_SECTIONAL_NONUPLE = 26;
	public final static int DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_HORIZONTAL = 27;
	public final static int DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_VERTICAL = 28;
	
	private Activity activityContext;
	
	CollageProvider(Activity context) {
		activityContext = context;
	}
	
	public static String getOrientationToString(int orientation) {
		switch (orientation) {
		case ImageComposition.DICECAM_ORIENTATION_PORTRAIT: return "Portrait";
		case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_LEFT: return "Landscape-Left";
		case ImageComposition.DICECAM_ORIENTATION_PORTRAIT_UPSIDE_DOWN: return "Portrait-UpsideDown";
		case ImageComposition.DICECAM_ORIENTATION_LANDSCAPE_RIGHT: return "Landscape-Right";
		}
		return null;
	}
	
	private Drawable getCollageButtonDrawable(int collageStatus, int count) {
		int dID = 0;

		switch (collageStatus) {
		// rectangle
		case DICECAM_COLLAGE_STATUS_RECTANGLE_SINGLE:
			dID = R.drawable.frame_single_rectangle;
			break;

		case DICECAM_COLLAGE_STATUS_RECTANGLE_DOUBLE_VERTICAL: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_double_rectangle_horizontal;
				break;
			case 1:
				dID = R.drawable.frame_double_rectangle_horizontal_1;
				break;
			case 2:
				dID = R.drawable.frame_double_rectangle_horizontal_2;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_RECTANGLE_DOUBLE_HORIZONTAL: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_double_rectangle_vertical;
				break;
			case 1:
				dID = R.drawable.frame_double_rectangle_vertical_1;
				break;
			case 2:
				dID = R.drawable.frame_double_rectangle_vertical_2;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_RECTANGLE_TRIPLE_VERTICAL: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_triple_rectangle_horizontal;
				break;
			case 1:
				dID = R.drawable.frame_triple_rectangle_horizontal_1;
				break;
			case 2:
				dID = R.drawable.frame_triple_rectangle_horizontal_2;
				break;
			case 3:
				dID = R.drawable.frame_triple_rectangle_horizontal_3;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_RECTANGLE_TRIPLE_HORIZONTAL: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_triple_rectangle_vertical;
				break;
			case 1:
				dID = R.drawable.frame_triple_rectangle_vertical_1;
				break;
			case 2:
				dID = R.drawable.frame_triple_rectangle_vertical_2;
				break;
			case 3:
				dID = R.drawable.frame_triple_rectangle_vertical_3;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_quad_rectangle;
				break;
			case 1:
				dID = R.drawable.frame_quad_rectangle_1;
				break;
			case 2:
				dID = R.drawable.frame_quad_rectangle_2;
				break;
			case 3:
				dID = R.drawable.frame_quad_rectangle_3;
				break;
			case 4:
				dID = R.drawable.frame_quad_rectangle_4;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_VERTICAL: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_quadruple_rectangle_horizontal;
				break;
			case 1:
				dID = R.drawable.frame_quadruple_rectangle_horizontal_1;
				break;
			case 2:
				dID = R.drawable.frame_quadruple_rectangle_horizontal_2;
				break;
			case 3:
				dID = R.drawable.frame_quadruple_rectangle_horizontal_3;
				break;
			case 4:
				dID = R.drawable.frame_quadruple_rectangle_horizontal_4;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_HORIZONTAL: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_quadruple_rectangle_vertical;
				break;
			case 1:
				dID = R.drawable.frame_quadruple_rectangle_vertical_1;
				break;
			case 2:
				dID = R.drawable.frame_quadruple_rectangle_vertical_2;
				break;
			case 3:
				dID = R.drawable.frame_quadruple_rectangle_vertical_3;
				break;
			case 4:
				dID = R.drawable.frame_quadruple_rectangle_vertical_4;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_RECTANGLE_NONUPLE: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_nonuple_rectangle;
				break;
			case 1:
				dID = R.drawable.frame_nonuple_rectangle_1;
				break;
			case 2:
				dID = R.drawable.frame_nonuple_rectangle_2;
				break;
			case 3:
				dID = R.drawable.frame_nonuple_rectangle_3;
				break;
			case 4:
				dID = R.drawable.frame_nonuple_rectangle_4;
				break;
			case 5:
				dID = R.drawable.frame_nonuple_rectangle_5;
				break;
			case 6:
				dID = R.drawable.frame_nonuple_rectangle_6;
				break;
			case 7:
				dID = R.drawable.frame_nonuple_rectangle_7;
				break;
			case 8:
				dID = R.drawable.frame_nonuple_rectangle_8;
				break;
			case 9:
				dID = R.drawable.frame_nonuple_rectangle_9;
				break;
			}
			break;
		}
		
		// square
		case DICECAM_COLLAGE_STATUS_SQUARE_SINGLE:
			dID = R.drawable.frame_single_square;
			break;

		case DICECAM_COLLAGE_STATUS_SQUARE_DOUBLE_VERTICAL: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_double_square_horizontal;
				break;
			case 1:
				dID = R.drawable.frame_double_square_horizontal_1;
				break;
			case 2:
				dID = R.drawable.frame_double_square_horizontal_2;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_SQUARE_DOUBLE_HORIZONTAL: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_double_square_vertical;
				break;
			case 1:
				dID = R.drawable.frame_double_square_vertical_1;
				break;
			case 2:
				dID = R.drawable.frame_double_square_vertical_2;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_SQUARE_TRIPLE_VERTICAL: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_triple_square_horizontal;
				break;
			case 1:
				dID = R.drawable.frame_triple_square_horizontal_1;
				break;
			case 2:
				dID = R.drawable.frame_triple_square_horizontal_2;
				break;
			case 3:
				dID = R.drawable.frame_triple_square_horizontal_3;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_SQUARE_TRIPLE_HORIZONTAL: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_triple_square_vertical;
				break;
			case 1:
				dID = R.drawable.frame_triple_square_vertical_1;
				break;
			case 2:
				dID = R.drawable.frame_triple_square_vertical_2;
				break;
			case 3:
				dID = R.drawable.frame_triple_square_vertical_3;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_quad_square;
				break;
			case 1:
				dID = R.drawable.frame_quad_square_1;
				break;
			case 2:
				dID = R.drawable.frame_quad_square_2;
				break;
			case 3:
				dID = R.drawable.frame_quad_square_3;
				break;
			case 4:
				dID = R.drawable.frame_quad_square_4;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_VERTICAL: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_quadruple_square_horizontal;
				break;
			case 1:
				dID = R.drawable.frame_quadruple_square_horizontal_1;
				break;
			case 2:
				dID = R.drawable.frame_quadruple_square_horizontal_2;
				break;
			case 3:
				dID = R.drawable.frame_quadruple_square_horizontal_3;
				break;
			case 4:
				dID = R.drawable.frame_quadruple_square_horizontal_4;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_HORIZONTAL: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_quadruple_square_vertical;
				break;
			case 1:
				dID = R.drawable.frame_quadruple_square_vertical_1;
				break;
			case 2:
				dID = R.drawable.frame_quadruple_square_vertical_2;
				break;
			case 3:
				dID = R.drawable.frame_quadruple_square_vertical_3;
				break;
			case 4:
				dID = R.drawable.frame_quadruple_square_vertical_4;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_SQUARE_NONUPLE: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_nonuple_square;
				break;
			case 1:
				dID = R.drawable.frame_nonuple_square_1;
				break;
			case 2:
				dID = R.drawable.frame_nonuple_square_2;
				break;
			case 3:
				dID = R.drawable.frame_nonuple_square_3;
				break;
			case 4:
				dID = R.drawable.frame_nonuple_square_4;
				break;
			case 5:
				dID = R.drawable.frame_nonuple_square_5;
				break;
			case 6:
				dID = R.drawable.frame_nonuple_square_6;
				break;
			case 7:
				dID = R.drawable.frame_nonuple_square_7;
				break;
			case 8:
				dID = R.drawable.frame_nonuple_square_8;
				break;
			case 9:
				dID = R.drawable.frame_nonuple_square_9;
				break;
			}
			break;
		}

		// div
		case DICECAM_COLLAGE_STATUS_SECTIONAL_SINGLE:
			dID = R.drawable.frame_single_div;
			break;

		case DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_VERTICAL: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_double_div_horizontal;
				break;
			case 1:
				dID = R.drawable.frame_double_div_horizontal_1;
				break;
			case 2:
				dID = R.drawable.frame_double_div_horizontal_2;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_HORIZONTAL: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_double_div_vertical;
				break;
			case 1:
				dID = R.drawable.frame_double_div_vertical_1;
				break;
			case 2:
				dID = R.drawable.frame_double_div_vertical_2;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_VERTICAL: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_triple_div_horizontal;
				break;
			case 1:
				dID = R.drawable.frame_triple_div_horizontal_1;
				break;
			case 2:
				dID = R.drawable.frame_triple_div_horizontal_2;
				break;
			case 3:
				dID = R.drawable.frame_triple_div_horizontal_3;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_HORIZONTAL: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_triple_div_vertical;
				break;
			case 1:
				dID = R.drawable.frame_triple_div_vertical_1;
				break;
			case 2:
				dID = R.drawable.frame_triple_div_vertical_2;
				break;
			case 3:
				dID = R.drawable.frame_triple_div_vertical_3;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_SQUARE: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_quad_div;
				break;
			case 1:
				dID = R.drawable.frame_quad_div_1;
				break;
			case 2:
				dID = R.drawable.frame_quad_div_2;
				break;
			case 3:
				dID = R.drawable.frame_quad_div_3;
				break;
			case 4:
				dID = R.drawable.frame_quad_div_4;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_VERTICAL: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_quadruple_div_horizontal;
				break;
			case 1:
				dID = R.drawable.frame_quadruple_div_horizontal_1;
				break;
			case 2:
				dID = R.drawable.frame_quadruple_div_horizontal_2;
				break;
			case 3:
				dID = R.drawable.frame_quadruple_div_horizontal_3;
				break;
			case 4:
				dID = R.drawable.frame_quadruple_div_horizontal_4;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_HORIZONTAL: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_quadruple_div_vertical;
				break;
			case 1:
				dID = R.drawable.frame_quadruple_div_vertical_1;
				break;
			case 2:
				dID = R.drawable.frame_quadruple_div_vertical_2;
				break;
			case 3:
				dID = R.drawable.frame_quadruple_div_vertical_3;
				break;
			case 4:
				dID = R.drawable.frame_quadruple_div_vertical_4;
				break;
			}
			break;
		}

		case DICECAM_COLLAGE_STATUS_SECTIONAL_NONUPLE: {
			switch (count) {
			case 0:
				dID = R.drawable.frame_nonuple_div;
				break;
			case 1:
				dID = R.drawable.frame_nonuple_div_1;
				break;
			case 2:
				dID = R.drawable.frame_nonuple_div_2;
				break;
			case 3:
				dID = R.drawable.frame_nonuple_div_3;
				break;
			case 4:
				dID = R.drawable.frame_nonuple_div_4;
				break;
			case 5:
				dID = R.drawable.frame_nonuple_div_5;
				break;
			case 6:
				dID = R.drawable.frame_nonuple_div_6;
				break;
			case 7:
				dID = R.drawable.frame_nonuple_div_7;
				break;
			case 8:
				dID = R.drawable.frame_nonuple_div_8;
				break;
			case 9:
				dID = R.drawable.frame_nonuple_div_9;
				break;
			}
			break;
		}
			
		default:
			return null;
		}
		
		return dID > 0 ? activityContext.getResources().getDrawable(dID) : null;
	}

	public static int getTotalNumberOfCollagePictures(int collageStatus) {
		switch (collageStatus) {
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_SINGLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_SINGLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_SINGLE:
			return 1;

		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_DOUBLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_DOUBLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_DOUBLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_DOUBLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_VERTICAL:
			return 2;

		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_TRIPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_TRIPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_TRIPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_TRIPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_HORIZONTAL:
			return 3;

		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_QUADRUPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_SQUARE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_VERTICAL:
			return 4;
			
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_NONUPLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_NONUPLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_NONUPLE:
			return 9;
			
		default:
			break;
		}
		return 0;
	}
	
	public static boolean individualImageShouldSupportOrientation(int collageStatus) {
		switch (collageStatus) {
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_SQUARE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_NONUPLE:
			return false;
		}
		return true;
	}

	private int[] getCollageButtonPaddings(int collageStatus) {
		switch (collageStatus) {
			// rectangle
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_SINGLE:
			return new int[]{5, 10, 5, 10};
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_DOUBLE_VERTICAL:
			return new int[]{10, 10, 10, 10};
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_TRIPLE_VERTICAL:
			return new int[]{7, 7, 7, 7};

			// square
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_SINGLE:
			return new int[]{5, 10, 5, 10};
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_DOUBLE_VERTICAL:
			return new int[]{10, 10, 10, 10};
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_TRIPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_QUADRUPLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_NONUPLE:
			return new int[]{7, 7, 7, 7};

			// sectional
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_SINGLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_HORIZONTAL:
			return new int[]{10, 10, 10, 10};
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_SQUARE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_NONUPLE:
			return new int[]{7, 7, 7, 7};
			
		default:
			return new int[]{5, 5, 5, 5};
		}
	}

	public static boolean hasSinglePicture(int collageStatus) {
		switch (collageStatus) {
		case CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_SINGLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SQUARE_SINGLE:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_SINGLE:
			return true;

		default:
			return false;
		}
	}

	public static boolean getCollageIndividualPictureOrientationSupported(
			int collageStatus) {
		switch (collageStatus) {
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_DOUBLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_VERTICAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_TRIPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_HORIZONTAL:
		case CollageProvider.DICECAM_COLLAGE_STATUS_SECTIONAL_QUADRUPLE_VERTICAL:
			return false;
		default:
			return true;
		}
	}
	
}

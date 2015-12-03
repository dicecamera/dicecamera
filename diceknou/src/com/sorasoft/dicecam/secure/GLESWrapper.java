package com.sorasoft.dicecam.secure;

import android.opengl.GLES20;

public class GLESWrapper {
	
	// {@link http://www.khronos.org/opengles/sdk/docs/man/xhtml/glViewport.xml}
	public static void glViewport(int x, int y, int width, int height) {
		if (width < 0) width = 1;
		if(height <= 0) height = 1; //divide 0 available on ratio calculation.
		GLES20.glViewport(x, y, width, height);
	}
	
}

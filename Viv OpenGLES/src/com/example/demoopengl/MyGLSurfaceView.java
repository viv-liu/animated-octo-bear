package com.example.demoopengl;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {

	public MyGLSurfaceView(Context context) {
		super(context);
		
		setEGLContextClientVersion(2);
		setRenderer(new MyGL20Renderer());
		
		// Render the view only when there is a change in the drawing data
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

}

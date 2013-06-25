package com.example.demoopengl;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;

public class OpenGLES20 extends Activity {
	
	private GLSurfaceView mGLView;
	//private AutoCompleteTextView atext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mGLView = new MyGLSurfaceView(this);
		setContentView(mGLView);
	}

	@Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public class MyGLSurfaceView extends GLSurfaceView {

		private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	    private float mPreviousX;
	    private float mPreviousY;
	    private final MyGL20Renderer mRenderer;
	    
		public MyGLSurfaceView(Context context) {
			super(context);
			
			setEGLContextClientVersion(2);
			mRenderer = new MyGL20Renderer();
			setRenderer(mRenderer);
			
			// Render the view only when there is a change in the drawing data
			//setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		}
		
		@Override
		public boolean onTouchEvent(MotionEvent e) {
		    // MotionEvent reports input details from the touch screen
		    // and other input controls. In this case, you are only
		    // interested in events where the touch position changed.

		    float x = e.getX();
		    float y = e.getY();

		    switch (e.getAction()) {
		        case MotionEvent.ACTION_MOVE:

		            float dx = x - mPreviousX;
		            float dy = y - mPreviousY;

		            // reverse direction of rotation above the mid-line
		            if (y > getHeight() / 2) {
		              dx = dx * -1 ;
		            }

		            // reverse direction of rotation to left of the mid-line
		            if (x < getWidth() / 2) {
		              dy = dy * -1 ;
		            }

		            mRenderer.mAngle += (dx + dy) * TOUCH_SCALE_FACTOR;  // = 180.0f / 320
		            requestRender();
		    }

		    mPreviousX = x;
		    mPreviousY = y;
		    return true;
		}
	}
}

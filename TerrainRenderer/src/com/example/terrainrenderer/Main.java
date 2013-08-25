package com.example.terrainrenderer;

import com.example.terrainrenderer.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.pm.ActivityInfo;

public class Main extends Activity implements OnClickListener{
	private Button leftB, rightB, attackB;
	private AnimatedView aView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.main);
        
        // Initialize buttons
        leftB = (Button)findViewById(R.id.left);
        leftB.setOnClickListener(this);
        rightB = (Button)findViewById(R.id.right);
        rightB.setOnClickListener(this);
        attackB = (Button)findViewById(R.id.attack);
        attackB.setOnClickListener(this);
        
        // Initialize AnimatedView
        aView = (AnimatedView) findViewById(R.id.anim_view);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	// If the activity is finishing, shutdown (aka close buffers)
    	// in AnimatedView
    	if(isFinishing()) {
    		aView.shutdown();
    	}
    }
    
    // Listen for button clicking
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.left:
			aView.shiftWindow(AnimatedView.LEFT);
			break;
		case R.id.right:
			aView.shiftWindow(AnimatedView.RIGHT);
			break;
		case R.id.attack:
			aView.obliterateObstacle();
			break;
		}
	}
}

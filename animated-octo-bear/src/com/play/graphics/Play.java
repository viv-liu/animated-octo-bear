package com.play.graphics;

import com.example.animated_octo_bear.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;

public class Play extends Activity implements OnClickListener{
	private Button leftB, rightB;
	private AnimatedView aView;
	private String filepath;
	private String name;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.play);
       
        Intent intent = getIntent();
        filepath = intent.getStringExtra("GetPath");
        name = intent.getStringExtra("GetFileName");
        
        // Initialize buttons
        leftB = (Button)findViewById(R.id.left);
        leftB.setOnClickListener(this);
        rightB = (Button)findViewById(R.id.right);
        rightB.setOnClickListener(this);
        
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
		}
	}
	
	  public String getName()
	  {
	     return name;
	  }

	  public String getPath()
	  {
	     return filepath;
	  }
	
}

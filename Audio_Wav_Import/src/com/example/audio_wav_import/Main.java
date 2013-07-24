package com.example.audio_wav_import;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;

public class Main extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        setContentView(R.layout.main);
    }
}

/**title_screen
 * 
 * The title screen of the game containing
 * two main buttons of play and import.
 * More buttons can be added later.
 * 
 */

/**
 * Called by: Opening the app
 * Calls: Import_UI and Play_UI
 * Input of:
 * Void
 * 
 * Output of:
 * Start Activity 
 * 
 * @author Shen Wang
 *
 */

package com.ui.module;

import com.example.animated_octo_bear.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class title_screen extends Activity {
			
		// Buttons
		private Button playbutton;
		private Button importbutton;
		
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);        
	        setContentView(R.layout.title_screen);
	        // Make it look nicer with a theme, rather than black
	        setTheme(android.R.style.Theme_Holo_Light);
	        
	        // Initialize buttons
	        playbutton = (Button) findViewById(R.id.button1);
	        importbutton = (Button) findViewById(R.id.button1);
	    }
		
	/**Upon Press of Play:
	 * A list of all files within the project folder appears.
	 * TODO: Make this list better looking than the import list?
	 * Upon press calls the Play_UI Function
	 */
		
		public void onClickPlay(View view) {
			Intent myIntent = new Intent(title_screen.this, Play_UI.class);
			myIntent.putExtra("folderpath", "/sdcard/"); //Optional parameters
			myIntent.putExtra("verifyext", "csv");
			startActivity(myIntent);
		}
	
	/**Upon Press of Import:
	 * Calls the Import_UI Class that lets
	 * the user navigate to the appropriate file they want to import.
	 * Then creates the file in the project folder for quick access later.
	 */
		
		public void onClickImport(View view) {
			Intent myIntent = new Intent(title_screen.this, Import_UI.class);
			myIntent.putExtra("folderpath", "/sdcard/"); //Optional parameters
			myIntent.putExtra("verifyext", "mp3");
			myIntent.putExtra("verifyext2", "wav");
			startActivity(myIntent);
		}
	
}

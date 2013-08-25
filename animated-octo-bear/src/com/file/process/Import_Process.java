/**Import_Process
 * 
 * Takes a valid filepath that the user has specified and turns that song into 
 * a processed list of information that would give a header
 * that contains full filepath for easy streaming
 * the processed data for choice of obstacles by the AI
 * other misc data to be determined from further investigation
 * into the AI.
 * 
 */

/**
 * Called by: Import_UI
 * Calls: Audio_process, AI_decision, 
 * 
 * Input of: 
 * filepath in an Intent
 * 
 * Output of:
 * a file in project folder that has all the information required
 * encoded within.
 * 
 * @author Shen Wang
 *
 */

package com.file.process;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList; 
import java.util.List;

import com.file.process.File_Process;

import com.example.animated_octo_bear.R;

import com.ui.module.Play_UI;
import com.ui.module.title_screen;
import com.audio.module.*;

import android.os.Bundle; 
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent; 
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Import_Process extends Activity {
	
  	private float thresholdMultiplier = 1.5f;
  	
  	/**
  	 *  HISTORY_SIZE relates to the amount of frames to include in the
  	 *  moving average calculation. A smaller value will result in a 
  	 *  more sensitive/rapidly changing moving average.
  	 */
  	private int HISTORY_SIZE = 50;
	
	// Folder to store generated terrain file
	public static final String TERRAIN_FOLDER_PATH = "Terrain/";
	
	// Folder to grab .wav file to analyze
	public final static String PACKAGE_FOLDER = "/Android/data/com.example.animated_octo_bear/";	
	
	// Path and Name of file
    private String filepath;
    private String name;
    private Button button;
    private Button button2;
    
 // File writing helpers
  	private File_Process m_fHelper;
  	private File_Process.ExternalStorageHelper m_esHelper;
  	
  	// Views
  	private TextView tv;
  	private Handler tvHandler;
  	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        Intent intent = getIntent();
        filepath = intent.getStringExtra("GetPath");
        name = intent.getStringExtra("GetFileName");
        setContentView(R.layout.import_process);
        // Make it look nicer with a theme, rather than black
        setTheme(android.R.style.Theme_Holo_Light);
        
        // Initialize views
        tv = (TextView) findViewById(R.id.textView_import_process);
        button = (Button) findViewById(R.id.button_import_calc);
        button2 = (Button) findViewById(R.id.button_import_play);
      
        // Initialize handler
        tvHandler = new Handler();
        
        // Initialize file helper
        m_fHelper = new File_Process();
    }
	
    public void onClickCalculate(View view) {
		// Get the current time in String format (appended to file name)
        //String curTime = DateFormat.format("h_mmaa", new Date()).toString();
        
        // Initialize external storage helper for writing
        m_esHelper = m_fHelper.new ExternalStorageHelper(this, 
        		PACKAGE_FOLDER + TERRAIN_FOLDER_PATH, 
        		name + ".csv");
        m_esHelper.makeWriteFile();
        
        // Construct folder path, looks something like /storage0/PACKAGE_FOLDER
        String path = Environment.getExternalStorageDirectory().toString();
        path += PACKAGE_FOLDER;
        // Ensure the file system is created, if not, make it
        File f = new File(path);
		if(!f.exists()) {
			f.mkdirs();
		}
		path = filepath;
		f = new File(path);
		if(!f.exists()) {
			Log.d("Viv's FileNotFoundError", "That's the end of PeaksDetect.java lol.");
			Toast.makeText(this, "File wasn't found", Toast.LENGTH_LONG).show();
		} else {
			button.setEnabled(false);
			Log.d("ButtonSet", "False");
	        Runnable r = new PeaksAnalysisThread(path);
			new Thread(r).start();
		}
	}
	public class PeaksAnalysisThread implements Runnable {
		public PeaksAnalysisThread(Object parameter) {
			filepath = (String) parameter;
		}
		// The run() method is what gets executed when
		// the thread is start()ed.
		public void run() {
			// Updates the textView on UI (thread) 
			tvHandler.post(new Runnable() {
				public void run() {
					tv.setText("Decoding Song...");
				}
			});
			try {
				calculate(filepath);
				// TODO: create a progress bar or something
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// Updates the textView on UI (thread) 
			tvHandler.post(new Runnable() {
				public void run() {
					tv.setText("All done");
					button2.setEnabled(true);
					button2.setText("Play The Game!");
				}
			});
			
		}
	}
	
	public void calculate( String FILE_Play ) throws Exception
	{
		WaveDecoder decoder = new WaveDecoder(new FileInputStream( FILE_Play  ));	// setup the audio file to be read using the WaveDecoder class							
		FFT fft = new FFT( 1024, 44100 );	// set up the FFT
		fft.window(FFT.HAMMING);	// helps to smoothen the audio data
		float[] samples = new float[1024];	// stores the raw audio samples (done 1024 bits at a time)
		float[] spectrum = new float[1024 / 2 + 1];	// stores the spectrum resulting from the FFT
		float[] lastSpectrum = new float[1024 / 2 + 1];	// stores the previous spectrum which is used to calculate the spectral flux
		List<Float> spectralFlux = new ArrayList<Float>( );	// stores spectral flux between subsequent spectrums
		List<Float> prunedSpectralFlux = new ArrayList<Float>( );	// same as spectralFlux, but ignores negative values
		// List<Float> peaks = new ArrayList<Float>( );	// stores the peaks based on the spectral flux
		List<Float> threshold = new ArrayList<Float>( );	// stores the threshold function	
		String peaks = new String();	// stores the peaks based on the spectral flux
		
		while( decoder.readSamples( samples ) > 0 )	// reads 1024 bits of the raw audio sample
		{			
			fft.forward( samples );	// perform an fft on the current sample
			System.arraycopy( spectrum, 0, lastSpectrum, 0, spectrum.length );	// store the previous spectrum 
			System.arraycopy( fft.getSpectrum(), 0, spectrum, 0, spectrum.length );	// calculates the new spectrum based on the new fft
			
			/**
			 * The following loop is responsible for calculating the spectral flux.
			 * It goes through the frequency bins one by one, calculating the
			 * difference in intensity between the current and previous spectrum.
			 * If a increase in intensity for a frequency is detected, it is added
			 * to the total spectralFlux for this time frame; negative changes are
			 * ignored for more effective beat detection. To change the frequencies
			 * that are used in the beat detection, simply change the parameters of
			 * the for loop so that only the frequencies you want are traversed.
			 */
			float flux = 0;
			for( int i = 0; i < spectrum.length; i++ )	// loops through the specified frequencies
			{
				float temp = (spectrum[i] - lastSpectrum[i]);	// calculates the change in intensity of a single frequency
				flux += temp < 0 ? 0 : temp;	// sums all the positive changes to create the spectral flux 
			}
			spectralFlux.add( flux );	// adds the recently calculated spectral flux to the array list					
		}
		
        threshold = new ThresholdFunction( HISTORY_SIZE, thresholdMultiplier ).calculate( spectralFlux );	
        //calculates the threshold function using the ThresholdFunction class
        
        // The following loop trims the spectral flux array using the threshold function
        // trimmed = max(spectralFlux - threshold, 0)
        for( int i = 0; i < threshold.size(); i++ )
        {
           if( threshold.get(i) <= spectralFlux.get(i) )
              prunedSpectralFlux.add( spectralFlux.get(i) - threshold.get(i) );
           else
              prunedSpectralFlux.add( (float)0 );
        }
        
        /**
         * The following for loop detects peaks by comparing the spectral flux of
         * a given time frame with the flux of the next time frame. A peak occurs
         * if the current spectral flux is greater than the next one. The peaks
         * array list is an array of floats where any non-zero number represents
         * a peak; the float value corresponds to the intensity of the peak.
         * This is then converted to a string with commas separating the values to be 
         * printed.
         */
        /*
         * This loop is to be moved to the ActivitySeeFile class later.
         * This is to make it more Modular and less prone to overwriting file information
         */
        for( int i = 0; i < prunedSpectralFlux.size() - 1; i++ )
        {
           //peaks.concat("/n"); //new line character
           if( prunedSpectralFlux.get(i) > prunedSpectralFlux.get(i+1) ) {
              //peaks.concat( "1" ); //1 for there is a peak
        	   m_esHelper.writeLineToFile("1");
        	   //Log.d("Peak", "1");
           } else {
              //peaks.concat( "0" ); //0 for there is no peak
        	   m_esHelper.writeLineToFile("0");
        	   //Log.d("No peak", "0");
           }
        }
        Log.d("PeaksAnalysis", "Done calculating all 1s and 0s.");
        
        m_esHelper.close();
	}

	/**Create Header:
	 * Creates a header information that is the full file path to the song then a new line separation
	 */
	
	/**Song Data:
	 * Calls Audio_process on the filepath given and the output 
	 * would be an array output.
	 * Then call the AI_deicsion which will then output the string
	 * to be written to the folder.
	 */
	
	
	/**Write Data:
	 * TODO Makes file within folder (that was created when application was first opened?)
	 * Name of file is the full song name without the extension .wav or .mp3 or w.e. file format we accept
	 * Save this filepath as "save_path"
	 * Writes the path data and the song info into the file together as a full set
	 * so that nothing will accidentally be overwritten.
	 */
	
	
	
	/**OnClickPlay
	 * When clicked can directly go to browse
	 * the folder where you can then choose the song to play
	 * the game.
	 */
	
	public void onClickPlay(View view) {
		Intent myIntent = new Intent(Import_Process.this, Play_UI.class);
		myIntent.putExtra("folderpath", "/Android/data/com.example.animated_octo_bear/Terrain/"); //Optional parameters
		myIntent.putExtra("verifyext", "csv");
		startActivity(myIntent);
	}
	
	
	/**Clean Up:
	 * Cleans up all variables and dumps everything.
	 */
}

package com.audio.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.audio.analysis.FFT;
import com.audio.analysis.ThresholdFunction;
import com.audio.io.WaveDecoder;
import com.example.audio_wav_import.FileIOHelper;
import com.example.audio_wav_import.R;

/**
 * Calculates the peaks of a song and stores them as 1s and 0s in .csv file
 * - First put test2.wav (in assets) into "/Android/data/com.example.audio_wav_import/"
 * - Find generated test2Terrain.csv in "/Android/data/com.example.audio_wav_import/Terrain/"
 * - For test2.wav (Radioactive), generates ~2500 binary values in file, or 5KB
 */
public class PeaksDetect extends Activity {

	// Folder to store generated terrain file
	private static final String TERRAIN_FOLDER_PATH = "Terrain/";
	
	// Folder to grab .wav file to analyze
	public final static String PACKAGE_FOLDER = "/Android/data/com.example.audio_wav_import/";	
	public final static String FILE = "test2.wav";
	
	// File writing helpers
	private FileIOHelper m_fHelper;
	private FileIOHelper.ExternalStorageHelper m_esHelper;
	
	/**
	 *  The threshold function is calculated using a moving average of
	 *  the spectral flux. The thresholdMultiplier is used to scale the
	 *  threshold function. A value of 1 will mean that the threshold
	 *  is just the average spectral flux, however you will probably
	 *  only want beats that are quiet a bit larger than the average to
	 *  be detected. A recommended value for this is around 1.5,
	 *  however it can vary based on the song. This value can also be
	 *  used to adjust the difficulty of the game; a lower multiplier
	 *  will have a more sensitive beat detection resulting in more
	 *  notes, while a larger multiplier will only detect the louder
	 *  beats.
	 */
	private float thresholdMultiplier = 1.5f;
	
	/**
	 *  HISTORY_SIZE relates to the amount of frames to include in the
	 *  moving average calculation. A smaller value will result in a 
	 *  more sensitive/rapidly changing moving average.
	 */
	private int HISTORY_SIZE = 50;
	
	// Views
	private TextView tv;
	private Button button;
	/** Handlers are used in conjunction with "threads" which are used 
	 *  to do calculations. 
	 *  
	 *  Threads are parallel programming infrastructures used to alleviate 
	 *  the UI (which is just a special thread) of any tedious
	 *  computations to keep the UI smooth for the user. Once a thread is 
	 *  started, it will run on its own (often on another core) in the
	 *  background until it finishes. The UI thread will keep running 
	 *  on its own as if like nothing happened.
	 *  
	 *  Handlers are used by a thread to communicate with the UI thread,
	 *  specifically to update UI views like TextViews and progress bars 
	 *  and buttons, etc. You CANNOT update the UI thread in a
	 *  background thread without a handler!  
	 *  
	 */
	private Handler tvHandler;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main);
        // Make it look nicer with a theme, rather than black
        setTheme(android.R.style.Theme_Holo_Light);
        
        // Initialize views
        tv = (TextView) findViewById(R.id.textView1);
        button = (Button) findViewById(R.id.button1);
        
        // Initialize handler
        tvHandler = new Handler();
        
        // Initialize file helper
        m_fHelper = new FileIOHelper();
        
    }

	// onClick called from button in layout XML 
	public void onClickCalculate(View view) {
		// Get the current time in String format (appended to file name)
        String curTime = DateFormat.format("h_mmaa", new Date()).toString();
        
        // Initialize external storage helper for writing
        m_esHelper = m_fHelper.new ExternalStorageHelper(this, 
        		PACKAGE_FOLDER + TERRAIN_FOLDER_PATH, 
        		"test2Terrain_" + curTime + ".csv");
        m_esHelper.makeWriteFile();
        
        // Construct folder path, looks something like /storage0/PACKAGE_FOLDER
        String path = Environment.getExternalStorageDirectory().toString();
        path += PACKAGE_FOLDER;
        // Ensure the file system is created, if not, make it
        File f = new File(path);
		if(!f.exists()) {
			f.mkdirs();
		}
		path += FILE;
		f = new File(path);
		if(!f.exists()) {
			Log.e("Viv's FileNotFoundError", "Good job buddy you didn't " +
					"put test.wav into " +
					PACKAGE_FOLDER);
			Log.d("Viv's FileNotFoundError", "That's the end of PeaksDetect.java lol.");
			Toast.makeText(this, "test2.wav wasn't found. Look at LogCat", Toast.LENGTH_LONG).show();
		} else {
			button.setEnabled(false);
	        Runnable r = new PeaksAnalysisThread(path);
			new Thread(r).start();
		}
	}
	public class PeaksAnalysisThread implements Runnable {
		private String path;
		public PeaksAnalysisThread(Object parameter) {
			path = (String) parameter;
		}
		// The run() method is what gets executed when
		// the thread is start()ed.
		public void run() {
			// Updates the textView on UI (thread) 
			tvHandler.post(new Runnable() {
				public void run() {
					tv.setText("Processing, gimme a sec.");
				}
			});
			try {
				calculate(path);
				// TODO: create a progress bar or something
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Updates the textView on UI (thread) 
			tvHandler.post(new Runnable() {
				public void run() {
					tv.setText("All done");
					button.setEnabled(true);
					button.setText("Do that again!");
				}
			});
			
		}
	}
	public String calculate( String FILE_Play ) throws Exception
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
		
        threshold = new ThresholdFunction( HISTORY_SIZE, thresholdMultiplier ).calculate( spectralFlux );	//calculates the threshold function using the ThresholdFunction class
        
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
        
        return peaks;
	}
/**
 * Write peaks using writeToFile function from other code.
 * okay i have no idea how to write this file atm. got some other stuff to do. will be back at 8 to talk
 * and figure this part out.
 * @param peaks which is the 1's and 0's to be outputed. No intensity information is included
 */

}


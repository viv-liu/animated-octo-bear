package com.audio.analysis;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;

import com.audio.analysis.FFT;
import com.audio.analysis.ThresholdFunction;
import com.audio.io.WaveDecoder;
import com.authorwjf.bounce.AnimatedView.Square;
import com.authorwjf.bounce.FileIOHelper.ExternalStorageHelper;
import com.example.audio_wav_import.FileIOHelper;

/**
 * Calculates the peaks of a song and displays the resulting plot.
 */
public class PeaksDetect extends Activity {
	/**
	 * FILE refers to the location of the audio file to be analyzed.
	 */	
	public static String FILE_Play = "/assets/01 Radioactive.wav";	
	
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

	public PeaksDetect( int historySize, float multiplier ){
		this.HISTORY_SIZE = historySize;
		this.thresholdMultiplier = multiplier;
	}

	public String calculate( String FILE_Play ) throws Exception
	{
		WaveDecoder decoder = new WaveDecoder( new FileInputStream( FILE_Play  ) );	// setup the audio file to be read using the WaveDecoder class							
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
           peaks.concat("/n"); //new line character
           if( prunedSpectralFlux.get(i) > prunedSpectralFlux.get(i+1) )
              peaks.concat( "1" ); //1 for there is a peak
           else
              peaks.concat( "0" ); //0 for there is no peak
        }
        
        return peaks;
	}
/**
 * Write peaks using writeToFile function from other code.
 * okay i have no idea how to write this file atm. got some other stuff to do. will be back at 8 to talk
 * and figure this part out.
 * @param peaks which is the 1's and 0's to be outputed. No intensity information is included
 */

	private void Write_File(String peaks) {
		
		FileIOHelper.ExternalStorageHelper();
		FileIOHelper.makeWriteFile();
		
	}
	
	public void Write_File(Context context, String peaks)  {  
		super(context, string);  
		mContext = context; 
		
		// Initialize FileIOHelper objects
		m_fHelper = new FileIOHelper();
		m_esHelper = m_fHelper.new ExternalStorageHelper(context, TERRAIN_FOLDER_PATH, "DemoTerrain.csv");
		m_esHelper.makeWriteFile();
		peaks = new String();
		
		// Load terrainData into an ArrayList of 0s and 1s, of type String
		terrainData = m_esHelper.readFromFile();
		
		h = new Handler();

		// Set scale for Density Independent Pixels
		DIP_SCALE = getResources().getDisplayMetrics().density;
		
		// Initialize drawables
		m_square = new Square(0, 0, 100);
	    m_gray = new Paint();
	    m_gray.setColor(Color.GRAY);
		
    } 
}


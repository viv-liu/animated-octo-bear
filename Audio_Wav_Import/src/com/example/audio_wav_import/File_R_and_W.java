package com.example.audio_wav_import;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.util.Log;

/**
 * Use to pass a string in and write to file or read from file.
 * Planning on passing "peaks" string output from Peaks.java into writeToFile
 * Then reading it using readFromFile to another string "display" or something.
 * Then display the full string text on screen as a test.
 * @author Shen Wang
 *
 */

public class File_R_and_W {
	
	public static void writeToFile(String data) { 
		// adding a Context seems to be the only way that the openFileOutput doesn't give an error? but what context do i call in peaksdetect
	    try {
	        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("radioactive.txt", Context.MODE_PRIVATE));
	        outputStreamWriter.write(data);
	        outputStreamWriter.close();
	    }
	    catch (IOException e) {
	        Log.e("Exception", "File write failed: " + e.toString());
	    } 
	}


	public String readFromFile() {

	    String ret = "";

	    try {
	        InputStream inputStream = openFileInput("radioactive.txt");

	        if ( inputStream != null ) {
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            String receiveString = "";
	            StringBuilder stringBuilder = new StringBuilder();

	            while ( (receiveString = bufferedReader.readLine()) != null ) {
	                stringBuilder.append(receiveString);
	            }

	            inputStream.close();
	            ret = stringBuilder.toString();
	        }
	    }
	    catch (FileNotFoundException e) {
	        Log.e("login activity", "File not found: " + e.toString());
	    } catch (IOException e) {
	        Log.e("login activity", "Can not read file: " + e.toString());
	    }

	    return ret;
	}
	
}

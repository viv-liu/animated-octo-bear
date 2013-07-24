package com.example.audio_wav_import;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import android.content.Context;

import com.audio.analysis.Peaks;

public class File_Write {
	
	public static void save(String filename, Peaks[] theObjectAr, 
			  Context ctx) {
			        FileOutputStream fos;
			        try {
			            fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);


			            ObjectOutputStream oos = new ObjectOutputStream(fos);
			            oos.writeObject(theObjectAr); 
			            oos.close();
			        } catch (FileNotFoundException e) {
			            e.printStackTrace();
			        }catch(IOException e){
			            e.printStackTrace();
			        }
			    }
	
}

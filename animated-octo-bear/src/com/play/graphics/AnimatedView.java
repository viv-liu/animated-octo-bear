package com.play.graphics;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.RectF;

import com.example.animated_octo_bear.R;
import com.file.process.File_Process;

public class AnimatedView extends ImageView{

	private static final String TERRAIN_FOLDER_PATH = "Terrain/";
	public static final int LEFT = -1, RIGHT = 1;
	private final float DIP_SCALE;
	int x = -1;
	int y = -1;
	private Handler h;
	private final int FRAME_RATE = 30;
	private Square m_square;
	private Paint m_gray, m_green, m_red;
	private int width, height;
	
	public AnimatedView(Context context, AttributeSet attrs)  {  
		super(context, attrs);  
		
    	h = new Handler();

		// Set scale for Density Independent Pixels
		DIP_SCALE = getResources().getDisplayMetrics().density;
		
		// Initialize drawables
		m_square = new Square(0, 0, 100);
		// Set left most square's coordinates, needed for dataWindow loading
		m_square.set(0, this.getHeight());
	    m_gray = new Paint();
	    m_gray.setColor(Color.GRAY);
	    m_green = new Paint();
	    m_green.setColor(Color.GREEN);
	    m_red = new Paint();
	    m_red.setColor(Color.RED);
	 		
	    // Get screen size
	    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	    Display display = wm.getDefaultDisplay();
	    Point size = new Point();
	    display.getSize(size);
	    width = size.x;
	    height = size.y;
		Log.d(String.valueOf(width), String.valueOf(height));
		
		// Count how many squares we can fit into the screen at once
		int i = 0;
		while(m_square.left <= width) {
			i++;
	   		m_square.left += m_square.width;
	   	}
		
		// Initialize DataWindow object with i
		m_dataWindow = new DataWindow(context, i);
		
		// Initialize x and y coordinates
	    x = 0;
	    y = height;
    } 
	
	private Runnable r = new Runnable() {
		@Override
		public void run() {
			invalidate(); 
		}
	};
	
	protected void onDraw(Canvas c) {  

		// Set left most square's coordinates
		m_square.set(0, this.getHeight());

		// Iterate through the elements of arrayWindow
    	for(Integer i: m_dataWindow.arrayWindow) {
    		if(i == 0) {
		    	// Draw gray square
		    	c.drawRect(m_square, m_gray);
		    } else if(i == 1) {
		    	// Draw green square
		    	c.drawRect(m_square, m_green);
		    }
    		// Place next square one-width right, same y-coordinate
    		m_square.set(m_square.left + m_square.width, m_square.bottom);
    	}
		
	    h.postDelayed(r, FRAME_RATE);
	} 
	
	public void shutdown() {
		m_dataWindow.shutdown();
	}
	
	// Shifts the window one unit left or right
	// NOTE: direction = AnimatedView.LEFT or direction = AnimatedView.RIGHT
	// See DataWindow.shiftWindow for more details
	public void shiftWindow(int direction) {
		m_dataWindow.shiftWindow(direction);
	}
	private int dipToPixel(int dip) {
		return (int) ((dip - 0.5f)/DIP_SCALE);
	}
	
	/*private int pixelToDip(int pixel) {
		return (int) (pixel * DIP_SCALE + 0.5f);
	}*/
	
	/** A convenient class for managing the array of values read
	 * from file. 
	 * 
	 * Four arrays are used: (x)prevScene, arrayScene, (x)nextScene, arrayWindow
	 * where (x) means not yet implemented
	 * 
	 *				beginIndex 	  endIndex	
	 *					v		    v
	 * --------------- --------------- ---------------
	 *|               |~|arrayWindow|~|               |
	 * --------------- --------------- ---------------
	 *   (x)prevScene	 arrayScene      (x)nextScene
	 *   
	 *   TODO: Incorporate nextScene when endIndex exceeds
	 *   File_Process.ARRAY_BUFFER_SIZE. Fire a thread to do this, use the launchThread stub. 
	 *   TODO: prevScene = arrayScene when arrayScene needs to be replaced by nextScene.
	 * @author Vivian
	 *
	 */
	private class DataWindow {
		public ArrayList<Integer> arrayWindow;
		private ArrayList<Integer> arrayScene;
		private ArrayList<Integer> nextScene;
		private ArrayList<Integer> prevScene;
		private String filename = Play.name;
		
		private int beginIndex = -1;
		private int endIndex = -1;
		
		private File_Process m_fHelper;
		private File_Process.ExternalStorageHelper m_esHelper;
		
		// windowSize refers to the integer number of squares that fit
		// on the screen, as determined outside this class (~13 on Nexus S)
		public DataWindow(Context context, int windowSize) {
			
			// Initialize fileIO helpers
			m_fHelper = new File_Process();
			m_esHelper = m_fHelper.new ExternalStorageHelper(context, TERRAIN_FOLDER_PATH, filename);
			if(m_esHelper.makeReadFile()) {
			
				// Initialize out-of-focus arrays
				prevScene = new ArrayList<Integer>();
				nextScene = new ArrayList<Integer>();
				
				// Initialize arrayScene by reading the first File_Process.ARRAY_BUFFER_SIZE # of elements
				arrayScene = m_esHelper.readFromFile();
				
			// File reading failed, just give arrayScene a single element
			} else {
				arrayScene = new ArrayList<Integer>();
				arrayScene.add(0);
			}
			// Initialize and populate arrayWindow by extracting windowSize 
			// number of elements from the start of arrayScene
			arrayWindow = new ArrayList<Integer>();
			for(int i = 0; i < arrayScene.size(); i++) {
				arrayWindow.add(arrayScene.get(i));
				if(arrayWindow.size() >= windowSize) {
					break;
				}
			}
			
			// Initialize indices
			beginIndex = 0;
			endIndex = arrayWindow.size();
		}
		
		// Shifts arrayWindow by one unit LEFT or RIGHT by modifying
		// beginIndex and endIndex to change beginning and end values of arrayWindow
		// NOTE: direction must be either AnimatedView.LEFT or AnimatedView.RIGHT
		public void shiftWindow(int direction) {
			// Only modify arrayWindow if beginLineNum does not become negative and
			// endIndex does not exceed arrayScene's size
			if(beginIndex + direction >= 0 && endIndex + direction < arrayScene.size()) {
				beginIndex += direction;
				endIndex += direction;
				if(direction == LEFT) {
					// Remove last element
					arrayWindow.remove(arrayWindow.size() - 1);
					// Add a new first element 
					arrayWindow.add(0, arrayScene.get(beginIndex));
				} else if (direction == RIGHT) {
					// Remove first element
					arrayWindow.remove(0);
					// Add a new last element
					arrayWindow.add(arrayWindow.size(), arrayScene.get(endIndex));
				}
			}
		}
		
		// Close the reader buffer in m_esHelper
		public void shutdown() {
			m_esHelper.close();
		}
		private void launchThread() {
			new Thread(new Runnable() {

				@Override
				public void run() {
					nextScene = m_esHelper.readFromFile();
				}
		}).start();
	}
}
	public class Square extends RectF {
		/** super fields:
		 * float bottom
		 * float left
		 * float right
		 * float top
		*/
		private final float height;
		private final float width;
		
		// bl stands for Bottom Left. 
		public Square(float x_blCoord, float y_blCoord, int sideInDips) {
			height = dipToPixel(sideInDips);
			width = height;
			super.left = x_blCoord;
			super.top = y_blCoord - height;
			super.right = x_blCoord + width;
			super.bottom = y_blCoord + height;
		}
		
		public void set(float x_bl, float y_bl) {
			super.set(x_bl, y_bl - width, x_bl + width, y_bl);
		}
	}
}

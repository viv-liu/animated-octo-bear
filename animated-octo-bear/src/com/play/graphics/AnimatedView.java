package com.play.graphics;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.graphics.RectF;

import com.play.graphics.*;
import com.file.process.*;

public class AnimatedView extends ImageView{

	private static final String TERRAIN_FOLDER_PATH = "Terrain/";
	public static final int LEFT = -1, RIGHT = 1;
	private final float DIP_SCALE;
	private static final int SCROLL_SPEED = -2;
	private static final int PERFECT_ZONE_WIDTH = 10;
	private static final int GOOD_ZONE_WIDTH = 100;
	private Handler h;
	private final int FRAME_RATE = 30;
	private Square m_floor;
	private Square m_obstacleStamp;
	private Obstacle m_nextObstacle;
	private HitZone m_perfectZone;
	private HitZone m_goodZone;
	private int xCoord;
	private Paint m_gray, m_green, m_red, m_yellow;
	private final int SCREEN_WIDTH, SCREEN_HEIGHT;
	
	private DataWindow m_dataWindow;

	public AnimatedView(Context context, AttributeSet attrs)  {  
		super(context, attrs);  
		// For the canvas
    	h = new Handler();
    	
    	// Set scale for Density Independent Pixels
    	DIP_SCALE = getResources().getDisplayMetrics().density;
    	
    	// Get screen size
	    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	    Display display = wm.getDefaultDisplay();
	    Point size = new Point();
	    display.getSize(size);
	    SCREEN_WIDTH = size.x;
	    SCREEN_HEIGHT = size.y;
		
		// Initialize drawables
		m_floor = new Square(0, 0, 100);
		m_obstacleStamp = new Square(0, 0, 100);
		m_nextObstacle = new Obstacle(0, 0, 100);
		m_perfectZone = new HitZone(200, PERFECT_ZONE_WIDTH);
		m_goodZone = new HitZone(200, GOOD_ZONE_WIDTH);
		
		// Set left most square's coordinates, needed for dataWindow loading
		xCoord = 0;	// xCoord is the bottom-left coordinate left most square
		m_floor.set(xCoord, this.getHeight());
	    m_gray = new Paint();
	    m_gray.setColor(Color.GRAY);
	    m_green = new Paint();
	    m_green.setColor(Color.GREEN);
	    m_red = new Paint();
	    m_red.setColor(Color.RED);
	    m_yellow = new Paint();
	    m_yellow.setColor(Color.parseColor("#44FFFF33"));
	    
		
		// Count how many squares we can fit into the screen at once
		int i = 0;
		while(m_floor.left <= SCREEN_WIDTH) {
			i++;
	   		m_floor.left += m_floor.width;
	   	}
		
		// Initialize DataWindow object with i number of squares
		m_dataWindow = new DataWindow(context, i);
		
/*		// Initialize x and y coordinates
	    x = 0;
	    y = height;*/
    } 
	
	private Runnable r = new Runnable() {
		@Override
		public void run() {
			invalidate(); 
		}
	};
	
	protected void onDraw(Canvas c) {  
		// Draw the hit zones
		c.drawRect(m_goodZone, m_yellow);
		c.drawRect(m_perfectZone, m_green);
		
		// Set left most square's coordinates
		m_floor.set(xCoord, SCREEN_HEIGHT);
		m_obstacleStamp.set(xCoord, SCREEN_HEIGHT - 150);
		m_nextObstacle.set(xCoord, SCREEN_HEIGHT - 150);
		m_nextObstacle.isFound = false;
		m_nextObstacle.isInZone = false;
	    	
	    // Iterate through the elements of arrayWindow
    	for(int i = 0; i < m_dataWindow.arrayWindow.size(); i++) {
    		if(m_dataWindow.arrayWindow.get(i) == 0) {
		    	// Draw gray square
		    	c.drawRect(m_floor, m_gray);
		    } else if(m_dataWindow.arrayWindow.get(i) == 1) {
		    	// If the isFound flag is false, this is the first
		    	// obstacle found in arrayWindow (meaning the first 1 in the array)
		    	if(!m_nextObstacle.isFound) {
		    		// Set the flag and store the index at which this obstacle is found
		    		// in arrayWindow
			    	m_nextObstacle.isFound = true;
			    	m_nextObstacle.arrayWindowIndex = i;
		    	}
		    	// Draw green square
		    	c.drawRect(m_floor, m_green);
		    	c.drawRect(m_obstacleStamp, m_gray);
		    }
    		// Place next square one-width right, same y-coordinate
    		m_floor.set(m_floor.left + m_floor.width, m_floor.bottom);
    		m_obstacleStamp.set(m_obstacleStamp.left + m_obstacleStamp.getWidth(), m_obstacleStamp.bottom);
    		// If the next obstacle wasn't found for this iteration, shift its coordinates one-width right,
    		// to examine the next position
    		if(!m_nextObstacle.isFound) {
    			m_nextObstacle.set(m_nextObstacle.left + m_nextObstacle.getWidth(), m_nextObstacle.bottom);
    		}
    	}
    	
    	// Collision detection
    	//************************************************************************
    	// Check if the next obstacle was found AND it intersects with the good zone...
    	/**	 m_gZ	  m_gZ
    	 * |  	  | |     _|_
    	 * | 	  | |    |___| <-obstacle
    	 * | 	  | |	   |
    	 * 		   ^m_pZ		
    	 */
    	if(m_nextObstacle.isFound && RectF.intersects(m_nextObstacle, m_goodZone)) {
    		m_nextObstacle.isInZone = true;
    		
    		//************************************************************************
    		// Check if the center of next obstacle is within the bounds of perfect zone
    		/**  m_gZ	  m_gZ
	    	 * |  	  |_|      |
	    	 * | 	 |___| <-obstacle
	    	 * | 	  | |	   |
	    	 * 		   ^m_pZ		
	    	 */
	    	if(m_nextObstacle.centerX() > m_perfectZone.left &&
		    		m_nextObstacle.centerX() < m_perfectZone.right) {
	    		// Perfect intersection
	    	} else {
	    		// Good intersection
	    	}
	    	//************************************************************************
	    //************************************************************************
	    // Check if this obstacle is on the left of the goodZone, if yes then it's game over.
	    /** m_perfectZone
    	 *   	m_gZ	  m_gZ
	     * 	 _|_ 	  | |       |
	     * 	|___| <- obstacle   |
	     * 	  | 	  | |	    |
	     * 		   	   ^m_pZ		
	     */
    	} else if(m_nextObstacle.left < m_goodZone.left) {
    		m_nextObstacle.isInZone = false;
    		Log.d("GG buddy", "you failed");
    	}
    	//************************************************************************
    	
    	// Update xCoord for scrolling
    	if(!m_dataWindow.isDoneScrolling()) {
    		xCoord += SCROLL_SPEED;
    	}
    	// if xCoord is one square-width out of the left edge of the screen, reset xCoord
		if(xCoord <= -1* m_floor.width) {
			if(shiftWindow(RIGHT)) {
				xCoord = 0;
			}
		}
	    h.postDelayed(r, FRAME_RATE);
	} 
	
	public void shutdown() {
		m_dataWindow.shutdown();
	}
	
	// Shifts the window one unit left or right
	// NOTE: direction = AnimatedView.LEFT or direction = AnimatedView.RIGHT
	// See DataWindow.shiftWindow for more details
	public boolean shiftWindow(int direction) {
		return m_dataWindow.shiftWindow(direction);
	}
	
	// Destroys the next obstacle by clearing the next "1" in arrayWindow and arrayScene
	public void obliterateObstacle() {
		if(m_nextObstacle.isInZone) {
			m_dataWindow.clearAOne(m_nextObstacle.arrayWindowIndex);
		} else {
		}
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
		
		private int beginIndex = -1;
		private int endIndex = -1;
		private String name = Play.getName();
		
		private boolean isDoneScrolling;
		private File_Process m_fHelper;
		private File_Process.ExternalStorageHelper m_esHelper;
		
		// windowSize refers to the integer number of squares that fit
		// on the screen, as determined outside this class (~13 on Nexus S)
		public DataWindow(Context context, int windowSize) {
			
			isDoneScrolling = false;
			// Initialize fileIO helpers
			m_fHelper = new File_Process();
			m_esHelper = m_fHelper.new ExternalStorageHelper(context, TERRAIN_FOLDER_PATH, name);
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
		public boolean shiftWindow(int direction) {
			// Only modify arrayWindow if beginIndex does not become negative and
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
				return true;
			} else {
				isDoneScrolling = true;
				return false;
			}
		}
		// Clears a 1 to a 0 in arrayScene corresponding to the supplied index associated with arrayWindow
		public void clearAOne(int arrayWindowIndex) {
			arrayScene.set(beginIndex + arrayWindowIndex, 0);
			arrayWindow.set(arrayWindowIndex, 0);
		}
		
		// Indicates that arrayWindow has reached the end of arrayScene
		public boolean isDoneScrolling() {
			return isDoneScrolling;
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
	
	public class HitZone extends RectF {
		private final float height;
		private final float width;
		
		public HitZone(int centerCoord, int width) {
			this.width = dipToPixel(width);
			height = SCREEN_HEIGHT;
			float centerCoordInPixels = dipToPixel(centerCoord);
			super.left = centerCoordInPixels - width/2;
			super.top = 0;
			super.right = centerCoordInPixels + width/2;
			super.bottom = height;
		}
	}
	
	public class Obstacle extends Square {
		// To be freely getted and setted
		public boolean isInZone;
		public boolean isFound;
		public int arrayWindowIndex;
		
		public Obstacle(float x_blCoord, float y_blCoord, int sideInDips) {
			super(x_blCoord, y_blCoord, sideInDips);
			// Is this object in the good and/or perfect zone?
			isInZone = false;
			isFound = false;
			// Index of the arrayWindow element this object represents (ie 0, 1, 2, ...)
			arrayWindowIndex = 0;
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
		public float getWidth() {
			return width;
		}
	}
}

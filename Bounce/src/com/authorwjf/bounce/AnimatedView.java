package com.authorwjf.bounce;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.graphics.RectF;

public class AnimatedView extends ImageView{

	private static final String TERRAIN_FOLDER_PATH = "Terrain/";
	private final float DIP_SCALE;
	private Context mContext;
	int x = -1;
	int y = -1;
	private int xVelocity = 10;
	private int yVelocity = 5;
	private Handler h;
	private final int FRAME_RATE = 30;
	private FileIOHelper m_fHelper;
	private FileIOHelper.ExternalStorageHelper m_esHelper;
	private ArrayList<String>terrainData;
	private Square m_square;
	private Paint m_gray;
	
	
	public AnimatedView(Context context, AttributeSet attrs)  {  
		super(context, attrs);  
		mContext = context; 
		
		// Initialize FileIOHelper objects
		m_fHelper = new FileIOHelper();
		m_esHelper = m_fHelper.new ExternalStorageHelper(context, TERRAIN_FOLDER_PATH, "DemoTerrain.csv");
		m_esHelper.makeReadFile();
		terrainData = new ArrayList<String>();
		
		// Load terrainData into an ArrayList of 0s and 1s, of type String
		terrainData = m_esHelper.readFromFile();
		
		h = new Handler();

		// Set scale for Density Independent Pixels
		DIP_SCALE = getResources().getDisplayMetrics().density;
		
		// Initialize drawables
		m_square = new Square(0, 0, 5);
	    m_gray = new Paint();
	    m_gray.setColor(Color.GRAY);
		
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
		c.drawRect(m_square, m_gray);
		
		// Iterate through all elements of terrainData ArrayList
	    for(String item: terrainData) {
		    int i = Integer.parseInt(item);
		    if(i == 0) {
		    	// Place next square 1-width right, same y-coordinate
		    	m_square.set(m_square.left + m_square.width, m_square.bottom);
		    } else if(i == 1) {
		    	// Place next square 1-width right, 1-height up 
		    	m_square.set(m_square.left + m_square.width, m_square.bottom - m_square.height);
		    }
		    c.drawRect(m_square, m_gray);
	    }
	    h.postDelayed(r, FRAME_RATE);
	} 
	
	private int dipToPixel(int dip) {
		return (int) ((dip - 0.5f)/DIP_SCALE);
	}
	
	private int pixelToDip(int pixel) {
		return (int) (pixel * DIP_SCALE + 0.5f);
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

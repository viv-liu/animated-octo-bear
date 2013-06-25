package com.example.demoopengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

public class MyGL20Renderer implements GLSurfaceView.Renderer {
	
	private Triangle mTriangle;
	//private Square mSquare;
	
	private final int X = 0, Y = 1, Z = 2;
	private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    
    public volatile float[] mRotationAxis = new float[3];
    public volatile float mAngle = 0;
    
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set the background frame color
		GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		
		mTriangle = new Triangle();
		//mSquare = new Square();
		
		
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
	    
		// Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        
        // Setup camera (view matrix)
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, -1.0f, 0.0f);
		
     // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        
        /*long time = SystemClock.uptimeMillis() % 4000L;
	    float angle = 0.090f * ((int) time);
	    mAngle = angle;*/
     // Setup rotation matrix
        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);
		Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);
		mTriangle.draw(mMVPMatrix);
	
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		 GLES20.glViewport(0, 0, width, height);
		 
		 float ratio = (float) width / height;
		 
		// this projection matrix is applied to object coordinates
	        // in the onDrawFrame() method
		 Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 2, 4);
		
	}
	
	public static int loadShader(int type, String shaderCode) {
		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
	    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);
		
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);
		
		return shader;
	}
	
	static class Triangle {
		
		private final String vertexShaderCode =
				"uniform mat4 uMVPMatrix; " +
			    "attribute vec4 aPosition;" +
			    "attribute vec4 aColor;" +
			    "varying vec4 vColor;" +
			    "void main() {" +
			    "  vColor = aColor;" +
			    "  gl_Position = aPosition * uMVPMatrix;" +
			    "}";
		
		private final String fragmentShaderCode =
			    "precision mediump float;" +
			    "varying vec4 vColor;" +
			    "void main() {" +
			    "  gl_FragColor = vColor;" +
			    "}";
        
		private final FloatBuffer vertexBuffer;
		private final int mProgram;
		private int mPositionHandle;
		private int mColorHandle;
		private int mMVPMatrixHandle;
		
		static final int COORDS_PER_VERTEX = 3;
		static final int COORDS_PER_COLOR = 4;
		static float triangleCoords[] = { 	// in clockwise order:
			0.0f, 0.633008459f, 0.0f, 		// top
			1.0f, 0.0f, 0.0f, 1.0f,			// red
			-0.5f, -0.311004243f, 0.0f,		// bottom left
			0.0f, 1.0f, 0.0f,	1.0f,		// green
			0.5f, -0.311004243f, 0.0f,		// bottom right
			0.0f, 0.0f, 1.0f, 1.0f			// blue
		};
		private final int vertexCount = triangleCoords.length / (COORDS_PER_VERTEX + COORDS_PER_COLOR);
		private final int vertexStride = COORDS_PER_VERTEX * 4 + COORDS_PER_COLOR * 4; 
		
		/*// Set color with red, green, blue and alpha (opacity) values
	    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };*/
	    
	    public Triangle() {

	        // initialize vertex byte buffer for shape coordinates
	        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
	        bb.order(ByteOrder.nativeOrder()); // endian

	        vertexBuffer = bb.asFloatBuffer();
	        vertexBuffer.put(triangleCoords);
	        vertexBuffer.position(0);
	        
	        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
	        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
	        mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
	        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
	        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
	        GLES20.glLinkProgram(mProgram);                  // creates OpenGL ES program executables
	    }
	    
	    public void draw(float [] mvpMatrix) {
			// Add program to OpenGL ES environment
			GLES20.glUseProgram(mProgram);
			
			// pass Position info
			mPositionHandle = GLES20.glGetAttribLocation(mProgram,  "aPosition");
			GLES20.glEnableVertexAttribArray(mPositionHandle);
			GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
			
			// pass Color info
			mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
			GLES20.glEnableVertexAttribArray(mColorHandle);	
			vertexBuffer.position(3);
			GLES20.glVertexAttribPointer(mColorHandle, COORDS_PER_COLOR, GLES20.GL_FLOAT, true, vertexStride, vertexBuffer);
			
			// pass mvpMatrix
			mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
			
			// draw
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
			
			// Disable vertex array
			GLES20.glDisableVertexAttribArray(mPositionHandle);
		}
	}
	
	/*static class Square {
		
		private final String vertexShaderCode =
			    "attribute vec4 vPosition;" +
			    "void main() {" +
			    "  gl_Position = vPosition;" +
			    "}";
		
		private final String fragmentShaderCode =
			    "precision mediump float;" +
			    "uniform vec4 vColor;" +
			    "void main() {" +
			    "  gl_FragColor = vColor;" +
			    "}";

	    private FloatBuffer vertexBuffer;
	    private ShortBuffer drawListBuffer;
	    private final int mProgram;
		private int mPositionHandle;
		private int mColorHandle;

	    // number of coordinates per vertex in this array
	    static final int COORDS_PER_VERTEX = 3;
	    static float squareCoords[] = { -0.5f,  0.5f, 0.0f,   // top left
	                                    -0.5f, -0.5f, 0.0f,   // bottom left
	                                     0.5f, -0.5f, 0.0f,   // bottom right
	                                     0.5f,  0.5f, 0.0f }; // top right

	    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices
	    
	    private final int vertexCount = squareCoords.length / COORDS_PER_VERTEX;
		private final int vertexStride = COORDS_PER_VERTEX * 4; // bytes per vertex;
		
		// Set color with red, green, blue and alpha (opacity) values
	    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

	    public Square() {
	        // initialize vertex byte buffer for shape coordinates
	        ByteBuffer bb = ByteBuffer.allocateDirect(
	        // (# of coordinate values * 4 bytes per float)
	                squareCoords.length * 4);
	        bb.order(ByteOrder.nativeOrder());
	        vertexBuffer = bb.asFloatBuffer();
	        vertexBuffer.put(squareCoords);
	        vertexBuffer.position(0);

	        // initialize byte buffer for the draw list
	        ByteBuffer dlb = ByteBuffer.allocateDirect(
	        // (# of coordinate values * 2 bytes per short)
	                drawOrder.length * 2);
	        dlb.order(ByteOrder.nativeOrder());
	        drawListBuffer = dlb.asShortBuffer();
	        drawListBuffer.put(drawOrder);
	        drawListBuffer.position(0);
	        
	        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
	        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
	        mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
	        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
	        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
	        GLES20.glLinkProgram(mProgram);                  // creates OpenGL ES program executables
	    }
	}*/
}

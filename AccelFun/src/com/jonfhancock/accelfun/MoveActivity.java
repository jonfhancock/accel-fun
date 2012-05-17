package com.jonfhancock.accelfun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MoveActivity extends Activity implements SensorEventListener {
	/** Called when the activity is first created. */
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;

	private SensorManager mSensorManager;

	private Sensor mAccelerometer;
	private final float NOISE = (float) 2.0;
	private TextView tvX;
	private TextView tvY;
	private TextView tvZ;
//	private TextView angle;
	private ImageView image;
	private Button buttonSet, button1,button2;
	private Sensor mOrientation;
	private RotationMode rotationMode;
	private boolean initialized = false;
	private boolean running = true;
	private AbsoluteLayout absLayout;
	
	private float up;
	private WindowManager mWindowManager;
	private DisplayMetrics metrics;
	private TextView mode;
	
	
	private enum RotationMode{
		ROLL,YAW
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.move);
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		tvX = (TextView) findViewById(R.id.x_axis);
		tvY = (TextView) findViewById(R.id.y_axis);
		tvZ = (TextView) findViewById(R.id.z_axis);
		absLayout = (AbsoluteLayout) findViewById(R.id.absLayout);
		mode = (TextView) findViewById(R.id.mode);
		mode.setText("Table (horizontal) mode: using pitch and roll");
//		angle = (TextView) findViewById(R.id.angle);
		image = (ImageView) findViewById(R.id.imageView1);
		buttonSet = (Button) findViewById(R.id.buttonSet);
		
		buttonSet.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(running){
					mSensorManager.unregisterListener(MoveActivity.this);
					buttonSet.setText("Move");
					running = false;
				}
				else{
					initialized = false;
					mSensorManager.registerListener(MoveActivity.this, mOrientation,
							SensorManager.SENSOR_DELAY_GAME);
					buttonSet.setText("Set");
					running = true;
				}
			}});
button1 = (Button) findViewById(R.id.button1);
		
button1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MoveActivity.this,AccelFunActivity.class);
				startActivity(i);

			}});
button2 = (Button) findViewById(R.id.button2);
		
button2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MoveActivity.this,BothActivity.class);
				startActivity(i);
			}});
		
		mInitialized = false;
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		mSensorManager.registerListener(this, mOrientation,
				SensorManager.SENSOR_DELAY_GAME);

	}

	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mOrientation,
				SensorManager.SENSOR_DELAY_GAME);
	}

	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		float yaw = event.values[0];
		float pitch = event.values[1];
		float roll = event.values[2];
		tvX.setText(Integer.toString((int)yaw));
		tvY.setText(Integer.toString((int)pitch));
		tvZ.setText(Integer.toString((int)roll));
		if(!initialized){
			
//			if(Math.abs(y) < 30 && Math.abs(z) < 30){
//				up = x;
//				rotationMode = RotationMode.YAW;
//			} else{
//				up = y;
//				rotationMode = RotationMode.ROLL;
//			}
			initialized = true;
		}
//		float azimuth = ((float) Math.toDegrees(x));
//		xInDeg.setText(Float.toString(x));
//		Matrix mat = image.getImageMatrix();
//		mat.postRotate(x);
//		mat.setRotate((float) Math.toDegrees(x));
//		if(Math.abs(pitch) > 20 || Math.abs(roll) > 20){
//			
//		} else{
		moveView(image,pitch,roll);
//		}
		int rotation = 0;
//		switch(rotationMode){
//		case YAW:
//			rotation = (int) (-x + up );
//			break;
//		case ROLL:
//			if(Math.abs(y) > 70){
//				rotation = (int) z;
//			}else{
//				rotation = (int) (y +90 );
//				
//				if(z <= 0){
//					rotation = -rotation;
//				}
//			}
////			rotation -= up;
//			break;
//		}
//		angle.setText(String.valueOf((int)rotation));
//		image.setImageBitmap(rotateAndScale(rawBitmap,(int)rotation,1f));

	}
	private void moveView(View v, float pitch,float roll){
		float top = v.getTop();
		if(v.getBottom()+v.getHeight()-pitch < metrics.heightPixels && v.getTop()-pitch > 0){
			top = v.getTop() - pitch;
		}
		float left = v.getLeft();
		if(v.getLeft() - roll > 0 && v.getRight()-roll-1 < metrics.widthPixels){
			left = v.getLeft() - roll;
		}
		float width = v.getWidth();
		float height = v.getHeight();
		
		
		 AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams((int)width, (int)height, (int)left, (int)top);
//	        lp.x = touchX - mRegistrationX;
//	        lp.y = touchY - mRegistrationY;
		 
		 absLayout.updateViewLayout(v, lp);
	}
//	public void onDrop (DragSource source,
//	         int x, int y, int xOffset, int yOffset,
//	        DragView dragView, Object dragInfo)
//	{
//	    View v = (View) dragInfo;
//	    int w = v.getWidth ();
//	    int h = v.getHeight ();
//	    int left = x - xOffset;
//	    int top = y - yOffset;
//	    DragLayer.LayoutParams lp = new DragLayer.LayoutParams (w, h, left, top);
//	    this.updateViewLayout(v, lp);
//	}
//	
//	static public Bitmap rotateAndScale(Bitmap bitmap, int angle, float scale) {
//        final double radAngle = Math.toRadians(angle);
//
//        final int bitmapWidth = bitmap.getWidth();
//        final int bitmapHeight = bitmap.getHeight();
//
//        final double cosAngle = Math.abs(Math.cos(radAngle));
//        final double sinAngle = Math.abs(Math.sin(radAngle));
//
//        float borderWidth = 0.0f;
//        
//        final int strokedWidth = (int) ((bitmapWidth + 2 * borderWidth) * scale);
//        final int strokedHeight = (int) ((bitmapHeight + 2 * borderWidth) * scale);
//
//        final int width = (int) (strokedHeight * sinAngle + strokedWidth * cosAngle);
//        final int height = (int) (strokedWidth * sinAngle + strokedHeight * cosAngle);
//
//        final float x = (width - bitmapWidth) / 2.0f;
//        final float y = (height - bitmapHeight) / 2.0f;
//
//        final Bitmap decored = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        final Canvas canvas = new Canvas(decored);
//        
//        canvas.scale(scale, scale, width / 2.0f, height / 2.0f);
//        canvas.rotate(angle, width / 2.0f, height / 2.0f);
//        
//        canvas.drawBitmap(bitmap, x, y, sPaint);
//
//        return decored;
//    }
	
//	 private static final Paint sPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
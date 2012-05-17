package com.jonfhancock.accelfun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AccelFunActivity extends Activity implements SensorEventListener {
	/** Called when the activity is first created. */
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;

	private SensorManager mSensorManager;

	private Sensor mAccelerometer;
	private final float NOISE = (float) 2.0;
	private TextView tvX;
	private TextView tvY;
	private TextView tvZ;
	private TextView angle;
	private TextView mode;
	private ImageView image;
	private Button buttonSet;
	private Bitmap rawBitmap;
	private Sensor mOrientation;
	private RotationMode rotationMode;
	private boolean initialized = false;
	private boolean running = true;
	
	private float up;
	private Button button1;
	private Button button2;
	
	
	private enum RotationMode{
		ROLL,YAW
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		tvX = (TextView) findViewById(R.id.x_axis);
		tvY = (TextView) findViewById(R.id.y_axis);
		tvZ = (TextView) findViewById(R.id.z_axis);
		angle = (TextView) findViewById(R.id.angle);
		mode = (TextView) findViewById(R.id.mode);
		image = (ImageView) findViewById(R.id.imageView1);
		buttonSet = (Button) findViewById(R.id.buttonSet);
		
		buttonSet.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(running){
					mSensorManager.unregisterListener(AccelFunActivity.this);
					buttonSet.setText("Start");
					running = false;
				}
				else{
					initialized = false;
					mSensorManager.registerListener(AccelFunActivity.this, mOrientation,
							SensorManager.SENSOR_DELAY_GAME);
					buttonSet.setText("Set");
					running = true;
				}
			}});
		button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent(AccelFunActivity.this,MoveActivity.class);
				startActivity(i);
			}});
		button2 = (Button) findViewById(R.id.button2);
		
		button2.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						Intent i = new Intent(AccelFunActivity.this,BothActivity.class);
						startActivity(i);
					}});
		rawBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.up_arrow_2);
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
			
			if(Math.abs(pitch) < 30 && Math.abs(roll) < 30){
				up = yaw;
				rotationMode = RotationMode.YAW;
				mode.setText("Table (horizontal) mode: using yaw");
			} else{
				up = pitch;
				rotationMode = RotationMode.ROLL;
				mode.setText("Handheld (vertical) mode: using pitch and roll");
			}
			initialized = true;
		}
//		float azimuth = ((float) Math.toDegrees(x));
//		xInDeg.setText(Float.toString(x));
//		Matrix mat = image.getImageMatrix();
//		mat.postRotate(x);
//		mat.setRotate((float) Math.toDegrees(x));
		int rotation = 0;
		switch(rotationMode){
		case YAW:
			rotation = (int) (-yaw + up );
			break;
		case ROLL:
			if(Math.abs(pitch) > 70){
				rotation = (int) roll;
			}else{
				rotation = (int) (pitch +90 );
				
				if(roll <= 0){
					rotation = -rotation;
				}
			}
//			rotation -= up;
			break;
		}
		angle.setText(String.valueOf((int)rotation));
		image.setImageBitmap(rotateAndScale(rawBitmap,(int)rotation,1f));

	}
	
	static public Bitmap rotateAndScale(Bitmap bitmap, int angle, float scale) {
        final double radAngle = Math.toRadians(angle);

        final int bitmapWidth = bitmap.getWidth();
        final int bitmapHeight = bitmap.getHeight();

        final double cosAngle = Math.abs(Math.cos(radAngle));
        final double sinAngle = Math.abs(Math.sin(radAngle));

        float borderWidth = 0.0f;
        
        final int strokedWidth = (int) ((bitmapWidth + 2 * borderWidth) * scale);
        final int strokedHeight = (int) ((bitmapHeight + 2 * borderWidth) * scale);

        final int width = (int) (strokedHeight * sinAngle + strokedWidth * cosAngle);
        final int height = (int) (strokedWidth * sinAngle + strokedHeight * cosAngle);

        final float x = (width - bitmapWidth) / 2.0f;
        final float y = (height - bitmapHeight) / 2.0f;

        final Bitmap decored = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(decored);
        
        canvas.scale(scale, scale, width / 2.0f, height / 2.0f);
        canvas.rotate(angle, width / 2.0f, height / 2.0f);
        
        canvas.drawBitmap(bitmap, x, y, sPaint);

        return decored;
    }
	
	 private static final Paint sPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
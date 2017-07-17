package accmouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener, OnTouchListener {

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private GraphicsView graphicsView;
	private AccelerometerMouseClient client;
	public static MouseButton leftButton = new MouseButton();
	public static int dBm = -200;
	public static WifiManager wifi;
	private boolean   accX = false, accY = false, deadzone = false;
	protected PowerManager.WakeLock mWakeLock;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		updatePrefs();
		graphicsView = new GraphicsView(this);
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		setContentView(graphicsView);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		graphicsView.setOnTouchListener(this);

		client = new AccelerometerMouseClient("Bullshit", 18250);
		dBm = getSignalStrength();
	}

	private void updatePrefs() {

	}

	public int getSignalStrength() {
		try {
			WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			if (!wifiManager.isWifiEnabled()) {

			}
			return wifiManager.getConnectionInfo().getRssi();
		} catch (Exception e) {

		}
		return -50;
	}

	public boolean onTouch(View v, MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN ) {
			leftButton.setState(true);
		} else if (event.getAction() == MotionEvent.ACTION_UP ) {
			leftButton.setState(false);
		}
		client.feedTouchFlags(leftButton.getState());
		return true;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	protected void onResume() {
		super.onResume();
		leftButton.setState(false);
		updatePrefs();

		client.pause(false);
		Log.d("Orientation", "" + this.getResources().getConfiguration().orientation);
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
		if (ManualConnectActivity.configured) {
			client.stop();
			client.forceUpdate(ManualConnectActivity.ipAddress, ManualConnectActivity.port);
			showToast("Attempting to connect to " + ManualConnectActivity.ipAddress + " on port " + ManualConnectActivity.port, Toast.LENGTH_LONG);
			client.run(true);
			ManualConnectActivity.configured = false;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this, mSensor);
		client.pause(true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.mWakeLock.release();
		client.stop();
	}

	public void onSensorChanged(SensorEvent event) {
		try {
			float x = accX ? (-event.values[0] + 9.81f) : event.values[0];
			float y = accY ? (-event.values[1]) : event.values[1];
			float z = event.values[2];

			{

					float dummyX = x;
					x = y;
					y = -dummyX;

			}
			if (deadzone) {
				x = applyDeadZoneX(x);
				y = applyDeadZoneY(y);
			}
			client.feedAccelerometerValues(x, y, z);
			if (!AccelerometerMouseClient.toastShown) {
				if (AccelerometerMouseClient.connected) {
					showToast("Connected to server at " + client.socket.getInetAddress().getHostAddress(), Toast.LENGTH_LONG);
				}
				AccelerometerMouseClient.toastShown = true;
			}
		} catch (Exception e) {

		}
	}

	public float applyDeadZoneX(float x) {
		if (x < 6 && x > 3) {
			x = 5f;
		}

		return x;
	}

	public float applyDeadZoneY(float y) {
		if (y > -.98 && y < .98) {
			y = 0f;
		}
		return y;
	}



	private void showToast(String string, int duration) {
		Toast toast = Toast.makeText(this, string, duration);
		toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 0);
		toast.show();
	}


	//Menu

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Connect"); // Index 1
		menu.add(0, 2, 0, "Disconnect"); // Index 2
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case 1:
			Intent i = new Intent(this, ManualConnectActivity.class);
			startActivity(i);
			break;
		case 2:
			client.stop();
			showToast("Disconnected from server.", Toast.LENGTH_SHORT);
			break;

		}
		return true;
	}


	//GraphicsView

	static public class GraphicsView extends SurfaceView implements Runnable {


		SurfaceHolder holder;

		public GraphicsView(Context context) {
			super(context);
			holder = getHolder();
		}

		public void run() {

}}}
package cn.edu.chd.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import cn.edu.chd.yitu.ChooseModel;

/**
 * @author Rowand jj
 *
 *摇一摇启动app的服务
 */
public class ShakeService extends Service
{
	private SensorManager manager = null;
	
	/**
	 * 重力传感器
	 */
	private Sensor sensor = null;
	
	private ShakeSensorListener mListener = null;
	
	private static final String TAG = "ShakeService";
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		initSensor();
		manager.registerListener(mListener, sensor, SensorManager.SENSOR_DELAY_UI);
		super.onCreate();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		manager.unregisterListener(mListener, sensor);
	}
	/**
	 * 初始化传感器
	 */
	private void initSensor()
	{
		manager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Vibrator vi = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		mListener = new ShakeSensorListener(vi, this);
	}
	
	public class ShakeSensorListener implements SensorEventListener
	{
		private Vibrator vi;
		private Context context;
		public ShakeSensorListener(Vibrator vi,Context context)
		{
			this.vi = vi;
			this.context = context;
		}
		@Override
		public void onSensorChanged(SensorEvent event)
		{
			
			int type = event.sensor.getType();
	        float[] values = event.values;
	        float x = values[0];
	        float y = values[1];
	        float z = values[2];
	        Log.i(TAG, "x:" + x + "y:" + y + "z:" + z);
	        Log.i(TAG, "Math.abs(x):" + Math.abs(x) + "Math.abs(y):" + Math.abs(y)
	                + "Math.abs(z):" + Math.abs(z));
	        if (type == Sensor.TYPE_ACCELEROMETER)
	        {
	            int value = 18;//阀值
	            if (x >= value || x <= -value || y >= value || y <= -value
	                    || z >= value || z <= -value)
	            {
	                vi.vibrate(100);//震动
	                //开启应用
	                Intent intent = new Intent(context,ChooseModel.class);
	                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                context.startActivity(intent);
	            }
	        }
		}
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
		}
	}

}

















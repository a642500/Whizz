package toxz.me.whizz.additional;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import com.unique.whizzdo.application.MyBinder;
import android.view.WindowManager.LayoutParams;
import org.jetbrains.annotations.Nullable;

/**
 * Created by carlos on 6/3/14.
 */
public class QuickSnapService extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private float[] valuesGravity;
    private float[] valuesGeomagnetic;
    private float[] values1 = new float[9];
    private float[] values2 = new float[3];
    private int tryCount = 0;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("onReceiver()", "receiver a broadcast screen on");
            handleBroadcastReceiver();
        }
    };

    private void handleBroadcastReceiver() {
        tryCount = 0;
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (tryCount >= 3) {
            mSensorManager.unregisterListener(this);
            return;
        }
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                valuesGravity = event.values;
                if (valuesGeomagnetic != null) {
                    SensorManager.getRotationMatrix(values1, null, valuesGravity, valuesGeomagnetic);
//                    Log.i("onSensorChanged()", "valuesGeomagnetic is " + Arrays.toString(valuesGeomagnetic));
//                    Log.i("onSensorChanged()", "valuesGravity is " + Arrays.toString(valuesGravity));
//                    Log.i("onSensorChanged()", "values1 is " + Arrays.toString(values1));

                    SensorManager.getOrientation(values1, values2);
//                    Log.i("onSensorChanged()", "case 1 orientation is " + Arrays.toString(values2));
//                    mSensorManager.unregisterListener(this);

                    if (Math.abs(values2[1]) < 0.15 && (Math.abs(Math.abs(values2[2]) - 1.5) < 0.15)) {
                        mSensorManager.unregisterListener(this);
                        Log.i("onSensorChanged()", "符合条件，拍照！");
                        Intent cameraIntent = new Intent(this, QuickSnapActivity.class);
                        cameraIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        cameraIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(cameraIntent);
                    } else {
                        Log.i("onSensorChanged()", "tryCount is " + tryCount);
                        tryCount++;
                    }
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                valuesGeomagnetic = event.values;
                if (valuesGravity != null) {
                    SensorManager.getRotationMatrix(values1, null, valuesGravity, valuesGeomagnetic);
//                    Log.i("onSensorChanged()", "valuesGeomagnetic is " + Arrays.toString(valuesGeomagnetic));
//                    Log.i("onSensorChanged()", "valuesGravity is " + Arrays.toString(valuesGravity));
//                    Log.i("onSensorChanged()", "values1 is " + Arrays.toString(values1));

                    SensorManager.getOrientation(values1, values2);
//                    Log.i("onSensorChanged()", "case 2 orientation is " + Arrays.toString(values2));
//                    mSensorManager.unregisterListener(this);

                    if (Math.abs(values2[1]) < 0.15 && (Math.abs(Math.abs(values2[2]) - 1.5) < 0.15)) {
                        mSensorManager.unregisterListener(this);
                        Log.i("onSensorChanged()", "符合条件，拍照！");
                        Intent cameraIntent = new Intent(this, QuickSnapActivity.class);
                        cameraIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        cameraIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(cameraIntent);
                    } else {
                        Log.i("onSensorChanged()", "tryCount is " + tryCount);
                        tryCount++;
                    }

                }
                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder(this);
    }

    @Override
    public void onCreate() {
        Log.i("onCreate()", "QuickSnapService is created!");
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        super.onCreate();
    }
}

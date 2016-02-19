package com.example.vijay.motionsensors;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.content.Context;
import android.widget.TextView;

public class SensorsActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;

    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mMagnetometer;

    private TextView accelerometer_sensor;
    private TextView gyroscope_readings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Get sensors
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        // Register sensors gyro, accelerometer and magnetometer
        mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope , SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer , SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sensors, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float [] mGravity = new float [9];
        float [] mGeomagnetic = new float [9];
        final TextView text1 = (TextView) findViewById(R.id.textView1);
        final TextView text2 = (TextView) findViewById(R.id.textView2);
        final TextView text3 = (TextView) findViewById(R.id.textView3);
        accelerometer_sensor = (TextView) findViewById(R.id.accelerometer_sensor);
        gyroscope_readings = (TextView) findViewById(R.id.gyroscope_readings);

        Sensor mySensor = event.sensor;
        if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            String acc_readings = String.format("X: %s\nY: %s\nZ: %s", x, y, z);
            accelerometer_sensor.setText(String.valueOf(acc_readings));
            if ( x  < -3) {text1.setText("left");}
            if ( x  >  3) {text1.setText("right");}
            if ( z  < -3) {text2.setText("up");}
            if ( z  >  3) {text2.setText("down");}
            if ( y  >  3) {text3.setText("forward");}
            if ( y  < -3) {text3.setText("backward");}
            mGravity = event.values.clone();
        }

        if (mySensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values.clone();
            String gyro_readings = String.format("X: %s\nY: %s\nZ: %s", mGeomagnetic[0], mGeomagnetic[1], mGeomagnetic[2]);
            gyroscope_readings.setText(String.valueOf(gyro_readings));
        }

        if(mGeomagnetic != null && mGravity != null ) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean hasRotation = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if(hasRotation) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                float x_gyro = orientation[0];
                float y_gyro = orientation[1];
                float z_gyro = orientation[2];
                String gyro_readings = String.format("X: %s\nY: %s\nZ: %s", x_gyro, y_gyro, z_gyro);
                gyroscope_readings.setText(String.valueOf(gyro_readings));
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
}

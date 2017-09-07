package edu.sjsu.vimmi_swami.ddd_project;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.Manifest;
import android.telephony.SmsManager;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import static java.sql.Types.NULL;

public class MainActivity extends Activity implements SensorEventListener {
    private static final int SEND_SMS_PERMISSION =0 ;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int BREAK_THRESHOLD = 600;
    String phoneNo = "+16504686717";
    String message ="Hi!, Saun is detected to be distracted driving!";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(MainActivity.this, senAccelerometer ,SensorManager.SENSOR_DELAY_NORMAL);

    }
// Send message if permission to SMS is granted on the device.
    protected void notifyEmergencyContacts() {
        Log.d("inside method", "inside notifyEmergencyContacts call");
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNo, null, message, null, null);
        Log.d("MSG", "SENT!!!!!");
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Distraction Detected");
        intent.putExtra(Intent.EXTRA_TEXT, "Hi,\n\n" +
                "   This is an auto generated notification for Distraction Driving Detection for Saun\n\n"+
                "Thank you,\n" +
                "DDD Team"
        );
        intent.setData(Uri.parse("mailto:vimmi.swami@gmail.com"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Log.d("EMAIL", "SENT!!!!!");
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                Log.d("Acc_x", Float.toString(x));
                Log.d("Acc_y", Float.toString(y));
                Log.d("Acc_z", Float.toString(z));
                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;
                Log.d("speed", Float.toString(speed));

                if (speed> BREAK_THRESHOLD){
                    notifyEmergencyContacts();
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(MainActivity.this);
    }

    protected void onResume() {
        super.onResume();
        if (MainActivity.this != null){
        senSensorManager.registerListener(MainActivity.this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);}
    }
}

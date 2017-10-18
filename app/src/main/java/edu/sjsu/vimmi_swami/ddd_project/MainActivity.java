package edu.sjsu.vimmi_swami.ddd_project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.telephony.SmsManager;
import android.app.Activity;
import android.widget.Button;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity implements SensorEventListener {
    private static final int SEND_SMS_PERMISSION =0 ;
    private SensorManager senSensorManager;
    private Button start_stop;
    private int count =0;
    private Sensor senAccelerometer, senMagnetometer, senGyrometer;
    private float acc_x=0, acc_y=0, acc_z=0,gyro_x=0, gyro_y=0, gyro_z=0 ,mag_x=0, mag_y=0, mag_z=0;
    List<String> phoneNo;
    String message ="Hi!, I have been detected by App DDD to be distracted driving!";

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senMagnetometer = senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        senGyrometer = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // Before any going further in app, check if the sensors required do work in device.
        Boolean temp1= senSensorManager.registerListener(MainActivity.this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Boolean temp2=  senSensorManager.registerListener(MainActivity.this, senMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        Boolean temp3= senSensorManager.registerListener(MainActivity.this, senGyrometer, SensorManager.SENSOR_DELAY_NORMAL);
        if(temp1||temp2||temp3){
            Toast.makeText(getApplicationContext(), "Some Sensors are not supported! The app will exit now!", Toast.LENGTH_SHORT).show();
            senSensorManager.unregisterListener(MainActivity.this);
            finish();
        }

        Context mContext = getApplicationContext();
        File file = new File(mContext.getCacheDir(), "contacts.txt");
        if(!file.exists()){
            Intent i = new Intent(this, Contacts.class);
            startActivity(i);
        }else {
            try {
                FileInputStream fin = new FileInputStream(file);
                String ret = convertStreamToString(fin);

                //Make sure you close all streams.
                fin.close();
                phoneNo = Arrays.asList(ret.split(","));
            }catch (Exception e){
                Log.e("Read Contacts", e.getMessage());
            }

        }

        setContentView(R.layout.activity_main);

        senSensorManager.registerListener(MainActivity.this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        senSensorManager.registerListener(MainActivity.this, senMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        senSensorManager.registerListener(MainActivity.this, senGyrometer, SensorManager.SENSOR_DELAY_NORMAL);


        start_stop = (Button)findViewById(R.id.start_stop);
        start_stop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (start_stop.getText().equals("Start!")) {
                    start_stop.setText("Stop!");
                    start_stop.setBackgroundColor(Color.parseColor("#ffcc0000"));
                } else {
                    senSensorManager.unregisterListener(MainActivity.this);
                    start_stop.setText("Start!");
                    start_stop.setBackgroundColor(Color.parseColor("#ff669900"));
                }
            }
        });

    }
// Send message if permission to SMS is granted on the device.
    public void notifyEmergencyContacts() {
        Toast.makeText(this,"ALERT",Toast.LENGTH_LONG).show();
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.buzzsound);
        mp.start();
        SmsManager sms = SmsManager.getDefault();
        for(String c : phoneNo) {
              sms.sendTextMessage(c, null, message, null, null);
        }
        mp.stop();
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        count++; //To be removed later
        Sensor mySensor = sensorEvent.sensor;
        SensorEventLoggerTask async = new SensorEventLoggerTask(this);

        async.execute(sensorEvent);
    }

    private class SensorEventLoggerTask extends AsyncTask<SensorEvent, Void, Boolean> {

        private MainActivity myContextRef;

        public SensorEventLoggerTask(MainActivity myContextRef) {
            this.myContextRef = myContextRef;
        }

        @Override
        protected Boolean doInBackground(SensorEvent... events) {
            String FILENAME = "sensor_cache.txt";

            File file = new File(myContextRef.getApplicationContext().getCacheDir(), FILENAME);
            SensorEvent event = events[0];
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                acc_x = event.values[0];
                acc_y = event.values[1];
                acc_z = event.values[2];

            }
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                gyro_x = event.values[0];
                gyro_y = event.values[1];
                gyro_z = event.values[2];

            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mag_x = event.values[0];
                mag_y = event.values[1];
                mag_z = event.values[2];

            }//String temp_sensor_data = System.currentTimeMillis()+" "+x+" "+y+" "+z+""+start_app_time;

            if (!file.exists()) {
                try{
                    file.createNewFile();
                }catch (Exception e){
                    Log.d("ERROR CREATIe FILE-MAIN",e.getMessage());
                }
            }
            try {
                Writer mwriter = new BufferedWriter(new FileWriter(file, true));
                String temp_sensor_data = System.currentTimeMillis() + "," + acc_x + "," + acc_y + "," + acc_z + "," + gyro_x + "," + gyro_y + "," + gyro_z + "," + mag_x + "," + mag_y + "," + mag_z+"\n";
                mwriter.write(temp_sensor_data);
                mwriter.close();
            } catch (IOException e) {
                Log.d("Controller", e.getMessage() + e.getLocalizedMessage() + e.getCause());
            }
            //Toast.makeText(getApplicationContext(), temp_sensor_data, Toast.LENGTH_LONG).show();
            //Logic to implement distraction detection

            if (count % 100 == 0) {

                file = new File(myContextRef.getApplicationContext().getCacheDir(), FILENAME);

                if(file.exists()) {
                    StringBuilder text = new StringBuilder();

                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String line;

                        while ((line = br.readLine()) != null) {
                            text.append(line);
                            text.append('\n');
                        }
                        br.close();
                    }
                    catch (IOException e) {
                        Log.d("Reading file", "ERROR" +e.getMessage());
                        //You'll need to add proper error handling here
                    }
                    Log.d("BEFORE DELETION Count", String.valueOf(count));
                    Log.d("BEFORE DELETION", String.valueOf(text));
                    boolean deleted = file.delete();
                }
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result != null && result== true) {
                myContextRef.notifyEmergencyContacts();
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
        senSensorManager.registerListener(MainActivity.this, senMagnetometer ,SensorManager.SENSOR_DELAY_NORMAL);
        senSensorManager.registerListener(MainActivity.this, senGyrometer ,SensorManager.SENSOR_DELAY_NORMAL);
    }
}

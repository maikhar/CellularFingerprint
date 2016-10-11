package com.example.maikhar.cellularfingerprint;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class secondActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy KK:mm:ss a");
    List<CustObj> clist_main;
    TextView parameters;
    int sec;
    ArrayList<String> clist;
    String abc1,str,result,result1;
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor compass;
    private SensorEventListener accelerometerListner;
    private SensorEventListener compassListner;


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},1);
        } else {
            //TODO
        }



        setContentView(R.layout.activity_second);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        compass = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (getIntent().getStringExtra("timer") == null)
            sec=5000;
        else
            sec = Integer.parseInt(getIntent().getStringExtra("timer"))*1000;

        clist = getIntent().getStringArrayListExtra("clist");

         clist_main = new ArrayList<>();
        parameters = (TextView) findViewById(R.id.parameters);
        CustObj c1;
        for(String name1: clist)
        {
            c1 = new CustObj(name1,"0");
            clist_main.add(c1);
        }




        Log.d("clist:",clist.toString());
       // Log.e("Size",String.valueOf(new_list.size()));


        display();
        Timer t = new Timer("MyTask",true);
        t.scheduleAtFixedRate(new timework(),0,sec);
    }

    private class timework extends TimerTask{

        @Override
        public void run() {
            updateUI();
            Thread t = new Thread() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            display();
                            Log.d("debug","Calling display method");
                        }
                    });
                }

            };
            t.start();
        }
    }



       // private class MyPhoneStateListener extends PhoneStateListener {
            /*
             * Get the Signal strength from the provider, each time there is an
             * update
             */
         //   @Override
           // public void onSignalStrengthsChanged(SignalStrength signalStrength) {
             //   super.onSignalStrengthsChanged(signalStrength);


               // abc1 = String.valueOf((2 * signalStrength
                 //       .getGsmSignalStrength()) - 113);

            /*SharedPreferences prefs = getSharedPreferences("DD", 0);

            SharedPreferences.Editor Editor = prefs.edit();

            Editor.putString("signal", abc1);*/

                //str = String.valueOf(signalStrength.getGsmSignalStrength());


                // Toast.makeText(
                // Tom Xue: lifecycle related
                // getApplicationContext(),
                // "Main Cell Dbm : "
                // + sig.getText(),
                // Toast.LENGTH_SHORT).show();

            //}
        //}
        //;/* End of private Class */






    public void updateUI(){

        Calendar c = Calendar.getInstance();
        String strDate = sdf.format(c.getTime());
        final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager
                .getCellLocation();
        String cellid = String.valueOf(cellLocation.getCid());



        for(CustObj co:clist_main){
            if(co.getName().equals("datetime")){
                co.setVal(strDate);
                Log.d("update","Calling UpdateUI");
            }
            if(co.getName().equals("cellid"))
            {
                co.setVal(cellid);
            }
            if(co.getName().equals("maincell"))
            {
                class MyPhoneStateListener extends PhoneStateListener {
                    /*
                     * Get the Signal strength from the provider, each time there is an
                     * update
                     */
                    @Override
                    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                        super.onSignalStrengthsChanged(signalStrength);


                        abc1 = String.valueOf((2 * signalStrength
                                .getGsmSignalStrength()) - 113);

            /*SharedPreferences prefs = getSharedPreferences("DD", 0);

            SharedPreferences.Editor Editor = prefs.edit();

            Editor.putString("signal", abc1);*/

                        str = String.valueOf(signalStrength.getGsmSignalStrength());


                        // Toast.makeText(
                        // Tom Xue: lifecycle related
                        // getApplicationContext(),
                        // "Main Cell Dbm : "
                        // + sig.getText(),
                        // Toast.LENGTH_SHORT).show();

                    }
                }
                co.setVal(str);
            }

            if(co.getName().equals("accelerometer"))
            {

                co.setVal(accele());
            }

            if(co.getName().equals("compass"))
            {
                compassListner = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
//                Log.d("pranjal","sensorchanged compass events = "+event);

                        final double alpha = 0.8;
                        final double linear_acceleration[];
                        final double gravity[];

                        gravity = new double[3];
                        linear_acceleration = new double[3];

                        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                        linear_acceleration[0] = event.values[0] - gravity[0];
                        linear_acceleration[1] = event.values[1] - gravity[1];
                        linear_acceleration[2] = event.values[2] - gravity[2];

                        result1 = ("Compass Data X: "+ String.valueOf(linear_acceleration[0])+" Y: "+String.valueOf(linear_acceleration[1])+" Z: "+String.valueOf(linear_acceleration[2]));


                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                };
                co.setVal(result1);
            }
        }

    }

    public void display()
    {
        StringBuilder builder = new StringBuilder();
        builder.setLength(0);
        for (CustObj c : clist_main) {

            builder.append(c.getName() + " :      " + c.getVal() + "\n");

        }

       /* Handler h = new Handler();
      Runnable r = new Runnable() {
          @Override
          public void run() {
                 h.postDelayed(this,,sec);
          }
      };*/



        parameters.setText(builder.toString());
        parameters.setTextColor(Color.BLACK);
        parameters.setTextSize(20);
    }

    public String accele()
    {
        accelerometerListner = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
//                Log.d("pranjal","sensorchanged accelerometer events = ");


                final double alpha = 0.8;
                final double linear_acceleration[];
                final double gravity[];


                gravity = new double[3];
                linear_acceleration = new double[3];

                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                linear_acceleration[0] = event.values[0] - gravity[0];
                linear_acceleration[1] = event.values[1] - gravity[1];
                linear_acceleration[2] = event.values[2] - gravity[2];

                result = ("Accelerometer Data X: "+ String.valueOf(linear_acceleration[0])+" Y: "+String.valueOf(linear_acceleration[1])+" Z: "+String.valueOf(linear_acceleration[2]));

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

        };
        return result;

    }
}

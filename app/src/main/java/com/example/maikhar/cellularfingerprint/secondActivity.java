package com.example.maikhar.cellularfingerprint;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
    String abc1,str,result="",result1="",mcc,mnc,neiglist="";
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor compass;
    private SensorEventListener accelerometerListner;
    private SensorEventListener compassListner;
    TelephonyManager Tel;
    Gpstracker gps;
    Double latitude,longitude;
    LocationManager locationManager;
    File myFile;
    OutputStreamWriter myOutWriter;
    FileOutputStream fOut;
    StringBuilder telephonyInfo;
    String Filepath = "/sdcard/cell.txt";
    private boolean Permission_Granted = false;
    private boolean GPS_Permission = false;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 100;
    private static final int MY_PERMISSIONS_FINE_LOCATION = 105;
    private RequestQueue mqueue;
    private static final String Url = "http://cellularfingerprint.pe.hu/upload/upload.php";
    private ProgressDialog pDialog=null;
    private String IFilename = "cell.txt;";
    private String Ifilepath = "";

    List<CellInfo> mNeighboringCellInfo;

    final String uploadFilePath = "/sdcard/";
    final String uploadFileName = "cell.txt";

    MyPhoneStateListener MyListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_second);
        telephonyInfo = new StringBuilder();
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},1);
        } else {
            //TODO
        }
        MyVolley.init(this);
        mqueue = MyVolley.getRequestQueue();


        if(checkPermission()) {
            Permission_Granted = true;
            myFile = new File(Filepath);

            if (!myFile.exists()) {
                try {
                    myFile.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        if(fileExist(IFilename)){
            this.deleteFile(IFilename);
        }




        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        compass = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        MyListener = new MyPhoneStateListener();
        Tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);


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

                result1 = "Compass Data X: "+ String.valueOf(linear_acceleration[0])+" Y: "+String.valueOf(linear_acceleration[1])+" Z: "+String.valueOf(linear_acceleration[2]);
                //Log.d("com",result1);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        Log.d("IntiliazeGps","true");
        if(checkGpsPermission()) {
            GPS_Permission = true;
            gps = new Gpstracker(secondActivity.this);
            latitude = (gps.getLatitude());
            longitude = gps.getLongitude();
            Log.d("IGps", "true");
            String svcName = Context.LOCATION_SERVICE;
            locationManager = (LocationManager) getSystemService(svcName);

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(true);
            String provider = locationManager.getBestProvider(criteria, true);

            Location l = locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.d("lat", String.valueOf(latitude));
        }

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

    public boolean fileExist(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

    private class timework extends TimerTask{

        @Override
        public void run() {
           if(Permission_Granted)
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
    public void startprogress(){
        pDialog = new ProgressDialog(secondActivity.this);

        pDialog.setMessage("Uploading File...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void stopprogress(){
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

    public boolean checkGpsPermission(){
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(secondActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(secondActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(secondActivity.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission Necessary");
                    alertBuilder.setMessage("GPS permission is necessary to get Location!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(secondActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_FINE_LOCATION);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(secondActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_FINE_LOCATION);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public boolean checkPermission()
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(secondActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(secondActivity.this, Manifest.permission.WRITE_CALENDAR)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(secondActivity.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission Necessary");
                    alertBuilder.setMessage("Write SDCard permission is necessary to Save file!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(secondActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(secondActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Permission_Granted = true;
                    myFile = new File(Filepath);

                    if (!myFile.exists()) {
                        try {
                            myFile.createNewFile();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    //Toast.makeText(getApplicationContext(),"permission granted",Toast.LENGTH_SHORT).show();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Permission_Granted = false;
                    Toast.makeText(getApplicationContext(),"You won't be able to Save data in file", Toast.LENGTH_SHORT).show();
                }

            }

            case MY_PERMISSIONS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                   GPS_Permission = true;
                } else {
                   GPS_Permission = false;
                    Toast.makeText(getApplicationContext(),"You won't be able to get Location Service", Toast.LENGTH_SHORT).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
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






    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void updateUI(){

        Calendar c = Calendar.getInstance();
        String strDate = sdf.format(c.getTime());
        final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager
                .getCellLocation();
        String cellid = String.valueOf(cellLocation.getCid());


           /* List<NeighboringCellInfo>  neighCell = null;
            //TelephonyManager telManager = ( TelephonyManager )getSystemService(Context.TELEPHONY_SERVICE);
            neighCell = telephonyManager.getNeighboringCellInfo();
            String neig = null;
            for (int i = 0; i < neighCell.size(); i++) {
                try {
                    NeighboringCellInfo thisCell = neighCell.get(i);
                    int thisNeighCID = thisCell.getCid();
                    int thisNeighRSSI = thisCell.getRssi();
                    int lac = thisCell.getLac();
                    neig = (" "+String.valueOf(thisNeighCID)+" - "+String.valueOf(thisNeighRSSI)+" " + String.valueOf(lac));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    NeighboringCellInfo thisCell = neighCell.get(i);
                    Log.d("err",neighCell.toString());
                }
            }
*/



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        //boolean statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        String networkOperator = telephonyManager.getNetworkOperator();
        mNeighboringCellInfo = telephonyManager.getAllCellInfo();
        Log.d("Neighbors : ", String.valueOf(mNeighboringCellInfo));
       /* if (!mNeighboringCellInfo.isEmpty()) {
            int i = 1;
            for (CellInfo cellInfo : mNeighboringCellInfo) {
                Log.i("cellinfo", "Neighbor CellInfo No." + i + " LAC:"
                                + cellInfo.getLac() + ", CID:"
                                + cellInfo.getCid() + ", RSSI:"
                                + cellInfo.getRssi());
                i++;
                telephonyInfo.append("Neighbor CellInfo No." + i
                        + ", Location Area Code: " + cellInfo.getLac()
                        + ", Cell ID: " + cellInfo.getCid()
                        + ", Signal Strength: " + cellInfo.getRssi() + "\r\n");
            }
        } else
            telephonyInfo.append("No Neighbor Cell Info!");*/






        for(CustObj co:clist_main){
            if(co.getName().equals("datetime")){
                co.setVal(strDate);



            }
            if(co.getName().equals("cellid"))
            {


                co.setVal(cellid);
            }
            if(co.getName().equals("maincell"))
            {


                co.setVal(abc1);
            }

            if(co.getName().equals("accelerometer"))
            {


                co.setVal(result);
            }

            if(co.getName().equals("compass"))
            {


                co.setVal(result1);
            }
            if(co.getName().equals("latitude"))
            {



                co.setVal(String.valueOf(latitude));

            }
            if(co.getName().equals("longitude"))
            {

                co.setVal(String.valueOf(longitude));
            }
            if(co.getName().equals("mcc"))
            {
                mcc = (networkOperator.substring(0, 3));

                co.setVal(String.valueOf(mcc));


            }
            if(co.getName().equals("mnc"))
            {
                mnc = (networkOperator.substring(3));


                co.setVal(String.valueOf(mnc));
            }

            if(co.getName().equals("neighbors"))
            {

                co.setVal(String.valueOf(mNeighboringCellInfo));
            }
        }



       /* try {


                FileOutputStream fOut=null;
                fOut = new FileOutputStream(myFile, true);
                myOutWriter = new OutputStreamWriter(
                        fOut);

            myOutWriter.append("\n");
            myOutWriter.append(cellid);
            myOutWriter.append("\n");
            myOutWriter.append(strDate);
            myOutWriter.append("\n");
            myOutWriter.append(abc1);
            myOutWriter.append("\n");
            myOutWriter.append(result);
            myOutWriter.append("\n");
            myOutWriter.append(result1);
            myOutWriter.append("\n");
            myOutWriter.append(String.valueOf(latitude));
            myOutWriter.append("\n");
            myOutWriter.append(String.valueOf(longitude));
            myOutWriter.append("\n");
            myOutWriter.append(mnc);
            myOutWriter.append("\n");
            myOutWriter.append(mcc);



            myOutWriter.close();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        try {
            FileOutputStream fo=null;
            fo = openFileOutput(IFilename, Context.MODE_APPEND);
            //myOutWriter = new OutputStreamWriter(fo);

            StringBuilder sb = new StringBuilder();
            sb.append(cellid+"\n"+strDate+"\n"+abc1+"\n"+result+"\n"+result1+"\n"+String.valueOf(latitude)+"\n"+String.valueOf(longitude)+"\n"+mnc+"\n"+mcc);
            fo.write(sb.toString().getBytes());
            /*myOutWriter.append(cellid);
            myOutWriter.append("\n");
            myOutWriter.append(strDate);
            myOutWriter.append("\n");
            myOutWriter.append(abc1);
            myOutWriter.append("\n");
            myOutWriter.append(result);
            myOutWriter.append("\n");
            myOutWriter.append(result1);
            myOutWriter.append("\n");
            myOutWriter.append(String.valueOf(latitude));
            myOutWriter.append("\n");
            myOutWriter.append(String.valueOf(longitude));
            myOutWriter.append("\n");
            myOutWriter.append(mnc);
            myOutWriter.append("\n");
            myOutWriter.append(mcc);
            myOutWriter.append("\n");

            myOutWriter.close();*/
            fo.close();
            Ifilepath = this.getFileStreamPath(IFilename).getAbsolutePath();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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






    protected void onResume() {
        super.onResume();
       Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        mSensorManager.registerListener(accelerometerListner, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(compassListner, compass, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);
        mSensorManager.unregisterListener(accelerometerListner);
        mSensorManager.unregisterListener(compassListner);
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        /*
         * Get the Signal strength from the provider, each time there is an
         * update
         */
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            //sig = (TextView) findViewById(R.id.Mrssi);

            abc1 = String.valueOf((2 * signalStrength
                    .getGsmSignalStrength()) - 113);

            SharedPreferences prefs = getSharedPreferences("DD", 0);

            SharedPreferences.Editor Editor = prefs.edit();

            Editor.putString("signal", abc1);

           str = String.valueOf(signalStrength.getGsmSignalStrength());

            //sig.setText("main cell dbm : " + abc1);
            // Toast.makeText(
            // Tom Xue: lifecycle related
            // getApplicationContext(),
            // "Main Cell Dbm : "
            // + sig.getText(),
            // Toast.LENGTH_SHORT).show();

        }
    };/* End of private Class */
    public void uploadFile(String path){
        startprogress();
        SimpleMultiPartRequest smp = new SimpleMultiPartRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                stopprogress();
                Log.d("resp:",response);
                Toast.makeText(getApplicationContext(),"Uploaded Successfully",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stopprogress();
                Log.e("Error:",error.getMessage());
                Toast.makeText(getApplicationContext(),"Error Ocurred",Toast.LENGTH_SHORT).show();
            }
        });
        smp.addFile("upload_file",path);
        mqueue.add(smp);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_2, menu);//Menu Resource, Menu
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Upload:
                if(!Permission_Granted){
                    Toast.makeText(getApplicationContext(),"Try again After Allowing Permission",Toast.LENGTH_LONG).show();
                    checkPermission();
                }
                else{
                    Log.d("filepath:",Ifilepath);
                    uploadFile(Ifilepath);
                }
               // Toast.makeText(getApplicationContext(),"Item 1 Selected",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

/*String stringNeighboring = "Neighboring List- Lac : Cid : RSSI\n";

							for (int i = 0; i < NeighboringList.size(); i++) {

								String dBm;
								int rssi = NeighboringList.get(i).getRssi();
								rssi = (2 * rssi) -113 ;
								if (rssi == NeighboringCellInfo.UNKNOWN_RSSI) {
									dBm = "Unknown RSSI";
								} else {
									dBm = String.valueOf(rssi) + " dBm";
								}

								stringNeighboring = stringNeighboring
										+ String.valueOf(NeighboringList.get(i)
												.getLac())
										+ "\t     :    "
										+ String.valueOf(NeighboringList.get(i)
												.getCid()) + "\t     :    "
										+ dBm + "\n";
							}
							*/
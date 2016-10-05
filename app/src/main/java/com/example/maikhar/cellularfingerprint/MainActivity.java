package com.example.maikhar.cellularfingerprint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;





public class MainActivity extends AppCompatActivity implements View.OnClickListener {

   // ArrayList<String> selection = new ArrayList<String>();
    //CheckBox check1,check2,check3,check4,check5,check6,check7,check8,check9,check10;
    ArrayList<String> clist;
    Boolean actgps;
    CheckBox gps,gps1;
    LocationManager manager;
    String timer;
    AlertDialog alert;
    AlertDialog alertGps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Uncomment the below code to Set the message and title from the strings.xml file
        //builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

        //Setting message manually and performing action on button click
        final EditText input=new EditText(MainActivity.this);
        input.setText("10");
        builder.setMessage("Default is 10 sec.") // chnage this
                .setCancelable(false)
                .setView(input)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //builder.setView(null);
                        String temp=input.getText().toString();
                        int value=10;
                        try {
                             value = Integer.parseInt(temp);
                        }catch (NumberFormatException e)
                        {
                            Toast.makeText(MainActivity.this,"Only Numbers",Toast.LENGTH_SHORT).show();
                        }
                        if( value<5 || value>15)
                        {

                            ((ViewGroup)input.getParent()).removeView(input);
                            AlertDialog alert = builder.create();
                            alert.setMessage("Value should be between 5 and 15 ");
                            alert.setTitle("*Enter the Timer value");
                            alert.show();
                        }
                        else
                        {
                            timer=String.valueOf(value);
                            Toast.makeText(getBaseContext(),"Success value is"+value,Toast.LENGTH_LONG).show();
                        }
                    }
                });

        //Creating dialog box
        alert = builder.create();
        //Setting the title manually
        alert.setTitle("Enter the Timer value");






        Button start = (Button)findViewById(R.id.start);

        clist = new ArrayList<>();
        gps = (CheckBox) findViewById(R.id.latitude);
        gps1 = (CheckBox) findViewById(R.id.longitude);
        start.setOnClickListener(this);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!statusOfGPS)
        {
            gps.setEnabled(false);
            gps1.setEnabled(false);
            //Log.e("debug","GPS is off");
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle("Delete entry")
                    .setMessage("Your GPS is off. Do you want to turn on your GPS?")
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                            gps.setEnabled(true);
                            gps1.setEnabled(true);
                            gps1.setText("Latitude");
                            gps.setText("Longitude");


                        }
                    });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();

                    gps1.setText("Enable location service");
                    gps.setText("Enable location service");

                }
            });

            alertGps=alertDialog.create();
        alertDialog.show();
        }




    }

   /* public boolean actuallyOn(){
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return statusOfGPS;

    }*/

    public void selectItinerary(View view) {



       // StringBuilder builder = new StringBuilder();




        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {

            case R.id.maincell:

                if (checked)
                {
                    //selection.add("maincell");
                    clist.add("maincell");
                }

                else
                {//selection.remove("maincell");

                    clist.remove("maincell");}
                break;
            case R.id.compass:

                if (checked)
                {//selection.add("compass");

                    clist.add("compass");}

                else
                {//selection.remove("compass");

                    clist.remove("compass");}

                break;
            case R.id.accelerometer:

                if (checked)
                {clist.add("accelerometer");}

                else
                {clist.remove("accelerometer");}

                break;
            case R.id.mcc:

                if (checked)
                {clist.add("mcc");}

                else
                {clist.remove("mcc");}

                break;
            case R.id.mnc:

                if (checked)
                {clist.add("mnc");}

                else
                {clist.remove("mnc");}

                break;
            case R.id.cellid:

                if (checked)
                {clist.add("cellid");}

                else
                {clist.remove("cellid");}

                break;
            case R.id.datettime:

                if (checked)
                {clist.add("datetime");}

                else
                {clist.remove("datetime");}

                break;
            case R.id.latitude:

                if (checked)
                {clist.add("latitude");}

                else
                {clist.remove("latitude");}

                break;
            case R.id.longitude:

                if (checked)
                {clist.add("longitude");}

                else
                {clist.remove("longitude");}

                break;
            case R.id.neighbors:

                if (checked)
                {clist.add("neighbors");}

                else
                {clist.remove("neighbors");}

                break;

        }

    }


    @Override
    public void onClick(View view) {
        Intent i = new Intent(MainActivity.this,secondActivity.class);
        //i.putStringArrayListExtra("sel",selection);
        i.putStringArrayListExtra("clist",clist);
        i.putExtra("timer",timer);
        startActivity(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.timerSetting:
                Toast.makeText(getApplicationContext(),"Chnage reload time in sec",Toast.LENGTH_LONG).show();
                alert.show();
                break;
        }
        return true;
    }


}



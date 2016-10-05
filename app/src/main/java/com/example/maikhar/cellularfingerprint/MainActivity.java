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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {

   // ArrayList<String> selection = new ArrayList<String>();
    //CheckBox check1,check2,check3,check4,check5,check6,check7,check8,check9,check10;
    ArrayList<String> clist;
    Boolean actgps;
    CheckBox gps,gps1;
    LocationManager manager;
    String timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter the interval in seconds");
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               timer = input.getText().toString();
              /*  int tim = Integer.parseInt(timer);
                if(tim <= 15 || tim >=5)
                {
                    AlertDialog dialog1 = builder.create();
                    dialog1.show();
                }
*/
            }

    });
  //      AlertDialog dialog = builder.create();
            builder.show();
    //    dialog.show();





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


                        }
                    });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
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
}



package com.example.myapplication;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ScannerFragment extends Fragment {
    FirebaseAuth auth;
    FirebaseUser user;
    AlertDialog.Builder alertDialog;
    Dialog dialog;
    SeekBar seekbar;
    String Date;
    TextView triggerlevel, seektriggerlevel, triggerlevelupd;
    final int maxValue = 100;
     int interval = maxValue / 5;
    int adjustedProgress,Progress ;
    TextView Moisture, Temprature, Humidity, Pumpstatusoff, Pumpstatuson, date;
    Spinner spinner;
    ImageButton imageButton;
    private Handler handler;
    String pump_trigger, pump_status, update_trigger;
    String formattedDate;
    DatabaseReference databaseRef;
    DatabaseReference database;
    LineChart mplinechart;
    LineDataSet lineDataSet1=new LineDataSet(dataValue1(),"Moisture");
    LineDataSet lineDataSet2=new LineDataSet(dataValue2(),"Moisture");
    ArrayList<ILineDataSet>dataSets=new ArrayList<>();
    LineData lineData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);
        imageButton = view.findViewById(R.id.ibLogout);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        seekbar = view.findViewById(R.id.seekbar);
        triggerlevel = view.findViewById(R.id.tvtriggerlevel);
        Moisture = view.findViewById(R.id.tvmoisture);
        Temprature = view.findViewById(R.id.tvtemprature);
        Humidity = view.findViewById(R.id.tvhumidity);
        Pumpstatusoff = view.findViewById(R.id.tvpumpoff);
        Pumpstatuson = view.findViewById(R.id.tvpumpon);
       /* spinner = view.findViewById(R.id.spinner);*/
        seektriggerlevel = view.findViewById(R.id.tvseeklevelupdat);
        triggerlevelupd = view.findViewById(R.id.tvtriggerupda);
       /* date = view.findViewById(R.id.tvdate);*/
        handler = new Handler();

        ///chart
        mplinechart=view.findViewById(R.id.linechart);
        lineDataSet2.setDrawValues(false);
        int color = ContextCompat.getColor(getContext(), R.color.black);
        lineDataSet2.setColor(color);
        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);
        LineData data = new LineData(dataSets);
        mplinechart.setDrawGridBackground(true);
        mplinechart.setDrawBorders(true);
        lineDataSet1.setColor(R.color.purple_700);
        /*lineDataSet1.setLineWidth(4);
        lineDataSet2.setLineWidth(4);
        lineDataSet1.setCircleRadius(3);
        lineDataSet2.setCircleRadius(3);*/
        lineDataSet2.setDrawFilled(true);
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.blackgradient);
            lineDataSet2.setFillDrawable(drawable);
        }
        else {
            lineDataSet2.setFillColor(Color.BLACK);
        }
        Description description = new Description();
        description.setText("Moisture Log");
        description.setTextColor(R.color.white);
        description.setTextSize(14);
        description.setTextAlign(Paint.Align.RIGHT);
        mplinechart.animateXY(2000,2000, Easing.EaseInOutSine,Easing.EaseInOutSine);
        mplinechart.setDescription(description);

            mplinechart.setData(data);
            mplinechart.getLegend().setEnabled(false);
            mplinechart.invalidate();







        ////method calls

        /*Spinner();*/
        onClicklistener();
        Reterivedatafromfirebase();




        return view;
    }

    public void onClicklistener() {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Log Out ");
                alertDialog.setMessage("Are you sure want to logout ?").setCancelable(false);
                alertDialog.setIcon(R.drawable.baseline_exit_to_app_24);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alertDialog.create().show();
            }

        });

    }

    public void Reterivedatafromfirebase() {

        databaseRef = FirebaseDatabase.getInstance().getReference("firebase-iot/live-data");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    String livedata = dataSnapshot.getValue().toString();
                    JSONObject jsonObject = new JSONObject(livedata);
                    String humidity = jsonObject.getString("humidity");
                    String moisture = jsonObject.getString("moisture");
                    String temprature = jsonObject.getString("temperature");
                     /*Date = jsonObject.getString("date");*/
                    pump_trigger = jsonObject.getString("pump_trigger");
                    pump_status = jsonObject.getString("pump_status");
                    update_trigger = jsonObject.getString("update_trigger");
                    Humidity.setText(humidity + " " + "%");
                    Moisture.setText(moisture + " " + "%");
                    Temprature.setText(temprature + " " + "%");
                    //triggerlevel.setText("Cur : " + pump_trigger);

                    if ("false".equals(pump_status)) {
                        Pumpstatusoff.setBackgroundResource(R.drawable.pumpstatussss);
                        Pumpstatuson.setBackgroundResource(R.drawable.pumpstatus1);
                    } else {
                        Pumpstatuson.setBackgroundResource(R.drawable.pumpstatuson);
                        Pumpstatusoff.setBackgroundResource(R.drawable.pumpstatus);

                    }
                    String updatedtrigger = String.valueOf(Progress);
                    if (!updatedtrigger.equals(pump_trigger)) {
                        databaseRef.child("update_trigger").setValue(updatedtrigger);
                        triggerlevelupd.setText("Upd :" + updatedtrigger);
                        triggerlevel.setText("Cur :" + pump_trigger);

                    } else {
                        triggerlevel.setText("Cur :" + pump_trigger);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Seekbar();
                        /*changedate();*/
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read value.", error.toException());
                Toast.makeText(getContext(), "Connect to internet and try again", Toast.LENGTH_SHORT).show();
            }
        });


    }

   /* public void Spinner() {
        List<String> Plantnames = new ArrayList<>();
        Plantnames.add("Aloe vera ");
        Plantnames.add("ZZ plant");
        Plantnames.add("Snake plant ");
        Plantnames.add("Money plant");
        ArrayAdapter<String> plantAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, Plantnames);
        plantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(plantAdapter);

    }*/

    public void Seekbar() {
       /*int trigger=Integer.parseInt(pump_trigger);
       seekbar.setProgress(trigger);*/

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Progress=progress;
                seektriggerlevel.setText("Prog : " + String.valueOf(progress));


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seektriggerlevel.setVisibility(View.GONE);
                Reterivedatafromfirebase();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seektriggerlevel.setVisibility(View.VISIBLE);
                Reterivedatafromfirebase();
            }
        });



    }
  /*  public void changedate() {
        String timestamp = Date;
        long millis = Long.parseLong(timestamp);
        Date datee = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        formattedDate = sdf.format(datee);
        date.setText(formattedDate);

    }*/
     private ArrayList<Entry> dataValue1(){
        ArrayList<Entry>data=new ArrayList<Entry>();
        data.add(new Entry(1,20));
         data.add(new Entry(2,98));
         data.add(new Entry(3,74));
         data.add(new Entry(4,59));
         data.add(new Entry(5,41));
         data.add(new Entry(6,28));
         data.add(new Entry(7,100));


         return data;
     }
  private ArrayList<Entry> dataValue2() {
      ArrayList<Entry> data = new ArrayList<Entry>();
      data.add(new Entry(1, 30));
      data.add(new Entry(2, 30));
      data.add(new Entry(3, 30));
      data.add(new Entry(4, 30));
      data.add(new Entry(5, 30));
      data.add(new Entry(6, 30));
      data.add(new Entry(7, 30));


      return data;
  }
 /*   public void ReteriveMoisture(){
        database = FirebaseDatabase.getInstance().getReference("firebase-iot/past-data");
        ArrayList<Entry> datavals = new ArrayList<Entry>();
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //String pastsata=snapshot.getValue().toString();
                if(snapshot.hasChildren()) {
                    for (DataSnapshot documentSnapshot : snapshot.getChildren()) {
                        Long moisture = documentSnapshot.child("moisture").getValue(Long.class);
                        Long temperature = documentSnapshot.child("temperature").getValue(Long.class);
                        int moistureValue = (moisture != null) ? moisture.intValue() : 0;
                        int temperatureValue = (temperature != null) ? temperature.intValue() : 0;
                        DataPoint dataPoint = new DataPoint(moistureValue, temperatureValue);
                        datavals.add(new Entry(dataPoint.getMoisture(), dataPoint.getTemperature()));
                    }
                    showChart(datavals);
                }else{
                    mplinechart.clear();
                    mplinechart.invalidate();
                }
               *//* try {
                    JSONObject jsonObject=new JSONObject(pastsata);
                    String moisture = jsonObject.getString("moisture");
                    String temprature = jsonObject.getString("temperature");

                } catch (JSONException e) {
                    e.printStackTrace();
                }*//*

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }*/

   /* private void showChart(ArrayList<Entry> datavals) {
      lineDataSet1.setValues(datavals);
      lineDataSet1.setLabel("Moisture Log");
      lineDataSet1.clear();
      dataSets.clear();
      dataSets.add(lineDataSet1);
      lineData = new LineData(dataSets);
      mplinechart.clear();
      mplinechart.setData(lineData);
      mplinechart.invalidate();


    }*/


}

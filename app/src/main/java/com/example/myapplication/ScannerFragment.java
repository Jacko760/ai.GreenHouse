package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScannerFragment extends Fragment {
    FirebaseAuth auth;
    FirebaseUser user ;
    AlertDialog.Builder alertDialog;
    Button button;
    private  String  clientId = "place your client id";
    private String clientSecret = "place your client secret";
    Response response = null;
    private String responseiot;
    private String output;
    String current_humid ,current_temprature,current_moisture,raw_moisture,trigger_level,pump_status;
    String url;
    String Url;
    TextView currenthumid,currenttemprature,currentmoisture,rawmoisture,triggerlevel,pumpstatus;

    Dialog dialog;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_scanner, container, false);
        ImageButton imageButton =view.findViewById(R.id.ibLogout);
        currenthumid=view.findViewById(R.id.tvcurrenthumid);
        currenttemprature=view.findViewById(R.id.tvcurrenttemprature);
        currentmoisture=view.findViewById(R.id.tvcurrentmoisture);
        rawmoisture=view.findViewById(R.id.tvrawmoisture);
        triggerlevel=view.findViewById(R.id.tvtriggerlevel);
        pumpstatus=view.findViewById(R.id.tvpumpstatus);
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait1);
        dialog.setCanceledOnTouchOutside(false);

        button=view.findViewById(R.id.btnInsight);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog=new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Log Out ");
                alertDialog.setMessage("Are you sure want to logout ?").setCancelable(false);
                alertDialog.setIcon(R.drawable.baseline_exit_to_app_24);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getContext(),LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_USER_ACTION);
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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    acessToken();
                    dialog.show();



                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*currenthumid.setVisibility(View.VISIBLE);
                currenttemprature.setVisibility(View.VISIBLE);
                currentmoisture.setVisibility(View.VISIBLE);
                rawmoisture.setVisibility(View.VISIBLE);
                triggerlevel.setVisibility(View.VISIBLE);
                pumpstatus.setVisibility(View.VISIBLE);*/

            }

        });
         return view;
}
        public void acessToken() throws Exception {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient client = new OkHttpClient();
                    url = "https://api2.arduino.cc/iot/v1/clients/token";
                    RequestBody requestBody = new FormBody.Builder()
                            .add("grant_type", "client_credentials")
                            .add("client_id", clientId)
                            .add("client_secret", clientSecret)
                            .add("audience", "https://api2.arduino.cc/iot")
                            .build();
                    Request request = new Request.Builder()
                            .url(url)
                            .header("content-type", "application/x-www-form-urlencoded")
                            .post(requestBody)
                            .build();
                    try {
                        response = client.newCall(request).execute();
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        output=jsonObject.getString("access_token");
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "run: ",e);
                        //throw new RuntimeException(e);
                    }
                    if (!response.isSuccessful()){
                        Log.d(TAG, "No: response :"+response.code());
                       getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(ScannerFragment.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    iotProperties();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                Log.d(TAG, "run: response :" + response.code());
                                //Toast.makeText(MainActivity.this, "Got the token" , Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();
        }
        public void iotProperties() throws Exception{

            new Thread(new Runnable() {
                @Override
                public void run() {

                    OkHttpClient Client = new OkHttpClient();
                    Url ="https://api2.arduino.cc/iot/v2/things/530e87a0-3767-4288-9aad-ea5f14107c6a/";
                    RequestBody requestBody = RequestBody.create("", MediaType.parse("application/json; charset=utf-8"));
                    Request request = new Request.Builder()
                            .url(Url)
                            .addHeader("Authorization", "Bearer "+ output)
                            .post(requestBody)
                            .build();
                    try {
                        response = Client.newCall(request).execute();
                        responseiot = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseiot);
                        JSONArray jsonArray=jsonObject.getJSONArray("properties");
                        current_humid=jsonArray.getJSONObject(0).getString("last_value");
                        current_temprature=jsonArray.getJSONObject(1).getString("last_value");
                        current_moisture=jsonArray.getJSONObject(2).getString("last_value");
                        raw_moisture=jsonArray.getJSONObject(3).getString("last_value");
                        trigger_level=jsonArray.getJSONObject(4).getString("last_value");
                        pump_status=jsonArray.getJSONObject(5).getString("last_value");


                    }catch (IOException e){
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //throw new RuntimeException(e);
                    }
                    if(!response.isSuccessful()){
                        Log.d(TAG, "No: response :"+response.code());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(ScannerFragment.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else{
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                currenthumid.setText("current_hunmid "+":"+current_humid);
                                currenttemprature.setText("current_temprature"+":"+current_temprature);
                                currentmoisture.setText("current_moisture"+":"+current_moisture);
                                rawmoisture.setText("raw_moisture"+":"+raw_moisture);
                                triggerlevel.setText("Trigger_level"+":"+trigger_level);
                                pumpstatus.setText("pump_status"+":"+pump_status);
                                Log.d(TAG, "run: response :" + response.code());
                                dialog.dismiss();

                            }
                        });

                    }


                }
            }).start();
        }



    }

package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.MediaType;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import okhttp3.RequestBody;
import okhttp3.Response;

public class GoogleLensActivity extends AppCompatActivity {

    private static final int PIC_ID = 123;
    private static final int MY_CAMERA_REQUEST = 213;
    private ImageView ivSetImage,cancelButton,ivAddtogreenhouse,ivupload;
    LinearLayout Displayplantinfo,Addtogreenhouse,containerlinearlayout;
    FrameLayout frameLayout;
    String firestorePlantname;
    private AppCompatButton ivOpenCamera;
    private AppCompatButton ivOpenSearchResult;

    private AppCompatButton ivOpenDiagnose;
    private AppCompatButton btnAddtogreenhouse,btntryagain;
    private TextView tvResult,tvplantInfo,tvdisplayPlantmain;
    private Bitmap bitmap;
    CardView cardView;
    private byte[] bArray;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
    private static final MediaType MEDIA_TYPE_PLAINTEXT = MediaType.parse("text/plain; charset=utf-8");
    private File finalFile;
    Response response = null;
    public String output;

    public String diagnose;

    private String downloadURL;
    String Plantname;
    Dialog dialogg;
    private FirebaseFirestore firestore;
    BottomNavigationView bottomNavigationView;
    GreenHouseFragment greenHouseFragment = new GreenHouseFragment();
   /*  SearchView searchView;
     RecyclerView recyclerView;
     ArrayList<SearchModel> arrayList = new ArrayList<>();
     ArrayList<SearchModel>searchList;
     String[] pName=new String[]{"Aloevera", "Zzplant","Moneyplant","Peacelilly","Tulsi"};
     int[] imgList=new int[]{R.drawable.aloevera,R.drawable.zzplant,R.drawable.moneyplant,R.drawable.peacelilly,R.drawable.tulsi};

*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firestore=FirebaseFirestore.getInstance();
        widgetInitialization();
        setOnClickListener();
        dialogg = new Dialog(GoogleLensActivity.this);
        dialogg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogg.setContentView(R.layout.dialog_wait1);
        dialogg.setCanceledOnTouchOutside(false);
       /* searchView.clearFocus();
        for(int i =0;i<pName.length;i++){
            SearchModel searchModel =  new SearchModel();
            searchModel.setPlantName(pName[i]);
            searchModel.setPlantImage(imgList[i]);
            arrayList.add(searchModel);*/
        }
       /* RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(GoogleLensActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        SearchAdapter searchAdapter = new SearchAdapter(GoogleLensActivity.this,arrayList);
        recyclerView.setAdapter(searchAdapter);*/
     /* searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String query) {
              searchList=new ArrayList<>();

              if(query.length()>0){
                  for(int i =0 ;i<arrayList.size();i++ ){
                      if(arrayList.get(i).getPlantName().toUpperCase().contains(query.toUpperCase())){
                          SearchModel searchModel = new SearchModel();
                          searchModel.setPlantName(arrayList.get(i).getPlantName());
                          searchModel.setPlantImage(arrayList.get(i).getPlantImage());
                          searchList.add(searchModel);

                      }
                  }
                  RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(GoogleLensActivity.this);
                  recyclerView.setLayoutManager(layoutManager);
                  SearchAdapter searchAdapter = new SearchAdapter(GoogleLensActivity.this,searchList);
                  recyclerView.setAdapter(searchAdapter);

              }else{
                  RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(GoogleLensActivity.this);
                  recyclerView.setLayoutManager(layoutManager);
                  SearchAdapter searchAdapter = new SearchAdapter(GoogleLensActivity.this,arrayList);
                  recyclerView.setAdapter(searchAdapter);

              }
              return false;
          }

          @Override
          public boolean onQueryTextChange(String newText) {
              searchList=new ArrayList<>();
              if(newText.length()>0){
                  for(int i =0 ;i<arrayList.size();i++ ){
                      if(arrayList.get(i).getPlantName().toUpperCase().contains(newText.toUpperCase())){
                          SearchModel searchModel = new SearchModel();
                          searchModel.setPlantName(arrayList.get(i).getPlantName());
                          searchModel.setPlantImage(arrayList.get(i).getPlantImage());
                          searchList.add(searchModel);
                      }
                  }
                  RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(GoogleLensActivity.this);
                  recyclerView.setLayoutManager(layoutManager);
                  SearchAdapter searchAdapter = new SearchAdapter(GoogleLensActivity.this,searchList);
                  recyclerView.setAdapter(searchAdapter);
              }else{
                  RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(GoogleLensActivity.this);
                  recyclerView.setLayoutManager(layoutManager);
                  SearchAdapter searchAdapter = new SearchAdapter(GoogleLensActivity.this,arrayList);
                  recyclerView.setAdapter(searchAdapter);
              }
              return false;

          }
      });
    }*/

    private void widgetInitialization() {
        ivSetImage = findViewById(R.id.ivSetImage);
        ivOpenCamera = findViewById(R.id.ivOpenCamera);
        ivOpenSearchResult = findViewById(R.id.ivGetResult);
        cardView=findViewById(R.id.ww);
        ivupload=findViewById(R.id.uppl);
        ivOpenDiagnose=findViewById(R.id.btndiagnose);
        /*searchView=findViewById(R.id.searcView);
        recyclerView=findViewById(R.id.recyclerView);*/

    }
    private void showBottomDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_activity);
        Displayplantinfo = dialog.findViewById(R.id.layoutDisplayplantinfo);
        cancelButton = dialog.findViewById(R.id.cancelButton);
        tvplantInfo=dialog.findViewById(R.id.tvPlantinfo);
        tvdisplayPlantmain=dialog.findViewById(R.id.tvdisplayPlantmain);
        Addtogreenhouse=dialog.findViewById(R.id.layoutAddtogreenhouse);
        btnAddtogreenhouse=dialog.findViewById(R.id.btnAddtogreenhouse);
        btntryagain=dialog.findViewById(R.id.btntryagain);
        containerlinearlayout=findViewById(R.id.containerlinearlayout);

        Displayplantinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionReference plantRef = firestore.collection("Greenhouse");
                //Query query = plantRef.whereEqualTo("plantName", output);
                firestore.collection("Greenhouse")
                        .whereEqualTo("plantName",output)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            String firestorePlantmain;



                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        firestorePlantmain = document.get("plantMaintenance").toString();
                                        firestorePlantname=document.get("plantName").toString();
                                        List<Map<String, Object>> documentList = (List<Map<String, Object>>)
                                                document.get("plantImages");
                                        List<DataModel> users = document.toObject(PlantImageModel.class).plantImages;
                                        Log.d(TAG, "Value of myField: " + firestorePlantmain);
                                        Log.d(TAG, "PlantName: " + firestorePlantname);
                                        DataModel dataModel = users.get(0);
                                        downloadURL = dataModel.getDownloadURL();
                                        Plantname=dataModel.getPlantName();

                                        Log.d(TAG, "onComplete: downloadURL :"+downloadURL);
                                        if(firestorePlantmain==null){
                                            tvdisplayPlantmain.setText("Working on it , will be updating shortly");
                                        }else{
                                        tvdisplayPlantmain.setText(firestorePlantmain);
                                        if("Rust".equals(output)||"Powdery".equals(output)) {
                                            btnAddtogreenhouse.setVisibility(View.GONE);
                                            Toast.makeText(GoogleLensActivity.this, "Continue enjoying our services", Toast.LENGTH_SHORT).show();
                                        }else if("Unknown".equals(output)){
                                            btntryagain.setVisibility(View.VISIBLE);
                                        }else{
                                            btnAddtogreenhouse.setVisibility(View.VISIBLE);

                                        }
                                        }
                                }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }


                            }
                        });

                //dialog.dismiss();
                //Toast.makeText(GoogleLensActivity.this,"Here is your plant details",Toast.LENGTH_SHORT).show();

            }
        });
        btnAddtogreenhouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finishActivity();
            }
        });
        btntryagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                checkPermission(android.Manifest.permission.CAMERA, MY_CAMERA_REQUEST);

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }


    private void finishActivity(){
        Intent intent = getIntent();
        intent.putExtra("downloadURL",downloadURL );
        intent.putExtra("plantName",firestorePlantname);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setOnClickListener() {
        ivOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(android.Manifest.permission.CAMERA, MY_CAMERA_REQUEST);
            }
        });

        ivOpenSearchResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dialogg.show();
                    BlobBitmap();


                } catch (IOException e) {
                    Log.d(TAG, "onClick: IOException" + e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        ivOpenDiagnose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    OpenDiagnose();

                } catch (IOException e) {
                    Log.d(TAG, "onClick: IOException" + e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void BlobBitmap() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //OkHttpClient client = new OkHttpClient();

                OkHttpClient client = new OkHttpClient.Builder()
                        .callTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS) // Set the socket read timeout to 30 seconds
                        .writeTimeout(30, TimeUnit.SECONDS) // Set the socket write timeout to 30 seconds
                        .build();// Set the socket timeout to 30 seconds
                String url = "http://34.82.181.33:1234/upload-image";
                //Log.d(TAG, "run: finalFile :"+finalFile);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", "image.jpg", RequestBody.create(MEDIA_TYPE_PNG, finalFile))
                        .build();
                Request request = new Request.Builder().url(url).post(requestBody).build();
                try {
                    response = client.newCall(request).execute();
                    String res = response.body().string();
                    JSONObject jsonObject = new JSONObject(res);
                    //Log.d(TAG, "run: jsonObject :"+jsonObject);
                    output = jsonObject.getString("predicted_class_name");



                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "run: IOException :" + e.getMessage());

                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                if (!response.isSuccessful()) {
                    Log.d(TAG, "No: response :" + response.code());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GoogleLensActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            showBottomDialog();
                            Log.d(TAG, "run: response :" + response.code());
                            tvplantInfo.setText(output);
                            dialogg.dismiss();
                            Toast.makeText(GoogleLensActivity.this, "uploaded successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }
    public void OpenDiagnose() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //OkHttpClient client = new OkHttpClient();
                OkHttpClient client = new OkHttpClient.Builder()
                        .callTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS) // Set the socket read timeout to 30 seconds
                        .writeTimeout(30, TimeUnit.SECONDS) // Set the socket write timeout to 30 seconds
                        .build();// Set the socket timeout to 30 seconds
                String url = "http://34.16.146.28:1234/upload-image";
                //Log.d(TAG, "run: finalFile :"+finalFile);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", "image.jpg", RequestBody.create(MEDIA_TYPE_PNG, finalFile))
                        .build();
                Request request = new Request.Builder().url(url).post(requestBody).build();
                try {
                    response = client.newCall(request).execute();
                    String res = response.body().string();
                    JSONObject jsonObject = new JSONObject(res);
                    //Log.d(TAG, "run: jsonObject :"+jsonObject);
                    output = jsonObject.getString("predicted_class_name");

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "run: IOException :" + e.getMessage());

                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                if (!response.isSuccessful()) {
                    Log.d(TAG, "No: response :" + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GoogleLensActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showBottomDialog();
                            Log.d(TAG, "run: response :" + response.code());
                            tvplantInfo.setText(output);
                            Toast.makeText(GoogleLensActivity.this, "uploaded successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(GoogleLensActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(GoogleLensActivity.this, new String[]{permission}, requestCode);
        } else {
            Toast.makeText(GoogleLensActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activityResultLauncher.launch(cameraIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == MY_CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(GoogleLensActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(cameraIntent);
            } else {
                Toast.makeText(GoogleLensActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new
            ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Bundle bundle = result.getData().getExtras();
                bitmap = (Bitmap) bundle.get("data");

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                // bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                //bArray = bos.toByteArray();
                ivSetImage.setImageBitmap(bitmap);
                ivupload.setVisibility(View.GONE);
                String path = saveToInternalStorage(bitmap);
                Log.d(TAG, "onActivityResult: path" + path);
                finalFile = new File(path, "blob_bro.jpg");
            }
        }
    });

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, "blob_bro.jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            bArray = bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

}

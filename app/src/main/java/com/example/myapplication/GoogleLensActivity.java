package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ml.TfliteModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionCloudImageLabelerOptions;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

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
    private ImageView ivSetImage;
    private ImageButton ivOpenCamera;
    private ImageButton ivOpenSearchResult;
    private TextView tvResult;

    private Bitmap bitmap;
    private byte[] bArray;

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
    private static final MediaType MEDIA_TYPE_PLAINTEXT = MediaType.parse("text/plain; charset=utf-8");


    private File finalFile;
    Response response = null;
    private String output;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        widgetInitialization();
        setOnClickListener();
    }

    private void widgetInitialization() {
        ivSetImage = findViewById(R.id.ivSetImage);
        ivOpenCamera = findViewById(R.id.ivOpenCamera);
        ivOpenSearchResult = findViewById(R.id.ivGetResult);
        tvResult = findViewById(R.id.tvResult);
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
                // predictImage();
                //detectImageFromFirebase();
                try {
                    // uploadImage(finalFile, "Alavera_Haseeb_bro");
                    haseebBlobBitmap();
                } catch (IOException e) {
                    Log.d(TAG, "onClick: IOException" + e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void uploadImage(File image, String imageName) throws IOException {
        Log.d(TAG, "uploadImage: image :" + image);
        String url = "http://34.125.40.202:1234/upload-image";
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", imageName, RequestBody.create(MEDIA_TYPE_PNG, image)).build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            Toast.makeText(this, "Error" + response, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "uploaded successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public void haseebBlobBitmap() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = "http://34.125.40.202:1234/upload-image";
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
                            Log.d(TAG, "run: response :" + response.code());
                            tvResult.setText("Predicted Class Label: " + output);
                            Toast.makeText(GoogleLensActivity.this, "uploaded successfully" + output, Toast.LENGTH_SHORT).show();
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

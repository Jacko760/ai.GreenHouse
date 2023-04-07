package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionCloudImageLabelerOptions;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions;

import androidx.annotation.NonNull;

import java.util.List;

public class GoogleLensActivity extends AppCompatActivity {

    private static final int PIC_ID = 123;
    private ImageView ivSetImage;
    private ImageButton ivOpenCamera;
    private ImageButton ivOpenSearchResult;
    private TextView tvResult;

    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        widgetInitialization();
        setOnClickListener();
    }

    private void widgetInitialization(){
        ivSetImage = findViewById(R.id.ivSetImage);
        ivOpenCamera = findViewById(R.id.ivOpenCamera);
        ivOpenSearchResult = findViewById(R.id.ivGetResult);
        tvResult = findViewById(R.id.tvResult);
    }

    private void setOnClickListener(){
        ivOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(cameraIntent);
            }
        });

        ivOpenSearchResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectImageFromFirebase();
            }
        });
    }


    private void detectImageFromFirebase(){
        if (bitmap != null) {
            FirebaseVisionOnDeviceImageLabelerOptions options = new FirebaseVisionOnDeviceImageLabelerOptions.Builder()
                    .build();
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVisionImageLabeler detector = FirebaseVision.getInstance().getOnDeviceImageLabeler(options);
            detector.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                @Override
                public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                    extractLabel(labels);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GoogleLensActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void extractLabel(List<FirebaseVisionImageLabel> labels) {
        for (FirebaseVisionImageLabel label : labels) {
            tvResult.append(label.getText() + "\n");
            tvResult.append(label.getConfidence() + "\n\n");
            tvResult.append(label.getEntityId() + "\n\n");
        }
    }


    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new
            ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData()!=null) {
                        Bundle bundle = result.getData().getExtras();
                        bitmap = (Bitmap) bundle.get("data");
                        ivSetImage.setImageBitmap(bitmap);
                    }
                }
            });

}

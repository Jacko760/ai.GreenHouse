package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionCloudImageLabelerOptions;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions;

import java.io.File;
import java.util.List;

public class SampleMLActivity extends BaseActivity implements View.OnClickListener{

    private Bitmap mBitmap;
    private ImageView mImageView;
    private TextView mTextView;

    public static final int RC_STORAGE_PERMS1 = 101;
    public static final int RC_STORAGE_PERMS2 = 102;
    public static final int RC_SELECT_PICTURE = 103;
    public static final int RC_TAKE_PICTURE = 104;
    public static final String ACTION_BAR_TITLE = "action_bar_title";
    public File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_mlactivity);

        mTextView = findViewById(R.id.text_view);
        mImageView = findViewById(R.id.image_view);
        findViewById(R.id.btn_device).setOnClickListener(this);
        findViewById(R.id.btn_cloud).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mTextView.setText(null);
        switch (view.getId()) {
            case R.id.btn_device:
                if (mBitmap != null) {
                    FirebaseVisionOnDeviceImageLabelerOptions options = new FirebaseVisionOnDeviceImageLabelerOptions.Builder()
                            .setConfidenceThreshold(0.7f)
                            .build();
                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mBitmap);
                    FirebaseVisionImageLabeler detector = FirebaseVision.getInstance().getOnDeviceImageLabeler(options);
                    detector.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                            extractLabel(labels);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mTextView.setText(e.getMessage());
                        }
                    });
                }
                break;
            case R.id.btn_cloud:
                if (mBitmap != null) {
                    MyHelper.showDialog(this);
                    FirebaseVisionCloudImageLabelerOptions options = new FirebaseVisionCloudImageLabelerOptions.Builder().setConfidenceThreshold(0.7f).build();
                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mBitmap);
                    FirebaseVisionImageLabeler detector = FirebaseVision.getInstance().getCloudImageLabeler(options);
                    detector.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                            MyHelper.dismissDialog();
                            extractLabel(labels);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                           MyHelper.dismissDialog();
                            mTextView.setText(e.getMessage());
                        }
                    });
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RC_STORAGE_PERMS1:
                case RC_STORAGE_PERMS2:
                    checkStoragePermission(requestCode);
                    break;
                case RC_SELECT_PICTURE:
                    Uri dataUri = data.getData();
                    String path = MyHelper.getPath(this, dataUri);
                    if (path == null) {
                        mBitmap = MyHelper.resizeImage(imageFile, this, dataUri, mImageView);
                    } else {
                        mBitmap = MyHelper.resizeImage(imageFile, path, mImageView);
                    }
                    if (mBitmap != null) {
                        mTextView.setText(null);
                        mImageView.setImageBitmap(mBitmap);
                    }
                    break;
                case RC_TAKE_PICTURE:
                    mBitmap = MyHelper.resizeImage(imageFile, imageFile.getPath(), mImageView);
                    if (mBitmap != null) {
                        mTextView.setText(null);
                        mImageView.setImageBitmap(mBitmap);
                    }
                    break;
            }
        }
    }

    private void extractLabel(List<FirebaseVisionImageLabel> labels) {
        for (FirebaseVisionImageLabel label : labels) {
            mTextView.append(label.getText() + "\n");
            mTextView.append(label.getConfidence() + "\n\n");
        }
    }


    public void checkStoragePermission(int requestCode) {
        switch (requestCode) {
            case RC_STORAGE_PERMS1:
                int hasWriteExternalStoragePermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasWriteExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {
                    selectPicture();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                }
                break;
            case RC_STORAGE_PERMS2:
                String[] PERMISSIONS = { android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA };
                if (!hasPermissions(this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS, requestCode);
                } else {
                    openCamera();
                }
                break;
        }
    }


}
package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class BottomNavActivity extends AppCompatActivity {

    private static final String TAG = "BottomNavActivity";
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    ScannerFragment scannerFragment = new ScannerFragment();

    GreenHouseFragment greenHouseFragment =new GreenHouseFragment();
    ProfileFragment profileFragment =new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        FloatingActionButton floatingActionButton;
        floatingActionButton=findViewById(R.id.fab);
        bottomNavigationView =findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BottomNavActivity.this, GoogleLensActivity.class);
                activityResultLauncher.launch(intent);
            }
        });
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bvhome:

                       getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();

                        return true;
                    case R.id.bvScanner:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,scannerFragment).commit();

                        return true;
                    case R.id.bvGreenhouse:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,greenHouseFragment).commit();

                        return true;
                    case R.id.bvProfile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,profileFragment).commit();

                        return true;
                }
                return false;
            }

        });

    }




    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new
            ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                String data = result.getData().getStringExtra("downloadURL");
                String plantName = result.getData().getStringExtra("plantName");
                Log.d(TAG, "onActivityResult: data" + data);
                Bundle args = new Bundle();
                args.putString("downloadURl", data);
                args.putString("plantName", plantName);
                greenHouseFragment.setArguments(args);

                getSupportFragmentManager().beginTransaction().replace(R.id.container, greenHouseFragment)
                        .commit();
                getSupportFragmentManager().beginTransaction().detach(greenHouseFragment).commit();
                bottomNavigationView.setSelectedItemId(R.id.bvGreenhouse);
                getSupportFragmentManager().beginTransaction().attach(greenHouseFragment).commit();
            }

        }
    });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.bottom_nav_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemid= item.getItemId();
        if(itemid==R.id.bvhome){
            Toast.makeText(this, "you are in home page", Toast.LENGTH_SHORT).show();
        } else if (itemid==R.id.bvScanner) {
            Toast.makeText(this, "You are in scanner page", Toast.LENGTH_SHORT).show();
        } else if (itemid==R.id.bvGreenhouse) {
            Toast.makeText(this, "You are in Greenhousepage", Toast.LENGTH_SHORT).show();
        }else if (itemid==R.id.bvProfile){
            Toast.makeText(this, "you are in profile page", Toast.LENGTH_SHORT).show();
        } else if (itemid==R.id.placeholder) {
            Toast.makeText(this, "You are in placeholder", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "hh", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
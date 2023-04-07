package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class BottomNavActivity extends AppCompatActivity {
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
        bottomNavigationView =findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
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
package com.example.myapplication;

import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.content.ContextCompat.registerReceiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String CHANNEL_ID = "Plant pal";
    private static final int NOTIFICATION_ID = 100;

    FirebaseAuth auth;
    FirebaseUser user;
    AlertDialog.Builder alertDialog;
    ViewPager viewPager;
    List<Integer> imageList;// = new ArrayList<>();
    ViewpagerAdapter adapter;
    ImageView imageView;
    BottomNavigationView bottomNavigationView;
    ImageSlider imageSlider;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ImageButton imageButton = view.findViewById(R.id.ibLogout);
        imageView = view.findViewById(R.id.ivexploregarden);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
//        textView.setText(" Welcome , " + "" +
//                user.getEmail()  );
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.house, null);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap largeIcon = bitmapDrawable.getBitmap();

        NotificationManager nm = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(getContext())
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.house)
                    .setContentText("New Message")
                    .setSubText("Welcome to PlantPal")
                    .setChannelId(CHANNEL_ID)
                    .build();
            nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "Phytech", NotificationManager.IMPORTANCE_HIGH));
        } else {
            notification = new Notification.Builder(getContext())
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.house)
                    .setContentText(" Welcome to Plantpal")
                    .setSubText("New Message")
                    .build();
        }
        // i can notify wherver i want to or when its needed
        //   nm.notify(NOTIFICATION_ID,notification);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GreenHouseFragment greenHouseFragment = new GreenHouseFragment();

                // Get the FragmentManager
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                // Start a new FragmentTransaction
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment with the GreenHouseFragment
                fragmentTransaction.replace(R.id.container, greenHouseFragment);

                // Add the transaction to the back stack (optional)
                fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();

                // Get the BottomNavigationView from the activity
                BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);

                // Set the selected item in the BottomNavigationView
                bottomNavigationView.setSelectedItemId(R.id.bvGreenhouse);

            }
        });
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
        imageSlider=view.findViewById(R.id.image_slider);
        ArrayList<SlideModel> slidemodels = new ArrayList<>();
        slidemodels.add(new SlideModel(R.drawable.ad1, ScaleTypes.FIT));
        slidemodels.add(new SlideModel(R.drawable.ad2, ScaleTypes.FIT));
        slidemodels.add(new SlideModel(R.drawable.ad3,ScaleTypes.FIT));
        slidemodels.add(new SlideModel(R.drawable.ad4,ScaleTypes.FIT));
        slidemodels.add(new SlideModel(R.drawable.ad5,ScaleTypes.FIT));
        imageSlider.setImageList(slidemodels,ScaleTypes.FIT);
        imageSlider.setSlideAnimation(AnimationTypes.ZOOM_OUT);
        imageSlider.setSlideAnimation(AnimationTypes.FLIP_VERTICAL);
        imageSlider.startSliding(1000); // with new period


        Log.d(TAG, "onCreateView: "+imageList);
        viewPager = view.findViewById(R.id.viewpager);
        if(imageList==null) {
            imageList = new ArrayList<>();
            if(imageList.size()==0) {
                imageList.add(R.drawable.get_started);
                imageList.add(R.drawable.identify);
                imageList.add(R.drawable.scan);
                imageList.add(R.drawable.planthealth);
                imageList.add(R.drawable.greenhouse);
                imageList.add(R.drawable.scarecrow);
                adapter = new ViewpagerAdapter(imageList);
                viewPager.setAdapter(adapter);
            }else{
                Toast.makeText(getContext(), "already added", Toast.LENGTH_SHORT).show();
            }
        }else{
            adapter = new ViewpagerAdapter(imageList);
            viewPager.setAdapter(adapter);
        }
        return view;

    }


}
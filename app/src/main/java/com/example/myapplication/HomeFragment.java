package com.example.myapplication;
import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment {

private static final  String CHANNEL_ID="Plant pal";
    private static final  int NOTIFICATION_ID=100;

    FirebaseAuth auth;
    FirebaseUser user ;
    AlertDialog.Builder alertDialog;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        ImageButton imageButton =view.findViewById(R.id.ibLogout);
        TextView textView=view.findViewById(R.id.tvHome);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        textView.setText(" Welcome , " + "" +
                user.getEmail()  );
        Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.house,null);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap largeIcon = bitmapDrawable.getBitmap();

       NotificationManager nm = (NotificationManager)getContext().getSystemService(Context.NOTIFICATION_SERVICE);
       Notification notification;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
             notification = new Notification.Builder(getContext())
                    .setLargeIcon(largeIcon)
                            .setSmallIcon(R.drawable.house)
                                    .setContentText("New Message")
                                            .setSubText("Welcome to PlantPal")
                    .setChannelId(CHANNEL_ID)
                                                    .build();
             nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID,"Plantpal",NotificationManager.IMPORTANCE_HIGH));
        }else{
            notification = new Notification.Builder(getContext())
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.house)
                    .setContentText("New Message")
                    .setSubText("Hey Welcome to Plantpal")
                    .build();
        }
        // i can notify wherver i want to or when its needed
        nm.notify(NOTIFICATION_ID,notification);


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

        return view;

    }


}
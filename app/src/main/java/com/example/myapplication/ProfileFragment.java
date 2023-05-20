package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


public class ProfileFragment extends Fragment {
EditText editText;
TextView textView;
    String userInput;
private SharedPreferences sharedPreferences;

    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        editText=view.findViewById(R.id.etgardenName);
        textView=view.findViewById(R.id.display_text);
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);


        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               userInput = editText.getText().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_input", userInput);
                editor.apply();


            }
        });
        return view;
    }
    public void onResume() {
        super.onResume();

        String savedText = sharedPreferences.getString("user_input",userInput);
        editText.setText(savedText);
    }

    @Override
    public void onPause() {
        super.onPause();
        userInput = editText.getText().toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_input", userInput);
        editor.apply();
        editText.setText(userInput);
    }



}
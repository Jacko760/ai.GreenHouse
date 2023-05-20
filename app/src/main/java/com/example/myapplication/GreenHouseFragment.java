package com.example.myapplication;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;


public class GreenHouseFragment extends Fragment {

    private static final String TAG = "GreenHouseFragment";
    private static final int REQUEST_GOOGLE_LENS = 1;


    String data;
    String name;
    String plantName, imageUrl;
    TextView tvaddplant;
    AppCompatButton btngetstarted;
    private static final int REQUEST_GOOGLE_LENSS = 1;

    RecyclerView recyclerView;
    ArrayList<DataModel> arrayList = new ArrayList<>() ;
    GreenhouseAdapter greenhouseAdapter;
    Bundle b;

    public GreenHouseFragment() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_green_house, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        tvaddplant=view.findViewById(R.id.tvgetstarted);
        btngetstarted=view.findViewById(R.id.btngetstarted);

        /*btngetstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GoogleLensActivity.class);
                startActivity(intent);
            }
        });*/







        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
       /* SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("greenhouse", Context.MODE_PRIVATE);
        int dataCount = sharedPreferences.getInt("dataCount", 0);
        if (dataCount > 0) {
            for (int i = 0; i < dataCount; i++) {
                String plantName = sharedPreferences.getString("plantName" + i, "");
                String imageUrl = sharedPreferences.getString("imageUrl" + i, "");
                if (!plantName.isEmpty() && !imageUrl.isEmpty()) {
                    arrayList.add(new DataModel(plantName, imageUrl));
                }
            }


        }*/

        // Set up the adapter and update the RecyclerView
         b = getArguments();
            Bundle b = getArguments();
            try {
                data = b.getString("downloadURl");
                name = b.getString("plantName");
            } catch (Exception e) {
                e.printStackTrace();
            }


            Log.d(TAG, "onCreateView: arrayList : " + arrayList.size());
            if(arrayList.size()==0 && b!=null) {

                arrayList.add(new DataModel(name, data));
                greenhouseAdapter = new GreenhouseAdapter(requireActivity(), arrayList);
                recyclerView.setAdapter(greenhouseAdapter);
            }else{
                try {

                    updateData(b);
                    GreenhouseAdapter greenhouseAdapter = new GreenhouseAdapter(requireActivity(), arrayList);
                    recyclerView.setAdapter(greenhouseAdapter);
                }catch (Exception e){
                    e.printStackTrace();
                }
                }
        setRecyclerViewData(plantName, imageUrl);
        return view;
    }

    private void updateData(Bundle bundle) {
        String newData = bundle.getString("downloadURl");
        String newName = bundle.getString("plantName");
        if (newData != null && newName != null) {

            boolean isDuplicate = false;
            for (DataModel dataModel : arrayList) {
                if (dataModel.getPlantName().equals(newName) && dataModel.getDownloadURL().equals(newData)) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate) {

                arrayList.add(new DataModel(newName, newData));
            }
            GreenhouseAdapter greenhouseAdapter = new GreenhouseAdapter(requireActivity(), arrayList);
            recyclerView.setAdapter(greenhouseAdapter);

        }
    }

    private void setRecyclerViewData(String plantName, String imageUrl) {
        if (plantName != null && imageUrl != null && !plantName.isEmpty() && !imageUrl.isEmpty()) {
            // Check if the data already exists in the list
            boolean isDuplicate = false;
            for (DataModel dataModel : arrayList) {
                if (dataModel.getPlantName() != null && dataModel.getDownloadURL() != null &&
                        dataModel.getPlantName().equals(plantName) && dataModel.getDownloadURL().equals(imageUrl)) {
                    isDuplicate = true;
                    break;
                }
            }

            if (!isDuplicate) {
                // Add the new data to the list
                arrayList.add(new DataModel(plantName, imageUrl));

                // Save the new data to SharedPreferences
                saveDataToSharedPreferences();

                // Notify the adapter about the new data
                if (greenhouseAdapter != null) {
                    greenhouseAdapter.notifyDataSetChanged();
                } else {
                    greenhouseAdapter = new GreenhouseAdapter(requireActivity(), arrayList);
                    recyclerView.setAdapter(greenhouseAdapter);
                }
            }
        }
    }

    private void saveDataToSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("greenhouse", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("dataCount", arrayList.size());
        for (int i = 0; i < arrayList.size(); i++) {
            editor.putString("plantName" + i, arrayList.get(i).getPlantName());
            editor.putString("imageUrl" + i, arrayList.get(i).getDownloadURL());
        }
        editor.apply();
    }



}



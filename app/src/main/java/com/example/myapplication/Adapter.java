package com.example.myapplication;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private ArrayList<model> dataModalArrayList;
    private Context context;

    public Adapter(ArrayList<model> dataModalArrayList, Context context) {
        this.dataModalArrayList = dataModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        model modal = dataModalArrayList.get(position);
        holder.titleTV.setText(modal.getTitle());
        holder.snippetTV.setText(modal.getLink());
        holder.descTV.setText(modal.getSnippet());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(modal.getLink()));
                context.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {

        return dataModalArrayList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTV, descTV, snippetTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.Title);
            descTV = itemView.findViewById(R.id.Description);
            snippetTV = itemView.findViewById(R.id.Snippet);

        }
    }
}

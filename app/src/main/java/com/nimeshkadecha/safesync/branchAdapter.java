package com.nimeshkadecha.safesync;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class branchAdapter extends RecyclerView.Adapter<branchAdapter.MyViewHolder> {

    ArrayList name;
    ArrayList Email;
    Context context;
    Activity activity;
    String N_Email;

    public branchAdapter(Context context, ArrayList Email, ArrayList name, Activity activity,String  N_Email) {
        this.context = context;
        this.Email = Email;
        this.name = name;
        this.N_Email = N_Email;
        this.activity = activity;
    }


    @NonNull
    @Override
    public branchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.design, parent, false);
        return new branchAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull branchAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.NGO_name.setText(String.valueOf(name.get(position)));
        holder.NGO_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("BranchName", String.valueOf(name.get(position)));
                bundle.putString("BranchEmail", String.valueOf(Email.get(position)));
                bundle.putString("NgoEmail", N_Email);

                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.BranchDetail,bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView NGO_name;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            NGO_name = itemView.findViewById(R.id.Name);

        }
    }
}

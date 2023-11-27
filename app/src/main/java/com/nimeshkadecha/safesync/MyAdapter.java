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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.nimeshkadecha.safesync.ui.details.NgoDetail;
import com.nimeshkadecha.safesync.ui.home.HomeFragment;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    ArrayList name;
    ArrayList Email;
    Context context;

    Activity activity;

    public MyAdapter(Context context,ArrayList Email, ArrayList name, Activity activity) {
        this.context = context;
        this.Email = Email;
        this.name = name;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.design, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.NGO_name.setText(String.valueOf(name.get(position)));
        holder.NGO_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("NgoName", String.valueOf(name.get(position)));
                bundle.putString("NgoEmail", String.valueOf(Email.get(position)));

                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.NgoDetail,bundle);

            }
        });
    }

    @Override
    public int getItemCount() {
        if (name.size() != 0) {
            return name.size();
        } else {
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView NGO_name;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            NGO_name = itemView.findViewById(R.id.Name);

        }
    }



}

package com.nimeshkadecha.safesync;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.jar.Attributes;

public class Equpment2Adapter extends RecyclerView.Adapter<Equpment2Adapter.MyViewHolder>{

    ArrayList Eq_Name, EQ_Quantity;

    Context context;


    public Equpment2Adapter(Context context, ArrayList EQ_Name, ArrayList EQ_Quantity ) {
        this.context = context;
        this.Eq_Name = EQ_Name;
        this.EQ_Quantity = EQ_Quantity;

    }

    @NonNull
    @Override
    public Equpment2Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.design2, parent, false);
        return new Equpment2Adapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Equpment2Adapter.MyViewHolder holder, int position) {
        holder.Name.setText(Eq_Name.get(position).toString());

        holder.Quentity.setText(EQ_Quantity.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return Eq_Name.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Name, Quentity,Delete,Edit;
        ImageView AddBtn, RemoveBtn;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            Name = itemView.findViewById(R.id.EqName);

            Quentity = itemView.findViewById(R.id.textquantity);

            RemoveBtn = itemView.findViewById(R.id.removeQuantityBtn);
            AddBtn = itemView.findViewById(R.id.addQuantityBtn);
            Delete = itemView.findViewById(R.id.Delete);
            Edit = itemView.findViewById(R.id.Edit);

            Delete.setVisibility(View.GONE);
            Edit.setVisibility(View.GONE);
            AddBtn.setVisibility(View.GONE);
            RemoveBtn.setVisibility(View.GONE);

        }
    }
}

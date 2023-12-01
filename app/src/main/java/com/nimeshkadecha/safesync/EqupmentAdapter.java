package com.nimeshkadecha.safesync;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class EqupmentAdapter extends RecyclerView.Adapter<EqupmentAdapter.MyViewHolder> {

    ArrayList Eq_Name, EQ_Quantity;

    Context context;

    TextInputEditText EQ_Name_EDT,EQ_Quantity_EDT;


    public EqupmentAdapter(Context context, ArrayList EQ_Name, ArrayList EQ_Quantity , TextInputEditText EQ_Name_EDT,TextInputEditText EQ_Quentity_EDT) {
        this.context = context;
        this.Eq_Name = EQ_Name;
        this.EQ_Quantity = EQ_Quantity;
        this.EQ_Name_EDT = EQ_Name_EDT;
        this.EQ_Quantity_EDT = EQ_Quentity_EDT;

    }

    @NonNull
    @Override
    public EqupmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.design2, parent, false);
        return new EqupmentAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EqupmentAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.Name.setText(Eq_Name.get(position).toString());

        holder.Quentity.setText(EQ_Quantity.get(position).toString());

        holder.Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(EQ_Name_EDT.getText().toString().equals("")&& EQ_Quantity_EDT.getText().toString().equals("")){

                    EQ_Name_EDT.setText(Eq_Name.get(position).toString());
                    EQ_Quantity_EDT.setText(EQ_Quantity.get(position).toString());
                    Toast.makeText(context, "Data filled", Toast.LENGTH_SHORT).show();

                    Eq_Name.remove(position);
                    EQ_Quantity.remove(position);
                    EqupmentAdapter.this.notifyDataSetChanged();
                }else{
                    Toast.makeText(context, "There are Some data in filed clear that data to add new", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int TempQuantity = Integer.parseInt(String.valueOf(EQ_Quantity.get(position)));
                TempQuantity += 1;
                EQ_Quantity.remove(position);

                EQ_Quantity.add(position, TempQuantity);

                EqupmentAdapter.this.notifyDataSetChanged();
            }
        });

        holder.RemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(EQ_Quantity.get(position)).equals("0")) {
                    Toast.makeText(context, "Long press to remove", Toast.LENGTH_SHORT).show();
                } else {

                    int TempQuantity = Integer.parseInt(String.valueOf(EQ_Quantity.get(position)));
                    TempQuantity -= 1;
                    EQ_Quantity.remove(position);

                    EQ_Quantity.add(position, TempQuantity);

                    EqupmentAdapter.this.notifyDataSetChanged();

                }
            }
        });

        holder.RemoveBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (String.valueOf(EQ_Quantity.get(position)).equals("0")) {
                    Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                    EQ_Quantity.remove(position);
                    Eq_Name.remove(position);
                    EqupmentAdapter.this.notifyDataSetChanged();
                    return true;
                }else {
                    return false;
                }

            }
        });

        holder.Delete.setOnClickListener(v-> {
            Toast.makeText(context, "Long press on delete button to remove", Toast.LENGTH_SHORT).show();
        });

        holder.Delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                    Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                    Eq_Name.remove(position);
                    EQ_Quantity.remove(position);
                    EqupmentAdapter.this.notifyDataSetChanged();
                    return true;
            }
        });

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
        }
    }
}

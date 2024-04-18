package com.example.fixeridetest.ADAPTER;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixeridetest.ADMIN.MechanicsRegistered;
import com.example.fixeridetest.Model.MechanicsRetrival;
import com.example.fixeridetest.R;

import java.util.List;

public class MechanicsAdapter extends RecyclerView.Adapter<MechanicsAdapter.MechanicViewHolder> {

    private List<MechanicsRetrival> mechanicsList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public MechanicsAdapter(Context context, List<MechanicsRetrival> mechanicsList) {
        this.context = context;
        this.mechanicsList = mechanicsList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MechanicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mechanic_retriving, parent, false);
        return new MechanicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MechanicViewHolder holder, int position) {
        MechanicsRetrival mechanic = mechanicsList.get(position);

        holder.nameTextView.setText("Name: " + mechanic.getName());
        holder.emailTextView.setText("Email: " + mechanic.getEmail());
        holder.phoneTextView.setText("Phone: " + mechanic.getPhone());
        holder.carTextView.setText("Car: " + mechanic.getCar());
        holder.registrationNumberTextView.setText("Registration Number: " + mechanic.getRegistrationNumber());

        // Handle the "Delete Account" button click
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition(); // Get the position of the item clicked
                if (listener != null && clickedPosition != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(clickedPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mechanicsList.size();
    }

    public void setData(List<MechanicsRetrival> mechanicsList) {
        this.mechanicsList = mechanicsList;
        notifyDataSetChanged();
    }

    public class MechanicViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView, phoneTextView, carTextView, registrationNumberTextView;
        Button deleteButton;

        public MechanicViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
            carTextView = itemView.findViewById(R.id.carTextView);
            registrationNumberTextView = itemView.findViewById(R.id.registrationNumberTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}

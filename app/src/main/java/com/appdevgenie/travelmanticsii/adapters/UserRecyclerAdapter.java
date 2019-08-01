package com.appdevgenie.travelmanticsii.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appdevgenie.travelmanticsii.R;
import com.appdevgenie.travelmanticsii.models.HolidayDeal;

import java.util.ArrayList;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.UserViewHolder> {

    private Context context;
    private ArrayList<HolidayDeal> holidayDeals;

    @NonNull
    @Override
    public UserRecyclerAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_destination, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRecyclerAdapter.UserViewHolder holder, int position) {

        HolidayDeal holidayDeal = holidayDeals.get(holder.getAdapterPosition());
        holder.tvCity.setText(holidayDeal.getCity());
        holder.tvCost.setText(holidayDeal.getCost());
        holder.tvResort.setText(holidayDeal.getResort());
    }

    @Override
    public int getItemCount() {
        return holidayDeals.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        private TextView tvCity;
        private TextView tvResort;
        private TextView tvCost;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCity = itemView.findViewById(R.id.tvItemDestinationCity);
            tvResort = itemView.findViewById(R.id.tvItemDestinationResort);
            tvCost = itemView.findViewById(R.id.tvItemDestinationCost);
        }
    }
}

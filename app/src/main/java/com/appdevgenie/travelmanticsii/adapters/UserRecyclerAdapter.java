package com.appdevgenie.travelmanticsii.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appdevgenie.travelmanticsii.R;
import com.appdevgenie.travelmanticsii.activities.AdminActivity;
import com.appdevgenie.travelmanticsii.models.HolidayDeal;
import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.ArrayList;

import static com.appdevgenie.travelmanticsii.utils.Constants.INTENT_EXTRA_DEAL;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.UserViewHolder> {

    private Context context;
    private ArrayList<HolidayDeal> holidayDeals;

    public UserRecyclerAdapter(Context context, ArrayList<HolidayDeal> holidayDeals) {
        this.context = context;
        this.holidayDeals = holidayDeals;
    }

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

        //DecimalFormat format = new DecimalFormat("###,###,##0.00");
        NumberFormat format = NumberFormat.getCurrencyInstance();
        //String currency = format.format(Double.parseDouble(holidayDeal.getCost()));
        holder.tvCost.setText(format.format(holidayDeal.getCost()));

        holder.tvResort.setText(holidayDeal.getResort());
        holder.ratingBar.setRating(Float.valueOf(holidayDeal.getRating()));

        Glide
                .with(context)
                .load(holidayDeal.getImageUrl())
                .fitCenter()
                .placeholder(R.drawable.ic_hotel_black_24dp)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if(holidayDeals == null){
            return 0;
        }else {
            return holidayDeals.size();
        }
    }

    /*public void setAdapterData(ArrayList<HolidayDeal> holidayDealArrayList){
        holidayDeals = holidayDealArrayList;
        notifyDataSetChanged();
    }*/

    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvCity;
        private TextView tvResort;
        private TextView tvCost;
        private ImageView imageView;
        private RatingBar ratingBar;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCity = itemView.findViewById(R.id.tvItemDestinationCity);
            tvResort = itemView.findViewById(R.id.tvItemDestinationResort);
            tvCost = itemView.findViewById(R.id.tvItemDestinationCost);
            imageView = itemView.findViewById(R.id.ivItemDestination);
            ratingBar = itemView.findViewById(R.id.ratingBar);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            HolidayDeal holidayDeal = holidayDeals.get(getAdapterPosition());
            //Toast.makeText(context, String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, AdminActivity.class);
            intent.putExtra(INTENT_EXTRA_DEAL, holidayDeal);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}

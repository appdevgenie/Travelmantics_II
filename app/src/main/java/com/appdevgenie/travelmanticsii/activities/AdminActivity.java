package com.appdevgenie.travelmanticsii.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.appdevgenie.travelmanticsii.R;

public class AdminActivity extends AppCompatActivity {

    private TextView tvCity;
    private TextView tvCost;
    private TextView tvReort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setupVariables();
    }

    private void setupVariables() {

        tvCity = findViewById(R.id.tvItemDestinationCity);
        tvCost = findViewById(R.id.tvItemDestinationCost);
        tvReort = findViewById(R.id.tvItemDestinationResort);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int selectedItem = item.getItemId();
        switch (selectedItem){
            case R.id.save_menu:
                saveHolidayDeal();
                return true;

            case R.id.delete_menu:
                deleteHolidayDeal();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveHolidayDeal() {


    }

    private void deleteHolidayDeal() {
    }
}

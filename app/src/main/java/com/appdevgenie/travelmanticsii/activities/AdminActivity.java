package com.appdevgenie.travelmanticsii.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appdevgenie.travelmanticsii.R;
import com.appdevgenie.travelmanticsii.models.HolidayDeal;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminActivity extends AppCompatActivity {

    private EditText etCity;
    private EditText etCost;
    private EditText etResort;

    private HolidayDeal holidayDeal;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("holidayDeal");

        setupVariables();
    }

    private void setupVariables() {

        etCity = findViewById(R.id.etItemDestinationCity);
        etCost = findViewById(R.id.etItemDestinationCost);
        etResort = findViewById(R.id.etItemDestinationResort);
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
                Toast.makeText(getApplicationContext(), "Holiday deal saved!", Toast.LENGTH_LONG).show();
                finish();
                return true;

            case R.id.delete_menu:
                deleteHolidayDeal();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveHolidayDeal() {

        String city = etCity.getText().toString();
        String cost = etCost.getText().toString();
        String resort = etResort.getText().toString();

        holidayDeal = new HolidayDeal(city, resort, cost);

        if(holidayDeal.getId() == null){
            databaseReference.push().setValue(holidayDeal);
        }else{
            databaseReference.child(holidayDeal.getId()).setValue(holidayDeal);
        }

    }

    private void deleteHolidayDeal() {
    }
}

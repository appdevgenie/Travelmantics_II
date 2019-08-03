package com.appdevgenie.travelmanticsii.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.appdevgenie.travelmanticsii.R;
import com.appdevgenie.travelmanticsii.models.HolidayDeal;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.appdevgenie.travelmanticsii.utils.Constants.DB_CHILD_DEAL;
import static com.appdevgenie.travelmanticsii.utils.Constants.DB_CHILD_DEAL_PICS;
import static com.appdevgenie.travelmanticsii.utils.Constants.INTENT_EXTRA_DEAL;
import static com.appdevgenie.travelmanticsii.utils.Constants.RC_IMAGE_SELECT;

public class AdminActivity extends AppCompatActivity {

    public static final String TAG = "adminActivity";

    private EditText etCity;
    private EditText etCost;
    private EditText etResort;
    private Button bSelectImage;
    private ImageView imageView;
    private FloatingActionButton floatingActionButton;

    private HolidayDeal holidayDeal = new HolidayDeal();

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child(DB_CHILD_DEAL);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child(DB_CHILD_DEAL_PICS);

        setupVariables();

        Intent intent = getIntent();
        if (intent != null) {
            HolidayDeal holidayDeal = intent.getParcelableExtra(INTENT_EXTRA_DEAL);
            if (holidayDeal != null) {
                etCity.setText(holidayDeal.getCity());
                etResort.setText(holidayDeal.getResort());
                etCost.setText(holidayDeal.getCost());


                this.holidayDeal = holidayDeal;
                loadImage(holidayDeal.getImageUrl());
            }
        }
    }

    private void setupVariables() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etCity = findViewById(R.id.etItemDestinationCity);
        etCost = findViewById(R.id.etItemDestinationCost);
        etResort = findViewById(R.id.etItemDestinationResort);

        bSelectImage = findViewById(R.id.bSelectImage);
        bSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //loadImage("");

                Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imageIntent.setType("image/jpeg");
                imageIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(imageIntent.createChooser(imageIntent, "Select image"), RC_IMAGE_SELECT);
            }
        });

        imageView = findViewById(R.id.ivResort);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveHolidayDeal();
                Toast.makeText(getApplicationContext(), "Holiday deal saved!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
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
        switch (selectedItem) {
            /*case R.id.save_menu:
                saveHolidayDeal();
                Toast.makeText(getApplicationContext(), "Holiday deal saved!", Toast.LENGTH_LONG).show();
                finish();
                return true;*/

            case R.id.delete_menu:
                deleteHolidayDeal();

                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_IMAGE_SELECT && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            final StorageReference storageImageRef = storageReference.child(imageUri.getLastPathSegment());
            storageImageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            String imagePath = uri.getPath();
                            holidayDeal.setImageUrl(url);
                            holidayDeal.setImageName(imagePath);

                            loadImage(url);

                            Toast.makeText(getApplicationContext(), "Image saved!", Toast.LENGTH_LONG).show();
                        }
                    });


                }
            });
        }
    }

    private void loadImage(String url) {
        //Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();

        Log.d(TAG, "loadImage: " + url);

        /*Picasso
                .get()
                .load(R.drawable.ic_hotel_black_24dp)
                .resize(50, 50)
                .centerCrop()
                .into(imageView);*/

        Glide
                .with(getApplicationContext())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_hotel_black_24dp)
                .into(imageView);
    }

    private void saveHolidayDeal() {

        String city = etCity.getText().toString();
        String cost = etCost.getText().toString();
        String resort = etResort.getText().toString();

        holidayDeal.setCity(city);
        holidayDeal.setCost(cost);
        holidayDeal.setResort(resort);

        //holidayDeal = new HolidayDeal(city, resort, cost);

        if (holidayDeal.getId() == null) {
            //new holiday deal
            databaseReference.push().setValue(holidayDeal);
        } else {
            //edit holiday deal
            databaseReference.child(holidayDeal.getId()).setValue(holidayDeal);
        }

    }

    private void deleteHolidayDeal() {

        if (holidayDeal != null) {
            databaseReference.child(holidayDeal.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Holiday deal deleted!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}

package com.appdevgenie.travelmanticsii.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.appdevgenie.travelmanticsii.R;
import com.appdevgenie.travelmanticsii.models.HolidayDeal;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static com.appdevgenie.travelmanticsii.activities.UserActivity.isAdmin;
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
    private RatingBar ratingBar;
    private ProgressBar progressBar;

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

                //DecimalFormat format = new DecimalFormat("###,###,##0.00");
                NumberFormat format = NumberFormat.getCurrencyInstance();
                //String currency = format.format(Double.parseDouble(holidayDeal.getCost()));
                etCost.setText(format.format(holidayDeal.getCost()));

                ratingBar.setRating(Float.valueOf(holidayDeal.getRating()));

                this.holidayDeal = holidayDeal;
                loadImage(holidayDeal.getImageUrl());
            }
        }
    }

    private void setupVariables() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        etCity = findViewById(R.id.etItemDestinationCity);
        etCost = findViewById(R.id.etItemDestinationCost);
        etResort = findViewById(R.id.etItemDestinationResort);

        progressBar = findViewById(R.id.progressBar);

        bSelectImage = findViewById(R.id.bSelectImage);
        bSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imageIntent.setType("image/jpeg");
                imageIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(imageIntent.createChooser(imageIntent, "Select image"), RC_IMAGE_SELECT);
            }
        });

        imageView = findViewById(R.id.ivResort);
        ratingBar = findViewById(R.id.ratingBar);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        if (isAdmin) {
            floatingActionButton.show();
            enableViews(true);
            bSelectImage.setVisibility(View.VISIBLE);
        } else {
            floatingActionButton.hide();
            enableViews(false);
            bSelectImage.setVisibility(View.INVISIBLE);
        }
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveHolidayDeal();

            }
        });
    }

    private void enableViews(boolean isEnabled) {
        etCity.setEnabled(isEnabled);
        etCost.setEnabled(isEnabled);
        etResort.setEnabled(isEnabled);
        ratingBar.setIsIndicator(!isEnabled);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.admin_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.delete_menu);
        if (isAdmin) {
            menuItem.setVisible(true);
        } else {
            menuItem.setVisible(false);
        }
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

                //finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_IMAGE_SELECT && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            progressBar.setVisibility(View.VISIBLE);

            final StorageReference storageImageRef = storageReference.child(imageUri.getLastPathSegment());
            storageImageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    storageImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            String imagePath = taskSnapshot.getMetadata().getPath();
                            holidayDeal.setImageUrl(url);
                            holidayDeal.setImageName(imagePath);

                            loadImage(url);

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Image saved!", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Error loading image!", Toast.LENGTH_LONG).show();
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
                .fitCenter()
                .placeholder(R.drawable.ic_hotel_black_24dp)
                .into(imageView);
    }

    private void saveHolidayDeal() {

        String city = etCity.getText().toString();
        float cost = Float.parseFloat(etCost.getText().toString());
        String resort = etResort.getText().toString();
        String rating = String.valueOf(ratingBar.getRating());

        if(!TextUtils.isEmpty(city) && !TextUtils.isEmpty(resort)) {

            holidayDeal.setCity(city);
            holidayDeal.setCost(cost);
            holidayDeal.setResort(resort);
            holidayDeal.setRating(rating);

            //holidayDeal = new HolidayDeal(city, resort, cost);

            if (holidayDeal.getId() == null) {
                //new holiday deal
                databaseReference.push().setValue(holidayDeal);
            } else {
                //edit holiday deal
                databaseReference.child(holidayDeal.getId()).setValue(holidayDeal);
            }

            Toast.makeText(getApplicationContext(), "Holiday deal saved!", Toast.LENGTH_LONG).show();
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }else{
            Toast.makeText(getApplicationContext(), "Enter all fields!", Toast.LENGTH_LONG).show();
        }

    }

    private void deleteHolidayDeal() {

        /*if (holidayDeal.getId() == null) {
            Toast.makeText(getApplicationContext(), "First create Holiday deal!", Toast.LENGTH_LONG).show();
            return;
        }*/
        if(holidayDeal.getId() != null) {
            databaseReference.child(holidayDeal.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Holiday deal deleted!", Toast.LENGTH_LONG).show();
                }
            });
            if(holidayDeal.getImageName() != null && !holidayDeal.getImageName().isEmpty()) {
                StorageReference imageRef = firebaseStorage.getReference().child(holidayDeal.getImageName());
                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Holiday deal image deleted!", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Unable to delete Holiday deal image!", Toast.LENGTH_LONG).show();
                    }
                });
            }

            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }else{
            Toast.makeText(getApplicationContext(), "First add Holiday deal!", Toast.LENGTH_LONG).show();
        }
    }
}

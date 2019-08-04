package com.appdevgenie.travelmanticsii.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appdevgenie.travelmanticsii.R;
import com.appdevgenie.travelmanticsii.adapters.UserRecyclerAdapter;
import com.appdevgenie.travelmanticsii.models.HolidayDeal;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.appdevgenie.travelmanticsii.utils.CheckNetworkConnection.isNetworkConnected;
import static com.appdevgenie.travelmanticsii.utils.Constants.DB_CHILD_ADMIN;
import static com.appdevgenie.travelmanticsii.utils.Constants.DB_CHILD_DEAL;
import static com.appdevgenie.travelmanticsii.utils.Constants.RC_SIGN_IN;

public class UserActivity extends AppCompatActivity implements ChildEventListener {

    public static final String TAG = "userActivity";

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<HolidayDeal> holidayDeals = new ArrayList<>();
    private ArrayList<String> keys = new ArrayList<>();
    private Context context;
    private UserRecyclerAdapter userRecyclerAdapter;
    private FloatingActionButton floatingActionButton;
    public static boolean isAdmin;
    private ImageView ivNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        setupVariables();
        setupFirebaseDatabase();
        setupFirebaseAuth();
    }

    private void setupFirebaseDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child(DB_CHILD_DEAL);
        databaseReference.addChildEventListener(this);
    }

    private void setupFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    signIn();
                } else {
                    checkIfAdmin(firebaseAuth.getUid());
                    //Toast.makeText(getApplicationContext(), "Welcome back " + firebaseAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void checkIfAdmin(String uid) {

        isAdmin = false;

        DatabaseReference databaseAdminReference = firebaseDatabase.getReference().child(DB_CHILD_ADMIN).child(uid);
        databaseAdminReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                isAdmin = true;
                floatingActionButton.show();
                //Toast.makeText(context, String.valueOf(isAdmin), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TextView tvUser = findViewById(R.id.tvUser);
        if(firebaseAuth.getCurrentUser() != null) {
             tvUser.setText(firebaseAuth.getCurrentUser().getDisplayName());
        }

    }

    private void signIn() {

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN && resultCode == RESULT_OK){
            Toast.makeText(context, "signed in", Toast.LENGTH_SHORT).show();
            setupFirebaseDatabase();
            populateRecyclerView();
        }
    }

    private void setupVariables() {
        context = getApplicationContext();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ivNetwork = findViewById(R.id.ivNetwork);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.hide();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int selectedItem = item.getItemId();

        switch (selectedItem) {
            case R.id.sign_out_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                                attachListener();
                            }
                        });
                detachListener();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isNetworkConnected(context)) {
            attachListener();
            ivNetwork.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);
        }else {
            Toast.makeText(context, "No network connection!", Toast.LENGTH_SHORT).show();
            ivNetwork.setImageResource(R.drawable.ic_signal_wifi_off_black_24dp);
        }

        populateRecyclerView();
    }

    private void populateRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rvResortList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        userRecyclerAdapter = new UserRecyclerAdapter(context, holidayDeals);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(userRecyclerAdapter);
    }

    public void attachListener() {
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public void detachListener() {
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        Log.d(TAG, "onChildAdded: ");

        HolidayDeal holidayDeal = dataSnapshot.getValue(HolidayDeal.class);
        if (holidayDeal != null) {
            String key = dataSnapshot.getKey();
            holidayDeal.setId(key);
            keys.add(key);
        }
        holidayDeals.add(holidayDeal);
        userRecyclerAdapter.notifyItemInserted(holidayDeals.size() -1);
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        Log.d(TAG, "onChildChanged: ");

        HolidayDeal holidayDeal = dataSnapshot.getValue(HolidayDeal.class);
        String key = dataSnapshot.getKey();
        int index = keys.indexOf(key);
        holidayDeals.set(index, holidayDeal);

        userRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        Log.d(TAG, "onChildRemoved: ");

        String key = dataSnapshot.getKey();
        int index = keys.indexOf(key);
        if (index != -1) {
            holidayDeals.remove(index);
            keys.remove(index);
            userRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}

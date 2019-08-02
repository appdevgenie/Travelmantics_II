package com.appdevgenie.travelmanticsii.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appdevgenie.travelmanticsii.R;
import com.appdevgenie.travelmanticsii.adapters.UserRecyclerAdapter;
import com.appdevgenie.travelmanticsii.models.HolidayDeal;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.appdevgenie.travelmanticsii.utils.Constants.DB_CHILD_DEAL;
import static com.appdevgenie.travelmanticsii.utils.Constants.RC_SIGN_IN;

public class UserActivity extends AppCompatActivity implements ChildEventListener {

    public static final String TAG = "userActivity";

    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<HolidayDeal> holidayDeals = new ArrayList<>();
    private Context context;
    private UserRecyclerAdapter userRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        setupFirebaseDatabase();
        setupFirebaseAuth();
        setupVariables();
    }

    private void setupFirebaseDatabase() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child(DB_CHILD_DEAL);
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
                    //Toast.makeText(getApplicationContext(), "Welcome back " + firebaseAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void signIn() {

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        this.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    private void setupVariables() {

        context = getApplicationContext();

        recyclerView = findViewById(R.id.rvResortList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        userRecyclerAdapter = new UserRecyclerAdapter(context, holidayDeals);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(userRecyclerAdapter);

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
            case R.id.add_menu:
                Intent intent = new Intent(UserActivity.this, AdminActivity.class);
                startActivity(intent);
                return true;

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
        attachListener();
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
            holidayDeal.setId(dataSnapshot.getKey());
        }
        holidayDeals.add(holidayDeal);
        userRecyclerAdapter.notifyItemInserted(holidayDeals.size() -1);
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        Log.d(TAG, "onChildChanged: ");

        //userRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        Log.d(TAG, "onChildRemoved: ");

        //TODO: fix recyclerview not updating after deletion

        //HolidayDeal holidayDeal = dataSnapshot.getValue(HolidayDeal.class);
        //holidayDeals.remove(holidayDeal);
        //userRecyclerAdapter.notifyDataSetChanged();
        //userRecyclerAdapter.setAdapterData(holidayDeals);

        /*String posKey = databaseReference.child(holidayDeal.getId()).getKey();
        holidayDeals.remove(holidayDeal.getId());*/
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}

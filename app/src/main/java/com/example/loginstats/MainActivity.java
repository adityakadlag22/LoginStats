package com.example.loginstats;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import Fragments.ui.Orders;
import Fragments.ui.Profile;
import authentication.LoginActivity;
import utilities.AddbusinessLogo;
import utilities.GetBusinessInfo;
import utilities.GetUserInfo;
import utilities.GetUserWebsite;
import utilities.NoInternet;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    private static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener listener;
    private static final String TAG = "MainActivity";
    private FirebaseUser currentUser;
    private Toolbar toolbar;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("WaitingList");
    private NavigationView navigationView;
    private String orderspen;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;

    //OwnerInfo
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkConnection();
        InitialiseFields();
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    Log.d(TAG, "onAuthStateChanged: No User");
                    SendUserToLoginActivity();
                } else if (currentUser.isEmailVerified()) {

                    userID = currentUser.getUid();
                } else {
                    SendUserToLoginActivity();
                }
            }
        };

        CheckBusinessUserIsRegistered();
        Log.d(TAG, "onCreate: UserId " + userID);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this
                , drawerLayout
                , toolbar
                , R.string.openNavDrawer
                , R.string.closeNavDrawer);



        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(nav);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new Orders()).commit();



    }

    private void InitialiseFields() {
        navigationView = findViewById(R.id.navigationMain);
        toolbar = findViewById(R.id.toolbarMain);
        drawerLayout = findViewById(R.id.drawerLayoutMain);
        bottomNavigationView=findViewById(R.id.bottomNavMain);
    }


    private void CheckBusinessUserIsRegistered() {
        myRef = FirebaseDatabase.getInstance().getReference("WaitingList");
        myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    userID = user.getUid();
                    boolean info = snapshot.child(userID).hasChild("DataAdded");
                    if (info) {
                        String stage = Objects.requireNonNull(snapshot.child(userID).child("DataAdded").getValue()).toString();
                        switch (stage) {
                            case "personal":
                                SendUserToLogoInfoActivity();
                                break;
                            case "logo":
                                SendUserToWebSiteActivity();
                                break;
                            case "verified":
                                Log.d(TAG, "onDataChange: Verified");
                                //T0d0
                                //Retrieve Orders
                                break;
                            case "all":GetOrdersNow();
                                       break;
                            case "website":
                                SendUserToBusinessInfoActivity();
                                break;
                        }
                    } else {
                        SendUserToUserInfoActivity();
                        Log.d(TAG, "onDataChange: User Sent to Info Activity");
                    }
                } else {
                    SendUserToLoginActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: Error " + error.toString());
            }
        });
    }

    private void GetOrdersNow() {
        userID = mAuth.getCurrentUser().getUid();
        if (userID != null) {
            myRef = FirebaseDatabase.getInstance().getReference("WaitingList").child(userID);
            myRef.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        orderspen = Objects.requireNonNull(snapshot.child("orderspen").getValue()).toString();
                        BadgeDrawable badgeDrawable=bottomNavigationView.getOrCreateBadge(R.id.orders);
                        int t=Integer.parseInt(orderspen);
                        if (t==0)
                        {
                         badgeDrawable.setVisible(false);
                        }
                        if (t!=0)
                        {
                            badgeDrawable.setNumber(t);
                            badgeDrawable.setVisible(true);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            SendUserToLoginActivity();
        }
        mAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.removeAuthStateListener(listener);
    }

    public void checkConnection() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (null == activeNetwork) {
            SendUserToNoNetworkActivity();
        }

    }
    private void SendUserToLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
    private void SendUserToWebSiteActivity() {
        Intent intent = new Intent(getApplicationContext(), GetUserWebsite.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void SendUserToNoNetworkActivity() {
        Intent intent = new Intent(getApplicationContext(), NoInternet.class);
        intent.putExtra("from", "main");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void SendUserToUserInfoActivity() {
        Intent intent = new Intent(getApplicationContext(), GetUserInfo.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void SendUserToBusinessInfoActivity() {
        Log.d(TAG, "SendUserToBusinessInfoActivity: Sending user to business activity");
        Intent intent = new Intent(getApplicationContext(), GetBusinessInfo.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    BottomNavigationView.OnNavigationItemSelectedListener nav=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selected_frag=null;
            switch (item.getItemId())
            {
             case R.id.orders:
                selected_frag=new Orders();
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                     Objects.requireNonNull(getSupportActionBar()).setTitle("Orders");
                 }else
                 {
                     getSupportActionBar().setTitle("Orders");
                 }

                 break;

             case R.id.profile:
                selected_frag=new Profile();
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                     Objects.requireNonNull(getSupportActionBar()).setTitle("Profile");
                 }else{getSupportActionBar().setTitle("Profile");}
                 item.setIcon(R.drawable.ic_profile);
                break;
            }
            assert selected_frag != null;
            MainActivity.this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container,selected_frag).commit();
            return true;
        }
    };

    private void SendUserToLogoInfoActivity() {
        Log.d(TAG, "SendUserToBusinessInfoActivity: Sending user to business activity");
        Intent intent = new Intent(getApplicationContext(), AddbusinessLogo.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signOut_Nav:
            {
                Toast.makeText(MainActivity.this, "dr", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                break;
            }

            case R.id.home_Nav:
                Toast.makeText(MainActivity.this, "we", Toast.LENGTH_SHORT).show();
                break;
            case R.id.changeProfile:
                break;
            case R.id.changeLogo:
                 Toast.makeText(MainActivity.this, "cilcked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.revenue:
                break;
            case R.id.historyOrder:
                Toast.makeText(MainActivity.this, "rw", Toast.LENGTH_SHORT).show();
                break;
        }
        DrawerLayout drawer;
        drawer = findViewById(R.id.drawerLayoutMain);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
package Fragments.ui;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.loginstats.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class Profile extends Fragment {
    private TextView userName, emailAddress, OrdersPending, todayRevenue, totalRevenue, totalOrders,
            phoneNumber, websiteText, serviceText, businessNametxt;
    private String name, email, orderspen, todayreve, totalreve, totalorde, phno, website, service, businessName;
    private ImageView profileImage, profileSettings;
    private View v;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener listener;
    private FirebaseUser currentUser;
    private static final String TAG = "Profile";
    private String userID;
    private String datareaded;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("WaitingList");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_profile, container, false);
        InitialiseFields();
        mAuth = FirebaseAuth.getInstance();
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    Log.d(TAG, "onAuthStateChanged: No User");
                } else if (currentUser.isEmailVerified()) {

                    userID = currentUser.getUid();
                } else {
                    Log.d(TAG, "onAuthStateChanged: No User");
                    userID = currentUser.getUid();
                }
            }
        };

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Profile Image");
            }
        });
        profileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Settings");
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RetrieveDataFromDatabase();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        RetrieveDataFromDatabase();
        return v;
    }


    private void SetDataToTextView() {
        userName.setText(name);
        emailAddress.setText(email);
        OrdersPending.setText(orderspen);
        totalOrders.setText(totalorde);
        todayRevenue.setText(todayreve);
        totalRevenue.setText(totalreve);
        phoneNumber.setText(phno);
        websiteText.setText(website);
        serviceText.setText(service);
        businessNametxt.setText(businessName);
    }

    private void RetrieveDataFromDatabase() {
        userID = mAuth.getCurrentUser().getUid();
        if (userID != null) {
            myRef = FirebaseDatabase.getInstance().getReference("WaitingList").child(userID);
            myRef.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                        email = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                        orderspen = Objects.requireNonNull(snapshot.child("orderspen").getValue()).toString();
                        totalorde = Objects.requireNonNull(snapshot.child("totalorders").getValue()).toString();
                        todayreve = Objects.requireNonNull(snapshot.child("todayreve").getValue()).toString();
                        totalreve = Objects.requireNonNull(snapshot.child("totalreve").getValue()).toString();
                        phno = Objects.requireNonNull(snapshot.child("phonenumber").getValue()).toString();
                        service = Objects.requireNonNull(snapshot.child("service").getValue()).toString();
                        website = Objects.requireNonNull(snapshot.child("website").getValue()).toString();
                        businessName = Objects.requireNonNull(snapshot.child("businessName").getValue()).toString();
                        SetDataToTextView();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void InitialiseFields() {
        userName = v.findViewById(R.id.Username);
        emailAddress = v.findViewById(R.id.tv_address);
        swipeRefreshLayout = v.findViewById(R.id.refreshLayoutProfile);
        OrdersPending = v.findViewById(R.id.OrdersPendingNum);
        todayRevenue = v.findViewById(R.id.todayRevenue);
        totalRevenue = v.findViewById(R.id.totalRevenue);
        totalOrders = v.findViewById(R.id.totalOrders);
        phoneNumber = v.findViewById(R.id.phoneNumber);
        websiteText = v.findViewById(R.id.website);
        profileImage = v.findViewById(R.id.profileImage);
        profileSettings = v.findViewById(R.id.profileSettings);
        serviceText = v.findViewById(R.id.service);
        businessNametxt = v.findViewById(R.id.businessNameTXT);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(listener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(listener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAuth.removeAuthStateListener(listener);

    }
}
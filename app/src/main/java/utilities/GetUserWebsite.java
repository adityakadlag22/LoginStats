package utilities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.loginstats.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GetUserWebsite extends AppCompatActivity {
    private EditText websiteEdit;
    private String website;
    private static final String TAG = "GetUserWebsite";
    private View view;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("WaitingList");
    private TextView skip;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_website);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        InitialiseFields();
        final ProgressButton progressButton = new ProgressButton(getApplicationContext(), view);
        if (user != null) {
            uid = user.getUid();
        }
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    myRef.child(uid).child("website").setValue("No Website")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        myRef.child(uid).child("DataAdded").setValue("website");
                                        progressButton.ButtonFinished();
                                        SendUserToBusinessInfoActivity();
                                    }
                                }
                            });
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressButton.ButtonActivated();
                website = websiteEdit.getText().toString();
                if (website.isEmpty()) {
                    progressButton.ButtonError();
                } else {
                    myRef.child(uid).child("website").setValue(website)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        myRef.child(uid).child("DataAdded").setValue("website");
                                        progressButton.ButtonFinished();
                                        SendUserToBusinessInfoActivity();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void InitialiseFields() {
        view = findViewById(R.id.progressButtonwebsite);
        websiteEdit = findViewById(R.id.website_edit);
        skip=findViewById(R.id.skipWebsite);
    }
    private void SendUserToBusinessInfoActivity() {
        Log.d(TAG, "SendUserToBusinessInfoActivity: Sending user to business activity");
        Intent intent = new Intent(getApplicationContext(), GetBusinessInfo.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
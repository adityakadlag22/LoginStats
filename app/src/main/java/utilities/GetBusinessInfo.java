package utilities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.example.loginstats.MainActivity;
import com.example.loginstats.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class GetBusinessInfo extends AppCompatActivity {

    private EditText businessName, businessDescription, businessAddress;
    private String name_business, desc_business, address_business, service_cat;
    private Spinner service_category;
    private String userID;
    private static final String TAG = "GetBusinessInfo";
    private Button submit;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("WaitingList");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getbusinessinfo);
        InitialiseFields();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentuser = mAuth.getCurrentUser();

        if (currentuser != null) {
            userID = currentuser.getUid();
            Log.d(TAG, "onCreate: UID taken" + userID);
        }


        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.serviceCategory, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        service_category.setAdapter(adapter2);
        service_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                service_cat = null;
                service_cat = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: gender" + service_cat);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        Places.initialize(getApplicationContext(),"AIzaSyCXbF55YLnNJa6hkllNG04YkaEwRLWiPa4");
//        businessAddress.setFocusable(false);
//        businessAddress.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                List<Place.Field> fieldList= Arrays.asList(Place.Field.ADDRESS
//                ,Place.Field.LAT_LNG,Place.Field.NAME);
//
//                Intent intent=new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY
//                        ,fieldList).build(getApplicationContext());
//
//                startActivityForResult(intent,100);
//            }
//        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_business = businessName.getText().toString();
                desc_business = businessDescription.getText().toString();
                address_business = businessAddress.getText().toString();
                Log.d(TAG, "onClick: name,desc,address " + name_business + desc_business + address_business);

                if (name_business != null && !name_business.isEmpty() && desc_business != null
                        && !desc_business.isEmpty() && address_business != null && !address_business.isEmpty()) {
                    if (service_cat != null && !service_cat.isEmpty()) {
                        myRef.child(userID).child("businessName").setValue(name_business);
                        myRef.child(userID).child("businessDescription").setValue(desc_business);
                        myRef.child(userID).child("address").setValue(address_business);
                        myRef.child(userID).child("service").setValue(service_cat);
                        myRef.child(userID).child("revenue").setValue("0");
                        myRef.child(userID).child("orderspen").setValue("0");
                        myRef.child(userID).child("totalorders").setValue("0");
                        myRef.child(userID).child("todayreve").setValue("0");
                        myRef.child(userID).child("totalreve").setValue("0");
                        myRef.child(userID).child("DataAdded").setValue("all")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        SendUserToMainActivity();
                                    }
                                });
                    }
                } else {

                    final Snackbar snackbar = Snackbar.make(submit, "Fill All Fields Correctly", Snackbar.LENGTH_LONG);
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                    snackbar.setBackgroundTint(getResources().getColor(R.color.black));
                    snackbar.setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                }
            }
        });
    }

    private void InitialiseFields() {
        businessName = findViewById(R.id.businessName_edit);
        businessDescription = findViewById(R.id.businessDescriptionEdit);
        businessAddress = findViewById(R.id.Address_businessEdit);
        service_category = findViewById(R.id.serviceCategory);
        submit = findViewById(R.id.submitBusinessInfo);
    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==100 && resultCode==RESULT_OK)
//        {
//            assert data != null;
//            Place place=Autocomplete.getPlaceFromIntent(data);
//          businessAddress.setText(place.getAddress());
//        }
//        else
//        {
//            assert data != null;
//            Status status=Autocomplete.getStatusFromIntent(data);
//            Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "onActivityResult2: tag"+status.getStatusMessage());
//        }
//    }
    private void SendUserToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
package utilities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Spinner;

import com.example.loginstats.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Calendar;
import java.util.GregorianCalendar;



public class GetUserInfo extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText fullName_edit, phone_edit, birthDayEdit, addharNumber_edit;
    private TextInputLayout birthDayEditLayout;
    private String fullName, phoneNumber, dateBirth, addharnumber;
    private String gender_choice = "Male";
    private Button submit;
    private Spinner genderSpinner;
    private int age;
    private ImageView datePicker;

    private static final String TAG = "GetUserInfo";
    private FirebaseAuth mAuth;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("WaitingList");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_infopersonal);
        InitialiseFields();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentuser = mAuth.getCurrentUser();

        if (currentuser != null) {
            String userid = currentuser.getUid();
            Log.d(TAG, "onCreate: UID taken" + userid);
        }


        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender_choice = null;
                gender_choice = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: gender" + gender_choice);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fullName = fullName_edit.getText().toString();
                phoneNumber = phone_edit.getText().toString();
                addharnumber = addharNumber_edit.getText().toString();
                FirebaseUser user = mAuth.getCurrentUser();
                String userid = user.getUid();

                if (fullName != null && !fullName.isEmpty() && phoneNumber != null && addharnumber != null && gender_choice != null && dateBirth != null) {
                    if (addharnumber.length() == 12) {
                        if (phoneNumber.length() == 10) {
                            if (age < 18) {
                                final Snackbar snackbar = Snackbar.make(submit, "Your are UnderAge, Better luck Next Time", Snackbar.LENGTH_LONG);
                                snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                                snackbar.setBackgroundTint(getResources().getColor(R.color.black));
                                snackbar.setAction("ok", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        snackbar.dismiss();

                                    }
                                });
                                snackbar.show();
                                //WORK NOT DONE
                            } else {
                                myRef.child(userid).child("phonenumber").setValue(phoneNumber);
                                myRef.child(userid).child("DataAdded").setValue("personal");
                                myRef.child(userid).child("name").setValue(fullName);
                                myRef.child(userid).child("gender").setValue(gender_choice);
                                myRef.child(userid).child("birthDate").setValue(dateBirth);
                                myRef.child(userid).child("Age").setValue(age);
                                myRef.child(userid).child("Aadhar").setValue(addharnumber).addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {SendUserToLogoInfoActivity();}
                                            }
                                        }
                                );
                            }
                        }
                        else{

                            final Snackbar snackbar = Snackbar.make(submit, "Enter Valid Phone Number", Snackbar.LENGTH_LONG);
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
                    else{

                        final Snackbar snackbar = Snackbar.make(submit, "Enter Valid Addhar Number", Snackbar.LENGTH_LONG);
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


    private void SendUserToLogoInfoActivity() {
        Intent intent = new Intent(getApplicationContext(), AddbusinessLogo.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    public void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void InitialiseFields() {
        fullName_edit = findViewById(R.id.fullName_edit);
        phone_edit = findViewById(R.id.phoneNumberEdit);
        submit = findViewById(R.id.submitPersonalInfo);
        birthDayEdit = findViewById(R.id.birthDateEdit);
        birthDayEditLayout = findViewById(R.id.birthDateEditLayout);
        datePicker = findViewById(R.id.datebirthpick);
        genderSpinner = findViewById(R.id.gender_Spinner);
        addharNumber_edit = findViewById(R.id.Addhar_edit);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        datePicker = null;
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dateBirth = month + "/" + dayOfMonth + "/" + year;
        birthDayEdit.setText(dateBirth);
        birthDayEdit.setVisibility(View.VISIBLE);
        birthDayEditLayout.setVisibility(View.VISIBLE);
        age = getAge(year, month, dayOfMonth);
    }

    public int getAge(int year, int month, int day) {

        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, noofyears;

        y = cal.get(Calendar.YEAR);// current year ,
        m = cal.get(Calendar.MONTH);// current month
        d = cal.get(Calendar.DAY_OF_MONTH);// current day
        cal.set(year, month, day);// here ur date
        noofyears = y - cal.get(Calendar.YEAR);


        if ((m < cal.get(Calendar.MONTH)) || ((m == cal.get(Calendar.MONTH)) && (d < cal.get(Calendar.DAY_OF_MONTH)))) {
            --noofyears;
        }

        if (noofyears != 0) {
            Log.d(TAG, "getAge: fqw");
        } else {
            Log.d(TAG, "getAge: sdf");
        }
        if (noofyears < 0)
            throw new IllegalArgumentException("age < 0");
        return noofyears;
    }
}
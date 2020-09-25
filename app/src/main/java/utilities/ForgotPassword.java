package utilities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.loginstats.MainActivity;
import com.example.loginstats.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPassword extends AppCompatActivity {

    private EditText emailEdit;
    private FirebaseAuth mAuth;
    private String email;
    private Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mAuth = FirebaseAuth.getInstance();
        emailEdit = findViewById(R.id.email_forgotActivity);
        reset = findViewById(R.id.reset_Password);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String message = bundle.getString("email");
        emailEdit.setText(message);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEdit.getText().toString();
                if (email.isEmpty()) {
                    final Snackbar snackbar = Snackbar.make(reset, "Fill the Field", Snackbar.LENGTH_LONG);
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                    snackbar.setBackgroundTint(getResources().getColor(R.color.black));
                    snackbar.show();
                } else {
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        final Snackbar snackbar = Snackbar.make(reset, "Check Your Email, Click on Link to Reset Password.", Snackbar.LENGTH_LONG);
                                        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                                        snackbar.setBackgroundTint(getResources().getColor(R.color.black));
                                        snackbar.show();
                                        SendUserToMainActivity();
                                    } else {
                                        final Snackbar snackbar = Snackbar.make(reset, "Something went Wrong", Snackbar.LENGTH_LONG);
                                        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                                        snackbar.setBackgroundTint(getResources().getColor(R.color.black));
                                        snackbar.show();
                                    }
                                }
                            });
                }
            }
        });
    }
    private void SendUserToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
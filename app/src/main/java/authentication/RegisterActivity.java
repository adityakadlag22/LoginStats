package authentication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import com.airbnb.lottie.LottieAnimationView;

import com.example.loginstats.MainActivity;
import com.example.loginstats.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


import utilities.NoInternet;

public class RegisterActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 121;
    private LottieAnimationView demo;
    private EditText emailedit, passwordedit;
    private ImageButton google, facebook;
    private Button emailsubmit;
    private String email, password;
    private RelativeLayout haveAccount;
    private static FirebaseAuth mAuth;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("WaitingList");

    private static final String TAG = "RegisterActivity";
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        InitialiseFields();
        checkConnection();
        mAuth = FirebaseAuth.getInstance();
        Button signup = findViewById(R.id.SignUp_btn);
        demo = findViewById(R.id.animation_view);

        // Configure Google Sign In/SignUp
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //Facebook SignUp
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailedit.getText().toString();
                password = passwordedit.getText().toString();
                if (email.isEmpty() || password.isEmpty()) {
                    final Snackbar snackbar = Snackbar.make(emailsubmit, "Fill the Fields", Snackbar.LENGTH_LONG);
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                    snackbar.setBackgroundTint(getResources().getColor(R.color.black));
                    snackbar.setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                } else {
                    demo.setVisibility(View.VISIBLE);
                    demo.playAnimation();
                    mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String userid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                        myRef.child(userid)
                                                .child("email").setValue(email).addOnCompleteListener(
                                                new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            final Snackbar snackbar = Snackbar.make(emailsubmit, "Check Your Email, Click on Link to Verify.", Snackbar.LENGTH_INDEFINITE);
                                                            snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                                                            snackbar.setBackgroundTint(getResources().getColor(R.color.black));
                                                            snackbar.setAction("ok", new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    snackbar.dismiss();
                                                                    demo.setVisibility(View.GONE);
                                                                    SendUserToLoginActivity();
                                                                }
                                                            });
                                                            snackbar.show();
                                                            mAuth.getCurrentUser().sendEmailVerification()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful())
                                                                        {
                                                                            Log.d(TAG, "onComplete: email Sent");
                                                                        }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }
                                        );
                                    } else {
                                        final Snackbar snackbar = Snackbar.make(emailsubmit, "Invalid email or Password", Snackbar.LENGTH_LONG);
                                        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                                        snackbar.setBackgroundTint(getResources().getColor(R.color.black));
                                        snackbar.setAction("ok", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                emailedit.setText(" ");
                                                passwordedit.setText(" ");
                                                demo.setVisibility(View.GONE);
                                                snackbar.dismiss();
                                            }
                                        });
                                        snackbar.show();
                                    }
                                }
                            });
                }
            }
        });

        demo.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                demo.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });
    }

    private void SendUserToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("emailLogin", emailedit.getText().toString());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }


    private void InitialiseFields() {
        emailedit = findViewById(R.id.email_edit_rege);
        passwordedit = findViewById(R.id.password_edit_rege);
        emailsubmit = findViewById(R.id.SignUp_btn);
        facebook = findViewById(R.id.fbicon);
        google = findViewById(R.id.Google_rege);
        haveAccount = findViewById(R.id.HaveAc);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void SendUserToLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void checkConnection() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (null == activeNetwork) {
            SendUserToNoNetworkActivity();
        }

    }

    private void SendUserToNoNetworkActivity() {
        Intent intent = new Intent(getApplicationContext(), NoInternet.class);
        intent.putExtra("from", "register");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            myRef.child(user.getUid()).child("email").setValue(user.getEmail())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: Email added");
                                            SendUserToMainActivity();
                                        }
                                    });

                        } else {
                            final Snackbar snackbar = Snackbar.make(google, "Failed", Snackbar.LENGTH_LONG);
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
}
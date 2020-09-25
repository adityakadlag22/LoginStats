package authentication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


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
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.loginstats.MainActivity;
import com.example.loginstats.R;
import com.example.loginstats.databinding.ActivityLoginBinding;
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

import utilities.ForgotPassword;
import utilities.NoInternet;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEdit, passwordEdit;
    private static final int RC_SIGN_IN = 121;
    private ImageButton google, facebook;
    private Button emailSubmit;
    private String email, password;
    private LottieAnimationView loginanim;
    private RelativeLayout NotAccount;
    private static FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private TextView forgotPassword;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("WaitingList");
    ActivityLoginBinding loginBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding= DataBindingUtil.setContentView(this,R.layout.activity_login);
        loginBinding.setLifecycleOwner(this);
        loginanim = findViewById(R.id.animation_view2);
        InitialiseFields();
        checkConnection();
        emailSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginanim.setVisibility(View.VISIBLE);
                loginanim.playAnimation();
                email = emailEdit.getText().toString();
                password = passwordEdit.getText().toString();
                if (email.isEmpty() || password.isEmpty()) {
                    final Snackbar snackbar = Snackbar.make(emailSubmit, "Fill the Fields", Snackbar.LENGTH_LONG);
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                    snackbar.setBackgroundTint(getResources().getColor(R.color.black));
                    snackbar.setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    loginanim.cancelAnimation();
                    loginanim.setVisibility(View.GONE);
                    snackbar.show();
                } else {

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        if (Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified()) {
                                            SendUserToMainActivity();
                                        } else {
                                            final Snackbar snackbar = Snackbar.make(emailSubmit, "Verify your Email", Snackbar.LENGTH_LONG);
                                            snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                                            snackbar.setBackgroundTint(getResources().getColor(R.color.black));
                                            snackbar.show();
                                            loginanim.cancelAnimation();
                                            loginanim.setVisibility(View.GONE);
                                        }
                                    } else {
                                        final Snackbar snackbar = Snackbar.make(emailSubmit, "Invalid email or Password", Snackbar.LENGTH_LONG);
                                        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                                        snackbar.setBackgroundTint(getResources().getColor(R.color.black));
                                        emailEdit.setText(" ");
                                        passwordEdit.setText(" ");
                                        snackbar.show();
                                        loginanim.cancelAnimation();
                                        loginanim.setVisibility(View.GONE);

                                    }
                                }
                            });
                }
            }
        });
        loginanim.playAnimation();
        loginanim.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        NotAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = emailEdit.getText().toString();
                SendUserToPasswordResetActivity(s);
            }
        });
        mAuth = FirebaseAuth.getInstance();


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

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Facebook", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SendUserToPasswordResetActivity(String s) {
        Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
        if (!s.isEmpty()) {
            intent.putExtra("email", s);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void SendUserToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void SendUserToRegisterActivity() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void InitialiseFields() {
        emailEdit = findViewById(R.id.email_edit);
        passwordEdit = findViewById(R.id.password_edit);
        emailSubmit = findViewById(R.id.SignIn_btn);
        facebook = findViewById(R.id.FB_btn);
        google = findViewById(R.id.Google_btn);
        forgotPassword = findViewById(R.id.ForgotPassword);
        NotAccount = findViewById(R.id.NoAccount);
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
        intent.putExtra("from", "login");
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
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
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
package utilities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;


import com.example.loginstats.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;


import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

public class AddbusinessLogo extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 32;
    String userID;
    private Uri filePath;
    private static final String TAG = "AddbusinessLogo";
    private String url;
    private StorageReference mStorageRef;
    private Button addPhoto;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("WaitingList");
    private CircleImageView profileImage;
    FirebaseAuth mAuth;
    FirebaseUser currentuser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbusiness_logo);
        InitialiseFields();

        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference("BusinessLogos");

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showFileChooser();
            }
        });
        if (currentuser != null) {
            userID = currentuser.getUid();
            Log.d(TAG, "onCreate: UID taken" + userID);
        }


            // Handle navigation icon press



        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath == null) {
                    showFileChooser();
                } else {
                    Upload();
                }

            }
        });
    }


    private void InitialiseFields() {
        profileImage = findViewById(R.id.imageView2);
        addPhoto = findViewById(R.id.addPhotoBtn);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null &&
                data.getData() != null) {
            filePath = data.getData();
            Picasso.with(this)
                    .load(filePath)
                    .into(profileImage);
        }
    }

    private void Upload() {

        final StorageReference riversRef = mStorageRef.child(System.currentTimeMillis()+"."+getExtention(filePath));
        final ProgressDialog progressDialog = new ProgressDialog(AddbusinessLogo.this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        progressDialog.setCancelable(false);
        riversRef.putFile(filePath)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        myRef.child(userID).child("logo").setValue(url)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        myRef.child(userID).child("DataAdded").setValue("logo");
                                        SendUserToBusinessActivity();
                                    }
                                });
                    }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        progressDialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
            }
        });
    }

    private void SendUserToBusinessActivity() {
        Intent intent = new Intent(getApplicationContext(), GetBusinessInfo.class);
        startActivity(intent);
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private String getExtention(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getFileExtensionFromUrl(cr.getType(uri));
    }


}
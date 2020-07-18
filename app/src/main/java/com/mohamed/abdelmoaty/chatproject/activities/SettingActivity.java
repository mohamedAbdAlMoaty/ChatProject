package com.mohamed.abdelmoaty.chatproject.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohamed.abdelmoaty.chatproject.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    CircleImageView circleImageView;
    TextView mName;
    EditText mStatus;
    Button statusbtn,imagebtn;
    LinearLayout rootLayout;
    FirebaseAuth auth;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        circleImageView=(CircleImageView) findViewById(R.id.profile_image);
        mName=(TextView) findViewById(R.id.displayname);
        mStatus=(EditText) findViewById(R.id.status);
        statusbtn=(Button)findViewById(R.id.statusbtn);
        imagebtn=(Button) findViewById(R.id.imagebtn);
        rootLayout=(LinearLayout) findViewById(R.id.rootLayout);

        auth= FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();

        getInformation();

        statusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save user in database
                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference("users").child(user_id);
                Map userInfo =new HashMap();
                userInfo.put("status",mStatus.getText().toString());
                current_user_db.updateChildren(userInfo);
            }
        });

        imagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(SettingActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SettingActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                }
                else {
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.setType("image/*");
                    startActivityForResult(i, 1);
                }
            }
        });


    }

    private void getInformation() {


        final ProgressDialog mProgress = new ProgressDialog(SettingActivity.this);
        mProgress.setMessage("Please Wait...");
        mProgress.show();



        //database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(user_id);
       // for offline data + class Offline
        mDatabase.keepSynced(true);
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                switch (dataSnapshot.getKey()) {
                    case "name":
                        mName.setText(dataSnapshot.getValue().toString());
                        break;
                    case "status":
                        mStatus.setText(dataSnapshot.getValue().toString());
                        break;
                    case "image":
                        if(!dataSnapshot.getValue().toString().equals("default")){
                            Picasso.with(SettingActivity.this).load(dataSnapshot.getValue().toString()).into(circleImageView);
                        }
                        break;
                }
                mProgress.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestcode ,String[] permissions ,int[] grantResults)
    {
        if(requestcode ==100 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("image/*");
            startActivityForResult(i, 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==1 && resultCode==RESULT_OK){
           Uri imageUri=data.getData();     // real image
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(SettingActivity.this);
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();   // crop image


                final ProgressDialog mProgress = new ProgressDialog(SettingActivity.this);
                mProgress.setMessage("Please Wait...");
                mProgress.show();


                    StorageReference filePath= FirebaseStorage.getInstance().getReference().child("profile_image").child(user_id+".jpg");
                    UploadTask uploadTask = filePath.putFile(resultUri);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            Snackbar.make(rootLayout,"something happend in storage rules",Snackbar.LENGTH_SHORT).show();
                            mProgress.dismiss();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Uri downloadUrl = taskSnapshot.getDownloadUrl();  // image from database after storage

                            //save user in database
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference("users").child(user_id);
                            Map userInfo =new HashMap();
                            userInfo.put("image",downloadUrl.toString());
                            current_user_db.updateChildren(userInfo);

                            circleImageView.setImageURI(resultUri);
                            mProgress.dismiss();
                            Snackbar.make(rootLayout,"image upload Successfully",Snackbar.LENGTH_SHORT).show();
                        }
                    });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

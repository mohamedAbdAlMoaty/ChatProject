package com.mohamed.abdelmoaty.chatproject.activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mohamed.abdelmoaty.chatproject.R;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail,loginPassword;
    private Button login_btn,register_btn;
    FirebaseAuth auth;
    LinearLayout rootLayout;
    private FirebaseAuth.AuthStateListener firebaseAuthAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail=(EditText) findViewById(R.id.login_email);
        loginPassword=(EditText) findViewById(R.id.login_password);
        login_btn=(Button) findViewById(R.id.login_btn);
        register_btn=(Button) findViewById(R.id.register_btn);
        rootLayout=(LinearLayout) findViewById(R.id.rootLayout);

        auth= FirebaseAuth.getInstance();
        firebaseAuthAuthStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                if(user !=null){
                    Intent i=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(i);
                    finish();
                    return;
                }
            }
        };

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });



    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    private void Login() {

        String Email=loginEmail.getText().toString();
        String Password=loginPassword.getText().toString();

        //check fields if its empty
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            loginEmail.setError("enter Email");

        }
        else if (TextUtils.isEmpty(Password)) {
            Snackbar.make(rootLayout, "please enter password", Snackbar.LENGTH_SHORT).show();
        }
        else {

            final ProgressDialog mProgress = new ProgressDialog(LoginActivity.this);
            mProgress.setMessage("Please Wait...");
            mProgress.show();

            //login in firebase
            auth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        mProgress.dismiss();
                        Snackbar.make(rootLayout, "login error", Snackbar.LENGTH_SHORT).show();
                    } else {
                        mProgress.dismiss();
                        Snackbar.make(rootLayout, "Login Successfully", Snackbar.LENGTH_SHORT).show();







                       final String  current_user_id = auth.getCurrentUser().getUid();
                        final String deviceToken= FirebaseInstanceId.getInstance().getToken();
                        DatabaseReference mNotification1 = FirebaseDatabase.getInstance().getReference().child("notifications");
                        mNotification1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(current_user_id)){
                                    DatabaseReference mNotification2 = FirebaseDatabase.getInstance().getReference().child("notifications").child(current_user_id);
                                    mNotification2.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                            DatabaseReference mNotification3 = FirebaseDatabase.getInstance().getReference().child("notifications").child(current_user_id).child(dataSnapshot.getKey());
                                               Map userInfo =new HashMap();
                                                userInfo.put("to_device_token",deviceToken);
                                               mNotification3.updateChildren(userInfo);
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
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });






                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(firebaseAuthAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(firebaseAuthAuthStateListener);
    }
}

package com.mohamed.abdelmoaty.chatproject.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mohamed.abdelmoaty.chatproject.R;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {


    private EditText registerEmail,registerPassword,username;
    private Button register_btn,register_login_btn;
    FirebaseAuth auth;
    LinearLayout rootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerEmail=(EditText) findViewById(R.id.register_email);
        registerPassword=(EditText) findViewById(R.id.register_password);
        username=(EditText) findViewById(R.id.username);
        register_btn=(Button) findViewById(R.id.register_btn);
        register_login_btn=(Button) findViewById(R.id.register_login_btn);
        rootLayout=(LinearLayout) findViewById(R.id.rootLayout);

        auth= FirebaseAuth.getInstance();

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register();
            }
        });
        register_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void Register() {

        String Email=registerEmail.getText().toString();
        String Password=registerPassword.getText().toString();
        final String Username=username.getText().toString();

        //check fields if its empty
        if(TextUtils.isEmpty(Email)){
            Snackbar.make(rootLayout,"please enter email address",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Password)){
            Snackbar.make(rootLayout,"please enter password",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Username)){
            Snackbar.make(rootLayout,"please enter username",Snackbar.LENGTH_SHORT).show();
        }
        else {

            final ProgressDialog mProgress = new ProgressDialog(RegisterActivity.this);
            mProgress.setMessage("Please Wait...");
            mProgress.show();

            //register in firebase
            auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Snackbar.make(rootLayout, "sign up error", Snackbar.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    } else {

                        final String  user_id = auth.getCurrentUser().getUid();

                        //save user in database
                        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference("users").child(user_id);
                        Map userInfo =new HashMap();
                        userInfo.put("name",Username);
                        userInfo.put("status","Hi there, i`m using Minions Chat App");
                        userInfo.put("image","default");
                        current_user_db.updateChildren(userInfo);


                        mProgress.dismiss();
                        Snackbar.make(rootLayout, "Register Successfully", Snackbar.LENGTH_SHORT).show();

                        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            });

        }
    }
}

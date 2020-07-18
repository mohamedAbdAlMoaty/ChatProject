package com.mohamed.abdelmoaty.chatproject.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohamed.abdelmoaty.chatproject.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    ImageView imgprof;
    TextView nameprof,statusprof,totalprof;
    Button btnrequest,btndecline;
    FirebaseAuth auth;
    String user_id;
    String current_user_id;
    String next_state ="send request";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgprof=(ImageView)findViewById(R.id.imgprof);
        nameprof=(TextView)findViewById(R.id.nameprof);
        statusprof=(TextView)findViewById(R.id.statusprof);
        totalprof=(TextView)findViewById(R.id.totalprof);
        btnrequest=(Button)findViewById(R.id.btnrequest);
        btndecline=(Button) findViewById(R.id.btndecline);

        auth= FirebaseAuth.getInstance();
        current_user_id= auth.getCurrentUser().getUid();
        user_id= getIntent().getStringExtra("user_id");
        btndecline.setVisibility(View.INVISIBLE);

        getInfo();
        btnrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRequest();
            }
        });
    }

    private void setRequest() {

        switch (next_state){
            case "send request":
                DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference().child("friend_request").child(current_user_id).child(user_id).child("request_type");
                mDatabase1.setValue("sent");
                DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference().child("friend_request").child(user_id).child(current_user_id).child("request_type");
                mDatabase2.setValue("receive");
                DatabaseReference mNotification = FirebaseDatabase.getInstance().getReference().child("notifications").child(user_id).push();
                Map userInfo =new HashMap();
                userInfo.put("from",current_user_id);
                userInfo.put("type","friend request");
                mNotification.updateChildren(userInfo);
                next_state ="cancel request";
                btnrequest.setText("Cancel Friend Request");
                break;

            case "cancel request":
                DatabaseReference mDatabase3 = FirebaseDatabase.getInstance().getReference().child("friend_request").child(current_user_id).child(user_id).child("request_type");
                mDatabase3.removeValue();
                DatabaseReference mDatabase4 = FirebaseDatabase.getInstance().getReference().child("friend_request").child(user_id).child(current_user_id).child("request_type");
                mDatabase4.removeValue();
                next_state ="send request";
                btnrequest.setText("Send Friend Request");
                break;

            case "accept request":
                String currentDate= DateFormat.getTimeInstance().format(new Date());
                 DatabaseReference mDatabase5 = FirebaseDatabase.getInstance().getReference().child("friends").child(current_user_id).child(user_id).child("date");
                 mDatabase5.setValue(currentDate);
                 DatabaseReference mDatabase6 = FirebaseDatabase.getInstance().getReference().child("friends").child(user_id).child(current_user_id).child("date");
                  mDatabase6.setValue(currentDate);
                DatabaseReference mDatabase7 = FirebaseDatabase.getInstance().getReference().child("friend_request").child(current_user_id).child(user_id).child("request_type");
                mDatabase7.removeValue();
                DatabaseReference mDatabase8 = FirebaseDatabase.getInstance().getReference().child("friend_request").child(user_id).child(current_user_id).child("request_type");
                mDatabase8.removeValue();
               next_state ="delete friend";
                btnrequest.setText("unfriend  ");
                btndecline.setVisibility(View.INVISIBLE);
                break;

            case "delete friend":
                DatabaseReference mDatabase9 = FirebaseDatabase.getInstance().getReference().child("friends").child(current_user_id).child(user_id);
                mDatabase9.removeValue();
                DatabaseReference mDatabase10 = FirebaseDatabase.getInstance().getReference().child("friends").child(user_id).child(current_user_id);
                mDatabase10.removeValue();
                next_state ="send request";
                btnrequest.setText("Send Friend Request  ");
                break;


        }

    }

    private void getInfo() {
        final ProgressDialog mProgress = new ProgressDialog(ProfileActivity.this);
        mProgress.setMessage("Please Wait...");
        mProgress.show();




        //database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(user_id);
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                switch (dataSnapshot.getKey()) {
                    case "name":
                        nameprof.setText(dataSnapshot.getValue().toString());
                        break;
                    case "status":
                        statusprof.setText(dataSnapshot.getValue().toString());
                        break;
                    case "image":
                        if(!dataSnapshot.getValue().toString().equals("default")){
                            Picasso.with(ProfileActivity.this).load(dataSnapshot.getValue().toString()).into(imgprof);
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



        //check for friend request
        DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference().child("friend_request").child(current_user_id);
        mDatabase1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_id)){
                    String req_type=dataSnapshot.child(user_id).child("request_type").getValue().toString();
                    if(req_type.equals("receive")){
                        next_state ="accept request";
                        btnrequest.setText("Accept Friend Request");
                        btndecline.setVisibility(View.VISIBLE);

                    }
                   else if(req_type.equals("sent")){
                        next_state ="cancel request";
                        btnrequest.setText("Cancel Friend Request");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // check for friends
        DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference().child("friends").child(current_user_id);
        mDatabase2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_id)){
                    next_state ="delete friend";
                    btnrequest.setText("unfriend  ");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}

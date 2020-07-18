package com.mohamed.abdelmoaty.chatproject.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mohamed.abdelmoaty.chatproject.R;
import com.mohamed.abdelmoaty.chatproject.adapter.CustomRecycleView;
import com.mohamed.abdelmoaty.chatproject.models.User;

import java.util.ArrayList;


public class UsersActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    String name;
    String status;
    String image;
    ArrayList<User> arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        arr=new ArrayList<User>();
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        recyclerView=(RecyclerView)findViewById(R.id.recyleview);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getUsers();
    }

    private void getUsers() {

         final ProgressDialog mProgress = new ProgressDialog(UsersActivity.this);
        mProgress.setMessage("Please Wait...");
        mProgress.show();

        DatabaseReference path1 = FirebaseDatabase.getInstance().getReference().child("users");
        path1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String user_id=dataSnapshot.getKey().toString();
                String name= dataSnapshot.child("name").getValue().toString();
                String status= dataSnapshot.child("status").getValue().toString();
                String image= dataSnapshot.child("image").getValue().toString();
                String icon=  dataSnapshot.child("online").getValue().toString();
                User user=new User(user_id,name,status,image,icon);
                arr.add(user);

                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(UsersActivity.this));
                CustomRecycleView adapter=new CustomRecycleView(UsersActivity.this,arr);
                recyclerView.setAdapter(adapter);
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
}

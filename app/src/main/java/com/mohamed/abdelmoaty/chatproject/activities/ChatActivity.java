package com.mohamed.abdelmoaty.chatproject.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohamed.abdelmoaty.chatproject.adapter.CustomRecycleMessage;
import com.mohamed.abdelmoaty.chatproject.models.Messages;
import com.mohamed.abdelmoaty.chatproject.R;
import com.mohamed.abdelmoaty.chatproject.Utils.GetTimeAgo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity {

    String mChatUser_id;
    Toolbar toolbar;
    TextView chatname;
    TextView chatseen;
    CircleImageView chatimg;
    ImageButton add;
    ImageButton send;
    EditText message;
    FirebaseAuth auth;
    LinearLayout rootLayout;
    RecyclerView recyclerView;
    ArrayList<Messages> arr;
    String  current_user_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar=(Toolbar) findViewById(R.id.toolbar);
        auth= FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);
        mChatUser_id= getIntent().getStringExtra("user_id");
        current_user_id = auth.getCurrentUser().getUid();
        chatUser();
        loadMessage();
         LayoutInflater actionBarLayout = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         View actionview=actionBarLayout.inflate(R.layout.customactionbar,null);
        chatname=(TextView) actionview.findViewById(R.id.chatname);
        chatseen=(TextView) actionview.findViewById(R.id.chatseen);
        chatimg=(CircleImageView)actionview. findViewById(R.id.chatimg);
        add=(ImageButton) findViewById(R.id.add);
        send=(ImageButton) findViewById(R.id.send);
        message=(EditText) findViewById(R.id.message);
        recyclerView=(RecyclerView) findViewById(R.id.recyleviewmessage);
        rootLayout=(LinearLayout) findViewById(R.id.rootLayout);
        arr=new ArrayList<Messages>();

        // Set up your ActionBar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
              actionBar.setDisplayHomeAsUpEnabled(true);
              actionBar.setDisplayShowCustomEnabled(true);
              actionBar.setCustomView(actionview);
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                }
                else {
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.setType("image/*");
                    startActivityForResult(i, 1);
                }
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

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri imgeUri = data.getData();
            final DatabaseReference send1 = FirebaseDatabase.getInstance().getReference().child("message").child(current_user_id).child(mChatUser_id).push();
            final String push_id = send1.getKey();

            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("message_images").child(push_id + ".jpg");
            UploadTask uploadTask = filePath.putFile(imgeUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                    Snackbar.make(rootLayout, "something happend in storage rules", Snackbar.LENGTH_SHORT).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUrl  = String.valueOf(taskSnapshot.getDownloadUrl());  // image from database after storage

                           Map collect = new HashMap();
                           collect.put("message", downloadUrl);
                           collect.put("seen", false);
                           collect.put("time", ServerValue.TIMESTAMP);
                           collect.put("type", "image");
                           collect.put("from", current_user_id);
                           send1.updateChildren(collect);
                           DatabaseReference send2 = FirebaseDatabase.getInstance().getReference().child("message").child(mChatUser_id).child(current_user_id).child(push_id);
                           send2.updateChildren(collect);

                }
            });


        }



    }

    private void loadMessage() {
        DatabaseReference load1= FirebaseDatabase.getInstance().getReference().child("message").child(current_user_id).child(mChatUser_id);
        load1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    String msg= dataSnapshot.child("message").getValue().toString();
                    String seen= dataSnapshot.child("seen").getValue().toString();
                    String time=  dataSnapshot.child("time").getValue().toString();
                    String type=  dataSnapshot.child("type").getValue().toString();
                    String from=  dataSnapshot.child("from").getValue().toString();
                    Messages message=new Messages(msg,seen,time,type,from);
                    arr.add(message);


                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                    CustomRecycleMessage adapter=new CustomRecycleMessage(ChatActivity.this,arr);
                    recyclerView.setAdapter(adapter);
                    recyclerView.scrollToPosition(arr.size()-1);  // when you write show the position of your write

                }


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

    private void sendMessage() {
        String mess=message.getText().toString().trim();
        if(TextUtils.isEmpty(mess)){
            Snackbar.make(rootLayout,"please enter message",Snackbar.LENGTH_SHORT).show();
        }else{
            DatabaseReference send1= FirebaseDatabase.getInstance().getReference().child("message").child(current_user_id).child(mChatUser_id).push();
           String push_id= send1.getKey();
            Map map=new HashMap();
            map.put("message",mess);
            map.put("seen", false);
            map.put("time", ServerValue.TIMESTAMP);
            map.put("type", "text");
            map.put("from", current_user_id);
            send1.updateChildren(map);
            DatabaseReference send2= FirebaseDatabase.getInstance().getReference().child("message").child(mChatUser_id).child(current_user_id).child(push_id);
            send2.updateChildren(map);
            message.setText("");


            DatabaseReference chat1 = FirebaseDatabase.getInstance().getReference().child("chat").child(current_user_id).child(mChatUser_id);
            DatabaseReference chat2 = FirebaseDatabase.getInstance().getReference().child("chat").child(mChatUser_id).child(current_user_id);
            Map chatmap=new HashMap();
            chatmap.put("seen",false);
            chatmap.put("timestamp", ServerValue.TIMESTAMP);
            chat1.updateChildren(chatmap);
            chat2.updateChildren(chatmap);
        }
    }

    private void chatUser() {
        DatabaseReference path1= FirebaseDatabase.getInstance().getReference().child("users").child(mChatUser_id);
        path1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               String name= dataSnapshot.child("name").getValue().toString();
               chatname.setText(name);
                String image= dataSnapshot.child("image").getValue().toString();
                String online= dataSnapshot.child("online").getValue().toString();
                if(online.equals("true")){
                    chatseen.setText("Online");
                }
                else{
                    GetTimeAgo gettimeAgo=new GetTimeAgo();
                    long lastTime=Long.parseLong(online);
                    String LASTtime=gettimeAgo.getTimeAgo(lastTime,getApplicationContext());
                    chatseen.setText(LASTtime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}

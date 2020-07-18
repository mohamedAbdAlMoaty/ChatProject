package com.mohamed.abdelmoaty.chatproject.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohamed.abdelmoaty.chatproject.R;
import com.mohamed.abdelmoaty.chatproject.models.User;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    FirebaseAuth auth;
    RecyclerView rec;
    String current_user_id;
    ArrayList<User> arr;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=  inflater.inflate(R.layout.fragment_chat, container, false);
        rec=(RecyclerView) view.findViewById(R.id.rec);
        auth= FirebaseAuth.getInstance();
        current_user_id=auth.getCurrentUser().getUid();
        arr=new ArrayList<User>();
        loadMessage();

        if(auth.getCurrentUser()!=null){
            //change online status if not availble network
             //  String user_id  = auth.getCurrentUser().getUid();
             //  DatabaseReference database = FirebaseDatabase.getInstance().getReference("users").child(user_id).child("online");
            //   database.onDisconnect().setValue(false);
        }

        return view;
    }

    private void loadMessage() {

        DatabaseReference chat= FirebaseDatabase.getInstance().getReference().child("chat").child(current_user_id);
        chat.keepSynced(true);
        chat.orderByChild("timestamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DatabaseReference message1= FirebaseDatabase.getInstance().getReference().child("message").child(current_user_id);

                        DatabaseReference usr = FirebaseDatabase.getInstance().getReference().child("users").child(dataSnapshot.getKey());
                        usr.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String user_id=dataSnapshot.getKey().toString();
                                final String name= dataSnapshot.child("name").getValue().toString();
                                final String image= dataSnapshot.child("image").getValue().toString();
                                final String icon=  dataSnapshot.child("online").getValue().toString();


                                Query message2= FirebaseDatabase.getInstance().getReference().child("message").child(current_user_id).child(dataSnapshot.getKey()).limitToLast(1);
                          message2.addValueEventListener(new ValueEventListener() {
                              @Override
                              public void onDataChange(DataSnapshot dataSnapshot) {
                                //  String msg= dataSnapshot.child("message").getValue().toString();
                                  Toast.makeText(getActivity(), dataSnapshot.child("message").getValue().toString()+"", Toast.LENGTH_LONG).show();
                                  //   String status=msg;
                                  //    User user=new User(user_id,name,status,image,icon);
                                  //   arr.add(user);

                                  //  rec.setHasFixedSize(true);
                                  //  rec.setLayoutManager(new LinearLayoutManager(getActivity()));
                                  // CustomRecycleView adapter=new CustomRecycleView(getActivity(),arr);
                                  // rec.setAdapter(adapter);
                              }

                              @Override
                              public void onCancelled(DatabaseError databaseError) {

                              }
                          });










                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


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

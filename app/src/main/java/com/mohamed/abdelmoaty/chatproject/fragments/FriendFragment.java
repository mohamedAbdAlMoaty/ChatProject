package com.mohamed.abdelmoaty.chatproject.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import com.google.firebase.database.ValueEventListener;
import com.mohamed.abdelmoaty.chatproject.R;
import com.mohamed.abdelmoaty.chatproject.adapter.CustomRecycleView;
import com.mohamed.abdelmoaty.chatproject.models.User;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {

    private RecyclerView mRecyclerView;
    FirebaseAuth auth;
    private ArrayList<User> arr;


    public FriendFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_friend, container, false);
        mRecyclerView=(RecyclerView)view.findViewById(R.id.friends_list);
        auth= FirebaseAuth.getInstance();
        arr=new ArrayList<User>();
        getUsers();
        return view;
    }


    private void getUsers() {

        String current_user_id= auth.getCurrentUser().getUid();
        DatabaseReference path1 = FirebaseDatabase.getInstance().getReference().child("friends").child(current_user_id);
        path1.keepSynced(true);
        path1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String date= dataSnapshot.child("date").getValue().toString();
                DatabaseReference path2 = FirebaseDatabase.getInstance().getReference().child("users").child(dataSnapshot.getKey());
                path2.keepSynced(true);
                path2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String user_id=dataSnapshot.getKey().toString();
                        String name= dataSnapshot.child("name").getValue().toString();
                        String image= dataSnapshot.child("image").getValue().toString();
                        String icon=  dataSnapshot.child("online").getValue().toString();
                        User user=new User(user_id,name,date,image,icon);
                        arr.add(user);

                        Toast.makeText(getActivity(), arr.size()+"", Toast.LENGTH_SHORT).show();
                        mRecyclerView.setHasFixedSize(true);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        CustomRecycleView adapter=new CustomRecycleView(getActivity(),arr);
                        mRecyclerView.setAdapter(adapter);
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

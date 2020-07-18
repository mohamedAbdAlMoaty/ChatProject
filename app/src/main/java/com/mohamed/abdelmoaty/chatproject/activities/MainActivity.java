package com.mohamed.abdelmoaty.chatproject.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.mohamed.abdelmoaty.chatproject.R;
import com.mohamed.abdelmoaty.chatproject.adapter.ViewPagerAdapter;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    Toolbar toolbar;
    TabLayout tabLayout;
    FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener firebaseAuthAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager=(ViewPager)findViewById(R.id.pager);
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        tabLayout=(TabLayout) findViewById(R.id.tabs);
        auth= FirebaseAuth.getInstance();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Minions Chat");

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        firebaseAuthAuthStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                if(user ==null){
                    Intent i=new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(i);
                    finish();
                    return;
                }
               else{
                   String  user_id = auth.getCurrentUser().getUid();
                   DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference("users").child(user_id);
                    Map userInfo =new HashMap();
                    userInfo.put("online","true");
                    current_user_db.updateChildren(userInfo);
               }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.Accountsetting:
                Intent intent=new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.Allusers:
                Intent in=new Intent(MainActivity.this, UsersActivity.class);
                startActivity(in);
                break;
            case R.id.signout:
                if(auth.getCurrentUser()!=null){
                    String  user_id = auth.getCurrentUser().getUid();
                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference("users").child(user_id);
                    Map userInfo =new HashMap();
                    userInfo.put("online", ServerValue.TIMESTAMP);
                    current_user_db.updateChildren(userInfo);
                }
                auth.signOut();
                Intent i=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
                break;
        }

        return true;
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
        if(auth.getCurrentUser()!=null){
            String  user_id = auth.getCurrentUser().getUid();
            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference("users").child(user_id);
            Map userInfo =new HashMap();
            userInfo.put("online",ServerValue.TIMESTAMP);
            current_user_db.updateChildren(userInfo);
        }
    }

}

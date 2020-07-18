package com.mohamed.abdelmoaty.chatproject.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mohamed.abdelmoaty.chatproject.fragments.ChatFragment;
import com.mohamed.abdelmoaty.chatproject.fragments.FriendFragment;
import com.mohamed.abdelmoaty.chatproject.fragments.RequestFragment;


public class ViewPagerAdapter extends FragmentPagerAdapter {


    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case  0:
                return new RequestFragment();
            case  1:
                return new ChatFragment();
            case  2:
                return new FriendFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case  0:
                return "Requests";
            case  1:
                return "Chats";
            case  2:
                return "Friends";
            default:
                return  null;
        }
    }
}
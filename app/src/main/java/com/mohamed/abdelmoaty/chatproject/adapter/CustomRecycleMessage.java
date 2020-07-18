package com.mohamed.abdelmoaty.chatproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mohamed.abdelmoaty.chatproject.models.Messages;
import com.mohamed.abdelmoaty.chatproject.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.Gravity.RIGHT;

/**
 * Created by HP on 6/23/2018.
 */

public class CustomRecycleMessage extends RecyclerView.Adapter<CustomRecycleMessage.ViewHolder> {

     ArrayList<Messages> arr;
    FirebaseAuth  auth;
    private Context context;


    public CustomRecycleMessage(Context context,ArrayList<Messages> arr) {

        this.arr=arr;
        this.context=context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_message,parent,false);
        return new ViewHolder(row);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        auth= FirebaseAuth.getInstance();
       String current_user_id = auth.getCurrentUser().getUid();
       if((arr.get(position).getFrom()).equals(current_user_id)){
           holder.message_message.setBackgroundResource(R.drawable.background_for_message);
           holder.message_img.setVisibility(View.INVISIBLE);
           holder.layout.setGravity(RIGHT);
       }else{
           holder.message_message.setBackgroundResource(R.drawable.background_message2);
       }

       if(arr.get(position).getType().equals("text")){
           holder.message_message.setText(arr.get(position).getMessage());
           holder.message_image.setVisibility(View.INVISIBLE);
       }else if(arr.get(position).getType().equals("image")){
           Picasso.with(context).load(arr.get(position).getMessage()).into(holder.message_image);
           holder.message_message.setVisibility(View.INVISIBLE);
       }


    }

    @Override
    public int getItemCount() {
        return arr.size();
    }


    public static class ViewHolder extends  RecyclerView.ViewHolder{
        public View mView;
        public CircleImageView message_img;
        public TextView message_message;
        public ImageView message_image;
        public LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            message_img=(CircleImageView) itemView.findViewById(R.id.message_img);
            message_message=(TextView) itemView.findViewById(R.id.message_message);
            message_image=(ImageView) itemView.findViewById(R.id.message_image);
           layout=(LinearLayout)itemView.findViewById(R.id.layout);
        }
    }

}

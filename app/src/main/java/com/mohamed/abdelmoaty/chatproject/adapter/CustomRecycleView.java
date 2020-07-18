package com.mohamed.abdelmoaty.chatproject.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mohamed.abdelmoaty.chatproject.activities.ChatActivity;
import com.mohamed.abdelmoaty.chatproject.activities.ProfileActivity;
import com.mohamed.abdelmoaty.chatproject.R;
import com.mohamed.abdelmoaty.chatproject.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;



public class CustomRecycleView extends RecyclerView.Adapter<CustomRecycleView.ViewHolder> {

    private ArrayList<User> arr;
    private Context context;


    public CustomRecycleView(Activity context, ArrayList<User> arr) {

        this.arr=arr;
        this.context=context;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.customrecycleview,parent,false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.name.setText(arr.get(position).getName());
        holder.status_date.setText(arr.get(position).getStatus_date());

        if(arr.get(position).getIcon()=="true"){
            holder.online_icon.setVisibility(View.VISIBLE);
        }
        else {
            holder.online_icon.setVisibility(View.INVISIBLE);
        }

        if(!arr.get(position).getImage().equals("default")){
            Picasso.with(context).load(arr.get(position).getImage()).into(holder.prof);
        }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (context.getClass().getSimpleName()){
                        case "UsersActivity":
                            Intent i = new Intent(context , ProfileActivity.class);
                            i.putExtra("user_id",arr.get(position).getUser_id());
                            context.startActivity(i);

                            break;
                        case "MainActivity":
                            CharSequence colors[] = new CharSequence[] {"Open Profile", "Send Message"};

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Select Options");
                            builder.setItems(colors, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case 0:
                                            Intent prof = new Intent(context , ProfileActivity.class);
                                            prof.putExtra("user_id",arr.get(position).getUser_id());
                                            context.startActivity(prof);
                                            break;
                                        case 1:
                                            Intent chat = new Intent(context , ChatActivity.class);
                                            chat.putExtra("user_id",arr.get(position).getUser_id());
                                            context.startActivity(chat);
                                            break;
                                    }
                                }
                            });
                            builder.show();
                            break;
                    }

                }
            });



    }

    @Override
    public int getItemCount() {
        return arr.size();
    }


    public static class ViewHolder extends  RecyclerView.ViewHolder{
        public View mView;
        public TextView name;
        public TextView status_date;
        public ImageView online_icon;
        public CircleImageView prof;

        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            name=(TextView) itemView.findViewById(R.id.nam);
            status_date = (TextView) itemView.findViewById(R.id.stat);
            online_icon=(ImageView) itemView.findViewById(R.id.online_icon);
            prof=(CircleImageView)itemView.findViewById(R.id.prof);

        }
    }

}

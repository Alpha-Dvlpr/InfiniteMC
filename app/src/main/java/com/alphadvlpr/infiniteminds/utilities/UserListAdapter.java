package com.alphadvlpr.infiniteminds.utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.objects.User;
import com.alphadvlpr.infiniteminds.users.EditUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<User> users;
    private Context context;

    public UserListAdapter(Context context, ArrayList<User> users){
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){ return new UserListAdapter.UserViewHolder(inflateResource(parent)); }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position){
        UserListAdapter.UserViewHolder userHolder = (UserListAdapter.UserViewHolder) holder;
        final User user = users.get(position);

        userHolder.nick.setText(user.getNickname());
        userHolder.nick.setTextColor(user.getAdmin() ? Color.BLUE : Color.BLACK);
        userHolder.email.setText(user.getEmail());
        userHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEditUser = new Intent(context, EditUser.class);
                toEditUser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Bundle b = new Bundle();
                b.putString("email", user.getEmail());
                b.putBoolean("admin", user.getAdmin());
                b.putString("nickname", user.getNickname());

                toEditUser.putExtras(b);
                context.startActivity(toEditUser);
            }
        });
    }

    @Override
    public int getItemCount(){ return users.size(); }

    private View inflateResource(ViewGroup parent){ return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false); }

    class UserViewHolder extends RecyclerView.ViewHolder{
        TextView email, nick;
        CardView container;

        UserViewHolder(@NonNull View itemView){
            super(itemView);

            container = itemView.findViewById(R.id.itemUserContainer);
            email = itemView.findViewById(R.id.itemUserMail);
            nick = itemView.findViewById(R.id.itemUserNick);
        }
    }
}

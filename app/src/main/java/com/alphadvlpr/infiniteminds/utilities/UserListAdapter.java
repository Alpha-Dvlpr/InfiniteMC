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

/**
 * Custom adapter for the RecyclerViews that contain the registered users.
 *
 * @author AlphaDvlpr.
 */
public class UserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> users;
    private Context context;

    /**
     * This method initializes the context and the list for the adapter to work.
     *
     * @param context The activity's context.
     * @param users   The ArrayList where the users will be held.
     * @author AlphaDvlpr.
     */
    public UserListAdapter(Context context, ArrayList<User> users) {
        this.users = users;
        this.context = context;
    }

    /**
     * This method creates a new ViewHolder for every item on the list.
     * The position is calculated automatically.
     *
     * @param parent   The parent containing the RecyclerView.
     * @param viewType The type of view to be shown.
     * @return Returns the proper type of view for the actual position.
     * @author AlphaDvlpr.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(inflateResource(parent));
    }

    /**
     * This method bind the ArrayList to the RecyclerView with the given layout for every position.
     * This also sets the listener for the articles containers.
     *
     * @param holder   The type of View for a certain position.
     * @param position The current position of the article on the RecyclerView.
     *                 This is given automatically.
     * @author AlphaDvlpr.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
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

    /**
     * This method calculates the total number of items the RecyclerView will have.
     *
     * @return Returns the number of users the ArrayList has.
     * @author AlphaDvlpr.
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * This method loads the view to the RecyclerView.
     *
     * @param parent The view that will contain the item.
     * @return Returns the inflater with the needed layout.
     * @author AlphaDvlpr.
     */
    private View inflateResource(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
    }

    /**
     * Custom class for all the users.
     *
     * @author AlphaDvlpr.
     */
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView email, nick;
        CardView container;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.itemUserContainer);
            email = itemView.findViewById(R.id.itemUserMail);
            nick = itemView.findViewById(R.id.itemUserNick);
        }
    }
}

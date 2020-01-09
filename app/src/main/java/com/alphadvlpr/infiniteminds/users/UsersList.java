package com.alphadvlpr.infiniteminds.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.objects.User;
import com.alphadvlpr.infiniteminds.utilities.UserListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UsersList extends AppCompatActivity {

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private RecyclerView listUsers;
    private SwipeRefreshLayout swipeEditUsers;
    private FloatingActionButton fabAdd;
    private String _email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);

        fabAdd = findViewById(R.id.fab);
        listUsers = findViewById(R.id.listUsers);
        swipeEditUsers = findViewById(R.id.refreshUsers);

        Intent prev = getIntent();

        _email = prev.getStringExtra("email");

        setActions(_email);
        initUsersList(_email);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUsersList(_email);
    }

    private void setActions(final String email){
        swipeEditUsers.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { initUsersList(email); }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { startActivity(new Intent(UsersList.this, NewUser.class)); }
        });
    }

    private void initUsersList(final String email){
        mDatabase.collection("users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<User> users = new ArrayList<>();

                        for(QueryDocumentSnapshot qds : queryDocumentSnapshots){
                            User aux = qds.toObject(User.class);

                            if(!aux.getEmail().equals(email)){ users.add(aux); }
                        }

                        UserListAdapter adapter = new UserListAdapter(UsersList.this, users);
                        listUsers.setLayoutManager(new LinearLayoutManager(UsersList.this));
                        listUsers.setAdapter(adapter);
                    }
                });

        swipeEditUsers.setRefreshing(false);
    }
}

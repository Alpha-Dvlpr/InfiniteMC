package com.alphadvlpr.infiniteminds.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.objects.User;
import com.alphadvlpr.infiniteminds.utilities.UserListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This class manages the list of all the registered users.
 *
 * @author AlphaDvlpr.
 */
public class UsersList extends AppCompatActivity {

    private RecyclerView listUsers;
    private SwipeRefreshLayout swipeEditUsers;
    private FloatingActionButton fabAdd;
    private String _email = "";
    private ProgressBar progressBar;
    private final int QUERY_LIMIT = 15;
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;
    private ArrayList<User> users;
    private LinearLayoutManager layout;
    private UserListAdapter adapter;

    /**
     * This method initializes all the views on this Activity.
     *
     * @param savedInstanceState The previous state of the activity if saved.
     * @author AlphaDvlpr.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);

        fabAdd = findViewById(R.id.fab);
        listUsers = findViewById(R.id.listUsers);
        swipeEditUsers = findViewById(R.id.refreshUsers);
        progressBar = findViewById(R.id.progressBar);

        Intent prev = getIntent();

        _email = prev.getStringExtra("email");

        setActions(_email);
        initUsersList(_email);
    }

    /**
     * If the activity was paused when it resumes it updates the articles list.
     *
     * @author AlphaDvlpr.
     */
    @Override
    protected void onResume() {
        super.onResume();
        initUsersList(_email);
    }

    /**
     * This method sets the actions for all the buttons of the view.
     *
     * @author AlphaDvlpr.
     */
    protected void setActions(final String email) {
        swipeEditUsers.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initUsersList(email);
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UsersList.this, NewUser.class));
            }
        });
    }

    /**
     * This method loads the data from the Firebase server to the list.
     *
     * @author AlphaDvlpr.
     */
    protected void initUsersList(final String email) {
        progressBar.setVisibility(View.VISIBLE);

        Query query = FirebaseFirestore.getInstance()
                .collection("users")
                .limit(QUERY_LIMIT);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                users = new ArrayList<>();

                for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                    User aux = qds.toObject(User.class);

                    if (!aux.getEmail().equals(email)) {
                        users.add(aux);
                    }
                }

                adapter = new UserListAdapter(UsersList.this, users);
                layout = new LinearLayoutManager(UsersList.this);
                listUsers.setLayoutManager(layout);
                listUsers.setAdapter(adapter);
                progressBar.setVisibility(View.INVISIBLE);
                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                            isScrolling = true;
                        }
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        int firstVisibleItem = layout.findFirstVisibleItemPosition();
                        int visibleItemCount = layout.getChildCount();
                        int totalItemCount = layout.getItemCount();

                        if (isScrolling && (firstVisibleItem + visibleItemCount == totalItemCount) && !isLastItemReached) {
                            isScrolling = false;

                            makeToast("Loading more users...");

                            Query nextQuery = FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .startAfter(lastVisible)
                                    .limit(QUERY_LIMIT);

                            nextQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                                        users.add(qds.toObject(User.class));
                                    }

                                    adapter.notifyDataSetChanged();
                                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                                    if (queryDocumentSnapshots.size() < QUERY_LIMIT) {
                                        isLastItemReached = true;
                                    }
                                }
                            });
                        }
                    }
                };

                listUsers.addOnScrollListener(onScrollListener);
            }
        });

        swipeEditUsers.setRefreshing(false);
    }

    /**
     * Method to show a Toast notification on the current view.
     *
     * @param msg The message to be displayed.
     * @author AlphaDvlpr.
     */
    private void makeToast(String msg) {
        Toast.makeText(UsersList.this, msg, Toast.LENGTH_SHORT).show();
    }
}

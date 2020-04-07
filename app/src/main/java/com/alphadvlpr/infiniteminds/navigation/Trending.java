package com.alphadvlpr.infiniteminds.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.objects.Article;
import com.alphadvlpr.infiniteminds.utilities.ArticleAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This class manages the Trending view.
 *
 * @author AlphaDvlpr.
 */
public class Trending extends AppCompatActivity {

    private FloatingActionButton fabSearch;
    private BottomAppBar mainBar;
    private RecyclerView listFav;
    private SwipeRefreshLayout swipeTrendingArticles;
    private ActionMenuItemView itemUsers;
    private ActionMenuItemView itemInfo;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = mDatabase.collection("articles");

    /**
     * This method initializes all the views on this Activity.
     *
     * @param savedInstanceState The previous saved state of the activity.
     * @author AlphaDvlpr.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trending);

        mainBar = findViewById(R.id.trendingBottomAppBar);
        fabSearch = findViewById(R.id.trendingSearchFAB);
        itemInfo = findViewById(R.id.menuAbout);
        itemUsers = findViewById(R.id.menuUsers);
        swipeTrendingArticles = findViewById(R.id.refreshFav);
        listFav = findViewById(R.id.trendingList);

        initFavList();
        setActions();
    }

    /**
     * This method loads the data from the Firebase server to the list.
     *
     * @author AlphaDvlpr.
     */
    protected void initFavList() {
        collectionReference
                .whereGreaterThan("visits", 0)
                .orderBy("visits", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Article> dataFavList = new ArrayList<>();

                        for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                            dataFavList.add(qds.toObject(Article.class));
                        }

                        ArticleAdapter adapter = new ArticleAdapter(Trending.this, dataFavList);
                        listFav.setLayoutManager(new LinearLayoutManager(Trending.this));
                        listFav.setAdapter(adapter);
                    }
                });

        swipeTrendingArticles.setRefreshing(false);
    }

    /**
     * This method sets the actions for the buttons of the view.
     *
     * @author AlphaDvlpr.
     */
    protected void setActions() {
        swipeTrendingArticles.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initFavList();
            }
        });

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Trending.this, Search.class));
                finish();
            }
        });

        mainBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Trending.this, Home.class));
                finish();
            }
        });

        itemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Trending.this, Info.class));
                finish();
            }
        });

        itemUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Trending.this, Login.class));
                finish();
            }
        });
    }
}

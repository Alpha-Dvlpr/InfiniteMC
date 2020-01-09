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
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    private FloatingActionButton fabSearch;
    private ActionMenuItemView itemUsers, itemTrending, itemInfo;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = mDatabase.collection("articles");
    private RecyclerView listMain;
    private SwipeRefreshLayout swipeMainArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        fabSearch = findViewById(R.id.homeSearchFAB);
        itemInfo = findViewById(R.id.menuAbout);
        itemTrending = findViewById(R.id.menuTrend);
        itemUsers = findViewById(R.id.menuUsers);
        swipeMainArticles = findViewById(R.id.refreshMain);
        listMain = findViewById(R.id.mainList);

        MobileAds.initialize(this, "ca-app-pub-2122172706327985~8237512049");

        initMainList();
        setActions();
    }

    private void setActions(){
        swipeMainArticles.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { initMainList(); }
        });

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Search.class));
                finish();
            }
        });

        itemTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Trending.class));
                finish();
            }
        });

        itemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Info.class));
                finish();
            }
        });

        itemUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Login.class));
                finish();
            }
        });
    }

    private void initMainList() {
        collectionReference
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Article> dataMainList = new ArrayList<>();

                        for(QueryDocumentSnapshot qds : queryDocumentSnapshots){ dataMainList.add(qds.toObject(Article.class)); }

                        listMain.setLayoutManager(new LinearLayoutManager(Home.this));
                        ArticleAdapter adapter = new ArticleAdapter(Home.this, dataMainList);
                        listMain.setAdapter(adapter);
                    }
                });

        swipeMainArticles.setRefreshing(false);
    }
}

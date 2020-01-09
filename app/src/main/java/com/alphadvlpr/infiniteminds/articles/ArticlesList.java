package com.alphadvlpr.infiniteminds.articles;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.objects.Article;
import com.alphadvlpr.infiniteminds.utilities.ArticleListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ArticlesList extends AppCompatActivity {

    private RecyclerView listArticles;
    private SwipeRefreshLayout swipeEditArticles;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private ArticleListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_list);

        listArticles = findViewById(R.id.listEditArticles);
        swipeEditArticles = findViewById(R.id.refreshEditArticles);

        swipeEditArticles.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { initArticlesList(); }
        });
        initArticlesList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initArticlesList();
    }

    private void initArticlesList(){
        mDatabase.collection("articles")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Article> articles = new ArrayList<>();

                        for(QueryDocumentSnapshot qds : queryDocumentSnapshots){ articles.add(qds.toObject(Article.class)); }

                        adapter = new ArticleListAdapter(ArticlesList.this, articles);
                        listArticles.setLayoutManager(new LinearLayoutManager(ArticlesList.this));
                        listArticles.setAdapter(adapter);
                    }
                });

        swipeEditArticles.setRefreshing(false);
    }
}

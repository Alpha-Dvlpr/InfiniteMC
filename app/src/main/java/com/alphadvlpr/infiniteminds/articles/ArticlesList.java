package com.alphadvlpr.infiniteminds.articles;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

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

/**
 * This class controls the articles list on the User's interface.
 *
 * @author AlphaDvlpr.
 */
public class ArticlesList extends AppCompatActivity {

    private RecyclerView listArticles;
    private SwipeRefreshLayout swipeEditArticles;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private ArticleListAdapter adapter;
    private ProgressBar progressBar;

    /**
     * This method initializes all the views on this Activity.
     *
     * @param savedInstanceState The previous saved state of the activity.
     * @author AlphaDvlpr.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_list);

        listArticles = findViewById(R.id.listEditArticles);
        swipeEditArticles = findViewById(R.id.refreshEditArticles);
        progressBar = findViewById(R.id.progressBar);

        swipeEditArticles.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initArticlesList();
            }
        });

        initArticlesList();
    }

    /**
     * If the activity was paused when it resumes it updates the articles list.
     *
     * @author AlphaDvlpr.
     */
    @Override
    protected void onResume() {
        super.onResume();
        initArticlesList();
    }

    /**
     * This method loads the data from the Firebase server to the list.
     *
     * @author AlphaDvlpr.
     */
    protected void initArticlesList() {
        progressBar.setVisibility(View.VISIBLE);

        mDatabase.collection("articles")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Article> articles = new ArrayList<>();

                        for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                            articles.add(qds.toObject(Article.class));
                        }

                        adapter = new ArticleListAdapter(ArticlesList.this, articles);
                        listArticles.setLayoutManager(new LinearLayoutManager(ArticlesList.this));
                        listArticles.setAdapter(adapter);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

        swipeEditArticles.setRefreshing(false);
    }
}

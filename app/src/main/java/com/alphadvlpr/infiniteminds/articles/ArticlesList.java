package com.alphadvlpr.infiniteminds.articles;

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
import com.alphadvlpr.infiniteminds.objects.Article;
import com.alphadvlpr.infiniteminds.utilities.ArticleListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
    private ArticleListAdapter adapter;
    private ProgressBar progressBar;
    private final int QUERY_LIMIT = 15;
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;
    private ArrayList<Article> articles;
    private LinearLayoutManager layout;

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

        Query query = FirebaseFirestore.getInstance()
                .collection("articles")
                .limit(QUERY_LIMIT);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                articles = new ArrayList<>();

                for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                    articles.add(qds.toObject(Article.class));
                }

                adapter = new ArticleListAdapter(ArticlesList.this, articles);
                layout = new LinearLayoutManager(ArticlesList.this);
                listArticles.setLayoutManager(layout);
                listArticles.setAdapter(adapter);
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

                            makeToast("Loading more articles...");

                            Query nextQuery = FirebaseFirestore.getInstance()
                                    .collection("articles")
                                    .startAfter(lastVisible)
                                    .limit(QUERY_LIMIT);

                            nextQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                                        articles.add(qds.toObject(Article.class));
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

                listArticles.addOnScrollListener(onScrollListener);
            }
        });

        swipeEditArticles.setRefreshing(false);
    }

    /**
     * Method to show a Toast notification on the current view.
     *
     * @param msg The message to be displayed.
     * @author AlphaDvlpr.
     */
    private void makeToast(String msg) {
        Toast.makeText(ArticlesList.this, msg, Toast.LENGTH_SHORT).show();
    }
}

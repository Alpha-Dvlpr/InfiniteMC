package com.alphadvlpr.infiniteminds.navigation;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.objects.Article;
import com.alphadvlpr.infiniteminds.utilities.ArticleAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This class manages the Home Fragment.
 *
 * @author AlphaDvlpr.
 */
public class HomeFragment extends Fragment {

    private RecyclerView listMain;
    private SwipeRefreshLayout swipeMainArticles;
    private Context context;
    private ProgressBar progressBar;
    private final int QUERY_LIMIT = 15;
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;
    private ArrayList<Article> articles;
    private LinearLayoutManager layout;
    private ArticleAdapter adapter;

    /**
     * This method loads a custom view into a container to show it to the user
     *
     * @param inflater           The tool to place the view inside the container.
     * @param container          The container where the view will be displayed.
     * @param savedInstanceState The previous state of the activity if it was saved.
     * @return Returns the view to be loaded.
     * @author AlphaDvlpr.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        swipeMainArticles = view.findViewById(R.id.refreshMain);
        listMain = view.findViewById(R.id.mainList);
        progressBar = view.findViewById(R.id.progressBar);

        context = getContext();

        initMainList();
        setActions();

        return view;
    }

    /**
     * This method sets the actions for the buttons of the view.
     *
     * @author AlphaDvlpr.
     */
    protected void setActions() {
        swipeMainArticles.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initMainList();
            }
        });
    }

    /**
     * This method loads the data from the Firebase server to the list.
     *
     * @author AlphaDvlpr.
     */
    private void initMainList() {
        progressBar.setVisibility(View.VISIBLE);

        Query query = FirebaseFirestore.getInstance()
                .collection("articles")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(QUERY_LIMIT);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                articles = new ArrayList<>();

                for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                    articles.add(qds.toObject(Article.class));
                }

                adapter = new ArticleAdapter(context, articles);
                layout = new LinearLayoutManager(context);
                listMain.setLayoutManager(layout);
                listMain.setAdapter(adapter);
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
                                    .orderBy("date", Query.Direction.DESCENDING)
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

                listMain.addOnScrollListener(onScrollListener);
            }
        });

        swipeMainArticles.setRefreshing(false);
    }

    /**
     * Method to show a Toast notification on the current view.
     *
     * @param msg The message to be displayed.
     * @author AlphaDvlpr.
     */
    private void makeToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}

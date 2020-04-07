package com.alphadvlpr.infiniteminds.navigation;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.objects.Article;
import com.alphadvlpr.infiniteminds.utilities.ArticleAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
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

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = mDatabase.collection("articles");
    private RecyclerView listMain;
    private SwipeRefreshLayout swipeMainArticles;
    private Context context;

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
        collectionReference
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Article> dataMainList = new ArrayList<>();

                        for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                            dataMainList.add(qds.toObject(Article.class));
                        }

                        listMain.setLayoutManager(new LinearLayoutManager(context));
                        ArticleAdapter adapter = new ArticleAdapter(context, dataMainList);
                        listMain.setAdapter(adapter);
                    }
                });

        swipeMainArticles.setRefreshing(false);
    }
}

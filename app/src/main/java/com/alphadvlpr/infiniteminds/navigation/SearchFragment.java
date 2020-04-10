package com.alphadvlpr.infiniteminds.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.objects.Article;
import com.alphadvlpr.infiniteminds.objects.Category;
import com.alphadvlpr.infiniteminds.utilities.ArticleAdapter;
import com.alphadvlpr.infiniteminds.utilities.StringProcessor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This class manages the Search Fragment.
 *
 * @author AlphaDvlpr.
 */
public class SearchFragment extends Fragment {

    private RecyclerView listSearch;
    private SearchView searchView;
    private EditText searchEditText;
    private ChipGroup listChips;
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        listSearch = view.findViewById(R.id.searchList);
        searchView = view.findViewById(R.id.searchField);
        listChips = view.findViewById(R.id.searchCategoriesChipContainer);
        searchEditText = searchView.findViewById(R.id.search_src_text);
        progressBar = view.findViewById(R.id.progressBar);

        context = getContext();
        Activity activity = getActivity();
        layout = new LinearLayoutManager(context);

        if (activity != null) {
            Intent prev = activity.getIntent();
            String prevCategory = prev.getStringExtra("category");

            if (prevCategory != null) {
                searchByChip(prevCategory.toLowerCase());
                searchEditText.setText(prevCategory.toUpperCase());
            }
        }

        progressBar.setVisibility(View.INVISIBLE);

        initFavCategoriesList();
        setActions();

        return view;
    }

    /**
     * This method sets the actions for the buttons of the view.
     *
     * @author AlphaDvlpr.
     */
    protected void setActions() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String[] splitQuery = StringProcessor.removeSpecial(StringProcessor.removeAccents(query)).toLowerCase().split(" ");
                ArrayList<String> keywords = new ArrayList<>();
                for (String s : splitQuery) {
                    if (!s.equals("")) {
                        keywords.add(s);
                    }
                }

                if (splitQuery.length >= 10) {
                    makeToast("MAXIMUM 10 WORDS");
                } else {
                    searchByName(keywords);
                    searchByCategory(keywords);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    /**
     * This method loads the data from the Firebase server to the list.
     *
     * @author AlphaDvlpr.
     */
    private void initFavCategoriesList() {
        searchEditText.setTextColor(Color.BLACK);
        searchEditText.setHintTextColor(Color.DKGRAY);

        Query query = FirebaseFirestore.getInstance()
                .collection("categories")
                .orderBy("name", Query.Direction.ASCENDING);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                    Category aux = qds.toObject(Category.class);
                    final Chip chip = new Chip(context);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(5, 5, 5, 5);

                    chip.setLayoutParams(layoutParams);
                    chip.setText(aux.getName().toUpperCase());
                    chip.setTextColor(Color.BLACK);
                    chip.setChipBackgroundColor(context.getResources().getColorStateList(R.color.colorAccent));
                    chip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            searchByChip(chip.getText().toString().toLowerCase());
                            searchEditText.setText(chip.getText().toString());
                        }
                    });

                    listChips.addView(chip);
                }
            }
        });
    }

    /**
     * This method fragment_search for articles by its name.
     *
     * @param s A String ArrayList containing all the words from the query which will be used to
     *          fetch all the articles from Firebase that contain one or more of those on its
     *          keywords array.
     * @author AlphaDvlpr.
     */
    private void searchByName(final ArrayList<String> s) {
        progressBar.setVisibility(View.VISIBLE);

        Query query = FirebaseFirestore.getInstance()
                .collection("articles")
                .whereArrayContainsAny("keywords", s)
                .orderBy("title", Query.Direction.ASCENDING)
                .limit(QUERY_LIMIT);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    makeToast("NO ARTICLES FOUND");
                } else {
                    articles = new ArrayList<>();

                    for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                        Article entry = qds.toObject(Article.class);

                        if (!checkExist(entry)) {
                            articles.add(entry);
                        }
                    }

                    adapter = new ArticleAdapter(context, articles);
                    listSearch.setLayoutManager(layout);
                    listSearch.setAdapter(adapter);
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
                                        .whereArrayContainsAny("keywords", s)
                                        .orderBy("title", Query.Direction.ASCENDING)
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

                    listSearch.addOnScrollListener(onScrollListener);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("SEARCH_ERROR", "onFailure: " + e.getMessage());
            }
        });
    }

    /**
     * This method fragment_search for articles by its categories.
     *
     * @param s A String ArrayList containing all the words from the query which will be used to
     *          fetch all the articles from Firebase that contain one or more of those on its
     *          categories array.
     * @author AlphaDvlpr.
     */
    private void searchByCategory(final ArrayList<String> s) {
        progressBar.setVisibility(View.VISIBLE);

        Query query = FirebaseFirestore.getInstance()
                .collection("articles")
                .whereArrayContainsAny("categories", s)
                .orderBy("title", Query.Direction.ASCENDING)
                .limit(QUERY_LIMIT);


        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    makeToast("NO CATEGORIES FOUND");
                } else {
                    articles = new ArrayList<>();

                    for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                        Article entry = qds.toObject(Article.class);

                        if (!checkExist(entry)) {
                            articles.add(entry);
                        }
                    }

                    adapter = new ArticleAdapter(context, articles);
                    listSearch.setLayoutManager(layout);
                    listSearch.setAdapter(adapter);
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
                                        .whereArrayContainsAny("categories", s)
                                        .orderBy("title", Query.Direction.ASCENDING)
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

                    listSearch.addOnScrollListener(onScrollListener);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("CATEGORY_ERROR", "onFailure: " + e.getMessage());
            }
        });
    }

    /**
     * This method fragment_search for articles that match a category given by a clicked chip.
     *
     * @param s A String which will be used to fetch all the articles from Firebase that contain
     *          contain that category on its categories array.
     * @author AlphaDvlpr.
     */
    private void searchByChip(final String s) {
        progressBar.setVisibility(View.VISIBLE);

        Query query = FirebaseFirestore.getInstance()
                .collection("articles")
                .whereArrayContains("categories", s)
                .orderBy("title", Query.Direction.ASCENDING)
                .limit(QUERY_LIMIT);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    makeToast("THERE ARE NO ARTICLES WITH '" + s + "' CATEGORY");
                } else {
                    articles = new ArrayList<>();

                    for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                        articles.add(qds.toObject(Article.class));
                    }

                    adapter = new ArticleAdapter(context, articles);
                    listSearch.setLayoutManager(layout);
                    listSearch.setAdapter(adapter);
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
                                        .whereArrayContains("categories", s)
                                        .orderBy("title", Query.Direction.ASCENDING)
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

                    listSearch.addOnScrollListener(onScrollListener);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("CHIP_ERROR", "onFailure: " + e.getMessage());
            }
        });
    }

    /**
     * This method checks if the ArrayList contains an Article or not. This is used for preventing
     * duplicate articles on the list because when a fragment_search is made the app checks by name and by
     * category at the same time, so an article may match two or more times depending on the number
     * of words that the query contains.
     *
     * @param actual The Article which is going to be checked.
     * @return Returns <code>TRUE</code> if the ArrayList contains the given Article and returns
     * <code>FALSE</code> if not.
     * @author AlphaDvlpr.
     */
    private boolean checkExist(Article actual) {
        for (int i = 0; i < articles.size(); i++) {
            if (articles.get(i).getTitle().equals(actual.getTitle())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Method to show a Toast notification on the current view.
     *
     * @param msg The message to be displayed.
     * @author AlphaDvlpr.
     */
    protected void makeToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}

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
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private ArrayList<Article> dataSearchList;
    private EditText searchEditText;
    private ChipGroup listChips;
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        listSearch = view.findViewById(R.id.searchList);
        searchView = view.findViewById(R.id.searchField);
        listChips = view.findViewById(R.id.searchCategoriesChipContainer);
        searchEditText = searchView.findViewById(R.id.search_src_text);

        initFavCategoriesList();
        setActions();

        context = getContext();
        Activity activity = getActivity();

        if (activity != null) {
            Intent prev = activity.getIntent();
            String prevCategory = prev.getStringExtra("category");

            if (prevCategory != null) {
                searchByChip(prevCategory.toLowerCase());
                searchEditText.setText(prevCategory.toUpperCase());
            }
        }

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

        mDatabase.collection("categories")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                            chip.setChipBackgroundColor(getResources().getColorStateList(R.color.colorAccent));
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
        mDatabase.collection("articles")
                .whereArrayContainsAny("keywords", s)
                .orderBy("title", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            makeToast("NO ARTICLES FOUND");
                        } else {
                            dataSearchList = new ArrayList<>();

                            for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                                Article entry = qds.toObject(Article.class);

                                if (!checkExist(entry)) {
                                    dataSearchList.add(entry);
                                }
                            }

                            ArticleAdapter adapter = new ArticleAdapter(context, dataSearchList);
                            listSearch.setLayoutManager(new LinearLayoutManager(context));
                            listSearch.setAdapter(adapter);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
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
        mDatabase.collection("articles")
                .whereArrayContainsAny("categories", s)
                .orderBy("title", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            makeToast("NO CATEGORIES FOUND");
                        } else {
                            dataSearchList = new ArrayList<>();

                            for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                                Article entry = qds.toObject(Article.class);

                                if (!checkExist(entry)) {
                                    dataSearchList.add(entry);
                                }
                            }

                            ArticleAdapter adapter = new ArticleAdapter(context, dataSearchList);
                            listSearch.setLayoutManager(new LinearLayoutManager(context));
                            listSearch.setAdapter(adapter);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
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
        mDatabase.collection("articles")
                .whereArrayContains("categories", s)
                .orderBy("title", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            makeToast("THERE ARE NO ARTICLES WITH '" + s + "' CATEGORY");
                        } else {
                            dataSearchList = new ArrayList<>();

                            for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                                dataSearchList.add(qds.toObject(Article.class));
                            }

                            ArticleAdapter adapter = new ArticleAdapter(context, dataSearchList);
                            listSearch.setLayoutManager(new LinearLayoutManager(context));
                            listSearch.setAdapter(adapter);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
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
        for (int i = 0; i < dataSearchList.size(); i++) {
            if (dataSearchList.get(i).getTitle().equals(actual.getTitle())) {
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

package com.alphadvlpr.infiniteminds.navigation;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.objects.Article;
import com.alphadvlpr.infiniteminds.objects.Category;
import com.alphadvlpr.infiniteminds.utilities.ArticleAdapter;
import com.alphadvlpr.infiniteminds.utilities.StringProcessor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class Search extends AppCompatActivity {

    private RecyclerView listSearch;
    private SearchView searchView;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private ArrayList<Article> dataSearchList;
    private BottomAppBar mainBar;
    private ActionMenuItemView itemUsers, itemTrending, itemInfo;
    private EditText searchEditText;
    private ChipGroup listChips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        listSearch = findViewById(R.id.searchList);
        searchView = findViewById(R.id.searchField);
        itemInfo = findViewById(R.id.menuAbout);
        itemTrending = findViewById(R.id.menuTrend);
        itemUsers = findViewById(R.id.menuUsers);
        mainBar = findViewById(R.id.searchBottomAppBar);
        listChips = findViewById(R.id.searchCategoriesChipContainer);
        searchEditText = searchView.findViewById(R.id.search_src_text);

        initFavCategoriesList();
        setActions();

        Intent prev = getIntent();
        String prevCategory = prev.getStringExtra("category");

        if(prevCategory != null){
            searchByChip(prevCategory.toLowerCase());
            searchEditText.setText(prevCategory.toUpperCase());
        }
    }

    private void setActions(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String[] splitQuery = StringProcessor.removeSpecial(StringProcessor.removeAccents(query)).toLowerCase().split(" ");
                ArrayList<String> keywords = new ArrayList<>();
                for(String s : splitQuery) { if(!s.equals("")) { keywords.add(s); } }

                if(splitQuery.length >= 10){ makeToast("MAXIMUM 10 WORDS"); }
                else{
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

        itemTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Search.this, Trending.class));
                finish();
            }
        });

        mainBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Search.this, Home.class));
                finish();
            }
        });

        itemUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Search.this, Login.class));
                finish();
            }
        });

        itemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Search.this, Info.class));
                finish();
            }
        });
    }

    private void initFavCategoriesList(){
        searchEditText.setTextColor(Color.BLACK);
        searchEditText.setHintTextColor(Color.DKGRAY);

        mDatabase.collection("categories")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot qds : queryDocumentSnapshots){
                            Category aux = qds.toObject(Category.class);
                            final Chip chip = new Chip(Search.this);

                            LinearLayout.LayoutParams layoutParams= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(5,5,5,5);

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

    private void searchByName(final ArrayList<String> s){
        mDatabase.collection("articles")
                .whereArrayContainsAny("keywords", s)
                .orderBy("title", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty()){ makeToast("NO ARTICLES FOUND"); }
                        else{
                            dataSearchList = new ArrayList<>();

                            for(QueryDocumentSnapshot qds : queryDocumentSnapshots){
                                Article entry = qds.toObject(Article.class);

                                if(!checkExist(entry)){ dataSearchList.add(entry); }
                            }

                            ArticleAdapter adapter = new ArticleAdapter(Search.this, dataSearchList);
                            listSearch.setLayoutManager(new LinearLayoutManager(Search.this));
                            listSearch.setAdapter(adapter);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { Log.d("SEARCH_ERROR", "onFailure: " + e.getMessage()); }
                });
    }

    private void searchByCategory(final ArrayList<String> s){
        mDatabase.collection("articles")
                .whereArrayContainsAny("categories", s)
                .orderBy("title", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty()){ makeToast("NO CATEGORIES FOUND"); }
                        else{
                            dataSearchList = new ArrayList<>();

                            for(QueryDocumentSnapshot qds : queryDocumentSnapshots){
                                Article entry = qds.toObject(Article.class);

                                if(!checkExist(entry)){ dataSearchList.add(entry); }
                            }

                            ArticleAdapter adapter = new ArticleAdapter(Search.this, dataSearchList);
                            listSearch.setLayoutManager(new LinearLayoutManager(Search.this));
                            listSearch.setAdapter(adapter);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { Log.d("CATEGORY_ERROR", "onFailure: " + e.getMessage()); }
                });
    }

    private void searchByChip(final String s){
        mDatabase.collection("articles")
                .whereArrayContains("categories", s)
                .orderBy("title", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty()){ makeToast("THERE ARE NO ARTICLES WITH '" + s + "' CATEGORY"); }
                        else{
                            dataSearchList = new ArrayList<>();

                            for(QueryDocumentSnapshot qds : queryDocumentSnapshots){ dataSearchList.add(qds.toObject(Article.class)); }

                            ArticleAdapter adapter = new ArticleAdapter(Search.this, dataSearchList);
                            listSearch.setLayoutManager(new LinearLayoutManager(Search.this));
                            listSearch.setAdapter(adapter);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { Log.d("CHIP_ERROR", "onFailure: " + e.getMessage()); }
                });
    }

    private boolean checkExist(Article actual){
        for(int i = 0; i < dataSearchList.size(); i++){ if(dataSearchList.get(i).getTitle().equals(actual.getTitle())){ return true; } }

        return false;
    }

    private void makeToast(String msg){ Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }
}

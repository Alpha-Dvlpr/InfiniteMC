package com.alphadvlpr.infiniteminds.categories;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.objects.Category;
import com.alphadvlpr.infiniteminds.utilities.CategoryListAdapter;
import com.alphadvlpr.infiniteminds.utilities.StringProcessor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This class manages the categories list inside the registered user's interface.
 *
 * @author AlphaDvlpr
 */
public class CategoriesList extends AppCompatActivity {

    private FloatingActionButton fab;
    private ArrayList<Category> categories;
    private CategoryListAdapter adapter;
    private RecyclerView categoriesList;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    /**
     * The right-to-left and left-to-right swipe delete gesture for the RecyclerView.
     */
    protected ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Category aux = categories.get(viewHolder.getAdapterPosition());

            categories.remove(aux);
            adapter.notifyDataSetChanged();

            mDatabase.collection("categories").document(aux.getName().toLowerCase()).delete();
        }
    };

    /**
     * The main function that executes when the view loads for the first time.
     *
     * @param savedInstanceState The previous state of the current activity if saved.
     * @author AlphaDvlpr.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_list);

        fab = findViewById(R.id.fab);
        categoriesList = findViewById(R.id.listCategories);

        setActions();
        initCategoriesList();
    }

    /**
     * Method that sets the actions for the buttons inside this view.
     *
     * @author AlphaDvlpr.
     */
    protected void setActions() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout = new LinearLayout(view.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(10, 10, 10, 10);

                final EditText categoryName = new EditText(view.getContext());
                categoryName.setHint("Category name");

                layout.addView(categoryName);

                final AlertDialog dialog = new AlertDialog.Builder(CategoriesList.this)
                        .setMessage("TYPE THE CATEGORY NAME")
                        .setView(layout)
                        .setCancelable(false)
                        .setPositiveButton("ADD", null)
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button buttonPos = (dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                        buttonPos.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String name = StringProcessor.removeAccents(categoryName.getText().toString());

                                if (name.isEmpty()) {
                                    makeToast("YOU MUST TYPE A NAME");
                                } else {
                                    mDatabase.collection("categories").document(name.toLowerCase())
                                            .set(new Category(name.toLowerCase()))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    makeToast("CATEGORY ADDED SUCCESSFULLY");
                                                    adapter.notifyDataSetChanged();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    makeToast("ERROR WHILE ADDING THE CATEGORY");
                                                }
                                            });
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                });
                dialog.show();
            }
        });
    }

    /**
     * Method that connects to the Firebase database for fetching all the data which collection is
     * 'categories' and then it loads the information to the RecyclerView.
     *
     * @author AlphaDvlpr.
     */
    protected void initCategoriesList() {
        mDatabase.collection("categories")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        categories = new ArrayList<>();

                        for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                            categories.add(qds.toObject(Category.class));
                        }

                        categoriesList.setLayoutManager(new LinearLayoutManager(CategoriesList.this));
                        adapter = new CategoryListAdapter(CategoriesList.this, categories);
                        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(categoriesList);
                        categoriesList.setAdapter(adapter);
                    }
                });
    }

    /**
     * Method to show a Toast notification on the current view.
     *
     * @param msg The message to be displayed.
     * @author AlphaDvlpr.
     */
    protected void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}

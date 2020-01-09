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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class CategoriesList extends AppCompatActivity {

    private FloatingActionButton fab;
    private ArrayList<Category> categories;
    private CategoryListAdapter adapter;
    private RecyclerView categoriesList;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_list);

        fab = findViewById(R.id.fab);
        categoriesList = findViewById(R.id.listCategories);

        setActions();
        initCategoriesList();
    }

    private void setActions(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout = new LinearLayout(view.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(10, 10, 10, 10);

                final EditText categoryName = new EditText(view.getContext());
                categoryName.setHint("Nombre de la categoría");

                layout.addView(categoryName);

                final AlertDialog dialog = new AlertDialog.Builder(CategoriesList.this)
                        .setMessage("INTRODUCE EL NOMBRE DE LA CATEGORÍA")
                        .setView(layout)
                        .setCancelable(false)
                        .setPositiveButton("AÑADIR", null)
                        .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
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

                                if(name.isEmpty()){ makeToast("Debes introducir un nombre"); }
                                else{
                                    mDatabase.collection("categories").document(name.toLowerCase())
                                            .set(new Category(name.toLowerCase()))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    makeToast("Categoría añadida con éxito");
                                                    adapter.notifyDataSetChanged();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) { makeToast("Error al añadir la categoría"); }
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

    private void initCategoriesList(){
        mDatabase.collection("categories")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        categories = new ArrayList<>();

                        for(QueryDocumentSnapshot qds : queryDocumentSnapshots){ categories.add(qds.toObject(Category.class)); }

                        categoriesList.setLayoutManager(new LinearLayoutManager(CategoriesList.this));
                        adapter = new CategoryListAdapter(CategoriesList.this, categories);
                        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(categoriesList);
                        categoriesList.setAdapter(adapter);
                    }
                });
    }

    private void makeToast(String msg){ Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) { return false; }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Category aux = categories.get(viewHolder.getAdapterPosition());

            categories.remove(aux);
            adapter.notifyDataSetChanged();

            mDatabase.collection("categories").document(aux.getName().toLowerCase()).delete();
        }
    };
}

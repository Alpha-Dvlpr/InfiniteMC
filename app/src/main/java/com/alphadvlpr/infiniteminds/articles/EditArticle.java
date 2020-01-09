package com.alphadvlpr.infiniteminds.articles;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.objects.Article;
import com.alphadvlpr.infiniteminds.objects.Image;
import com.alphadvlpr.infiniteminds.utilities.ImageDecoder;
import com.alphadvlpr.infiniteminds.utilities.ImageListAdapter;
import com.alphadvlpr.infiniteminds.utilities.StringProcessor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class EditArticle extends AppCompatActivity {

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private static final int SELECT_FILE = 1;
    private ArrayList<Image> images = new ArrayList<>();
    private RecyclerView imagesList;
    private String reference = "";
    private ImageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.edit_article);

        final EditText editCategoryOne = findViewById(R.id.editArticleCategoryOne),
                editCategoryTwo = findViewById(R.id.editArticleCategoryTwo),
                editCategoryThree = findViewById(R.id.editArticleCategoryThree),
                editCategoryFour = findViewById(R.id.editArticleCategoryFour),
                editCategoryFive = findViewById(R.id.editArticleCategoryFive),
                editCategorySix = findViewById(R.id.editArticleCategorySix),
                editContent = findViewById(R.id.editArticleContent),
                editLinkOne = findViewById(R.id.editArticleDownloadOne),
                editLinkTwo = findViewById(R.id.editArticleDownloadTwo),
                editLinkThree = findViewById(R.id.editArticleDownloadThree),
                editLinkFour = findViewById(R.id.editArticleDownloadFour),
                editTitle = findViewById(R.id.editArticleTitle);
        final TextView textContent = findViewById(R.id.eac);
        final Button buttonUpdate = findViewById(R.id.editArticleUpdateButton),
                buttonCancel = findViewById(R.id.editArticleCancelButton),
                buttonDelete = findViewById(R.id.editArticleDeleteArticle),
                buttonUploadPhoto = findViewById(R.id.editArticleTakePicture);
        imagesList = findViewById(R.id.editArticleImagesList);
        final ProgressBar progressBar = findViewById(R.id.editArticleProgress);

        progressBar.setVisibility(View.GONE);

        final Intent prev = getIntent();

        ArrayList<String> imagesStringBitmap = prev.getStringArrayListExtra("images"),
                oldCategories = prev.getStringArrayListExtra("categories"),
                oldDownloadLinks = prev.getStringArrayListExtra("downloadURL");

        editTitle.setText(prev.getStringExtra("title"));
        editContent.setText(prev.getStringExtra("content"));

        for(String key : Objects.requireNonNull(prev.getStringArrayListExtra("keywords"))){ reference += "_" + key; }

        for(String s : Objects.requireNonNull(imagesStringBitmap)){ images.add(new Image(s)); }

        imagesList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ImageListAdapter(images);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(imagesList);
        imagesList.setAdapter(adapter);

        assert oldDownloadLinks != null;
        editLinkOne.setText(oldDownloadLinks.get(0));
        editLinkTwo.setText(oldDownloadLinks.get(1));
        editLinkThree.setText(oldDownloadLinks.get(2));
        editLinkFour.setText(oldDownloadLinks.get(3));

        assert oldCategories != null;
        editCategoryOne.setText(oldCategories.get(0));
        editCategoryTwo.setText(oldCategories.get(1));
        editCategoryThree.setText(oldCategories.get(2));
        editCategoryFour.setText(oldCategories.get(3));
        editCategoryFive.setText(oldCategories.get(4));
        editCategorySix.setText(oldCategories.get(5));

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snack = Snackbar.make(v, "¿ELIMINAR EL ARTÍCULO?", Snackbar.LENGTH_LONG);
                snack.setAction("SI", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabase.collection("articles").document(reference)
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            makeToast("Documento eliminado con éxito");
                                            finish();
                                        } else{ makeToast("No se ha podido eliminar el artículo"); }
                                    }
                                });
                    }
                }).show();
            }
        });

        buttonUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(images.size() == 2){ makeToast("Solo se permiten 2 imagenes"); }
                else{
                    String[] types = {"image/jpg", "image/jpeg"};

                    Intent camera = new Intent();
                    camera.setType("image/*");
                    camera.setAction(Intent.ACTION_GET_CONTENT);
                    camera.putExtra(Intent.EXTRA_MIME_TYPES, types);
                    startActivityForResult(Intent.createChooser(camera, "Selecciona una imagen."), SELECT_FILE);
                }
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newContent = editContent.getText().toString();

                if(newContent.isEmpty()){ textContent.setTextColor(Color.RED); }
                else{ textContent.setTextColor(Color.BLACK); }

                ArrayList<String>newDownloadURL = new ArrayList<>();
                newDownloadURL.add(StringProcessor.checkAndFixLink(editLinkOne.getText().toString()));
                newDownloadURL.add(StringProcessor.checkAndFixLink(editLinkTwo.getText().toString()));
                newDownloadURL.add(StringProcessor.checkAndFixLink(editLinkThree.getText().toString()));
                newDownloadURL.add(StringProcessor.checkAndFixLink(editLinkFour.getText().toString()));

                ArrayList<String> newCategories = new ArrayList<>();
                newCategories.add(StringProcessor.removeSpecial(StringProcessor.removeAccents(editCategoryOne.getText().toString().toLowerCase())));
                newCategories.add(StringProcessor.removeSpecial(StringProcessor.removeAccents(editCategoryTwo.getText().toString().toLowerCase())));
                newCategories.add(StringProcessor.removeSpecial(StringProcessor.removeAccents(editCategoryThree.getText().toString().toLowerCase())));
                newCategories.add(StringProcessor.removeSpecial(StringProcessor.removeAccents(editCategoryFour.getText().toString().toLowerCase())));
                newCategories.add(StringProcessor.removeSpecial(StringProcessor.removeAccents(editCategoryFive.getText().toString().toLowerCase())));
                newCategories.add(StringProcessor.removeSpecial(StringProcessor.removeAccents(editCategorySix.getText().toString().toLowerCase())));

                ArrayList<String> newImagesStringBitmap = new ArrayList<>();
                for(Image i : images){ newImagesStringBitmap.add(i.getStringBitmap()); }

                if(newContent.isEmpty()){ makeToast("Los campos con '*' son obligatorios."); }
                else{
                    buttonUpdate.setVisibility(View.GONE);
                    buttonCancel.setVisibility(View.GONE);
                    buttonDelete.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    Article updatedArticle = new Article(prev.getStringExtra("title"),
                            newContent, newImagesStringBitmap,
                            Timestamp.now(), newCategories, newDownloadURL,
                            prev.getLongExtra("visits", -1),
                            prev.getStringArrayListExtra("keywords"));

                    mDatabase.collection("articles").document(reference)
                            .set(updatedArticle)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    makeToast("DOCUMENTO ACTUALIZADO CON EXITO");
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    buttonUpdate.setVisibility(View.VISIBLE);
                                    buttonCancel.setVisibility(View.VISIBLE);
                                    buttonDelete.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);

                                    makeToast("ERROR AL ACTUALIZAR EL DOCUMENTO");
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent imageReturnedIntent){
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Uri selectedImage;

        if (requestCode == SELECT_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                selectedImage = imageReturnedIntent.getData();
                String selectedPath = Objects.requireNonNull(selectedImage).getPath();

                if (requestCode == SELECT_FILE && selectedPath != null) {
                    InputStream imageStream = null;

                    try { imageStream = getContentResolver().openInputStream(selectedImage); }
                    catch (FileNotFoundException e) { e.printStackTrace(); }

                    Bitmap bmp = BitmapFactory.decodeStream(imageStream);
                    Bitmap scaled = ImageDecoder.getScaledBitmap(bmp, 480, 360);

                    images.add(new Image(ImageDecoder.encode(scaled)));
                }
            }
        }

        imagesList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ImageListAdapter(images);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(imagesList);
        imagesList.setAdapter(adapter);
    }

    private void makeToast(String msg){ Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) { return false; }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            images.remove(viewHolder.getAdapterPosition());
            adapter.notifyDataSetChanged();
        }
    };
}

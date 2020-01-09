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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class NewArticle extends AppCompatActivity {

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private static final int SELECT_FILE = 1;
    private ArrayList<Image> images = new ArrayList<>();
    private ArrayList<String> imagesStringBitmap = new ArrayList<>();
    private RecyclerView imagesList;
    private ImageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.new_article);

        final EditText editCategoryOne = findViewById(R.id.newArticleCategoryOne),
                editCategoryTwo = findViewById(R.id.newArticleCategoryTwo),
                editCategoryThree = findViewById(R.id.newArticleCategoryThree),
                editCategoryFour = findViewById(R.id.newArticleCategoryFour),
                editCategoryFive = findViewById(R.id.newArticleCategoryFive),
                editCategorySix = findViewById(R.id.newArticleCategorySix),
                editContent = findViewById(R.id.newArticleContent),
                editLinkOne = findViewById(R.id.newArticleDownloadOne),
                editLinkTwo = findViewById(R.id.newArticleDownloadTwo),
                editLinkThree = findViewById(R.id.newArticleDownloadThree),
                editLinkFour = findViewById(R.id.newArticleDownloadFour),
                editTitle = findViewById(R.id.newArticleTitle);
        final TextView textTitle = findViewById(R.id.natt),
                textContent = findViewById(R.id.nact);
        final Button buttonPublish = findViewById(R.id.newArticlePublish),
                buttonCancel = findViewById(R.id.newArticleCancel),
                buttonUploadPhoto = findViewById(R.id.newArticleTakePicture);
        imagesList = findViewById(R.id.newArticleImagesList);
        final ProgressBar progressBar = findViewById(R.id.newArticleProgress);

        progressBar.setVisibility(View.GONE);

        final Intent prev = getIntent();

        buttonPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString();
                String content = editContent.getText().toString();

                ArrayList<String>downloadURL = new ArrayList<>();
                downloadURL.add(StringProcessor.checkAndFixLink(editLinkOne.getText().toString()));
                downloadURL.add(StringProcessor.checkAndFixLink(editLinkTwo.getText().toString()));
                downloadURL.add(StringProcessor.checkAndFixLink(editLinkThree.getText().toString()));
                downloadURL.add(StringProcessor.checkAndFixLink(editLinkFour.getText().toString()));

                ArrayList<String> categories = new ArrayList<>();
                categories.add(StringProcessor.removeSpecial(StringProcessor.removeAccents(editCategoryOne.getText().toString().toLowerCase())));
                categories.add(StringProcessor.removeSpecial(StringProcessor.removeAccents(editCategoryTwo.getText().toString().toLowerCase())));
                categories.add(StringProcessor.removeSpecial(StringProcessor.removeAccents(editCategoryThree.getText().toString().toLowerCase())));
                categories.add(StringProcessor.removeSpecial(StringProcessor.removeAccents(editCategoryFour.getText().toString().toLowerCase())));
                categories.add(StringProcessor.removeSpecial(StringProcessor.removeAccents(editCategoryFive.getText().toString().toLowerCase())));
                categories.add(StringProcessor.removeSpecial(StringProcessor.removeAccents(editCategorySix.getText().toString().toLowerCase())));

                String originalTitle = editTitle.getText().toString().toLowerCase();
                String[] splitTitle = StringProcessor.removeSpecial(StringProcessor.removeAccents(originalTitle.toLowerCase())).split(" ");
                ArrayList<String> keywords = new ArrayList<>();
                for(String s : splitTitle) { if(!s.equals("")) { keywords.add(s); } }

                for(Image i : images){ imagesStringBitmap.add(i.getStringBitmap()); }

                if(title.isEmpty()){ textTitle.setTextColor(Color.RED); }
                else{ textTitle.setTextColor(Color.BLACK); }

                if(content.isEmpty()){ textContent.setTextColor(Color.RED); }
                else{ textContent.setTextColor(Color.BLACK); }

                if(title.isEmpty() || content.isEmpty()){ makeToast("Los campos con '*' son obligatorios."); }
                else{
                    buttonCancel.setVisibility(View.GONE);
                    buttonPublish.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    Article article = new Article(title, content, imagesStringBitmap, Timestamp.now(), categories, downloadURL, 0L, keywords);

                    String reference = "";
                    for(String key : keywords){ reference += "_" + key; }

                    mDatabase.collection("articles").document(reference)
                            .set(article)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    makeToast("DOCUMENTO CREADO CON EXITO");
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    buttonCancel.setVisibility(View.VISIBLE);
                                    buttonPublish.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                    makeToast("ERROR AL CREAR EL DOCUMENTO");
                                }
                            });

                    mDatabase.collection("users").document(Objects.requireNonNull(prev.getStringExtra("email")))
                            .update("published", FieldValue.increment(1));
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
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

    public void makeToast(String msg){ Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }

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

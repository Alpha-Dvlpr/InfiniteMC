package com.alphadvlpr.infiniteminds.navigation;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.utilities.ImageDecoder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This class manages how the information is shown when an article is opened from the RecyclerViews
 * on Home, Trending and Search.
 *
 * @author AlphaDvlpr.
 */
public class Content extends AppCompatActivity {

    private ArrayList<String> downloadURL;
    private int categoriesCounter = 0;
    private boolean notSecond = false;
    private boolean notThird = false;
    private boolean notFourth = false;
    private Intent receivedIntent;
    private Button mainDownloadButton;
    private Button secondDownloadButton;
    private Button thirdDownloadButton;
    private Button fourthDownloadButton;
    private ImageView image;
    private LinearLayout imagesContainer;
    private ChipGroup chipGroup;
    private CardView contentContainer;
    private CardView otherContainer;
    private CardView chipContainer;
    private CardView imageContainer;
    private TextView content;
    private TextView visits;
    private TextView title;

    /**
     * This method initializes all the views on this Activity.
     *
     * @param savedInstanceState The previous saved state of the activity.
     * @author AlphaDvlpr.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);

        mainDownloadButton = findViewById(R.id.downloadButton);
        secondDownloadButton = findViewById(R.id.secondDownloadButton);
        thirdDownloadButton = findViewById(R.id.thirdDownloadButton);
        fourthDownloadButton = findViewById(R.id.fourthDownloadButton);
        image = findViewById(R.id.imageContent);
        content = findViewById(R.id.articleContent);
        visits = findViewById(R.id.visitsCounter);
        title = findViewById(R.id.contentTitle);
        otherContainer = findViewById(R.id.otherLinksContainer);
        imageContainer = findViewById(R.id.imageContainer);
        contentContainer = findViewById(R.id.contentContainer);
        chipContainer = findViewById(R.id.chipContainer);
        chipGroup = findViewById(R.id.chipCategories);
        imagesContainer = findViewById(R.id.otherImagesContainer);
        receivedIntent = getIntent();
        AdView adView = findViewById(R.id.contentAd);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        setInformationToView();
    }

    /**
     * This method gets the information from the intent and displays it on the correct view.
     *
     * @author AlphaDvlpr.
     */
    protected void setInformationToView() {
        long selectedDownloads = receivedIntent.getLongExtra("visits", -1);
        ArrayList<String> imagesList = receivedIntent.getStringArrayListExtra("image");
        ArrayList<String> selectedCategories = receivedIntent.getStringArrayListExtra("categories");
        String selectedContent = receivedIntent.getStringExtra("content");
        String selectedTitle = receivedIntent.getStringExtra("title");
        downloadURL = receivedIntent.getStringArrayListExtra("downloadURL");

        title.setText(selectedTitle);
        visits.setText("Visits: " + selectedDownloads);

        if (Objects.requireNonNull(imagesList).isEmpty()) {
            imageContainer.setVisibility(View.GONE);
            imagesContainer.setVisibility(View.GONE);
        } else {
            image.setImageBitmap(ImageDecoder.decode(imagesList.get(0)));

            final float SCALE = getResources().getDisplayMetrics().density;

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (190 * SCALE));
            lp.setMargins(10, 10, 10, 10);

            if (imagesList.size() == 1) {
                imagesContainer.setVisibility(View.GONE);
            } else {
                imagesContainer.setVisibility(View.VISIBLE);

                for (int i = 1; i < imagesList.size(); i++) {
                    Bitmap imageBitmap = ImageDecoder.decode(imagesList.get(i));

                    ImageView newImage = new ImageView(Content.this);
                    newImage.setScaleType(ImageView.ScaleType.FIT_XY);
                    newImage.setLayoutParams(lp);
                    newImage.setImageBitmap(imageBitmap);

                    imagesContainer.addView(newImage);
                }
            }
        }

        if (Objects.equals(selectedContent, "")) {
            contentContainer.setVisibility(View.GONE);
            mainDownloadButton.setVisibility(View.GONE);
        } else {
            content.setText(selectedContent);
        }

        for (String s : Objects.requireNonNull(selectedCategories)) {
            if (!s.equals("")) {
                categoriesCounter++;
            }
        }

        if (categoriesCounter == 0) {
            chipContainer.setVisibility(View.GONE);
        } else {
            Chip aux;

            for (String s : selectedCategories) {
                if (!s.isEmpty()) {
                    aux = new Chip(this);
                    aux.setText(s.toUpperCase());
                    aux.setTextColor(Color.BLACK);
                    aux.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

                    final Chip finalAux = aux;
                    aux.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent toCategorySearch = new Intent(Content.this, MainActivity.class);
                            toCategorySearch.putExtra("target", "SEARCH");
                            toCategorySearch.putExtra("category", finalAux.getText().toString().toLowerCase());
                            startActivity(toCategorySearch);
                        }
                    });

                    chipGroup.addView(aux);
                }
            }
        }

        for (int i = 0; i < downloadURL.size(); i++) {
            if (downloadURL.get(0).equals("")) {
                mainDownloadButton.setVisibility(View.GONE);
            } else {
                mainDownloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startDownload(downloadURL.get(0));
                    }
                });
            }

            if (downloadURL.get(1).equals("")) {
                secondDownloadButton.setVisibility(View.GONE);
                notSecond = true;
            } else {
                secondDownloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startDownload(downloadURL.get(1));
                    }
                });
            }

            if (downloadURL.get(2).equals("")) {
                thirdDownloadButton.setVisibility(View.GONE);
                notThird = true;
            } else {
                thirdDownloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startDownload(downloadURL.get(2));
                    }
                });
            }

            if (downloadURL.get(3).equals("")) {
                fourthDownloadButton.setVisibility(View.GONE);
                notFourth = true;
            } else {
                fourthDownloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startDownload(downloadURL.get(3));
                    }
                });
            }
        }

        if (notSecond && notThird && notFourth) {
            otherContainer.setVisibility(View.GONE);
        }
    }

    /**
     * This method opens the browser to start a download with the given URL.
     *
     * @param url The URL that contains a downloadable object.
     * @author AlphaDvlpr.
     */
    protected void startDownload(String url) {
        Uri uri = Uri.parse(url);
        Intent openBrowser = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(openBrowser);
    }
}

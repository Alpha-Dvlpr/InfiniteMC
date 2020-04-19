package com.alphadvlpr.infiniteminds.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.navigation.Content;
import com.alphadvlpr.infiniteminds.objects.Article;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom adapter for Trending RecyclerView.
 *
 * @author AlphaDvlpr.
 */
public class TrendingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int LIST_AD_DELTA = 10;
    private static final int CONTENT = 0;
    private static final int AD = 1;
    private List<Article> articles;
    private Context context;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private int lastPosition = -1;

    /**
     * This method initializes the context and the list for the adapter to work.
     *
     * @param context  The activity's context.
     * @param articles The ArrayList where the articles will be held.
     * @author AlphaDvlpr.
     */
    public TrendingListAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.articles = articles;
    }

    /**
     * This method creates a new ViewHolder for every item on the list depending on the type of
     * View that has to be shown at a certain position. The position is calculated automatically.
     *
     * @param parent   The parent containing the RecyclerView.
     * @param viewType The type of view to be shown.
     * @return Returns the proper type of view for the actual position.
     * @author AlphaDvlpr.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == CONTENT) {
            return new ArticleViewHolder(inflateResource(parent, R.layout.item_article_big));
        } else {
            return new AdViewHolder(inflateResource(parent, R.layout.item_ad_trending));
        }
    }

    /**
     * This method bind the ArrayList to the RecyclerView with the given layout for every position.
     * This also sets the listener for the articles containers.
     *
     * @param holder   The type of View for a certain position.
     * @param position The current position of the article on the RecyclerView.
     *                 This is given automatically.
     * @author AlphaDvlpr.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == CONTENT) {
            ArticleViewHolder articleHolder = (ArticleViewHolder) holder;
            Article article = articles.get(getRealPosition(position));

            final String title = article.getTitle();
            final String content = article.getContent();
            final ArrayList<String> images = article.getImages();
            final ArrayList<String> downloadURLs = article.getDownloadURL();
            final ArrayList<String> categories = article.getCategories();
            final ArrayList<String> keywords = article.getKeywords();
            final Long visits = article.getVisits();

            if (images.isEmpty()) {
                articleHolder.image.setVisibility(View.GONE);
            } else {
                articleHolder.image.setImageBitmap(ImageDecoder.decode(images.get(0)));
            }

            articleHolder.title.setText(title);
            articleHolder.visits.setText("Visitas: " + visits);
            articleHolder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String reference = "";
                    for (String s : keywords) {
                        reference += "_" + s;
                    }

                    Intent toContent = new Intent(context, Content.class);
                    toContent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    toContent.putExtra("title", title);
                    toContent.putExtra("content", content);
                    toContent.putExtra("visits", visits);
                    toContent.putStringArrayListExtra("downloadURL", downloadURLs);
                    toContent.putStringArrayListExtra("categories", categories);
                    toContent.putExtra("numberOfImages", images.size());

                    if (images.size() > 2) {
                        ArrayList<String> imageIDs = new ArrayList<>();
                        SharedPreferences preferences = context.getSharedPreferences(reference, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();

                        for (int i = 0; i < images.size(); i++) {
                            String currentImage = images.get(i);
                            String id = "image" + i;

                            editor.putString(id, currentImage);
                            imageIDs.add(id);
                        }

                        toContent.putExtra("reference", reference);
                        toContent.putStringArrayListExtra("image", imageIDs);
                        editor.apply();
                    } else {
                        toContent.putStringArrayListExtra("image", images);
                    }

                    mDatabase.collection("articles").document(reference)
                            .update("visits", FieldValue.increment(1));

                    context.startActivity(toContent);
                }
            });

            articleHolder.container.setAnimation(AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top));
            lastPosition = position;

        } else {
            AdViewHolder adHolder = (AdViewHolder) holder;

            AdRequest adRequest = new AdRequest.Builder().build();
            if (adRequest != null && adHolder.adView != null) {
                adHolder.adView.loadAd(adRequest);
            }
        }
    }

    /**
     * This method calculates the total number of items the RecyclerView will have.
     *
     * @return Returns the number of articles the ArrayList has plus the number of ads that will
     * be shown between articles.
     * @author AlphaDvlpr.
     */
    @Override
    public int getItemCount() {
        int additionalContent = 0;
        if (articles.size() > 0 && LIST_AD_DELTA > 0 && articles.size() > LIST_AD_DELTA) {
            additionalContent = articles.size() / LIST_AD_DELTA;
        }
        return articles.size() + additionalContent;
    }

    /**
     * This method returns a view depending on the position it will occupy on the RecyclerView.
     *
     * @return Returns the view to be shown at the given position.
     * @author AlphaDvlpr.
     */
    @Override
    public int getItemViewType(int position) {
        if (position > 0 && (position % LIST_AD_DELTA == 0)) {
            return AD;
        }
        return CONTENT;
    }

    /**
     * This method loads the view to the RecyclerView.
     *
     * @param parent     The view that will contain the item.
     * @param resourceId The ID of the item to be displayed.
     * @return Returns the inflater depending on the ID given.
     * @author AlphaDvlpr.
     */
    private View inflateResource(ViewGroup parent, int resourceId) {
        return LayoutInflater.from(parent.getContext()).inflate(resourceId, parent, false);
    }

    /**
     * This method gets the real position of an article of a view depending if it is an ad or an article.
     *
     * @param position The position to be occupied on the RecyclerView.
     * @return Returns the position the item will occupy on the RecyclerView.
     * @author AlphaDvlpr.
     */
    private int getRealPosition(int position) {
        if (LIST_AD_DELTA == 0) {
            return position;
        } else {
            return position - position / LIST_AD_DELTA;
        }
    }

    /**
     * Custom class for all the articles.
     *
     * @author AlphaDvlpr.
     */
    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, visits;
        CardView container;

        ArticleViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.itemArticleCardView);
            image = itemView.findViewById(R.id.itemArticleImage);
            title = itemView.findViewById(R.id.itemArticleTitle);
            visits = itemView.findViewById(R.id.itemArticleVisits);
        }
    }

    /**
     * Custom class for the ads.
     *
     * @author AlphaDvlpr.
     */
    static class AdViewHolder extends RecyclerView.ViewHolder {
        AdView adView;
        CardView container;

        AdViewHolder(@NonNull View itemView) {
            super(itemView);

            adView = itemView.findViewById(R.id.itemAdContent);
            container = itemView.findViewById(R.id.itemAdCardView);
        }
    }
}

package com.alphadvlpr.infiniteminds.utilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Article> articles;
    private static final int LIST_AD_DELTA = 5, CONTENT = 0, AD = 1;
    private Context context;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private int lastPosition = -1;

    public ArticleAdapter(Context context, List<Article> articles){
        this.context = context;
        this.articles = articles;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        if(viewType == CONTENT){ return new ArticleViewHolder(inflateResource(parent, R.layout.item_article_big)); }
        else { return new AdViewHolder(inflateResource(parent, R.layout.item_ad)); }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if(viewType == CONTENT){
            ArticleViewHolder articleHolder = (ArticleViewHolder) holder;
            final Article article = articles.get(getRealPosition(position));

            if(article.getImages().isEmpty()){ articleHolder.image.setVisibility(View.GONE); }
            else{ articleHolder.image.setImageBitmap(ImageDecoder.decode(article.getImages().get(0))); }

            articleHolder.title.setText(article.getTitle());
            articleHolder.visits.setText("Visitas: " + article.getVisits());
            articleHolder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("title", article.getTitle());
                    b.putString("content", article.getContent());
                    b.putStringArrayList("image", article.getImages());
                    b.putLong("visits", article.getVisits());
                    b.putStringArrayList("downloadURL", article.getDownloadURL());
                    b.putStringArrayList("categories", article.getCategories());
                    b.putString("date", convertDate(article.getDate()));

                    Intent toContent = new Intent(context, Content.class);
                    toContent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    String reference = "";
                    for(String s : Objects.requireNonNull(article).getKeywords()){ reference += "_" + s; }

                    mDatabase.collection("articles").document(reference)
                            .update("visits", FieldValue.increment(1));

                    toContent.putExtras(b);
                    context.startActivity(toContent);
                }
            });
            articleHolder.container.setAnimation(AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top));
            lastPosition = position;

        }else{
            AdViewHolder adHolder = (AdViewHolder) holder;

            AdRequest adRequest = new AdRequest.Builder().build();
            if (adRequest != null && adHolder.adView != null){ adHolder.adView.loadAd(adRequest); }
        }
    }

    @Override
    public int getItemCount(){
        int additionalContent = 0;
        if (articles.size() > 0 && LIST_AD_DELTA > 0 && articles.size() > LIST_AD_DELTA) { additionalContent = articles.size() / LIST_AD_DELTA; }
        return articles.size() + additionalContent;
    }

    @Override
    public int getItemViewType(int position){
        if (position > 0 && (position % LIST_AD_DELTA == 0)) { return AD; }
        return CONTENT;
    }

    private View inflateResource(ViewGroup parent, int resourceId){ return LayoutInflater.from(parent.getContext()).inflate(resourceId, parent, false); }

    private int getRealPosition(int position) {
        if (LIST_AD_DELTA == 0) { return position; }
        else { return position - position / LIST_AD_DELTA; }
    }

    private String convertDate(Timestamp t){
        String day = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(t.toDate());
        String hour = new SimpleDateFormat("HH:mm", Locale.FRANCE).format(t.toDate());

        return  day + " a las " + hour;
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title, visits;
        CardView container;

        ArticleViewHolder(@NonNull View itemView){
            super(itemView);

            container = itemView.findViewById(R.id.itemArticleCardView);
            image = itemView.findViewById(R.id.itemArticleImage);
            title = itemView.findViewById(R.id.itemArticleTitle);
            visits = itemView.findViewById(R.id.itemArticleVisits);
        }
    }

    class AdViewHolder extends RecyclerView.ViewHolder{
        AdView adView;
        CardView container;

        AdViewHolder(@NonNull View itemView){
            super(itemView);

            adView = itemView.findViewById(R.id.itemAdContent);
            container = itemView.findViewById(R.id.itemAdCardView);
        }
    }
}

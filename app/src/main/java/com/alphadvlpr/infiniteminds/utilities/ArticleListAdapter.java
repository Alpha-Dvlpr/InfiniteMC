package com.alphadvlpr.infiniteminds.utilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.articles.EditArticle;
import com.alphadvlpr.infiniteminds.objects.Article;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArticleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Article> articles;
    private Context context;

    public ArticleListAdapter(Context context, ArrayList<Article> articles){
        this.articles = articles;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){ return new ArticleListAdapter.ArticleViewHolder(inflateResource(parent)); }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position){
        ArticleListAdapter.ArticleViewHolder articleHolder = (ArticleListAdapter.ArticleViewHolder) holder;
        final Article article = articles.get(position);

        articleHolder.title.setText(article.getTitle());
        articleHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEditArticle = new Intent(context, EditArticle.class);
                toEditArticle.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Bundle b = new Bundle();
                b.putString("title", Objects.requireNonNull(article).getTitle());
                b.putString("content", article.getContent());
                b.putStringArrayList("images", article.getImages());
                b.putLong("visits", article.getVisits());
                b.putStringArrayList("downloadURL", article.getDownloadURL());
                b.putStringArrayList("categories", article.getCategories());
                b.putStringArrayList("keywords", article.getKeywords());

                toEditArticle.putExtras(b);
                context.startActivity(toEditArticle);
            }
        });
    }

    @Override
    public int getItemCount(){ return articles.size(); }

    private View inflateResource(ViewGroup parent){ return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article_small, parent, false); }

    class ArticleViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        CardView container;

        ArticleViewHolder(@NonNull View itemView){
            super(itemView);
            container = itemView.findViewById(R.id.itemArticleSmallContainer);
            title = itemView.findViewById(R.id.itemArticleSmallTitle);
        }
    }
}

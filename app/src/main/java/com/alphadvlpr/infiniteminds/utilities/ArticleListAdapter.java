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

/**
 * Custom adapter for the RecyclerViews that contain the articles to be edited. The only difference
 * with {@link HomeListAdapter} is that this one doesn't contain ads.
 *
 * @author AlphaDvlpr.
 */
public class ArticleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Article> articles;
    private Context context;

    /**
     * This method initializes the context and the list for the adapter to work.
     *
     * @param context  The activity's context.
     * @param articles The ArrayList where the articles will be held.
     * @author AlphaDvlpr.
     */
    public ArticleListAdapter(Context context, ArrayList<Article> articles) {
        this.articles = articles;
        this.context = context;
    }

    /**
     * This method creates a new ViewHolder for every item on the list.
     * The position is calculated automatically.
     *
     * @param parent   The parent containing the RecyclerView.
     * @param viewType The type of view to be shown.
     * @return Returns the proper type of view for the actual position.
     * @author AlphaDvlpr.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArticleViewHolder(inflateResource(parent));
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

    /**
     * This method calculates the total number of items the RecyclerView will have.
     *
     * @return Returns the number of articles the ArrayList has.
     * @author AlphaDvlpr.
     */
    @Override
    public int getItemCount() {
        return articles.size();
    }

    /**
     * This method loads the view to the RecyclerView.
     *
     * @param parent The view that will contain the item.
     * @return Returns the inflater with the needed layout.
     * @author AlphaDvlpr.
     */
    private View inflateResource(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article_small, parent, false);
    }

    /**
     * Custom class for all the articles.
     *
     * @author AlphaDvlpr.
     */
    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        CardView container;

        ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.itemArticleSmallContainer);
            title = itemView.findViewById(R.id.itemArticleSmallTitle);
        }
    }
}

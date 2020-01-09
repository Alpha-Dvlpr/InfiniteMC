package com.alphadvlpr.infiniteminds.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.objects.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Category> categories;
    private Context context;

    public CategoryListAdapter(Context context, ArrayList<Category> categories){
        this.categories = categories;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){ return new CategoryListAdapter.CategoryViewHolder(inflateResource(parent)); }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position){
        CategoryListAdapter.CategoryViewHolder userHolder = (CategoryListAdapter.CategoryViewHolder) holder;
        final Category category = categories.get(position);

        userHolder.name.setText(category.getName().toUpperCase());
    }

    @Override
    public int getItemCount(){ return categories.size(); }

    private View inflateResource(ViewGroup parent){ return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false); }

    class CategoryViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        CardView container;

        CategoryViewHolder(@NonNull View itemView){
            super(itemView);

            container = itemView.findViewById(R.id.itemUserContainer);
            name = itemView.findViewById(R.id.itemCategoryTitle);
        }
    }
}

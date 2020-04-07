package com.alphadvlpr.infiniteminds.utilities;

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

/**
 * Custom adapter for the RecyclerViews that contain the categories.
 *
 * @author AlphaDvlpr.
 */
public class CategoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Category> categories;

    /**
     * This method initializes the context and the list for the adapter to work.
     *
     * @param categories The ArrayList where the categories will be held.
     * @author AlphaDvlpr.
     */
    public CategoryListAdapter(ArrayList<Category> categories) {
        this.categories = categories;
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
        return new CategoryViewHolder(inflateResource(parent));
    }

    /**
     * This method bind the ArrayList to the RecyclerView with the given layout for every position.
     *
     * @param holder   The type of View for a certain position.
     * @param position The current position of the article on the RecyclerView.
     *                 This is given automatically.
     * @author AlphaDvlpr.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CategoryListAdapter.CategoryViewHolder userHolder = (CategoryListAdapter.CategoryViewHolder) holder;
        final Category category = categories.get(position);

        userHolder.name.setText(category.getName().toUpperCase());
    }

    /**
     * This method calculates the total number of items the RecyclerView will have.
     *
     * @return Returns the number of categories the ArrayList has.
     * @author AlphaDvlpr.
     */
    @Override
    public int getItemCount() {
        return categories.size();
    }

    /**
     * This method loads the view to the RecyclerView.
     *
     * @param parent The view that will contain the item.
     * @return Returns the inflater with the needed layout.
     * @author AlphaDvlpr.
     */
    private View inflateResource(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
    }

    /**
     * Custom class for all the categories.
     *
     * @author AlphaDvlpr.
     */
    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        CardView container;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.itemUserContainer);
            name = itemView.findViewById(R.id.itemCategoryTitle);
        }
    }
}

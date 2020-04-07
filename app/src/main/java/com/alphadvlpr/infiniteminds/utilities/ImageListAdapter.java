package com.alphadvlpr.infiniteminds.utilities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.objects.Image;

import java.util.ArrayList;

/**
 * Custom adapter for the RecyclerViews that contain images.
 *
 * @author AlphaDvlpr.
 */
public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {

    private ArrayList<Image> images;

    /**
     * This method initializes the context and the list for the adapter to work.
     *
     * @param images The ArrayList where the images will be held.
     * @author AlphaDvlpr.
     */
    public ImageListAdapter(ArrayList<Image> images) {
        this.images = images;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.image, parent, false);
        return new ViewHolder(view);
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image image = images.get(position);
        String imageStringBitmap = image.getStringBitmap();
        holder.image.setScaleType(ImageView.ScaleType.FIT_XY);
        holder.image.setImageBitmap(ImageDecoder.decode(imageStringBitmap));
    }

    /**
     * This method calculates the total number of items the RecyclerView will have.
     *
     * @return Returns the number of images the ArrayList has.
     * @author AlphaDvlpr.
     */
    @Override
    public int getItemCount() {
        return images.size();
    }

    /**
     * Custom class for all the images.
     *
     * @author AlphaDvlpr.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }
}

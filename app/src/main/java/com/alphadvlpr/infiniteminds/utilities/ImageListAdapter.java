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

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {

    private ArrayList<Image> images;

    public ImageListAdapter(ArrayList<Image> images){ this.images = images; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        Image image  = images.get(position);
        String imageStringBitmap = image.getStringBitmap();
        holder.image.setScaleType(ImageView.ScaleType.FIT_XY);
        holder.image.setImageBitmap(ImageDecoder.decode(imageStringBitmap));
    }

    @Override
    public int getItemCount(){ return images.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        ViewHolder(@NonNull View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }
}

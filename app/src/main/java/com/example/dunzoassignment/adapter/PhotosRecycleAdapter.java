package com.example.dunzoassignment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.dunzoassignment.R;
import com.example.dunzoassignment.networking.model.Photo;
import com.example.dunzoassignment.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class PhotosRecycleAdapter extends RecyclerView.Adapter<PhotosRecycleAdapter.BaseViewHolder> {

    private RequestManager requestManager;
    private List<Photo> photos;
    private Photo refreshItem;
    private Photo emptyItem;

    public PhotosRecycleAdapter(RequestManager requestManager) {
        this.requestManager = requestManager;
        photos = new ArrayList<>();
        refreshItem = new Photo();
        refreshItem.setId("footer");
        refreshItem.setSpanSize(12);
        refreshItem.setViewType(Constants.VIEW_TYPE.REFRESH_ITEM);

        emptyItem = new Photo();
        emptyItem.setId("empty");
        emptyItem.setSpanSize(12);
        emptyItem.setViewType(Constants.VIEW_TYPE.EMPTY_ITEM);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        BaseViewHolder baseViewHolder;
        switch (viewType) {
            default:
            case Constants.VIEW_TYPE.DEFAULT_ITEM: {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
                baseViewHolder = new PhotoViewHolder(v);
                break;
            }
            case Constants.VIEW_TYPE.REFRESH_ITEM: {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
                baseViewHolder = new RefreshViewHolder(v);
                break;
            }
            case Constants.VIEW_TYPE.EMPTY_ITEM: {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty, parent, false);
                baseViewHolder = new RefreshViewHolder(v);
                break;
            }
        }

        return baseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case Constants.VIEW_TYPE.DEFAULT_ITEM : {
                Photo photo = photos.get(position);
                String url = "https://farm" + photo.getFarm() + ".staticflickr.com/" + photo.getServer() + "/" + photo.getId() + "_" + photo.getSecret() + "_m.jpg";
                requestManager.load(url)
                        .into(((PhotoViewHolder)holder).image);
                break;
            }
            case Constants.VIEW_TYPE.REFRESH_ITEM: {
                break;
            }
            case Constants.VIEW_TYPE.EMPTY_ITEM: {
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return photos.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void addPhotos(List<Photo> photoList, int page) {
        if (page == 1 && !photos.isEmpty()) {
            int size = photos.size();
            photos.clear();
            notifyItemRangeRemoved(0, size);
        }

        for (Photo photo : photoList) {
            photos.add(photo);
            notifyItemInserted(photos.indexOf(photo));
        }
    }

    public void addRefreshLayout() {
        removeRefreshLayout();
        photos.add(refreshItem);
        notifyItemInserted(photos.size() - 1);
    }

    public void removeRefreshLayout() {
        int index = photos.indexOf(refreshItem);
        photos.remove(refreshItem);
        if (index != -1) {
            notifyItemRemoved(index);
        }
    }

    public void addEmptyItem() {
        int size = photos.size();
        photos.clear();
        notifyItemRangeRemoved(0, size);
        photos.add(emptyItem);
        notifyItemInserted(photos.size() - 1);
    }

    public int getSpanSize(int position) {
        return photos.get(position).getSpanSize();
    }

    public static class PhotoViewHolder extends BaseViewHolder {
        AppCompatImageView image;
        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.photo_image);
        }
    }

    public static class RefreshViewHolder extends BaseViewHolder {
        RefreshViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class EmptyResultViewHolder extends BaseViewHolder {
        EmptyResultViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    abstract static class BaseViewHolder extends RecyclerView.ViewHolder {
        BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

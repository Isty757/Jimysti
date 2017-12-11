package com.ro.sapientia.ms.jimysty.sapiadvertiser.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Drako on 13-Nov-17.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    public boolean isClickable = true;

    private List<String> mProfilePictures = Collections.emptyList();
    private List<String> mData = Collections.emptyList();
    private List<String> mDescription = Collections.emptyList();
    private List<String> mImages = Collections.emptyList();
    private List<String> mId = Collections.emptyList();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private LongItemClickListener mLongItemClickListener;

    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, List<String> profilPictures, List<String> title , List<String> description , List<String> images , List<String> id) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = title;
        this.mDescription = description;
        this.mImages = images;
        this.mProfilePictures = profilPictures;
        this.mId = id;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_recyclerview_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String animal = mData.get(position);
        String description = mDescription.get(position);
        String image = mImages.get(position);
        String profilePicture = mProfilePictures.get(position);
        holder.myTextView.setText(animal);
        holder.myDescriptionView.setText(description);
        if (profilePicture.matches("")){
            profilePicture = "https://firebasestorage.googleapis.com/v0/b/sapiadvertiser-a59e8.appspot.com/o/profileplaceholder.jpg?alt=media&token=f59384c2-a18d-4858-b93d-325f00fddfad";
        }

        Glide.with(mInflater.getContext()).load(profilePicture)
                .thumbnail(0.5f)
                .crossFade()
                .error(R.drawable.profileplaceholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.myProfilePictureImageView);


        Glide.with(mInflater.getContext()).load(image)
                .thumbnail(0.5f)
                .crossFade()
                .error(R.drawable.noimage)
                .override(500,300)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.myImageView);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private CircleImageView myProfilePictureImageView;
        private TextView myTextView;
        private TextView myDescriptionView;
        private ImageView myImageView;

        private ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tv_title);
            myDescriptionView =  itemView.findViewById(R.id.tv_some_detail);
            myImageView = itemView.findViewById(R.id.iv_advertisement_image);
            myProfilePictureImageView = itemView.findViewById(R.id.iv_profile_picture);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
        @Override
        public boolean onLongClick(View view) {
            if (mLongItemClickListener != null) mLongItemClickListener.onLongItemClick(view, getAdapterPosition());
            return true;
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mId.get(id);
    }
    public String getDescription(int id){
        return mDescription.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
    public void setLongClickListener (LongItemClickListener longClickListener){
        this.mLongItemClickListener = longClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public interface LongItemClickListener{
        boolean onLongItemClick(View view, int position);
    }
}
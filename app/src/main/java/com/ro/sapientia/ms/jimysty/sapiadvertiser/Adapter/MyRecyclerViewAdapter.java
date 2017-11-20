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

import com.ro.sapientia.ms.jimysty.sapiadvertiser.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * Created by Drako on 13-Nov-17.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<String> mData = Collections.emptyList();
    private List<String> mDescription = Collections.emptyList();
    private List<String> mImages = Collections.emptyList();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, List<String> title , List<String> description , List<String> images ) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = title;
        this.mDescription = description;
        this.mImages = images;

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
        holder.myTextView.setText(animal);
        holder.myDescriptionView.setText(description);
        new LongOperation().execute(image , holder);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView myTextView;
        private TextView myDescriptionView;
        private ImageView myImageView;

        private ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tv_title);
            myDescriptionView =  itemView.findViewById(R.id.tv_some_detail);
            myImageView = itemView.findViewById(R.id.iv_advertisement_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mData.get(id);
    }
    public String getDescription(int id){
        return mDescription.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    private static class LongOperation extends AsyncTask<Object, Void, Bitmap> {

        Bitmap bmp = null;
        ViewHolder holder = null;

        @Override
        protected Bitmap doInBackground(Object... params) {

            String urlImage = (String) params[0];
            holder = (ViewHolder) params[1];
            URL url = null;
            if (urlImage.matches("")){

            }
            else {
                try {
                    url = new URL(urlImage);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                try {
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            holder.myImageView.setImageBitmap(result);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
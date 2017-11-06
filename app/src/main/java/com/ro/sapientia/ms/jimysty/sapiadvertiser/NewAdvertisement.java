package com.ro.sapientia.ms.jimysty.sapiadvertiser;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Drako on 04-Nov-17.
 */

public class NewAdvertisement extends BasicActivity {

    private GridView gridView;
    private GridViewAdapter gridAdapter;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.new_advertisement);

        Button tmpButton = findViewById(R.id.bt_select_location);

        tmpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "android.intent.action.SEND_MULTIPLE"), 1);
            }
        });

        gridView = (GridView) findViewById(R.id.gridView);
        //gridAdapter = new GridViewAdapter(this, R.layout.grid_view_item, getData());
        //gridView.setAdapter(gridAdapter);
    }

    // Prepare some dummy data for gridview
    /*
    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        for (int i = 0; i < imgs.length(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));
            imageItems.add(new ImageItem(bitmap, "Image#" + i));
        }
        return imageItems;
    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            System.out.println("++data" + data.getClipData().getItemCount());// Get count of image here.

            System.out.println("++count" + data.getClipData().getItemCount());
            Uri selectedImage = data.getClipData().getItemAt(0).getUri();//As of now use static position 0 use as per itemcount.
            Bitmap bitmap = null;
            //        Uri selectedImage1 = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("+++ clipdate" + selectedImage);

            //ImageView imageView = (ImageView) findViewById(R.id.imgView);
            //imageView.setImageBitmap(bitmap);
            //        }
        }

    }

}

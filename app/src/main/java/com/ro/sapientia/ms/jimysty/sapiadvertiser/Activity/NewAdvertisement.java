package com.ro.sapientia.ms.jimysty.sapiadvertiser.Activity;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.ro.sapientia.ms.jimysty.sapiadvertiser.BasicActivity;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Adapter.GridViewAdapter;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Class.ImageItem;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.R;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.StaticMethods;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Drako on 04-Nov-17.
 */

public class NewAdvertisement extends BasicActivity {

    private GridView gridView;
    private GridViewAdapter gridAdapter;

    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.new_advertisement);

        Button tmpButton = findViewById(R.id.bt_add_images);

        tmpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });

        FloatingActionButton faButton = findViewById(R.id.fbt_add_event);
        faButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewAdvertisement.this.finish();
                //StaticMethods.goToListAdvertisementsActivity(NewAdvertisement.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){

                    Uri mImageUri=data.getData();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded  = cursor.getString(columnIndex);
                    cursor.close();

                    ArrayList<ImageItem> list = new ArrayList<ImageItem>();

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                    ImageItem imItem = new ImageItem(getScaledBitmap(bitmap,(float)0.5 ) , "Titulus");
                    list.add(imItem);

                    gridView = findViewById(R.id.gridView);
                    gridAdapter = new GridViewAdapter(this, R.layout.grid_view_item, list );
                    gridView.setAdapter(gridAdapter);

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();

                        ArrayList<ImageItem> list = new ArrayList<ImageItem>();

                        if (mClipData.getItemCount() >6){
                            Toast.makeText(this, "Maximum 6 images can be selected!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            for (int i = 0; i < mClipData.getItemCount(); i++) {

                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                mArrayUri.add(uri);
                                // Get the cursor
                                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                                // Move to first row
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                imageEncoded = cursor.getString(columnIndex);
                                imagesEncodedList.add(imageEncoded);
                                cursor.close();

                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                                ImageItem imItem = new ImageItem(getScaledBitmap(bitmap, (float) 0.5), "Titulus " + i);
                                list.add(imItem);
                            }
                            gridView = findViewById(R.id.gridView);
                            gridAdapter = new GridViewAdapter(this, R.layout.grid_view_item, list);
                            gridView.setAdapter(gridAdapter);

                            Log.d("LOG_TAG", "Selected Images" + mArrayUri.size());
                        }
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public Bitmap getScaledBitmap(Bitmap bitmap, float scale) {
        Integer originalHeight = bitmap.getHeight();
        Integer originalWidth = bitmap.getWidth();

        Integer requiredHeight = Math.round(originalHeight * scale);
        Integer requiredWidth = Math.round(originalWidth * scale);

        return Bitmap.createScaledBitmap(bitmap, requiredWidth, requiredHeight, true);
    }
}

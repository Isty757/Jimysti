package com.ro.sapientia.ms.jimysty.sapiadvertiser.Activity;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.BasicActivity;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Adapter.GridViewAdapter;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Class.Advertisement;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Class.ImageItem;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Class.User;
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

    private int PICK_IMAGE_MULTIPLE = 1;
    private String imageEncoded;
    private List<String> imagesEncodedList;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser currentFirebaseUser;
    private StorageReference mStorageRef;

    private User googleUser;

    private ArrayList<String> imagesList = new ArrayList<>();
    private ArrayList<Uri> imageURIs = new ArrayList<>();

    private static final String TAG = "NewAdvertisement";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_new_advertisement);


        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(NewAdvertisement.this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            googleUser = new User(personGivenName, personFamilyName, "0740227129", personPhoto.toString());
            //googleUser = new User(personGivenName, personFamilyName, "0740227129");
        }


        Button tmpButton = findViewById(R.id.bt_add_images);

        tmpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(NewAdvertisement.this, R.anim.button_click_animation));
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
                overridePendingTransition(R.anim.zoom_in,R.anim.zoom_in);

            }
        });
        mStorageRef = FirebaseStorage.getInstance().getReference();

        FloatingActionButton faButton = findViewById(R.id.fbt_add_event);
        faButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText title = findViewById(R.id.et_newTitle);
                final EditText description = findViewById(R.id.et_newDescription);

                if ((title.getText().toString().matches("")) || (description.getText().toString().matches(""))) {
                    Toast.makeText(NewAdvertisement.this,"Please fill all empty fields!", Toast.LENGTH_LONG).show();
                }
                else {
                    myRef = database.getReference(currentFirebaseUser.getUid());


                    //imagesList.add("https://firebasestorage.googleapis.com/v0/b/sapiadvertiser-a59e8.appspot.com/o/hirdetes.jpg?alt=media&token=b6e4e6aa-b46d-4cbc-8aa1-a400a9c1b60b");
                    //imagesList.add("https://firebasestorage.googleapis.com/v0/b/sapiadvertiser-a59e8.appspot.com/o/felveteli.jpg?alt=media&token=dd13af65-5abe-46ed-86a0-59ad58b60aa7");
                    //User m = new User("Barabas", "Levente", "0740227129");


                    Log.d("AZAZ2", "" + imageURIs.size());
                    final int[] cnt = {0};
                    for (int i = 0; i < imageURIs.size(); i++) {
                        mStorageRef = FirebaseStorage.getInstance().getReference(title.getText().toString()+i);
                        mStorageRef.putFile(imageURIs.get(i))
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get a URL to the uploaded content
                                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                        imagesList.add(downloadUrl.toString());
                                        Log.d("AZAZ2", "Itten igen pontoson");
                                        Toast.makeText(NewAdvertisement.this, "Rendben van", Toast.LENGTH_SHORT).show();
                                        Advertisement myAdvertisement;
                                        if( googleUser != null){
                                             myAdvertisement = new Advertisement(title.getText().toString(), description.getText().toString(), imagesList, googleUser);
                                        }
                                        else {
                                            googleUser = new User("","","","");
                                            myAdvertisement = new Advertisement(title.getText().toString(), description.getText().toString(), imagesList, googleUser);
                                        }


                                        //Advertisement myAdvertisement = new Advertisement(title.getText().toString(), description.getText().toString(), imagesList);
                                        myRef.child(title.getText().toString()).setValue(myAdvertisement);

                                        myRef = database.getReference("Advertisements");
                                        myRef.child(title.getText().toString()).setValue(myAdvertisement);

                                        if (cnt[0] == imageURIs.size()-1) {
                                            NewAdvertisement.this.finish();
                                            StaticMethods.goToListAdvertisementsActivity(NewAdvertisement.this);
                                        }
                                        Log.d(TAG, cnt[0]+"");
                                        cnt[0]++;
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        // ...
                                        Log.d("AZAZ2", exception.getMessage());
                                        Toast.makeText(NewAdvertisement.this, "nincs" + exception.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }



                }

            }
        });

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        //Toast.makeText(this, "" + currentFirebaseUser.getUid(), Toast.LENGTH_SHORT).show();

        database = FirebaseDatabase.getInstance();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.zoom_in,R.anim.zoom_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){

                    Uri mImageUri=data.getData();

                    imageURIs.add(mImageUri);
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded  = cursor.getString(columnIndex);
                    cursor.close();

                    ArrayList<ImageItem> list = new ArrayList<ImageItem>();

                    //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                    Bitmap bitmap = Bitmap.createScaledBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri), 120, 120, false);
                    ImageItem imItem = new ImageItem(bitmap , "Titulus");
                    list.add(imItem);

                    gridView = findViewById(R.id.gridView);
                    gridAdapter = new GridViewAdapter(this, R.layout.item_grid_view, list );
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

                                Bitmap bitmap = Bitmap.createScaledBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri), 120, 120, false);

                                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                                ImageItem imItem = new ImageItem(bitmap, "Titulus " + i);
                                list.add(imItem);
                                imageURIs.add(uri);
                            }
                            gridView = findViewById(R.id.gridView);
                            gridAdapter = new GridViewAdapter(this, R.layout.item_grid_view, list);
                            gridView.setAdapter(gridAdapter);

                            Log.d("LOG_TAG", "Selected Images" + mArrayUri.size());
                        }
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong" + e, Toast.LENGTH_LONG).show();
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

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}

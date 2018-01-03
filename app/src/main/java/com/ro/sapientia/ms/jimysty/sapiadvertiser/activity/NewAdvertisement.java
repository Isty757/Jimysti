package com.ro.sapientia.ms.jimysty.sapiadvertiser.activity;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
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

/**
 * Created by Drako on 04-Nov-17.
 */

public class NewAdvertisement extends BasicActivity {

    private GridView gridView;
    private GridViewAdapter gridAdapter;

    private int PICK_IMAGE_MULTIPLE = 1;

    private StorageReference mStorageRef;

    private User googleUser;

    private ArrayList<String> imagesList = new ArrayList<>();
    private ArrayList<Uri> imageURIs = new ArrayList<>();

    private static final String TAG = "NewAdvertisement";

    private int cnt = 0;

    /**
     * read data from textboxes and create a new advertisement
     * upload that advertisement to firebase
     * @param icicle
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_new_advertisement);

        ArrayList<ImageItem> list = new ArrayList<>();
        Bitmap icon = BitmapFactory.decodeResource(NewAdvertisement.this.getResources(), R.drawable.defaultgridview);
        list.add(new ImageItem(icon,"Advertisement Title"));
        list.add(new ImageItem(icon,"Advertisement Title"));
        list.add(new ImageItem(icon,"Advertisement Title"));
        gridView = findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.item_grid_view, list );
        gridView.setAdapter(gridAdapter);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(NewAdvertisement.this);
        if (acct != null) {
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            Uri personPhoto = acct.getPhotoUrl();
            if (personPhoto != null) {
                googleUser = new User(personGivenName, personFamilyName, "", personPhoto.toString(), personEmail);
            }

            myRef = database.getReference(currentFirebaseUser.getUid()).child("User").child("image");
            if (personPhoto != null) {
                myRef.setValue(personPhoto.toString());
            }
        }
        else{
            googleUser = new User("","","","","");
        }
        myRef = database.getReference(currentFirebaseUser.getUid()).child("User");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String firstName = (String) dataSnapshot.child("firstName").getValue();
                String lastName = (String) dataSnapshot.child("lastName").getValue();
                String mobileNumber = (String) dataSnapshot.child("mobileNumber").getValue();
                String email = (String) dataSnapshot.child("email").getValue();

                if (firstName!= null){
                    googleUser.setFirstName(firstName);
                }
                if (lastName!= null){
                    googleUser.setLastName(lastName);
                }
                if (mobileNumber!= null){
                    googleUser.setMobileNumber(mobileNumber);
                }
                if (email!= null){
                    googleUser.setEmail(email);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

                myRef = database.getReference("Advertisements");
                final String key =  myRef.push().getKey();
                myRef = database.getReference(currentFirebaseUser.getUid());

                for (int i = 0; i < imageURIs.size(); i++) {
                    mStorageRef = FirebaseStorage.getInstance().getReference(key + "/" +title.getText().toString()+i);
                    mStorageRef.putFile(imageURIs.get(i))
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get a URL to the uploaded content
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    if (downloadUrl != null) {
                                        imagesList.add(downloadUrl.toString());
                                    }
                                    Log.d("AZAZ2", "Itten igen pontoson");
                                    Toast.makeText(NewAdvertisement.this, "Rendben van", Toast.LENGTH_SHORT).show();
                                    Advertisement myAdvertisement;
                                    if( googleUser != null){
                                         myAdvertisement = new Advertisement(key,title.getText().toString(), description.getText().toString(), imagesList, googleUser);
                                    }
                                    else {
                                        googleUser = new User("","","","", "");
                                        myAdvertisement = new Advertisement(key,title.getText().toString(), description.getText().toString(), imagesList, googleUser);
                                    }
                                    myRef.child(key).setValue(myAdvertisement);
                                    myRef = database.getReference("Advertisements");
                                    myRef.child(key).setValue(myAdvertisement);

                                    if (cnt == imageURIs.size()-1) {
                                        NewAdvertisement.this.finish();
                                        StaticMethods.goToListAdvertisementsActivity(NewAdvertisement.this);
                                    }
                                    cnt++;
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.zoom_in,R.anim.zoom_out);
    }

    /**
     * after we selected an image or images this function give us a list of this pictures
     * @param requestCode what we gave it before
     * @param resultCode what the result is
     * @param data what we get from that intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
                // Get the Image from data
                if(data.getData()!=null){
                    imageURIs.clear();
                    Uri mImageUri=data.getData();

                    ArrayList<ImageItem> list = new ArrayList<>();

                    Bitmap bitmap = Bitmap.createScaledBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri), 120, 120, false);
                    ImageItem imItem = new ImageItem(bitmap , "Titulus");
                    list.add(imItem);
                    imageURIs.add(mImageUri);

                    gridView = findViewById(R.id.gridView);
                    gridAdapter = new GridViewAdapter(this, R.layout.item_grid_view, list );
                    gridView.setAdapter(gridAdapter);
                } else {
                    if (data.getClipData() != null) {
                        imageURIs.clear();
                        ClipData mClipData = data.getClipData();
                        ArrayList<ImageItem> list = new ArrayList<>();

                        if (mClipData.getItemCount() >6){
                            Toast.makeText(this, "Maximum 6 images can be selected!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            for (int i = 0; i < mClipData.getItemCount(); i++) {

                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();

                                Bitmap bitmap = Bitmap.createScaledBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri), 120, 120, false);

                                ImageItem imItem = new ImageItem(bitmap, "Titulus " + i);
                                list.add(imItem);
                                imageURIs.add(uri);
                            }
                            gridView = findViewById(R.id.gridView);
                            gridAdapter = new GridViewAdapter(this, R.layout.item_grid_view, list);
                            gridView.setAdapter(gridAdapter);

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
}

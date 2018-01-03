package com.ro.sapientia.ms.jimysty.sapiadvertiser.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.BasicActivity;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.R;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.StaticMethods;

import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by barth on 11/13/2017.
 */

public class Profile extends BasicActivity {

    boolean clicked = true;

    private EditText first;
    private EditText last;
    private EditText email;
    private EditText phone;
    private EditText passw;
    private EditText confirm;

    private CircleImageView profilePicture;

    /**
     * load the current users data to edit boxes
     * the user can modify this data
     * @param icicle
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_profile);

        FloatingActionButton logout = findViewById(R.id.fb_logout_profile);
        FloatingActionButton edit = findViewById(R.id.fb_edit_profile);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                StaticMethods.goToLoginScreenActivity(Profile.this);
                finish();
            }
        });
        first = findViewById(R.id.pr_first);
        last = findViewById(R.id.pr_last);
        email = findViewById(R.id.pr_email);
        phone = findViewById(R.id.pr_phone);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            first.setFocusableInTouchMode(clicked);
            last.setFocusableInTouchMode(clicked);
            email.setFocusableInTouchMode(clicked);
            phone.setFocusableInTouchMode(clicked);

            clicked = !clicked;

            if(clicked){
                String fname = first.getText().toString();
                String lname = last.getText().toString();
                String Email = email.getText().toString();
                String Phone = phone.getText().toString();

                myRef = null;
                if (currentFirebaseUser != null) {
                    myRef = database.getReference(currentFirebaseUser.getUid()).child("User");
                }

                HashMap<String, String> Map = new HashMap<>();

                Map.put("firstName",fname);
                Map.put("lastName",lname);
                Map.put("email",Email);
                Map.put("mobileNumber",Phone);

                if (myRef != null) {
                    myRef.setValue(Map);
                    Toast.makeText(Profile.this,"The changes have been saved",Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(Profile.this,"Now you can edit your profile!",Toast.LENGTH_SHORT).show();
            }
            }
        });

        profilePicture = findViewById(R.id.iv_image_profile);
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200);
            }
        });

        if (currentFirebaseUser != null) {
            myRef = database.getReference(currentFirebaseUser.getUid()).child("User");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String first_name = (String) dataSnapshot.child("firstName").getValue();
                    String last_name = (String) dataSnapshot.child("lastName").getValue();
                    String emailc = (String) dataSnapshot.child("email").getValue();
                    String phone_number = (String) dataSnapshot.child("mobileNumber").getValue();
                    String profilePictureString = (String) dataSnapshot.child("image").getValue();

                    first.setText(first_name);
                    last.setText(last_name);
                    email.setText(emailc);
                    phone.setText(phone_number);
                    TextView userName = findViewById(R.id.tv_name_profile);
                    String userNameString = first_name + " " + last_name;
                    userName.setText(userNameString);

                    if (!Profile.this.isDestroyed() ){
                        Glide.with(Profile.this).load(profilePictureString)
                                .thumbnail(0.5f)
                                .crossFade()
                                .error(R.drawable.profileplaceholder)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(profilePicture);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    //Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        StaticMethods.goToListAdvertisementsActivity(Profile.this);
        finish();
    }

    /**
     * get the selected picture
     * @param requestCode
     * @param resultCode
     * @param imageReturnedIntent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK) {
            if (requestCode == 200) {
                Uri selectedImageUri = imageReturnedIntent.getData();
                if (null != selectedImageUri) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = Bitmap.createScaledBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri), 120, 120, false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    CircleImageView profilePicture = findViewById(R.id.iv_image_profile);
                    profilePicture.setImageBitmap(bitmap);
                }
            }
        }
    }
}

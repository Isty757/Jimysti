package com.ro.sapientia.ms.jimysty.sapiadvertiser.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.Manifest;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.BasicActivity;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Class.User;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.R;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Adapter.ViewPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Drako on 30-Oct-17.
 */

public class AboutAdvertisement extends BasicActivity {

    ViewPager viewPager;
    ViewPagerAdapter adapter;

    private String[] images = {
            "https://firebasestorage.googleapis.com/v0/b/sapiadvertiser-a59e8.appspot.com/o/hirdetes.jpg?alt=media&token=b6e4e6aa-b46d-4cbc-8aa1-a400a9c1b60b",
            "https://firebasestorage.googleapis.com/v0/b/sapiadvertiser-a59e8.appspot.com/o/felveteli.jpg?alt=media&token=dd13af65-5abe-46ed-86a0-59ad58b60aa7",
            "https://firebasestorage.googleapis.com/v0/b/sapiadvertiser-a59e8.appspot.com/o/sapi.jpg?alt=media&token=2b32d318-d4f3-443c-b5e7-d2f7b469013d"
    };

    private final int REQUEST_CODE = 5;

    FirebaseDatabase database;
    DatabaseReference myRef;

    String phoneNumber = "0740000000";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_advertisement);

        database = FirebaseDatabase.getInstance();


        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("ADVERTISEMENT");
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("ADVERTISEMENT");
        }

        if (newString!= null){
            myRef = database.getReference("Advertisements").child(newString);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        String title = (String) dataSnapshot.child("title").getValue();
                        String description = (String) dataSnapshot.child("description").getValue();
                        ArrayList<String> images = (ArrayList<String>) dataSnapshot.child("images").getValue();

                        String profilePicture = (String) dataSnapshot.child("googleUser").child("image").getValue();
                        String firstName = (String) dataSnapshot.child("googleUser").child("firstName").getValue();
                        String lastName = (String) dataSnapshot.child("googleUser").child("lastName").getValue();
                        phoneNumber = (String) dataSnapshot.child("googleUser").child("mobileNumber").getValue();

                        TextView tvName = findViewById(R.id.tv_profile_name_aboutAdvertisement);
                        ImageView ivProfilePicture = findViewById(R.id.iv_profile);

                        //Uri myUri = Uri.parse(profilePicture);
                        //ivProfilePicture.setImageURI(myUri);
                        String fullName = firstName + " " + lastName;
                        tvName.setText(fullName);

                        Glide.with(getApplicationContext()).load(profilePicture)
                                .thumbnail(0.5f)
                                .crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(ivProfilePicture);
                    /*
                    for (DataSnapshot messageSnapshot: dataSnapshot.child("googleUser").getChildren()){
                        String firstName = (String) messageSnapshot.child("firstName").getValue();
                        String lastName = (String) messageSnapshot.child("lastName").getValue();
                        phoneNumber = (String) messageSnapshot.child("mobileNumber").getValue();

                        TextView tvName = findViewById(R.id.tv_profile_name_aboutAdvertisement);

                        tvName.setText(firstName + " " + lastName);
                    }
*/
                        TextView tvTitle = findViewById(R.id.tv_title_about_advertisement);
                        TextView tvDescription = findViewById(R.id.tv_description_about_advertisement);


                        tvTitle.setText(title);
                        tvDescription.setText(description);
                        viewPager = findViewById(R.id.vp_images);
                        adapter = new ViewPagerAdapter(AboutAdvertisement.this, images);
                        viewPager.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e){

                    }

                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    //Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }

        //viewPager = findViewById(R.id.vp_images);
        //adapter = new ViewPagerAdapter(AboutAdvertisement.this , images);
        //viewPager.setAdapter(adapter);

        Button callCreator = findViewById(R.id.bt_callCreator);

        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        callCreator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(AboutAdvertisement.this, R.anim.button_click_animation));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + phoneNumber));
                        if (ContextCompat.checkSelfPermission(AboutAdvertisement.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(AboutAdvertisement.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
                        }
                        else {
                            startActivity(callIntent);
                        }
                    }
                }, 300);

            }
        });
    }

    //monitor phone call activities
    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;

        String LOG_TAG = "LOGGING 123";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (TelephonyManager.CALL_STATE_RINGING == state) {
                // phone ringing
                Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // active
                Log.i(LOG_TAG, "OFFHOOK");

                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // run when class initial and phone call ended,
                // need detect flag from CALL_STATE_OFFHOOK
                Log.i(LOG_TAG, "IDLE");

                if (isPhoneCalling) {

                    Log.i(LOG_TAG, "restart app");

                    // restart app
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(
                                    getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    isPhoneCalling = false;
                }

            }
        }
    }
}

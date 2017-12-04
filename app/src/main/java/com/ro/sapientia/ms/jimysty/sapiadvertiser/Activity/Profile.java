package com.ro.sapientia.ms.jimysty.sapiadvertiser.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.BasicActivity;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.R;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.StaticMethods;

import java.util.HashMap;

/**
 * Created by barth on 11/13/2017.
 */

public class Profile extends BasicActivity {

    boolean clicked = true;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_profile);

        FloatingActionButton loguot = findViewById(R.id.fb_logout_profile);
        FloatingActionButton edit = findViewById(R.id.fb_edit_profile);

        loguot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                StaticMethods.goToLoginScreenActivity(Profile.this);
                finish();
            }
        });
        final EditText first = (EditText) findViewById(R.id.pr_first);
        final EditText last = (EditText) findViewById(R.id.pr_last);
        final EditText email = (EditText) findViewById(R.id.pr_email);
        final EditText phone = (EditText) findViewById(R.id.pr_phone);
        final EditText passw = (EditText) findViewById(R.id.pr_password);
        final EditText confirm = (EditText) findViewById(R.id.pr_confirm_password);



        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                first.setFocusableInTouchMode(clicked);
                last.setFocusableInTouchMode(clicked);
                email.setFocusableInTouchMode(clicked);
                phone.setFocusableInTouchMode(clicked);
                passw.setFocusableInTouchMode(clicked);
                confirm.setFocusableInTouchMode(clicked);

                clicked = !clicked;

                if(clicked){
                    String fname = first.getText().toString();
                    String lname = last.getText().toString();
                    String Email = email.getText().toString();
                    String Phone = phone.getText().toString();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference myRef = database.getReference(user.getUid()).child("User");

                    HashMap<String, String> Map = new HashMap<String, String>();

                    Map.put("First Name",fname);
                    Map.put("Last Name",lname);
                    Map.put("Email",Email);
                    Map.put("Phone Number",Phone);

                    myRef.setValue(Map);
                }
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = database.getReference(user.getUid()).child("User");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String value = dataSnapshot.getValue(String.class);
                //Log.d(TAG, "Value is: " + value);
                String first_name = (String) dataSnapshot.child("First Name").getValue();
                String last_name = (String) dataSnapshot.child("Last Name").getValue();
                String emailc = (String) dataSnapshot.child("Email").getValue();
                String phone_number = (String) dataSnapshot.child("Phone Number").getValue();

                first.setText(first_name);
                last.setText(last_name);
                email.setText(emailc);
                phone.setText(phone_number);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
               //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        StaticMethods.goToListAdvertisementsActivity(Profile.this);
        finish();

    }
}

package com.ro.sapientia.ms.jimysty.sapiadvertiser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Drako on 01-Nov-17.
 */

public class BasicActivity extends AppCompatActivity {

    protected FirebaseAuth mAuth;
    protected FirebaseUser currentFirebaseUser;
    protected FirebaseDatabase database;
    protected DatabaseReference myRef;

    /**
     * initialize firebase variables and set the status bar translucent
     * @param icicle
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        mAuth = FirebaseAuth.getInstance();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
    }

}


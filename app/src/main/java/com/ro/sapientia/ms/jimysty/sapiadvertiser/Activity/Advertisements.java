package com.ro.sapientia.ms.jimysty.sapiadvertiser.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


import com.google.firebase.auth.FirebaseAuth;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.BasicActivity;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.R;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.StaticMethods;

/**
 * Created by Drako on 06-Nov-17.
 */

public class Advertisements extends BasicActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.advertisement);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add:
                //Intent mainIntent = new Intent(Advertisements.this,NewAdvertisement.class);
                //Advertisements.this.startActivity(mainIntent);
                StaticMethods.goToCreateNewAdvertisementActivity(Advertisements.this);
                return true;
            case R.id.item_action_search:
                StaticMethods.goToTellAboutAdvertisementActivity(Advertisements.this);
                return true;
            case R.id.item_face:
                FirebaseAuth.getInstance().signOut();
                StaticMethods.goToLoginScreenActivity(Advertisements.this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

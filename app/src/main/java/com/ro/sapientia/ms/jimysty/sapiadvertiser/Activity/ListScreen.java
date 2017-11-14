package com.ro.sapientia.ms.jimysty.sapiadvertiser.Activity;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Adapter.MyRecyclerViewAdapter;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.BasicActivity;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Class.Advertisement;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.R;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.StaticMethods;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Drako on 06-Nov-17.
 */

public class ListScreen extends BasicActivity implements MyRecyclerViewAdapter.ItemClickListener{

    private MyRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advertisement);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //default values for recycler view
        ArrayList<String> noTitle = new ArrayList<>();
        ArrayList<String> noDescription = new ArrayList<>();
        ArrayList<String> imagesList = new ArrayList<>();
        noTitle.add("No Title");
        noDescription.add("Check the internet connection");
        imagesList.add("");

        // set up the RecyclerView
        recyclerView = findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, noTitle , noDescription , imagesList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        // database references
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Advertisements");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<String> titleList = new ArrayList<>();
                ArrayList<String> descriptionList = new ArrayList<>();
                ArrayList<String> imagesList = new ArrayList<>();

                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    String title = (String) messageSnapshot.child("title").getValue();
                    String description = (String) messageSnapshot.child("description").getValue();

                    ArrayList<String> images = (ArrayList<String>) messageSnapshot.child("images").getValue();
                    //Advertisement myAdvertisement = new Advertisement(title , description , images);
                    titleList.add(title);
                    descriptionList.add(description);
                    imagesList.add(images.get(0));
                }
                adapter = new MyRecyclerViewAdapter(ListScreen.this, titleList , descriptionList , imagesList);
                adapter.setClickListener(ListScreen.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemClick(View view, int position) {
        StaticMethods.goToTellAboutAdvertisementActivity(ListScreen.this , adapter.getItem(position));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add:
                StaticMethods.goToCreateNewAdvertisementActivity(ListScreen.this);
                return true;
            case R.id.item_action_search:
                return true;
            case R.id.item_face:
               StaticMethods.goToProfile(ListScreen.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

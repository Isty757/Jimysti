package com.ro.sapientia.ms.jimysty.sapiadvertiser.Activity;

import android.content.ClipData;
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

    MyRecyclerViewAdapter adapter;

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.advertisement);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        ArrayList<String> animalNames = new ArrayList<>();
        animalNames.add("Horse");
        animalNames.add("Cow");
        animalNames.add("Camel");
        animalNames.add("Sheep");
        animalNames.add("Goat");
        ArrayList<String> description = new ArrayList<>();
        description.add("Szép ló :D");
        description.add("Ez egy tehéén");
        description.add("Cameltoe ???");
        description.add("Báráánykaaa");
        description.add("Attila? Kecske...négy lába van");
        ArrayList<String> imagesList = new ArrayList<>();
        imagesList.add("https://firebasestorage.googleapis.com/v0/b/sapiadvertiser-a59e8.appspot.com/o/hirdetes.jpg?alt=media&token=b6e4e6aa-b46d-4cbc-8aa1-a400a9c1b60b");
        imagesList.add("https://firebasestorage.googleapis.com/v0/b/sapiadvertiser-a59e8.appspot.com/o/felveteli.jpg?alt=media&token=dd13af65-5abe-46ed-86a0-59ad58b60aa7");
        imagesList.add("https://firebasestorage.googleapis.com/v0/b/sapiadvertiser-a59e8.appspot.com/o/sapi.jpg?alt=media&token=2b32d318-d4f3-443c-b5e7-d2f7b469013d");
        imagesList.add("https://firebasestorage.googleapis.com/v0/b/sapiadvertiser-a59e8.appspot.com/o/hirdetes.jpg?alt=media&token=b6e4e6aa-b46d-4cbc-8aa1-a400a9c1b60b");
        imagesList.add("https://firebasestorage.googleapis.com/v0/b/sapiadvertiser-a59e8.appspot.com/o/felveteli.jpg?alt=media&token=dd13af65-5abe-46ed-86a0-59ad58b60aa7");

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
                    //String[] images = (String[]) messageSnapshot.child("images").getValue();
                    Log.d("AZAZ", "Value is: " + images.get(0) );
                    Advertisement myAdvertisement = new Advertisement(title , description , images);
                    titleList.add(title);
                    descriptionList.add(description);
                    imagesList.add(images.get(0));
                }
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_list);
                recyclerView.setLayoutManager(new LinearLayoutManager(ListScreen.this));
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

        // set up the RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, animalNames , description , imagesList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(ListScreen.this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            //Log.d("LEGYENAZ", personPhoto.toString());
            //ImageView googlePicture = findViewById(R.id.item_face);
            //ClipData.Item picture = (ClipData.Item)findViewById(R.id.item_face);
            //googlePicture.setImageURI(personPhoto);
            //googlePicture.setVisibility(View.INVISIBLE);
        }


    }
    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        StaticMethods.goToTellAboutAdvertisementActivity(ListScreen.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add:
                //Intent mainIntent = new Intent(ListScreen.this,NewAdvertisement.class);
                //ListScreen.this.startActivity(mainIntent);
                StaticMethods.goToCreateNewAdvertisementActivity(ListScreen.this);
                return true;
            case R.id.item_action_search:
                StaticMethods.goToTellAboutAdvertisementActivity(ListScreen.this);
                return true;
            case R.id.item_face:
                FirebaseAuth.getInstance().signOut();
                StaticMethods.goToLoginScreenActivity(ListScreen.this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

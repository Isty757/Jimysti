package com.ro.sapientia.ms.jimysty.sapiadvertiser.activity;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.SearchView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.BasicActivity;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Fragment.AdvertisementsFragment;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Fragment.MyAdvertisementsFragment;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.R;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.StaticMethods;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.ZoomOutPageTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Drako on 06-Nov-17.
 */

public class ListScreen extends BasicActivity{

    private static final String TAG = "ListScreen";

    private Menu menu;

    AdvertisementsFragment advertisementsFragment = new AdvertisementsFragment();
    MyAdvertisementsFragment myAdvertisementsFragment = new MyAdvertisementsFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement);

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.wood);
        mp.start();

        handleIntent(getIntent());

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if (currentFirebaseUser != null) {
            myRef = database.getReference(currentFirebaseUser.getUid());

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        loadGoogleProfilePicture(messageSnapshot);
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.d(TAG, error.getMessage());
                }
            });
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Bundle sendSearchText = new Bundle();
            sendSearchText.putString("Search",query);
            if ((advertisementsFragment != null) && (myAdvertisementsFragment != null)) {
                advertisementsFragment.setSearchText(sendSearchText);
                myAdvertisementsFragment.setSearchText(sendSearchText);
            }
        }
    }

    private void loadGoogleProfilePicture(DataSnapshot messageSnapshot) {
        try {
            String profilePicture = (String) messageSnapshot.child("image").getValue();
            if (menu != null) {
                final MenuItem item = menu.findItem(R.id.iv_profil_picture_img);
                if (item != null) {
                    Glide.with(ListScreen.this).load(profilePicture).asBitmap().into(new SimpleTarget<Bitmap>(100, 100) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            item.setIcon(new BitmapDrawable(getResources(), resource));
                        }
                    });
                }
            }
        }
        catch (Exception e){
            Log.d(TAG , e.getMessage());
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(advertisementsFragment, "All Advertisement");
        adapter.addFragment(myAdvertisementsFragment, "My Advertisements");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.item_action_search).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if ((advertisementsFragment != null) && (myAdvertisementsFragment != null)){
                    advertisementsFragment.setSearchText(null);
                    myAdvertisementsFragment.setSearchText(null);
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_face:
                if (currentFirebaseUser != null){
                    StaticMethods.goToProfile(ListScreen.this);
                    finish();
                }
                else{
                    dialogIfUserWantToLogin();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void dialogIfUserWantToLogin(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListScreen.this);

        // set title
        alertDialogBuilder.setTitle("Click Yes to Log In");
        // set dialog message
        alertDialogBuilder
                .setMessage("To modify your profile you have to be logged in!")
                .setCancelable(false)
                .setPositiveButton("Go to login",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        StaticMethods.goToLoginScreenActivity(ListScreen.this);
                        finish();
                    }
                })
                .setNegativeButton("Stay here",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
}

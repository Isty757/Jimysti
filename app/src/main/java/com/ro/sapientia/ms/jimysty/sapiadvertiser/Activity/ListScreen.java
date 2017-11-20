package com.ro.sapientia.ms.jimysty.sapiadvertiser.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


import com.ro.sapientia.ms.jimysty.sapiadvertiser.BasicActivity;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Fragment.AdvertisementsFragment;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Fragment.MyAdvertisementsFragment;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.R;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.StaticMethods;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Drako on 06-Nov-17.
 */

public class ListScreen extends BasicActivity{

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement);

        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AdvertisementsFragment(), "All Advertisement");
        adapter.addFragment(new MyAdvertisementsFragment(), "My Advertisements");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

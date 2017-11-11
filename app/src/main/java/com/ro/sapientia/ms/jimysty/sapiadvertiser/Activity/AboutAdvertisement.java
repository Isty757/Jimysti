package com.ro.sapientia.ms.jimysty.sapiadvertiser.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.ro.sapientia.ms.jimysty.sapiadvertiser.BasicActivity;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.R;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Adapter.ViewPagerAdapter;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.StaticMethods;

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

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.about_advertisement);


        viewPager = findViewById(R.id.vp_images);
        adapter = new ViewPagerAdapter(AboutAdvertisement.this , images);
        viewPager.setAdapter(adapter);

        Button tmpButton = findViewById(R.id.bt_callCreator);

        tmpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent mainIntent = new Intent(AboutAdvertisement.this,NewAdvertisement.class);
                //AboutAdvertisement.this.startActivity(mainIntent);
                StaticMethods.goToCreateNewAdvertisementActivity(AboutAdvertisement.this);
            }
        });
    }
}

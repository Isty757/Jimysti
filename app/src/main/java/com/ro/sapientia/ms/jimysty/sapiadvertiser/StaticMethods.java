package com.ro.sapientia.ms.jimysty.sapiadvertiser;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.ro.sapientia.ms.jimysty.sapiadvertiser.activity.AboutAdvertisement;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.activity.ListScreen;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.activity.LoginSignUpScreen;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.activity.NewAdvertisement;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.activity.Profile;

/**
 * Created by Drako on 11-Nov-17.
 */

public class StaticMethods {

    private static final int RESET_STATE_DELAY_MILLIS = 300;

    public static void goToLoginScreenActivity(Context context) {
        Intent i = new Intent(context, LoginSignUpScreen.class);
        context.startActivity(i);
    }
    public static void goToListAdvertisementsActivity(Context context) {
        Intent i = new Intent(context, ListScreen.class);
        context.startActivity(i);
    }
    public static void goToCreateNewAdvertisementActivity(Context context) {
        Intent i = new Intent(context, NewAdvertisement.class);
        context.startActivity(i);
    }
    public static void goToTellAboutAdvertisementActivity(final Context context ,final String currentAdvertisement) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(context, AboutAdvertisement.class);
                i.putExtra("ADVERTISEMENT",currentAdvertisement);
                context.startActivity(i);
            }
        }, RESET_STATE_DELAY_MILLIS);

    }
    public static void goToProfile(Context context) {
        Intent i = new Intent(context, Profile.class);
        context.startActivity(i);
    }
}

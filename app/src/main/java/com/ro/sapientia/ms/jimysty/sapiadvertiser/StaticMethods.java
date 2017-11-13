package com.ro.sapientia.ms.jimysty.sapiadvertiser;

import android.content.Context;
import android.content.Intent;

import com.ro.sapientia.ms.jimysty.sapiadvertiser.Activity.AboutAdvertisement;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Activity.ListScreen;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Activity.LoginSignUpScreen;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Activity.NewAdvertisement;

/**
 * Created by Drako on 11-Nov-17.
 */

public class StaticMethods {

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
    public static void goToTellAboutAdvertisementActivity(Context context) {
        Intent i = new Intent(context, AboutAdvertisement.class);
        context.startActivity(i);
    }
}

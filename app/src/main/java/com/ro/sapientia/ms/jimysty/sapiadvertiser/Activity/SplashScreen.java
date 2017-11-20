package com.ro.sapientia.ms.jimysty.sapiadvertiser.Activity;

import android.os.Handler;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.BasicActivity;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.R;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.StaticMethods;

public class SplashScreen extends BasicActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    /** Firebase intance **/
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                //Intent mainIntent = new Intent(SplashScreen.this,LoginScreen.class);
                //SplashScreen.this.startActivity(mainIntent);
                user = mAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    StaticMethods.goToListAdvertisementsActivity(SplashScreen.this);
                    SplashScreen.this.finish();
                } else {
                    // User is signed out
                    StaticMethods.goToLoginScreenActivity(SplashScreen.this);
                    SplashScreen.this.finish();
                }
                //overridePendingTransition(R.anim.slide_in_rigth, R.anim.slide_out_left);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}

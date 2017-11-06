package com.ro.sapientia.ms.jimysty.sapiadvertiser;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class SplashScreen extends BasicActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash_screen);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashScreen.this,LoginScreenActivity.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
                //overridePendingTransition(R.anim.slide_in_rigth, R.anim.slide_out_left);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}

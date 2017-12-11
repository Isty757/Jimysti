package com.ro.sapientia.ms.jimysty.sapiadvertiser.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.BasicActivity;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.R;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.StaticMethods;

/**
 * Created by Drako on 30-Oct-17.
 */

public class LoginSignUpScreen extends BasicActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginSignUpScreen";

    private GoogleApiClient mGoogleApiClient;
    private final int RC_SIGN_IN = 11;

    private static final int DELAY = 200;

    private boolean forgotPasswordField = false;

    private ImageView logoImageView;

    private EditText etEmail;
    private EditText etPassword;

    private TextView forgotPassword;
    private TextView tvAppName;

    private Button loginWithoutSignUpButton;
    private Button loginButton;

    private myTask mytask = new myTask();
    private ProgressBar progressbar;
    private ConstraintLayout linearLayout;

    private SignInButton googleSignInButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        initializeViewWithControl();

        initializeGoogleApi();

        settingListeners();

        delayingItems();
    }

    /**
     * check if password or email fields are empty or not. If one of them is empty a Toast message will be shown
     *
     */
    private void checkIfFieldsAreEmpty(){
        if (forgotPasswordField){
            etPassword.setVisibility(View.VISIBLE);
            googleSignInButton.setVisibility(View.VISIBLE);
            forgotPassword.setVisibility(View.VISIBLE);
            loginButton.setText(R.string.login_screen_button_text);
            forgotPasswordField = false;
            if (etEmail.getText().toString().matches("")){
                Toast.makeText(LoginSignUpScreen.this, "E-mail field is empty!", Toast.LENGTH_SHORT).show();
            }
            else{
                mAuth.sendPasswordResetEmail(etEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginSignUpScreen.this, "Email sent!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
        else{
            login();
        }
    }

    /**
     * check if email or password field are empty or not and
     * check if password field contain minimum 6 character or not
     * if everything is okay then we can sign in if we have an account
     * if we don't have one then we have to create it
     */
    private void login(){
        if (etEmail.getText().toString().matches("") || etPassword.getText().toString().matches("")){
            Toast.makeText(LoginSignUpScreen.this, "Fill E-mail and Password field!", Toast.LENGTH_SHORT).show();
            progressbar.setVisibility(View.INVISIBLE);
            linearLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        }
        else {
            if (etPassword.getText().toString().length() <= 5) {
                Toast.makeText(LoginSignUpScreen.this, "Password must contain min 6 character", Toast.LENGTH_SHORT).show();
                TextView forgotPassword = findViewById(R.id.tv_forgotPassword);
                forgotPassword.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.INVISIBLE);
                linearLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            } else {
                mAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginSignUpScreen.this, "Authentication Success.", Toast.LENGTH_SHORT).show();
                                    StaticMethods.goToListAdvertisementsActivity(LoginSignUpScreen.this);
                                    LoginSignUpScreen.this.finish();
                                } else {
                                    //Toast.makeText(LoginSignUpScreen.this, "Creating new user...", Toast.LENGTH_SHORT).show();
                                    createNewUser();
                                }
                            }
                        });
            }
        }
    }
    /**
     * create user with email and password if it's not wrong
     */
    private void createNewUser(){
        mAuth.createUserWithEmailAndPassword(etEmail.getText().toString().trim(), etPassword.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            StaticMethods.goToListAdvertisementsActivity(LoginSignUpScreen.this);
                            LoginSignUpScreen.this.finish();
                        } else {
                            Toast.makeText(LoginSignUpScreen.this, "Wrong email or password.", Toast.LENGTH_SHORT).show();
                            forgotPassword.setVisibility(View.VISIBLE);
                            progressbar.setVisibility(View.INVISIBLE);
                            linearLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        }
                    }
                });
    }

    /**
     * sign in with google
     */
    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * check if sign in result is okay or not
     * @param requestCode is a code that we add it
     * @param resultCode returns that result is success or not
     * @param data returns data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                progressbar.setVisibility(View.INVISIBLE);
                linearLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                Toast.makeText(LoginSignUpScreen.this, "Sign in with Google Failed!", Toast.LENGTH_SHORT).show();
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    /**
     * register our google account in firebase
     * @param acct this is our google account
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginSignUpScreen.this, "Authentication with Google success.",Toast.LENGTH_SHORT).show();
                            StaticMethods.goToListAdvertisementsActivity(LoginSignUpScreen.this);
                            LoginSignUpScreen.this.finish();
                        } else {
                            Toast.makeText(LoginSignUpScreen.this, "Authentication with Google failed.",Toast.LENGTH_SHORT).show();
                            progressbar.setVisibility(View.INVISIBLE);
                            linearLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not be available.
        Log.d(TAG , "onConnectionFailed:" + connectionResult);
    }

    /**
     * animate every buttons, textviews and logos
     */
    private void delayingItems(){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_anim);
        logoImageView.startAnimation(animation);

        googleSignInButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                googleSignInButton.setVisibility(View.VISIBLE);
            }
        }, DELAY);
        loginWithoutSignUpButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                loginWithoutSignUpButton.setVisibility(View.VISIBLE);
            }
        }, 2*DELAY);
        loginButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                loginButton.setVisibility(View.VISIBLE);
            }
        }, 3*DELAY);

        etPassword.postDelayed(new Runnable() {
            @Override
            public void run() {
                etPassword.setVisibility(View.VISIBLE);
            }
        }, 4*DELAY);

        etEmail.postDelayed(new Runnable() {
            @Override
            public void run() {
                etEmail.setVisibility(View.VISIBLE);
            }
        }, 5*DELAY);

        tvAppName.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.bouncing_anim);
                logoImageView.startAnimation(animation2);
                tvAppName.setVisibility(View.VISIBLE);
            }
        }, 6*DELAY);
    }

    /**
     * get all ID s from XML
     */
    private void initializeViewWithControl(){
        //Edit Texts
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        //Text views
        forgotPassword = findViewById(R.id.tv_forgotPassword);
        tvAppName = findViewById(R.id.tv_appName);
        //Buttons
        loginWithoutSignUpButton = findViewById(R.id.bt_loginWithoutSignUp_loginScreen);
        googleSignInButton = findViewById(R.id.sign_in_button);
        loginButton = findViewById(R.id.loginButton);
        //Image View
        logoImageView = findViewById(R.id.iv_sapi);
        //progress bar
        progressbar = (ProgressBar) findViewById(R.id.progressBar1);
        mytask.execute();
        progressbar.setVisibility(View.INVISIBLE);
        linearLayout = findViewById(R.id.container);

    }
    //initialize google API
    private void initializeGoogleApi(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    /**
     * set listeners
     */
    private void settingListeners(){
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressbar.setVisibility(View.VISIBLE);
                linearLayout.setBackgroundColor(getResources().getColor(R.color.colorGrey));
                view.startAnimation(AnimationUtils.loadAnimation(LoginSignUpScreen.this, R.anim.button_click_animation));
                signInWithGoogle();
            }
        });

        loginWithoutSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressbar.setVisibility(View.VISIBLE);
                linearLayout.setBackgroundColor(getResources().getColor(R.color.colorGrey));
                view.startAnimation(AnimationUtils.loadAnimation(LoginSignUpScreen.this, R.anim.button_click_animation));
                StaticMethods.goToListAdvertisementsActivity(LoginSignUpScreen.this);
                progressbar.setVisibility(View.INVISIBLE);
                linearLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressbar.setVisibility(View.VISIBLE);
                linearLayout.setBackgroundColor(getResources().getColor(R.color.colorGrey));
                view.startAnimation(AnimationUtils.loadAnimation(LoginSignUpScreen.this, R.anim.button_click_animation));
                checkIfFieldsAreEmpty();
            }
        });

        forgotPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                etPassword.setVisibility(View.INVISIBLE);
                googleSignInButton.setVisibility(View.INVISIBLE);
                forgotPassword.setVisibility(View.INVISIBLE);
                loginButton.setText("Send Email");
                forgotPasswordField = true;
                return false;
            }
        });
    }


    class myTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressbar.setProgress(0);
            progressbar.setMax(100);
            int progressbarstatus = 0;
        };

        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < 20; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                progressbar.incrementProgressBy(10);
            }
            return "completed";
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

        }

        @Override
        protected void onProgressUpdate(String... values) {

            super.onProgressUpdate(values);
        }
    }
}

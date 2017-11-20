package com.ro.sapientia.ms.jimysty.sapiadvertiser.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.BasicActivity;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.R;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.StaticMethods;

/**
 * Created by Drako on 30-Oct-17.
 */

public class LoginSignUpScreen extends BasicActivity implements GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private final int RC_SIGN_IN = 11;

    private boolean forgotPasswordField = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        final ImageView image1 = findViewById(R.id.iv_sapi);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_anim);
        image1.startAnimation(animation);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        final SignInButton googleSignInButton = findViewById(R.id.sign_in_button);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });
        final EditText etEmail = findViewById(R.id.et_email);
        final EditText etPassword = findViewById(R.id.et_password);
        final TextView forgotPassword = findViewById(R.id.tv_forgotPassword);

        final Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (forgotPasswordField){
                    etPassword.setVisibility(View.VISIBLE);
                    googleSignInButton.setVisibility(View.VISIBLE);
                    forgotPassword.setVisibility(View.VISIBLE);
                    loginButton.setText("Login");
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
        });
        googleSignInButton.postDelayed(new Runnable() {
            @Override
            public void run() {

                googleSignInButton.setVisibility(View.VISIBLE);

                //Animation animation3 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in_out_anim);
                //signInButton.startAnimation(animation3);
            }
        }, 200);
        loginButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                loginButton.setVisibility(View.VISIBLE);
            }
        }, 400);

        etPassword.postDelayed(new Runnable() {
            @Override
            public void run() {
                etPassword.setVisibility(View.VISIBLE);
            }
        }, 600);

        etEmail.postDelayed(new Runnable() {
            @Override
            public void run() {
                etEmail.setVisibility(View.VISIBLE);
            }
        }, 800);
        final TextView tvAppName = findViewById(R.id.tv_appName);
        tvAppName.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.bouncing_anim);
                image1.startAnimation(animation2);
                tvAppName.setVisibility(View.VISIBLE);
            }
        }, 1000);


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


    private void signInWithGoogle() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void login(){

        EditText etEmail = findViewById(R.id.et_email);
        EditText etPassword = findViewById(R.id.et_password);

        if (etEmail.getText().toString().matches("") || etPassword.getText().toString().matches("")){
            Toast.makeText(LoginSignUpScreen.this, "Fill E-mail and Password field!", Toast.LENGTH_SHORT).show();
        }
        else {
            if (etPassword.getText().toString().length() <= 5) {
                Toast.makeText(LoginSignUpScreen.this, "Password must contain min 6 character", Toast.LENGTH_SHORT).show();
                TextView forgotPassword = findViewById(R.id.tv_forgotPassword);
                forgotPassword.setVisibility(View.VISIBLE);
            } else {
                mAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginSignUpScreen.this, "Authentication Success.", Toast.LENGTH_SHORT).show();
                                    //Intent mainIntent = new Intent(LoginSignUpScreen.this,AboutAdvertisement.class);
                                    //LoginSignUpScreen.this.startActivity(mainIntent);
                                    StaticMethods.goToListAdvertisementsActivity(LoginSignUpScreen.this);
                                    LoginSignUpScreen.this.finish();
                                } else {
                                    //Toast.makeText(LoginSignUpScreen.this, "Creating new user...", Toast.LENGTH_SHORT).show();
                                    createUser();
                                }
                            }
                        });
            }
        }
    }

    private void createUser(){

        EditText etEmail = findViewById(R.id.et_email);
        EditText etPassword = findViewById(R.id.et_password);

        mAuth.createUserWithEmailAndPassword(etEmail.getText().toString().trim(), etPassword.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Intent mainIntent = new Intent(LoginSignUpScreen.this,AboutAdvertisement.class);
                            //LoginSignUpScreen.this.startActivity(mainIntent);
                            StaticMethods.goToListAdvertisementsActivity(LoginSignUpScreen.this);
                            LoginSignUpScreen.this.finish();
                        } else {
                            Toast.makeText(LoginSignUpScreen.this, "Wrong email or password.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

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
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginSignUpScreen.this, "Authentication with Google success.",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Intent mainIntent = new Intent(LoginSignUpScreen.this,Advertisements.class);
                            //LoginSignUpScreen.this.startActivity(mainIntent);
                            StaticMethods.goToListAdvertisementsActivity(LoginSignUpScreen.this);
                            LoginSignUpScreen.this.finish();
                        } else {
                            Toast.makeText(LoginSignUpScreen.this, "Authentication with Google failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("Google", "onConnectionFailed:" + connectionResult);
    }
}

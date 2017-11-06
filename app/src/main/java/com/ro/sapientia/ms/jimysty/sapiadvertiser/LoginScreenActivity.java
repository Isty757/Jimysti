package com.ro.sapientia.ms.jimysty.sapiadvertiser;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
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

/**
 * Created by Drako on 30-Oct-17.
 */

public class LoginScreenActivity extends BasicActivity implements GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private final int RC_SIGN_IN = 11;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

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

        final Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();

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
        final EditText etPassword = findViewById(R.id.et_password);
        etPassword.postDelayed(new Runnable() {
            @Override
            public void run() {
                etPassword.setVisibility(View.VISIBLE);
            }
        }, 600);
        final EditText etEmail = findViewById(R.id.et_email);
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
    }


    private void signInWithGoogle() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void login(){

        EditText etEmail = (EditText) findViewById(R.id.et_email);
        EditText etPassword = (EditText) findViewById(R.id.et_password);

        if (etPassword.getText().toString().length() <= 5){
            Toast.makeText(LoginScreenActivity.this, "Password must contain min 6 character", Toast.LENGTH_SHORT).show();
        }
        if (etEmail.getText().toString().matches("") || etPassword.getText().toString().matches("")){
            Toast.makeText(LoginScreenActivity.this, "Fill E-mail and Password field!", Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginScreenActivity.this, "Authentication Success.", Toast.LENGTH_SHORT).show();
                                Intent mainIntent = new Intent(LoginScreenActivity.this,AboutAdvertisement.class);
                                LoginScreenActivity.this.startActivity(mainIntent);
                            } else {
                                Toast.makeText(LoginScreenActivity.this, "Authentication failed22.", Toast.LENGTH_SHORT).show();
                                createUser();
                            }
                        }
                    });
        }
    }

    private void createUser(){

        EditText etEmail = (EditText) findViewById(R.id.et_email);
        EditText etPassword = (EditText) findViewById(R.id.et_password);

        Log.d("LOGIN22", "createUserWithEmail:success");
        mAuth.createUserWithEmailAndPassword(etEmail.getText().toString().trim(), etPassword.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent mainIntent = new Intent(LoginScreenActivity.this,Advertisements.class);
                            LoginScreenActivity.this.startActivity(mainIntent);
                        } else {
                            Toast.makeText(LoginScreenActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(LoginScreenActivity.this, "Authentication with Google success.",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent mainIntent = new Intent(LoginScreenActivity.this,AboutAdvertisement.class);
                            LoginScreenActivity.this.startActivity(mainIntent);
                        } else {
                            Toast.makeText(LoginScreenActivity.this, "Authentication with Google failed.",Toast.LENGTH_SHORT).show();
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

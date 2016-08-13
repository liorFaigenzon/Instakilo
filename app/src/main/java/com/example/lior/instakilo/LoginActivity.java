package com.example.lior.instakilo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.lior.instakilo.models.Model;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    private CallbackManager callbackManager;
    private View progressView;
    private View loginFormView;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Model.getInstance().signOut();

        progressView = findViewById(R.id.login_progress);
        loginFormView = findViewById(R.id.login_form);

        // TODO: change to != null
        // Check if the user already signed in
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

            // Start the main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {

            // Initialize Facebook Login button
            callbackManager = CallbackManager.Factory.create();
            loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
            loginButton.setReadPermissions("email", "public_profile");
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d("Nir", "facebook:onSuccess:" + loginResult);

                    // Attempt to login
                    attemptLogin(loginResult);
                }

                @Override
                public void onCancel() {
                    Log.d("Nir", "facebook:onCancel");
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d("Nir", "facebook:onError", error);
                }
            });
        }

        /*authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Nir", "onAuthStateChanged:signed_in:" + user.getUid());

                    Intent intent = new Intent(LoginActivityTest.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // User is signed out
                    Log.d("Nir", "onAuthStateChanged:signed_out");
                }
            }
        };*/
    }

    private void attemptLogin(LoginResult loginResult) {

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);

        // Sign in
        Model.getInstance().signIn(loginResult.getAccessToken(), new Model.AuthListener() {
            @Override
            public void onDone(String userId, Exception e) {

                // Check if the login was successful
                if (userId != null) {

                    // Start the main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }

                // Hide the progress bar
                //showProgress(false);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        /*if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}


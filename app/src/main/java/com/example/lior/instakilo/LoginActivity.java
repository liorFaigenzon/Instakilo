package com.example.lior.instakilo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final int SIGN_IN_GOOGLE = -1;

    private CallbackManager callbackManager;
    private View progressView;
    private View loginFormView;
    private LoginButton loginButton;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Model.getInstance().signOut();

        progressView = findViewById(R.id.login_progress);
        loginFormView = findViewById(R.id.login_form);

        // Check if the user already signed in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            // Start the main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {

            // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.google_client_id))
                    .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                    .requestEmail()
                    .build();

            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            // Customize sign-in button. The sign-in button can be displayed in
            // multiple sizes and color schemes. It can also be contextually
            // rendered based on the requested scopes. For example. a red button may
            // be displayed when Google+ scopes are requested, but a white button
            // may be displayed when only basic profile is requested. Try adding the
            // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
            // difference.
            SignInButton signInButton = (SignInButton) findViewById(R.id.button_google_login);
            signInButton.setSize(SignInButton.SIZE_WIDE);
            signInButton.setScopes(gso.getScopeArray());

            findViewById(R.id.button_google_login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                    startActivityForResult(signInIntent, SIGN_IN_GOOGLE);
                }
            });

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
        Log.d("Nir", "ReqCode: " + requestCode + ", ResCode: " + resultCode + ", FaceCode: " + loginButton.getRequestCode());
        if (requestCode == loginButton.getRequestCode()) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (resultCode == RESULT_OK && requestCode == SIGN_IN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d("Nir", "handleGoogleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {

            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("Nir", "firebaseAuthWithGoogle: " + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Nir", "signInWithCredential:onComplete: " + task.isSuccessful());

                        // Start the main activity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Nir", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Nir", "Google:onConnectionFailed");
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


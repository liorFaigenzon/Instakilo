package com.example.lior.instakilo;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivityTest extends AppCompatActivity {

    //public static final int RC_SIGN_IN = 100;

    private CallbackManager callbackManager;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity_test);


        // Get firebase authentication
        auth = FirebaseAuth.getInstance();

        //auth.signOut();

        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Nir", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
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

        authListener = new FirebaseAuth.AuthStateListener() {
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
        };

        /*if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivityTest.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(AuthUI.FACEBOOK_PROVIDER)
                            .build(),
                    RC_SIGN_IN);
        }*/
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Log.d("Nir", "onActivityResult:RESULT_OK");
                // user is signed in!
                //finish();
                Intent intent = new Intent(LoginActivityTest.this, MainActivity.class);
                startActivity(intent);
                finish()
            } else {
                Log.d("Nir", "onActivityResult:RESULT_CANCLE");
                // user is not signed in. Maybe just wait for the user to press
                // "sign in" again, or show a message
            }
        }*/
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("Nir", "handleFacebookAccessToken:" + token);

        // Get an access token for the sign in user
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Nir", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Nir", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivityTest.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

package com.example.lior.instakilo.models.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lior.instakilo.models.Comment;
import com.example.lior.instakilo.models.Model;
import com.example.lior.instakilo.models.Post;
import com.facebook.AccessToken;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ModelFirebase {

    private final static DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    private PostFirebase postFirebase;
    private CommentFirebase commentFirebase;

    public ModelFirebase(Context context){
        Firebase.setAndroidContext(context);
        postFirebase = new PostFirebase();
        commentFirebase = new CommentFirebase();
    }

    public static DatabaseReference getDatabase() {
        return database;
    }

    public void signIn(AccessToken token, final Model.AuthListener listener) {
        Log.d("Nir", "Login access token: " + token);

        // Retrieve the credentials using the access token
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        // Sign in
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Nir", "signInWithCredential:onComplete:" + task.isSuccessful());
                        listener.onDone(FirebaseAuth.getInstance().getCurrentUser().getUid(),null);

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Nir", "signInWithCredential", task.getException());
                            listener.onDone(null,task.getException());
                        }
                    }
                });
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public String getUserId() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }else{
            return null;
        }
    }

    public void getAll(Model.ModelClass model, String lastUpdateDate, Model.GetManyListener listener) {

        // Check the type of the model to get
        switch (model){
            case POST:
                postFirebase.getAll(lastUpdateDate, listener);
                break;
            case COMMENT:
                commentFirebase.getAll(lastUpdateDate, listener);
                break;
        }
    }

    public void getById(Model.ModelClass model, String id, final Model.GetOneListener listener) {

        // Check the type of the model to get
        switch (model){
            case POST:
                postFirebase.getById(id, listener);
                break;
            case COMMENT:
                commentFirebase.getById(id, listener);
                break;
        }
    }

    public void getCommentsByPostId(String postId, String lastUpdateDate, Model.GetManyListener listener) {
        commentFirebase.getByPostId(postId, lastUpdateDate, listener);
    }

    public void add(Object model, Model.AddListener listener) {

        // Check the type of the model to add
        if (model instanceof Post) {
            postFirebase.add(model, listener);
        } else if (model instanceof Comment) {
            commentFirebase.add(model, listener);
        }
    }

    public void update(Object model, Model.UpdateListener listener) {

        // Check the type of the model to update
        if (model instanceof Post) {
            postFirebase.update(model, listener);
        } else if (model instanceof Comment) {
            commentFirebase.update(model, listener);
        }
    }

    public void delete(Object model, Model.DeleteListener listener) {

        // Check the type of the model to delete
        if (model instanceof Post) {
            postFirebase.delete(model, listener);
        } else if (model instanceof Comment) {
            commentFirebase.delete(model, listener);
        }
    }

    public void attachCacheListener(Model.ModelClass model, Model.AttachCacheListener listener) {

        // Check the type of the model to attache to it the listener
        switch (model){
            case POST:
                postFirebase.attachCacheListener(listener);
                break;
            case COMMENT:
                commentFirebase.attachCacheListener(listener);
                break;
        }
    }
}


















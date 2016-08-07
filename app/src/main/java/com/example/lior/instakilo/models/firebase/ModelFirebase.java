package com.example.lior.instakilo.models.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lior.instakilo.models.Model;
import com.example.lior.instakilo.models.Post;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

public class ModelFirebase {

    private final static DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    private PostFirebase postFirebase;

    public ModelFirebase(Context context){
        Firebase.setAndroidContext(context);
        postFirebase = new PostFirebase();
    }

    public static DatabaseReference getDatabase() {
        return database;
    }

    public void signup(String email, String password, final Model.AuthListener listener){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            listener.onDone(FirebaseAuth.getInstance().getCurrentUser().getUid(),null);
                        }else{
                            listener.onDone(null,task.getException());
                        }
                    }
                });
    }

    public void login(String email, String password, final Model.AuthListener listener) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            listener.onDone(FirebaseAuth.getInstance().getCurrentUser().getUid(),null);
                        }else{
                            listener.onDone(null,task.getException());
                        }
                    }
                });
    }

    public String getUserId() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }else{
            return null;
        }
    }

    public void signout() {
        FirebaseAuth.getInstance().signOut();
    }

    public void getAll(Model.ModelClass model, String lastUpdateDate, Model.GetAllListener listener) {

        // Check the type of the model to get
        switch (model){
            case POST:
                postFirebase.getAll(lastUpdateDate, listener);
                break;
        }
    }

    public void getById(Model.ModelClass model, String id, final Model.GetOneListener listener) {

        // Check the type of the model to get
        switch (model){
            case POST:
                postFirebase.getById(id, listener);
                break;
        }
    }

    public void add(Object model, Model.AddListener listener) {

        // Check the type of the model to add
        if (model instanceof Post) {
            postFirebase.add(model, listener);
        }
    }

    public void update(Object model, Model.UpdateListener listener) {

        // Check the type of the model to update
        if (model instanceof Post) {
            postFirebase.update(model, listener);
        }
    }

    public void delete(Object model, Model.DeleteListener listener) {

        // Check the type of the model to delete
        if (model instanceof Post) {
            postFirebase.delete(model, listener);
        }
    }
}


















package com.example.lior.instakilo.models.firebase;

import android.util.Log;
import android.widget.Toast;

import com.example.lior.instakilo.MyApplication;
import com.example.lior.instakilo.models.Model;
import com.example.lior.instakilo.models.Post;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class PostFirebase implements IModelFirebase {

    @Override
    public void getAll(String lastUpdateDate, final Model.GetManyListener listener) {

        // Get all recent posts that are not cached already
        Query queryPosts = ModelFirebase.getDatabase().child("posts").orderByChild("lastUpdated").startAt(lastUpdateDate);

        queryPosts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final List<Object> postList = new LinkedList<>();

                // Gather all the posts to a list
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    post.setId(postSnapshot.getKey());
                    postList.add(post);
                }

                // Return the list as a result
                listener.onResult(postList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Nir", "getAllPosts:onCancelled", databaseError.toException());
                listener.onCancel();
            }
        });
    }

    @Override
    public void getById(String id, final Model.GetOneListener listener) {

        // Get a reference to the post specified
        DatabaseReference postRef = ModelFirebase.getDatabase().child("posts").child(id);

        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // Retrieve the post
                Post post = snapshot.getValue(Post.class);
                listener.onResult(post);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Nir", "getOnePost:onCancelled", databaseError.toException());
                listener.onCancel();
            }
        });
    }

    @Override
    public void add(Object model, final Model.AddListener listener) {

        // Cast the model to post
        Post post = (Post)model;

        // Create date object with now's date to save as the last update of the post
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = dateFormatGmt.format(new Date()).toString();

        // Set the last update of the post to now's date
        post.setLastUpdated(date);

        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        final String postId = ModelFirebase.getDatabase().child("posts").push().getKey();
        Map<String, Object> postValues = post.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + postId, postValues);
        childUpdates.put("/user-posts/" + post.getAuthorId() + "/" + postId, postValues);

        ModelFirebase.getDatabase().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                listener.onComplete(databaseError, databaseReference, postId);
            }
        });
    }

    @Override
    public void update(Object model, final Model.UpdateListener listener) {

        // Cast the model to post
        final Post post = (Post)model;

        // Check if the post exists
        ModelFirebase.getDatabase().child("posts").child(post.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // Update the post if it exists
                        if (dataSnapshot.getValue(Post.class) != null) {

                            // Create date object with now's date to save as the last update of the post
                            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
                            String date = dateFormatGmt.format(new Date()).toString();

                            // Set the last update of the post to now's date
                            post.setLastUpdated(date);

                            // Update the post at /user-posts/$userid/$postid and at
                            // /posts/$postid simultaneously
                            Map<String, Object> postValues = post.toMap();
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/posts/" + post.getId(), postValues);
                            childUpdates.put("/user-posts/" + post.getAuthorId() + "/" + post.getId(), postValues);

                            ModelFirebase.getDatabase().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    listener.onComplete(databaseError, databaseReference, post.getId());
                                }
                            });
                        } else {
                            listener.onComplete(null, null, post.getId());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Nir", "updatePost:onCancelled", databaseError.toException());
                    }
                });
    }

    @Override
    public void delete(Object model, final Model.DeleteListener listener) {

        // Cast the model to post
        final Post post = (Post)model;

        // Delete post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + post.getId(), null);
        childUpdates.put("/user-posts/" + post.getAuthorId() + "/" + post.getId(), null);

        ModelFirebase.getDatabase().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                listener.onComplete(databaseError, databaseReference, post.getId());
            }
        });
    }
}

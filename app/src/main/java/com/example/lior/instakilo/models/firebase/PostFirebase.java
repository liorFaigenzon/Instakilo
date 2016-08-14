package com.example.lior.instakilo.models.firebase;

import android.util.Log;
import android.widget.Toast;

import com.example.lior.instakilo.MyApplication;
import com.example.lior.instakilo.models.Comment;
import com.example.lior.instakilo.models.Model;
import com.example.lior.instakilo.models.Post;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PostFirebase implements IModelFirebase {

    @Override
    public void getAll(final String lastUpdateDate, final Model.GetManyListener listener) {

        // Get all recent posts that are not cached already
        Query queryPosts = ModelFirebase.getDatabase().child("posts").orderByChild("lastUpdated").startAt(lastUpdateDate);

        queryPosts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final List<Object> postList = new LinkedList<>();
                Iterable<DataSnapshot> children =  snapshot.getChildren();

                if (snapshot.getChildrenCount() != 0 && lastUpdateDate != null) {
                    children.iterator().next();
                }

                // Gather all the posts to a list
                for (DataSnapshot postSnapshot : children) {
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

        // Set the last update of the post to now's date
        post.setLastUpdated();

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

                            // Set the last update of the post to now's date
                            post.setLastUpdated();

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

        // Get ref to the comments of the post
        final DatabaseReference commentsOfPostRef = ModelFirebase.getDatabase().child("post-comments").child(post.getId());

        commentsOfPostRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> commChildUpdates = new HashMap<>();

                // Delete all the comments of the post
                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    commChildUpdates.put("/comments/" + commentSnapshot.getKey(), null);
                }

                ModelFirebase.getDatabase().updateChildren(commChildUpdates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        // Delete post at /user-posts/$userid/$postid and at
                        // /posts/$postid simultaneously
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/posts/" + post.getId(), null);
                        childUpdates.put("/user-posts/" + post.getAuthorId() + "/" + post.getId(), null);
                        childUpdates.put("/post-comments/" + post.getId(), null);

                        ModelFirebase.getDatabase().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                listener.onComplete(databaseError, databaseReference, post.getId());
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onComplete(databaseError, commentsOfPostRef.getRef(), post.getId());
            }
        });
    }

    @Override
    public void attachCacheListener(final Model.AttachCacheListener listener) {
        ModelFirebase.getDatabase().child("posts").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("Nir", "Post:onChildAdded:" + dataSnapshot.getKey());

                // Retrieve the post
                Post post = dataSnapshot.getValue(Post.class);
                post.setId(dataSnapshot.getKey());
                listener.onChildAdded(post);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("Nir", "Post:onChildChanged:" + dataSnapshot.getKey());

                // Retrieve the post
                Post post = dataSnapshot.getValue(Post.class);
                post.setId(dataSnapshot.getKey());
                listener.onChildChanged(post);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("Nir", "Post:onChildRemoved:" + dataSnapshot.getKey());

                // Retrieve the post
                Post post = dataSnapshot.getValue(Post.class);
                post.setId(dataSnapshot.getKey());
                listener.onChildRemoved(post);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("Nir", "Post:onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Nir", "Post:onCancelled", databaseError.toException());
                Toast.makeText(MyApplication.getAppContext(), "Failed to load posts.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void attachUpdateUIListener(final Model.AttachUpdateUIListener listener) {
        ModelFirebase.getDatabase().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onDataChange(dataSnapshot.getValue(Post.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

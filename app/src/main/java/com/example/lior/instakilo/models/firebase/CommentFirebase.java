package com.example.lior.instakilo.models.firebase;

import android.util.Log;

import com.example.lior.instakilo.models.Comment;
import com.example.lior.instakilo.models.Model;
import com.example.lior.instakilo.models.Post;
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

public class CommentFirebase implements IModelFirebase {

    @Override
    public void getAll(String lastUpdateDate, final Model.GetAllListener listener) {

        // Get all recent comments that are not cached already
        Query queryPosts = ModelFirebase.getDatabase().child("comments").orderByChild("lastUpdated").startAt(lastUpdateDate);

        queryPosts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final List<Object> commentList = new LinkedList<>();

                // Gather all the comments to a list
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }

                // Return the list as a result
                listener.onResult(commentList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Nir", "getAllComments:onCancelled", databaseError.toException());
                listener.onCancel();
            }
        });
    }

    @Override
    public void getById(String id, final Model.GetOneListener listener) {

        // Get a reference to the comment specified
        DatabaseReference postRef = ModelFirebase.getDatabase().child("comments").child(id);

        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // Retrieve the comment
                Comment comment = snapshot.getValue(Comment.class);
                listener.onResult(comment);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Nir", "getOneComment:onCancelled", databaseError.toException());
                listener.onCancel();
            }
        });
    }

    @Override
    public void add(Object model, final Model.AddListener listener) {

        // Cast the model to comment
        Comment comment = (Comment)model;

        // Create date object with now's date to save as the last update of the comment
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = dateFormatGmt.format(new Date()).toString();

        // Set the last update of the comment to now's date
        comment.setLastUpdated(date);

        // Create new comment at /post-comments/$postid/$commentid and at
        // /comments/$commentid simultaneously
        final String commentId = ModelFirebase.getDatabase().child("comments").push().getKey();
        Map<String, Object> commentValues = comment.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/comments/" + commentId, commentValues);
        childUpdates.put("/post-comments/" + comment.getPostId() + "/" + commentId, commentValues);

        ModelFirebase.getDatabase().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                listener.onComplete(databaseError, databaseReference, commentId);
            }
        });
    }

    @Override
    public void update(Object model, final Model.UpdateListener listener) {

        // Cast the model to comment
        final Comment comment = (Comment)model;

        // Check if the comment exists
        ModelFirebase.getDatabase().child("comments").child(comment.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // Update the comment if it exists
                        if (dataSnapshot.getValue(Comment.class) != null) {

                            // Create date object with now's date to save as the last update of the comment
                            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
                            String date = dateFormatGmt.format(new Date()).toString();

                            // Set the last update of the comment to now's date
                            comment.setLastUpdated(date);

                            // Update the comment at /post-comments/$postid/$commentid and at
                            // /comments/$commentid simultaneously
                            Map<String, Object> commentValues = comment.toMap();
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/comments/" + comment.getId(), commentValues);
                            childUpdates.put("/post-comments/" + comment.getPostId() + "/" + comment.getId(), commentValues);

                            ModelFirebase.getDatabase().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    listener.onComplete(databaseError, databaseReference, comment.getId());
                                }
                            });
                        } else {
                            listener.onComplete(null, null, comment.getId());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Nir", "updateComment:onCancelled", databaseError.toException());
                    }
                });
    }

    @Override
    public void delete(Object model, final Model.DeleteListener listener) {

        // Cast the model to comment
        final Comment comment = (Comment)model;

        // Delete comment at /post-comments/$postid/$commentid and at
        // /comments/$commentid simultaneously
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/comments/" + comment.getId(), null);
        childUpdates.put("/post-comments/" + comment.getPostId() + "/" + comment.getId(), null);

        ModelFirebase.getDatabase().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                listener.onComplete(databaseError, databaseReference, comment.getId());
            }
        });
    }
}
package com.example.lior.instakilo.dummy;

import com.example.lior.instakilo.models.Comment;
import com.example.lior.instakilo.models.Model;
import com.example.lior.instakilo.models.callbacks.OnItemsLoadedCallback;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class CommentContent {


    public static List<Comment> ITEMS = new ArrayList<Comment>();

    private static CommentContent INSTANCE;

    private static OnItemsLoadedCallback mCallback;

    public static CommentContent getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new CommentContent();
        }
        return INSTANCE;
    }

    public static void setCallback(OnItemsLoadedCallback callback) {
        mCallback = callback;
    }

    public static void deleteComment(Comment comment) {
        final Comment deletComment = comment;
        Model.getInstance().delete(deletComment, new Model.DeleteListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference, String key) {
                ITEMS.remove(deletComment);
                mCallback.onLoadedComment(ITEMS);
            }
        });


    }

    public static void addComment(Comment comment) {
        ITEMS.add(comment);
        mCallback.onLoadedComment(ITEMS);
    }

    public  static void getCommentByPostId(String postId)
    {
        ITEMS = new ArrayList<>();
        Model.getInstance().getCommentsByPostId(postId, new Model.GetManyListener() {
            @Override
            public void onResult(List<Object> objects) {

                // Add some sample items.
                for (Object comment:  objects) {
                    ITEMS.add((Comment) comment);
                }

                if(mCallback !=null) {
                    mCallback.onLoadedComment(ITEMS);
                }
            }

            @Override
            public void onCancel() {
            }
        });
    }

}

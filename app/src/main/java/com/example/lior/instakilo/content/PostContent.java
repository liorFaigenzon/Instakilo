package com.example.lior.instakilo.content;

import com.example.lior.instakilo.MainActivity;
import com.example.lior.instakilo.models.Model;
import com.example.lior.instakilo.models.Post;
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
public class PostContent {
    /**
     * An array of sample (dummy) items.
     */
    public static final List<Post> ITEMS = new ArrayList<Post>();

    private static PostContent INSTANCE;

    private static OnItemsLoadedCallback mCallback;

    public static PostContent getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new PostContent();
        }
        return INSTANCE;
    }

    public static void setCallback(OnItemsLoadedCallback callback) {
        mCallback = callback;
    }

    public static void deletePost(Post post) {
        final Post deletPost = post;
        Model.getInstance().delete(deletPost, new Model.DeleteListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference, String key) {
                ITEMS.remove(deletPost);
                mCallback.onLoadedPost(ITEMS);
            }
        });


    }

    public static void addPost(Post post) {
        ITEMS.add(post);
        mCallback.onLoadedPost(ITEMS);
    }

    static {
        MainActivity.isDataLoading = true;
        Model.getInstance().getAll(Model.ModelClass.POST, new Model.GetManyListener() {
            @Override
            public void onResult(List<Object> objects) {

                // Add some sample items.
                for (Object post:  objects) {
                    ITEMS.add((Post)post);
                }
                if(mCallback !=null) {
                    mCallback.onLoadedPost(ITEMS);
                }

                MainActivity.isDataLoading = false;
            }

            @Override
            public void onCancel() {
            }
        });

    }
}

package com.example.lior.instakilo.dummy;

import com.example.lior.instakilo.models.Model;
import com.example.lior.instakilo.models.Post;
import com.example.lior.instakilo.models.callbacks.OnItemsLoadedCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Post> ITEM_MAP = new HashMap<String, Post>();

    private static final int COUNT = 9;

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

    static {
        Model.getInstance().getAll(Model.ModelClass.POST, new Model.GetManyListener() {
            @Override
            public void onResult(List<Object> objects) {

                // Add some sample items.
                for (Object post:  objects) {
                    ITEMS.add((Post)post);
                }
                if(mCallback !=null) {
                    mCallback.onLoaded(ITEMS);
                }
            }

            @Override
            public void onCancel() {

            }
        });

    }

    private static void addItem(Post post) {
        ITEMS.add(post);
        ITEM_MAP.put(post.getId(), post);
    }

    private static Post createDummyItem(int position) {
        return new Post(String.valueOf(position),String.valueOf(position),String.valueOf(position),String.valueOf(position), position, null);
    }
}

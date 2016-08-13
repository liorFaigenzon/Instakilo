package com.example.lior.instakilo.dummy;

import com.example.lior.instakilo.models.Comment;
import com.example.lior.instakilo.models.Model;
import com.example.lior.instakilo.models.callbacks.OnItemsLoadedCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class CommentContent {


    public static final List<Comment> ITEMS = new ArrayList<Comment>();

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


    static {
        Model.getInstance().getAll(Model.ModelClass.COMMENT, new Model.GetManyListener() {
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

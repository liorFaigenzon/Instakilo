package com.example.lior.instakilo.dummy;

import com.example.lior.instakilo.models.Comment;

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
public class CommentContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Comment> ITEMS = new ArrayList<Comment>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Comment> ITEM_MAP = new HashMap<String, Comment>();

    private static final int COUNT = 9;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(Comment Comment) {
        ITEMS.add(Comment);
        ITEM_MAP.put(Comment.getId(), Comment);
    }

    private static Comment createDummyItem(int position) {
        return new Comment("me","me","1","1");
    }

}

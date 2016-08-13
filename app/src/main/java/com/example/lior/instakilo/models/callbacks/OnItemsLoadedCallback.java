package com.example.lior.instakilo.models.callbacks;

import com.example.lior.instakilo.models.Comment;
import com.example.lior.instakilo.models.Post;

import java.util.List;

/**
 * Created by lior on 13/08/2016.
 */
public abstract class OnItemsLoadedCallback {

    public abstract void onLoadedPost(List<Post> items);
    public abstract void onLoadedComment(List<Comment> items);
}

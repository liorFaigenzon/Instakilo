package com.example.lior.instakilo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lior.instakilo.CommentFragment.OnListFragmentInteractionListener;
import com.example.lior.instakilo.dummy.CommentContent;
import com.example.lior.instakilo.models.Comment;
import com.example.lior.instakilo.models.Post;
import com.example.lior.instakilo.models.callbacks.OnItemsLoadedCallback;

import java.util.List;


public class MyCommentRecyclerViewAdapter extends RecyclerView.Adapter<MyCommentRecyclerViewAdapter.ViewHolder> {

    private  List<Comment> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyCommentRecyclerViewAdapter(List<Comment> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        CommentContent.getInstance().setCallback(new OnItemsLoadedCallback() {
            @Override
            public void onLoadedPost(List<Post> items) {

            }

            @Override
            public void onLoadedComment(List<Comment> items) {
                mValues = items;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_comment_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.mName.setText(holder.mItem.getAuthorName());
        holder.mContent.setText(holder.mItem.getContent());
        holder.mDate.setText(holder.mItem.getLastUpdated());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mName;
        public final TextView mContent;
        public final TextView mDate;
        public Comment mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.name);
            mContent=  (TextView) view.findViewById(R.id.content);
            mDate = (TextView) view.findViewById(R.id.date);

        }

        @Override
        public String toString() {
       //     return super.toString() + " '" + mContentView.getText() + "'";
            return "";
        }
    }
}

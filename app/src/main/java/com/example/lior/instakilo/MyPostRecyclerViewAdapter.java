package com.example.lior.instakilo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lior.instakilo.PostListFragment.OnListFragmentInteractionListener;
import com.example.lior.instakilo.dummy.PostContent;
import com.example.lior.instakilo.models.Comment;
import com.example.lior.instakilo.models.Model;
import com.example.lior.instakilo.models.Post;
import com.example.lior.instakilo.models.callbacks.OnItemsLoadedCallback;

import java.util.List;
import java.util.concurrent.ExecutionException;

//


public class MyPostRecyclerViewAdapter extends RecyclerView.Adapter<MyPostRecyclerViewAdapter.ViewHolder> {

    private static final int TYPE_DETAIL = 1;
    private static final int TYPE_MATRIX = 2;
    public  int mViewType =  1;
    private List<Post> mValues;
    //private final List<DummyItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private int mResource;

    public MyPostRecyclerViewAdapter(List<Post> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mResource = R.layout.fragment_post_row;
        PostContent.getInstance().setCallback(new OnItemsLoadedCallback() {
            @Override
            public void onLoadedPost(List<Post> items) {
                mValues = items;
                notifyDataSetChanged();
            }

            @Override
            public void onLoadedComment(List<Comment> items) {

            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        // here your custom logic to choose the view type
        return mViewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.mResource, parent, false);
        switch (viewType) {
            case TYPE_DETAIL:
                return new ViewDetailHolder(view);

        }

        return new ViewMatrixHolder(view);
    }


    public void changeResource(int resource) {
        this.mResource = resource;
        if(mResource == R.layout.fragment_post_row) {
            this.mViewType = 1;
        }
        if(mResource == R.layout.fragment_post_matirx) {
            this.mViewType = 2;
        }
        this.notifyDataSetChanged(); // Updates the view that holds the data
        //notifyDataSetChanged(); Use only if not setting the adapter and the data needs to be updated only!
    }

    @Override
    //fill the row with item info
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);


        switch (holder.getItemViewType()) {

            case TYPE_DETAIL:

                final ViewDetailHolder viewDetailHolder = (ViewDetailHolder)holder;



                try {
                    Model.getInstance().loadPhoto(holder.mItem.getPhotoId(), new Model.LoadPhotoListener() {
                        @Override
                        public void onResult(Bitmap photo) {
                            holder.mtopImg.setImageBitmap(photo);
                        }
                    });
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // check to see if each individual textview is null.
                // if not, assign some text!
                if (viewDetailHolder.mAuthorName != null) {
                    viewDetailHolder.mAuthorName.setText(holder.mItem.getAuthorName());
                }
                if (viewDetailHolder.likes != null) {
                    viewDetailHolder.likes.setText("Likes: ");
                }
                if (viewDetailHolder.mlikesTxt != null) {
                    viewDetailHolder.mlikesTxt.setText(Integer.toString(holder.mItem.getLikeCounter()));
                }


                if (viewDetailHolder.likePic != null) {
                    viewDetailHolder.likePic.setTag(position);
                    viewDetailHolder.likePic.setOnClickListener(mOnLikeClickListener);
                 }



                break;
            case TYPE_MATRIX:

                try {
                    BitmapFactory.Options sg;
                    Model.getInstance().loadPhoto(holder.mItem.getPhotoId(), new Model.LoadPhotoListener() {
                        @Override
                        public void onResult(Bitmap photo) {
                            holder.mtopImg.setImageBitmap(photo);


                        }
                    });
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
             break;
        }


        //


        //holder.mbtd.setText(mValues.get(position).content);
       // holder.mlikesTxt.setText("5");
        //holder.mContentView.setText(mValues.get(position).content);

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


    private View.OnClickListener mOnLikeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            // int position = (Integer) v.getTag();

            //if(((ImageView)v).getDrawable().equals(R.drawable.heart_outline))
            {
                // Access the row position here to get the correct data item
                mValues.get(position).incLikeCounter();
                ((ImageView) v).setImageResource(R.drawable.heart_full);
            }
            //else
            {
                // Access the row position here to get the correct data item
                //mItem.decLikeCounter();
                //((ImageView) v).setImageResource(R.drawable.heart_outline);
            }

            notifyDataSetChanged();
        }


    };



    @Override
    public int getItemCount() {
        return mValues.size();
    }

    //fill the row with constant info
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Post mItem;
        public final ImageView mtopImg;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mtopImg = (ImageView)  mView.findViewById(R.id.topImg);
        }

        @Override
        public String toString() {
         //   return super.toString() + " '" + mContentView.getText() + "'";
            return "";
        }

    }

    public class ViewDetailHolder extends ViewHolder {
        public final View mView;
        public final TextView mAuthorName;
        public final TextView likes;
        public final TextView mlikesTxt;
        public final ImageView likePic;
        public Post mItem;


        public ViewDetailHolder(View view) {
            super(view);
            mView = view;
            mAuthorName = (TextView) mView.findViewById(R.id.authorName);
            likes = (TextView) mView.findViewById(R.id.likes);

            mlikesTxt = (TextView)  mView.findViewById(R.id.likesTxt);
            likePic = (ImageView)  mView.findViewById(R.id.likePic);
        }

        @Override
        public String toString() {
            //   return super.toString() + " '" + mContentView.getText() + "'";
            return "";
        }

    }

    public class ViewMatrixHolder extends ViewHolder {
        public final View mView;
        public Post mItem;


        public ViewMatrixHolder(View view) {
            super(view);
            mView = view;
        }

        @Override
        public String toString() {
            //   return super.toString() + " '" + mContentView.getText() + "'";
            return "";
        }

    }


}

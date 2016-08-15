package com.example.lior.instakilo;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lior.instakilo.models.Model;
import com.example.lior.instakilo.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostDetailFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static PostDetailFragment fragment;
    Post post;
    TextView mlikesTxt;


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PostDetailFragment() {

    }

    public static PostDetailFragment newInstance() {
       if (fragment ==null)
       {
           fragment = new PostDetailFragment();
       }

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        post = bundle.getParcelable("com.example.instakilo.Post");
        View mView = inflater.inflate(R.layout.fragment_post_detail, container, false);
        TextView mAuthorName = (TextView) mView.findViewById(R.id.authorName);
        TextView likes = (TextView) mView.findViewById(R.id.likes);
        mlikesTxt = (TextView)  mView.findViewById(R.id.likesTxt);
        final ImageView topImg = (ImageView)  mView.findViewById(R.id.topImg);
        ImageView likePic = (ImageView)  mView.findViewById(R.id.likePic);
        if (post.isUserLiked(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            likePic.setPressed(true);
            //likePic.setImageResource(R.drawable.heart_full);
        }
        try {
            Model.getInstance().loadPhoto(post.getPhotoId(), new Model.LoadPhotoListener() {
                @Override
                public void onResult(Bitmap photo) {
                    topImg.setImageBitmap(photo);
                }
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (mAuthorName != null) {
            mAuthorName.setText(post.getAuthorName());
        }
        if (likes != null) {
            likes.setText("Likes: ");
        }
        if (mlikesTxt != null) {
           mlikesTxt.setText(Integer.toString(post.getLikeCounter()));
        }


        if (likePic != null) {

            if (post.isUserLiked(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                likePic.setImageResource(R.drawable.heart_full);

            } else {

                likePic.setImageResource(R.drawable.heart_outline);
            }
            likePic.setOnClickListener(mOnLikeClickListener);
        }
        return mView;
    }

    private  void UpdateLike()
    {
        mlikesTxt.setText(Integer.toString(post.getLikeCounter()));
    }

    private View.OnClickListener mOnLikeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // int position = (Integer) v.getTag();
           boolean isLike=  post.toggleLikeCounter(FirebaseAuth.getInstance().getCurrentUser().getUid());
            Model.getInstance().update(post, new Model.UpdateListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference, String key) {
                    Log.d("Alon", "Post liked:" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                }
            });

            if(isLike)
            {
                // Access the row position here to get the correct data item
                //mValues.get(position).incLikeCounter(FirebaseAuth.getInstance().getCurrentUser().getUid());
                ((ImageView) v).setImageResource(R.drawable.heart_full);
            }
            else
            {
                // Access the row position here to get the correct data item
                //mItem.decLikeCounter();
                ((ImageView) v).setImageResource(R.drawable.heart_outline);
            }
            //PostDetailFragment.newInstance().UpdateLike();
        }
    };

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}

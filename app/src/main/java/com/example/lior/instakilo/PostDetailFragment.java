package com.example.lior.instakilo;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lior.instakilo.models.Model;
import com.example.lior.instakilo.models.Post;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Post post;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PostDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostDetailFragment newInstance(String param1, String param2) {
        PostDetailFragment fragment = new PostDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        TextView mlikesTxt = (TextView)  mView.findViewById(R.id.likesTxt);
        final ImageView topImg = (ImageView)  mView.findViewById(R.id.topImg);
        ImageView likePic = (ImageView)  mView.findViewById(R.id.likePic);


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

            likePic.setOnClickListener(mOnLikeClickListener);
        }

        
        return mView;
    }

    private View.OnClickListener mOnLikeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // int position = (Integer) v.getTag();

            //if(((ImageView)v).getDrawable().equals(R.drawable.heart_outline))
            {
                // Access the row position here to get the correct data item
                post.incLikeCounter();
                ((ImageView) v).setImageResource(R.drawable.heart_full);
                //Delegate delegate = (Delegate) getActivity();
                //delegate.like();
            }
            //else
            {
                //Delegate delegate = (Delegate) getActivity();
                //delegate.deslike();
                // Access the row position here to get the correct data item
                //mItem.decLikeCounter();
                //((ImageView) v).setImageResource(R.drawable.heart_outline);
            }

        }


    };

    // TODO: Rename method, update argument and hook method into UI event
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

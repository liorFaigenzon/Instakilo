package com.example.lior.instakilo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserMainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserMainFragment extends Fragment{


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final int ORDERING_GRID = 3, ORDERING_LIST = 1;

    private OnFragmentInteractionListener mListener;

    public UserMainFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static UserMainFragment newInstance() {
        UserMainFragment fragment = new UserMainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_main_page, container, false);

        ImageView userImg = (ImageView) v.findViewById(R.id.userImg);
        userImg.setImageBitmap((Bitmap) getActivity().getIntent().getParcelableExtra("profilePicture"));

        TextView authorName = (TextView) v.findViewById(R.id.authorName);
        authorName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        Button logout = (Button) v.findViewById(R.id.button_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                Intent login = new Intent(getContext(), LoginActivity.class);
                getActivity().startActivity(login);
                getActivity().finish();
            }
        });

        ImageView matrixPic = (ImageView) v.findViewById(R.id.matrixPic);
        ImageView listPic = (ImageView) v.findViewById(R.id.listPic);


        listPic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ImageView matrixPic = (ImageView)  getView().findViewById(R.id.matrixPic);
                ImageView listPic = (ImageView)  v.findViewById(R.id.listPic);

                // TODO Auto-generated method stub
                matrixPic.setImageResource(R.drawable.black_matrix);
                listPic.setImageResource(R.drawable.blue_list);

                changeLayoutOrdering(ORDERING_LIST);
            }
        });

        matrixPic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ImageView matrixPic = (ImageView) getView().findViewById(R.id.matrixPic);
                ImageView listPic = (ImageView) getView().findViewById(R.id.listPic);

                matrixPic.setImageResource(R.drawable.blue_matrix);
                listPic.setImageResource(R.drawable.black_list);

                changeLayoutOrdering(ORDERING_GRID);;
            }
        });
        return v;

    }

    private void changeLayoutOrdering(int numColumns) {
        PostListFragment.changeNumColumns(numColumns);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


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
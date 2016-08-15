package com.example.lior.instakilo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lior.instakilo.dummy.PostContent;
import com.example.lior.instakilo.models.Post;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PostListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private static int mColumnCount = 1;
    private static OnListFragmentInteractionListener mListener;
    private static RecyclerView mRecyclerView;
    private static MyPostRecyclerViewAdapter mAdapter;
    private static Context mContext;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PostListFragment() {
    }


    @SuppressWarnings("unused")
    public static PostListFragment newInstance(int columnCount) {
        PostListFragment fragment = new PostListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    public static void changeNumColumns(int newNumColumns) {
        mColumnCount = newNumColumns;
        populateRecyclerView(mRecyclerView, mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            mContext = context;
            populateRecyclerView(mRecyclerView, mContext);
            mAdapter = new MyPostRecyclerViewAdapter(PostContent.ITEMS, mListener);
            mRecyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    public static void populateRecyclerView(RecyclerView recyclerView, Context context) {

        if(mAdapter == null) {
            mAdapter = new MyPostRecyclerViewAdapter(PostContent.ITEMS, mListener);
        }

        if (mColumnCount <= 1) {
            mAdapter.changeResource(R.layout.fragment_post_row);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mAdapter.changeResource(R.layout.fragment_post_matirx);
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
       if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                   + " must implement OnListFragmentInteractionListener");
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
    public interface OnListFragmentInteractionListener {

        void onListFragmentInteraction(Post post);

        void onLongListFragmentInteraction(Post mItem);
    }
}

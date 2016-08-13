package com.example.lior.instakilo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.lior.instakilo.models.Comment;
import com.example.lior.instakilo.models.Post;

public class DetailActivity extends AppCompatActivity implements PostDetailFragment.OnFragmentInteractionListener,
                                                                 CommentFragment.OnListFragmentInteractionListener{

    PostDetailFragment postDetailFragment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Intent i = getIntent();
        Post post = i.getParcelableExtra("com.example.instakilo.Post");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        postDetailFragment =new PostDetailFragment();
        //UserMainFragment fragment = new UserMainFragment();
        Bundle bundle = new Bundle();
        //ViewGroup.LayoutParams params = findViewById(android.R.id.content).getLayoutParams();
        // params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        //params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        //fragment.getView().setLayoutParams(params);
        bundle.putParcelable("com.example.instakilo.Post", post);
        postDetailFragment.setArguments(bundle);
        ft.replace(R.id.mainFragment,postDetailFragment);
        //ft.replace(R.id.main_frag_container,fragmentInstance);
       // ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(Comment item) {

    }


}

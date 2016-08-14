package com.example.lior.instakilo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.lior.instakilo.dummy.CommentContent;
import com.example.lior.instakilo.models.Comment;
import com.example.lior.instakilo.models.Model;
import com.example.lior.instakilo.models.Post;
import com.example.lior.instakilo.models.dialogs.DialogicFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements PostDetailFragment.OnFragmentInteractionListener,
                                                                 CommentFragment.OnListFragmentInteractionListener, View.OnClickListener{

    PostDetailFragment postDetailFragment ;
    Post post;
    private FloatingActionButton mAddNewRecordFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        setupFragment();
        setup();
    }

    private void setupFragment() {
        Intent i = getIntent();
        post = i.getParcelableExtra("com.example.instakilo.Post");
        CommentContent.getCommentByPostId(post.getId());
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

    private void setup() {
        mAddNewRecordFab = (FloatingActionButton) findViewById(R.id.activity_Post_AddNewRecord_FloatingActionButton);
        mAddNewRecordFab.setOnClickListener(this);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View v) {
        MaterialDialog materialDialog = DialogicFactory.getInputDialog(this);
        materialDialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                String content = dialog.getInputEditText().getText().toString().trim();
                if (content != "") {
                    final Comment newComment = new Comment(FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), post.getId(), content);

                    // Show progress bar before adding the comment
                    MainActivity.mainProgressBar.setVisibility(View.VISIBLE);

                    Model.getInstance().add(newComment, new Model.AddListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference, String key) {
                            CommentContent.addComment(newComment);
                        }
                    });
                }
            }
        }).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                //TODO: do what the fuck you want...
            }
        }).build();
        materialDialog.show();
    }

    @Override
    public void onListFragmentInteraction(Comment item) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommentContent.ITEMS = new ArrayList<>();
    }
}

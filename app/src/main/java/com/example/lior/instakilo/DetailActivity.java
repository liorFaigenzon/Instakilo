package com.example.lior.instakilo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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
    public static ProgressBar commentsProgressBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        commentsProgressBar = (ProgressBar)findViewById(R.id.commentProgressBar);
        DetailActivity.commentsProgressBar.setVisibility(View.VISIBLE);

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
        Bundle bundle = new Bundle();
        bundle.putParcelable("com.example.instakilo.Post", post);
        postDetailFragment.setArguments(bundle);
        ft.replace(R.id.mainFragment,postDetailFragment);
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
                    DetailActivity.commentsProgressBar.setVisibility(View.VISIBLE);

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

            }
        }).build();
        materialDialog.show();
    }

    @Override
    public void onListFragmentInteraction(Comment item) {

    }

    @Override
    public void onLongListFragmentInteraction(Comment comment) {
        final Comment deleteComment = comment;
        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_SHORT;

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(comment.getAuthorId())) {

            MaterialDialog materialDialog = DialogicFactory.getAcceptDialog(this);
            materialDialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    CommentContent.getInstance().deleteComment(deleteComment);
                    Toast.makeText(context,"Deleted comment",duration).show();

                }
            }).onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                }
            }).build();
            materialDialog.show();
        }
        else
        {
            Toast.makeText(context,"Can't delete comment", duration).show();;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommentContent.ITEMS = new ArrayList<>();
    }
}

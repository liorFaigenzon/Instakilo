package com.example.lior.instakilo;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.example.lior.instakilo.dummy.PostContent;
import com.example.lior.instakilo.models.Model;
import com.example.lior.instakilo.models.PicModeSelectDialogFragment;
import com.example.lior.instakilo.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends FragmentActivity implements UserMainFragment.OnFragmentInteractionListener,
                                                               PostListFragment.OnListFragmentInteractionListener,
                                                               PostDetailFragment.OnFragmentInteractionListener
{

    static final int REQUEST_IMAGE_CAPTURE = 100;
    static final int REQUEST_IMAGE_SELECT = 200;
    private FloatingActionButton mAddNewRecordFab;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Model.getInstance().attachCacheListener(Model.ModelClass.POST);
        Model.getInstance().attachCacheListener(Model.ModelClass.COMMENT);
        setup();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i("FragmentAlertDialog", "camera click!");
    }

    @Override
    public void onListFragmentInteraction(Post post) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("com.example.instakilo.Post", post);
        startActivity(intent);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        PostDetailFragment fragment =new PostDetailFragment();
        //UserMainFragment fragment = new UserMainFragment();
        Bundle bundle = new Bundle();
        //ViewGroup.LayoutParams params = findViewById(android.R.id.content).getLayoutParams();
       // params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        //params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        //fragment.getView().setLayoutParams(params);
        bundle.putParcelable("com.example.instakilo.Post", post);
        //bundle.putParcelable("setLayoutParams.Post", params);
        fragment.setArguments(bundle);
       // ft.replace(R.id.main_frag_container2,fragment);
        //ft.addToBackStack(null);
        //ft.commit();

    }

    private void setup() {
        mAddNewRecordFab = (FloatingActionButton) findViewById(R.id.activity_Post_AddNewRecord_FloatingActionButton);
        mAddNewRecordFab.setOnClickListener(new DialogPicActivity(this));
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_SELECT)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_IMAGE_CAPTURE)
                onCapturePhotoResult(data);
        }
    }

    private void onCapturePhotoResult(Intent data) {

        // Get thumbnail photo
        final Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        addPost(thumbnail);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap thumbnail = null;

        // Check if selected photo
        if (data != null) {
            try {

                // Get a bitmap object from the selected photo
                thumbnail = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                addPost(thumbnail);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("Nir", "onSelectFromGalleryResult:noPhotoSelected");
        }
    }

    private void addPost(final Bitmap thumbnail) {
        // Save the thumbnail to cloudinary
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // Add a new post
        final String photoId = FirebaseAuth.getInstance().getCurrentUser().getUid() + timeStamp + ".jpg";
        final Post newPost = new Post(FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), photoId);

        // Add the photo
        Model.getInstance().add(newPost, new Model.AddListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference, String key) {
                Model.getInstance().savePhoto(thumbnail, photoId);
                PostContent.getInstance().addPost(newPost);
            }
        });


    }

    private void dispatchCameraIntent() {

        // Create camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check that there is an activity that can handle such request
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {

            // Start camera intent
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchGalleryIntent() {

        // Create camera intent
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");

        // Check that there is an activity that can handle such request
        if (galleryIntent.resolveActivity(getPackageManager()) != null) {

            // Start gallery intent to select single image
            startActivityForResult(Intent.createChooser(galleryIntent, "Select File"), REQUEST_IMAGE_SELECT);
        }
    }

    public class DialogPicActivity implements View.OnClickListener, PicModeSelectDialogFragment.IPicModeSelectListener{

        MainActivity muserMainFragment;

        DialogPicActivity(MainActivity userMainFragment)
        {
            muserMainFragment = userMainFragment;
        }

        @Override
        public void onPicModeSelected(String mode) {
            if (mode.equalsIgnoreCase("camera"))
                muserMainFragment.dispatchCameraIntent();
            else if (mode.equalsIgnoreCase("gallery"))
                muserMainFragment.dispatchGalleryIntent();
            else Log.i("FragmentAlertDialog", "cancel click!");
        }

        @Override
        public void onClick(View v) {
            PicModeSelectDialogFragment frag = new PicModeSelectDialogFragment();
            frag.setiPicModeSelectListener(this);
            frag.show(getFragmentManager(), "Choose way");
        }


    }
}

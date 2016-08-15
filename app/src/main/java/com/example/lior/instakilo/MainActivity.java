package com.example.lior.instakilo;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.lior.instakilo.dummy.PostContent;
import com.example.lior.instakilo.models.Model;
import com.example.lior.instakilo.models.PicModeSelectDialogFragment;
import com.example.lior.instakilo.models.Post;
import com.example.lior.instakilo.models.dialogs.DialogicFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.io.IOException;
import android.net.Uri;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends FragmentActivity implements UserMainFragment.OnFragmentInteractionListener,
                                                               PostListFragment.OnListFragmentInteractionListener,
                                                               PostDetailFragment.OnFragmentInteractionListener
{
    static final int REQUEST_IMAGE_CAPTURE = 100;
    static final int REQUEST_IMAGE_SELECT = 200;
    static final int REQUEST_PERMISSIONS_CAMERA = 300;
    static final int REQUEST_PERMISSIONS_STORAGE = 400;
    private FloatingActionButton mAddNewRecordFab;
    public static ProgressBar mainProgressBar = null;
    private Bitmap thumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainProgressBar = (ProgressBar)findViewById(R.id.mainProgressBar);
        MainActivity.mainProgressBar.setVisibility(View.VISIBLE);

        Model.getInstance().attachCacheListener(Model.ModelClass.POST);
        Model.getInstance().attachCacheListener(Model.ModelClass.COMMENT);
        setup();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i("FragmentAlertDialog", "camera click!");
    }

    @Override
    public void onLongListFragmentInteraction(Post post) {
        final Post deletePost = post;
        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_SHORT;


        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(deletePost.getAuthorId())) {
            MaterialDialog materialDialog = DialogicFactory.getAcceptDialog(this);
            materialDialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    PostContent.getInstance().deletePost(deletePost);
                    Toast.makeText(context,"Deleted photo",duration).show();

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
            Toast.makeText(context,"Can't delete photo", duration).show();;
        }
    }

    @Override
    public void onListFragmentInteraction(Post post) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("com.example.instakilo.Post", post);
        startActivity(intent);
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
            else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                onCapturePhotoResult(data);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_CAMERA: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchCameraIntent();
                }

                return;
            }
            case REQUEST_PERMISSIONS_STORAGE: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String photoId = FirebaseAuth.getInstance().getCurrentUser().getUid() + timeStamp + ".jpg";

                    if (this.thumbnail != null) {
                        Model.getInstance().savePhoto(thumbnail, photoId);
                    }
                }

                return;
            }
        }
    }

    private void onCapturePhotoResult(Intent data) {

        Uri imageUri = data.getData();

        this.getContentResolver().notifyChange(imageUri, null);
        ContentResolver cr = this.getContentResolver();

        // Get thumbnail photo
        Bitmap thumbnail = null;
        try {
            thumbnail = MediaStore.Images.Media.getBitmap(cr, imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        this.thumbnail = thumbnail;

        // Save the thumbnail to cloudinary
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // Add a new post
        final String photoId = FirebaseAuth.getInstance().getCurrentUser().getUid() + timeStamp + ".jpg";
        final Post newPost = new Post(FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), photoId);

        // Show progress bar before adding the post
        MainActivity.mainProgressBar.setVisibility(View.VISIBLE);

        // Add the photo
        Model.getInstance().add(newPost, new Model.AddListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference, String key) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_STORAGE);
                } else {
                    Model.getInstance().savePhoto(thumbnail, photoId);
                }
                newPost.setId(key);
                PostContent.getInstance().addPost(newPost);
            }
        });
    }

    private void dispatchCameraIntent() {

        // Create camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check that there is an activity that can handle such request
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            final String photoId = FirebaseAuth.getInstance().getCurrentUser().getUid() + timeStamp + ".jpg";

            File photoFile = null;
            try {
                photoFile = Model.getInstance().createTempImageFile(photoId);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.lior.instakilo",
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // Start camera intent
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
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
            if (mode.equalsIgnoreCase("camera")) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.CAMERA}, REQUEST_PERMISSIONS_CAMERA);
                } else {
                    muserMainFragment.dispatchCameraIntent();
                }
            }
            else if (mode.equalsIgnoreCase("gallery")) {
                muserMainFragment.dispatchGalleryIntent();
            } else {
                Log.i("FragmentAlertDialog", "cancel click!");
            }
        }

        @Override
        public void onClick(View v) {
            PicModeSelectDialogFragment frag = new PicModeSelectDialogFragment();
            frag.setiPicModeSelectListener(this);
            frag.show(getFragmentManager(), "Choose way");
        }


    }
}

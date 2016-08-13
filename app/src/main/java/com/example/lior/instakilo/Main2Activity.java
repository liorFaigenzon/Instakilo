package com.example.lior.instakilo;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.example.lior.instakilo.models.Post;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main2Activity extends FragmentActivity implements UserMainFragment.OnFragmentInteractionListener,
                                                               PostListFragment.OnListFragmentInteractionListener,
                                                               PostDetailFragment.OnFragmentInteractionListener,
                                                                UserMainFragment.Delegate
{

    static final int REQUEST_IMAGE_CAPTURE = 100;
    static final int REQUEST_IMAGE_SELECT = 200;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

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
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

        // Save the thumbnail to cloudinary
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //Model.getInstance().savePhoto(thumbnail, "test1" + timeStamp + ".jpg");
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap thumbnail = null;

        // Check if selected photo
        if (data != null) {
            try {

                // Get a bitmap object from the selected photo
                thumbnail = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

                // Save the thumbnail to cloudinary
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                //Model.getInstance().savePhoto(thumbnail, "test1" + timeStamp + ".jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("Nir", "onSelectFromGalleryResult:noPhotoSelected");
        }
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

    @Override
    public void saveCamera() {
        dispatchCameraIntent();
    }

    @Override
    public void saveGallary() {
        dispatchGalleryIntent();
    }
}

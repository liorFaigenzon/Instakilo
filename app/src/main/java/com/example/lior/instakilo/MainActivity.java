package com.example.lior.instakilo;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.lior.instakilo.models.Model;
import com.example.lior.instakilo.models.PicModeSelectDialogFragment;
import com.example.lior.instakilo.models.Post;
import com.example.lior.instakilo.models.PostAdapter;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends ListActivity{

    static final int REQUEST_IMAGE_CAPTURE = 100;
    static final int REQUEST_IMAGE_SELECT = 200;

    // declare class variables
    private ArrayList<Post> m_parts = new ArrayList<Post>();
    private Runnable viewParts;
    private PostAdapter m_adapter;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView list = getListView();
        GridView gridview = (GridView) findViewById(R.id.gridview);
        ImageView matrixPic = (ImageView) findViewById(R.id.matrixPic);
        ImageView listPic = (ImageView) findViewById(R.id.listPic);
        ImageView takePic = (ImageView) findViewById(R.id.takePic);

        takePic.setOnClickListener(new DialogPicActivity());

        listPic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ImageView matrixPic = (ImageView) findViewById(R.id.matrixPic);
                ImageView listPic = (ImageView) findViewById(R.id.listPic);
                ListView list = getListView();
                GridView gridview = (GridView) findViewById(R.id.gridview);
                // TODO Auto-generated method stub
                matrixPic.setImageResource(R.drawable.black_matrix);
                listPic.setImageResource(R.drawable.blue_list);

                gridview.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
            }
        });

        matrixPic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ImageView matrixPic = (ImageView) findViewById(R.id.matrixPic);
                ImageView listPic = (ImageView) findViewById(R.id.listPic);
                ListView list = getListView();
                GridView gridview = (GridView) findViewById(R.id.gridview);

                matrixPic.setImageResource(R.drawable.blue_matrix);
                listPic.setImageResource(R.drawable.black_list);

                // TODO Auto-generated method stub
                list.setVisibility(View.GONE);
                gridview.setVisibility(View.VISIBLE);
            }
        });

        gridview.setVisibility(View.GONE);

        // instantiate our PostAdapter class
        m_adapter = new PostAdapter(this, R.layout.post_listview, m_parts);
        setListAdapter(m_adapter);
        gridview.setAdapter(m_adapter);


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Post post = (Post)m_adapter.getItem(position);
                Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
                intent.putExtra("com.example.instakilo.Post", post);
                startActivity(intent);
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           public void onItemClick(AdapterView<?> parent, View v,
                                  int position, long id) {
               Post post = (Post)m_adapter.getItem(position);
               Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
               intent.putExtra("com.example.instakilo.Post", post);
               startActivity(intent);
            }
        });

        // here we are defining our runnable thread.
        viewParts = new Runnable(){
            public void run(){
                handler.sendEmptyMessage(0);
            }
        };

        // here we call the thread we just defined - it is sent to the handler below.
        Thread thread =  new Thread(null, viewParts, "MagentoBackground");
        thread.start();
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
        Model.getInstance().savePhoto(thumbnail, "test1" + timeStamp + ".jpg");
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
                Model.getInstance().savePhoto(thumbnail, "test1" + timeStamp + ".jpg");
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

    private Handler handler = new Handler()
    {
        public void handleMessage(final Message msg)
        {
            // create some objects
            // here is where you could also request data from a server
            // and then create objects from that data.

            Model.getInstance().getAll(Model.ModelClass.POST, new Model.GetManyListener() {
                @Override
                public void onResult(List<Object> objects) {
                    for (Object post : objects) {
                        m_parts.add((Post)post);
                    }
                }

                @Override
                public void onCancel() {

                }
            });

            /*Model.getInstance().attachCacheListener(Model.ModelClass.POST);

            Model.getInstance().getAll(Model.ModelClass.POST, new Model.GetManyListener() {
                @Override
                public void onResult(List<Object> objects) {
                    Log.d("Nir", "Objects returned: " + objects.size());

                    Model.getInstance().add(firstPost, new Model.AddListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference, String key) {
                            Log.d("Nir", key);
                        }
                    });
                }

                @Override
                public void onCancel() {

                }
            });*/

            //dispatchCameraIntent();
            //dispatchGalleryIntent();
            /*Model.getInstance().loadPhoto("test120160812_141416", new Model.LoadPhotoListener() {
                @Override
                public void onResult(Bitmap photo) {
                    if (photo != null) {
                        Log.d("Nir", "Download photo from cloudinary succeeded");
                    }
                }
            });*/

            m_adapter = new PostAdapter(MainActivity.this, R.layout.post_listview, m_parts);

            // display the list.
            setListAdapter(m_adapter);
        }


    };

    public class DialogPicActivity implements View.OnClickListener,PicModeSelectDialogFragment.IPicModeSelectListener
    {
        @Override
        public void onPicModeSelected(String mode) {
            if (mode.equalsIgnoreCase("camera"))
                Log.i("FragmentAlertDialog", "camera click!");
            else if (mode.equalsIgnoreCase("gallery")) Log.i("FragmentAlertDialog", "gallery click!");
            else   Log.i("FragmentAlertDialog", "cancel click!");
        }

        @Override
        public void onClick(View v) {
            PicModeSelectDialogFragment frag = new PicModeSelectDialogFragment();
            frag.setiPicModeSelectListener(this);
            frag.show(getFragmentManager(), "Choose way");
        }
    }
}
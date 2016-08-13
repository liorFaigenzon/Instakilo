package com.example.lior.instakilo;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.lior.instakilo.models.PicModeSelectDialogFragment;
import com.example.lior.instakilo.models.Post;
import com.example.lior.instakilo.models.PostAdapter;

import java.util.ArrayList;

public class MainActivity extends ListActivity {

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
        //FrameLayout fragment_container = (FrameLayout) findViewById(R.id.fragment_container);

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

        //fragment_container.setVisibility(View.GONE);
        gridview.setVisibility(View.GONE);

        // instantiate our PostAdapter class
       // m_adapter = new PostAdapter(this, R.layout.activity_login, m_parts);
       // setListAdapter(m_adapter);
        //gridview.setAdapter(m_adapter);


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //FrameLayout fragment_container = (FrameLayout) findViewById(R.id.fragment_container);
                //PostDetailFragment fragmentInstance = new PostDetailFragment();
                Post post = (Post)m_adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putParcelable("com.example.instakilo.Post", post);
                //fragmentInstance.setArguments(bundle);
                //FragmentManager fragmentManager = getFragmentManager();
                //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                // add fragment to the fragment container layout
                //fragment_container.addView(R.id.fragment_container, fragmentInstance).commit();;

                //Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
                //intent.putExtra("com.example.instakilo.Post", post);
                //startActivity(intent);
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           public void onItemClick(AdapterView<?> parent, View v,
                                  int position, long id) {
               Post post = (Post)m_adapter.getItem(position);
              // Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
              // intent.putExtra("com.example.instakilo.Post", post);
              // startActivity(intent);
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

    private Handler handler = new Handler()
    {
        public void handleMessage(final Message msg)
        {
            // create some objects
            // here is where you could also request data from a server
            // and then create objects from that data.

            Post firstPost = new Post("2", "Nir Kigelman", "photo2");
            firstPost.incLikeCounter();
            firstPost.getLikeUsers().put("3", true);
            firstPost.incLikeCounter();
            firstPost.getLikeUsers().put("4", true);
            firstPost.incLikeCounter();
            firstPost.getLikeUsers().put("2", true);

            m_parts.add(firstPost);
            m_parts.add(firstPost);
            m_parts.add(firstPost);
            m_parts.add(firstPost);

            //m_adapter = new PostAdapter(MainActivity.this, R.layout.post_listview, m_parts);

            // display the list.
            //setListAdapter(m_adapter);
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
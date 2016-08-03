package com.example.lior.instakilo;


import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.lior.instakilo.model.Post;
import com.example.lior.instakilo.model.PostAdapter;

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

        // instantiate our PostAdapter class
        m_adapter = new PostAdapter(this, R.layout.post_listview, m_parts);
        setListAdapter(m_adapter);

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
        public void handleMessage(Message msg)
        {
            // create some objects
            // here is where you could also request data from a server
            // and then create objects from that data.
            //(String id,String photoId,  String userId, String title, String content, int likeCounter,  boolean checked)
            m_parts.add(new Post("id","photoid","userid","New POST","CONTENT xxxyyyy",5,true));
            m_parts.add(new Post("id","photoid","user id","Much wow","very cool",15,true));

            m_adapter = new PostAdapter(MainActivity.this, R.layout.post_listview, m_parts);

            // display the list.
            setListAdapter(m_adapter);
        }
    };
}
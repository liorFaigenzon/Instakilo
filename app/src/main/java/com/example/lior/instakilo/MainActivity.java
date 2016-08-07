package com.example.lior.instakilo;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.lior.instakilo.models.Model;
import com.example.lior.instakilo.models.Post;
import com.example.lior.instakilo.models.PostAdapter;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

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
        public void handleMessage(final Message msg)
        {
            // create some objects
            // here is where you could also request data from a server
            // and then create objects from that data.
            //(String id,String photoId,  String userId, String title, String content, int likeCounter,  boolean checked)
            Model.getInstance().signup("kigelman.nir@gmail.com", "123456", new Model.AuthListener() {
                @Override
                public void onDone(String userId, Exception e) {
                    //Model.getInstance().add(new Post("5","photoid","userid","New POST","CONTENT xxxyyyy",5,true));
                }
            });

            Post firstPost = new Post("2", "Nir Kigelman", "photo2");
            firstPost.incLikeCounter();
            firstPost.getLikeUsers().put("3", true);
            firstPost.incLikeCounter();
            firstPost.getLikeUsers().put("4", true);
            firstPost.incLikeCounter();
            firstPost.getLikeUsers().put("2", true);
            Model.getInstance().add(firstPost, new Model.AddListener() {

                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference, String key) {
                    if (databaseError == null) {
                        Log.i("Nir", "Post has been added!");
                        Log.i("Nir", "Post id: " + key);

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Model.getInstance().delete(new Post(key, "2", null, null, 1, null), new Model.DeleteListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference, String key) {
                                if (databaseError == null) {
                                    Log.i("Nir", "Post has been deleted!");
                                    Log.i("Nir", "Post id: " + key);
                                } else {
                                    Log.w("Nir", "Delete post got error: ", databaseError.toException());
                                }
                            }
                        });
                    } else {
                        Log.w("Nir", "Add post got error: ", databaseError.toException());
                    }
                }
            });

            m_parts.add(firstPost);

            m_adapter = new PostAdapter(MainActivity.this, R.layout.post_listview, m_parts);

            // display the list.
            setListAdapter(m_adapter);
        }
    };
}
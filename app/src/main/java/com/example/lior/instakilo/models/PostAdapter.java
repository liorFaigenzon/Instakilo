package com.example.lior.instakilo.models;

/**
 * Created by lior on 03/08/2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.lior.instakilo.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class PostAdapter extends ArrayAdapter<Post> {

    // declaring our ArrayList of Posts
    private ArrayList<Post> objects;

    public int getCount() {
        return objects.size();
    }

    public long getItemId(int position) {
        return 0;
    }

    public Post getItem(int position) {
        return objects.get(position);
    }

    /* here we must override the constructor for ArrayAdapter
    * the only variable we care about now is ArrayList<Post> objects,
    * because it is the list of objects we want to display.
    */
    public PostAdapter(Context context, int textViewResourceId, ArrayList<Post> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;

    }

    /*
     * we are overriding the getView method here - this is what defines how each
     * list Post will look.
     */
    public View getView(int position, View convertView, ViewGroup parent){

        // assign the view we are converting to a local variable
        View v = convertView;
        LayoutInflater inflater = null;
        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        //if (v == null)
         {
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Post object.
		 */
        Post i = objects.get(position);

        if (i != null) {

            if (parent instanceof GridView)
            {
                v = inflater.inflate(R.layout.fragment_post_matirx,null);
                //ImageView img = (ImageView) v.findViewById(R.id.topImg);
                //img.getLayoutParams().height = 200;
                //img.getLayoutParams().width = 300;
                //img.requestLayout();
            }
            else if (parent instanceof ListView)
            {
                v = inflater.inflate(R.layout.fragment_post_row, null);
                // This is how you obtain a reference to the TextViews.
                // These TextViews are created in the XML files we defined.



            }
        }

        // the view must be returned to our activity
        return v;

    }



    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private View.OnClickListener mOnLikeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();

            //if(((ImageView)v).getDrawable().equals(R.drawable.heart_outline))
            {
                // Access the row position here to get the correct data item
                objects.get(position).incLikeCounter(FirebaseAuth.getInstance().getCurrentUser().getUid());
                ((ImageView) v).setImageResource(R.drawable.heart_full);
            }
            //else
            {
                // Access the row position here to get the correct data item
                objects.get(position).decLikeCounter(FirebaseAuth.getInstance().getCurrentUser().getUid());
                ((ImageView) v).setImageResource(R.drawable.heart_outline);
            }

            notifyDataSetChanged();
        }


    };

    private View.OnClickListener mOnCommentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            // Access the row position here to get the correct data item
            Post pst = objects.get(position);

        }
    };

}

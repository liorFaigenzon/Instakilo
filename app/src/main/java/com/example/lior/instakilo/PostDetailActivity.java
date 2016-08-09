package com.example.lior.instakilo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lior.instakilo.models.Post;

/**
 * Created by lior on 09/08/2016.
 */
public class PostDetailActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_detailview);
        Bundle bundle = getIntent().getExtras();
        Post post = bundle.getParcelable("com.example.instakilo.Post");
        View v = this.findViewById(android.R.id.content);
        TextView ttd = (TextView) v.findViewById(R.id.toptextdata);
        TextView mt = (TextView) v.findViewById(R.id.middletext);
        TextView mtd = (TextView) v.findViewById(R.id.middletextdata);
        TextView bt = (TextView) v.findViewById(R.id.bottomtext);
        TextView btd = (TextView) v.findViewById(R.id.desctext);
        ImageView likePic = (ImageView) v.findViewById(R.id.likePic);
        ImageView commentPic = (ImageView) v.findViewById(R.id.commentPic);

        //likePic.setTag(position);
        likePic.setOnClickListener(mOnLikeClickListener);
        //commentPic.setTag(position);
        commentPic.setOnClickListener(mOnCommentClickListener);


        // check to see if each individual textview is null.
        // if not, assign some text!
        if (mt != null) {
            mt.setText("Content: ");
        }
        if (mtd != null) {
            mtd.setText(post.getPhotoId());
        }
        if (bt != null) {
            bt.setText("Likes: ");
        }
        if (btd != null) {
            btd.setText(Integer.toString(post.getLikeCounter()));
        }
    }

    private View.OnClickListener mOnLikeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();

            //if(((ImageView)v).getDrawable().equals(R.drawable.heart_outline))
            {
                // Access the row position here to get the correct data item
                //objects.get(position).incLikeCounter();
                ((ImageView) v).setImageResource(R.drawable.heart_full);
            }
            //else
            {
                // Access the row position here to get the correct data item
                //objects.get(position).decLikeCounter();
                ((ImageView) v).setImageResource(R.drawable.heart_outline);
            }

            //notifyDataSetChanged();
        }


    };

    private View.OnClickListener mOnCommentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            // Access the row position here to get the correct data item
            //Post pst = objects.get(position);

        }
    };

}

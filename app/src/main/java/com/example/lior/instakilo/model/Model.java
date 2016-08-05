package com.example.lior.instakilo.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.lior.instakilo.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by eliav.menachi on 25/03/2015.
 */
public class Model {
    private final static Model instance = new Model();
    Context context;
    ModelFirebase modelFirebase;
    ModelCoulinary modelCloudinary;
    ModelSql modelSql;

    private Model(){
        context = MyApplication.getAppContext();
        modelFirebase = new ModelFirebase(MyApplication.getAppContext());
        //modelCloudinary = new ModelCoulinary();
        modelSql = new ModelSql(MyApplication.getAppContext());
    }

    public static Model getInstance(){
        return instance;
    }

    public interface AuthListener{
        void onDone(String userId, Exception e);
    }
    public void login(String email, String pwd, AuthListener listener){
        modelFirebase.login( email,  pwd,  listener);
    }
    public void signup(String email, String pwd, AuthListener listener){
        modelFirebase.signup( email,  pwd,  listener);
    }
    public String getUserId(){
        return modelFirebase.getUserId();
    }

    public void signout(){
        modelFirebase.signout();
    }

    public interface GetPostsListener{
        public void onResult(List<Post> students);
        public void onCancel();
    }

    public void getAllPostsAsynch(final GetPostsListener listener){
        final String lastUpdateDate = PostSql.getLastUpdateDate(modelSql.getReadbleDB());
        modelFirebase.getAllPostsAsynch(new GetPostsListener() {
            @Override
            public void onResult(List<Post> students) {
                if(students != null && students.size() > 0) {
                    //update the local DB
                    String reacentUpdate = lastUpdateDate;
                    for (Post s : students) {
                        PostSql.add(modelSql.getWritableDB(), s);
                        if (reacentUpdate == null || s.getLastUpdated().compareTo(reacentUpdate) > 0) {
                            reacentUpdate = s.getLastUpdated();
                        }
                        Log.d("TAG","updating: " + s.toString());
                    }
                    PostSql.setLastUpdateDate(modelSql.getWritableDB(), reacentUpdate);
                }
                //return the complete student list to the caller
                List<Post> res = PostSql.getAllPosts(modelSql.getReadbleDB());
                listener.onResult(res);
            }

            @Override
            public void onCancel() {
                listener.onCancel();
            }
        },lastUpdateDate);
    }

    public interface GetPost{
        public void onResult(Post student);
        public void onCancel();
    }
    public void getPostById(String id, GetPost listener){
        modelFirebase.getPostById(id,listener);
    }

    public void add(Post pst){
        //modelFirebase.add(pst);

    }


    public void saveImage(final Bitmap imageBitmap, final String imageName) {
        saveImageToFile(imageBitmap,imageName); // synchronously save image locally
        Thread d = new Thread(new Runnable() {  // asynchronously save image to parse
            @Override
            public void run() {
                modelCloudinary.saveImage(imageBitmap,imageName);
            }
        });
        d.start();
    }

    public interface LoadImageListener{
        public void onResult(Bitmap imageBmp);
    }

    public void loadImage(final String imageName, final LoadImageListener listener) {
        AsyncTask<String,String,Bitmap> task = new AsyncTask<String, String, Bitmap >() {
            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap bmp = loadImageFromFile(imageName);              //first try to fin the image on the device
                if (bmp == null) {                                      //if image not found - try downloading it from parse
                    bmp = modelCloudinary.loadImage(imageName);
                    if (bmp != null) saveImageToFile(bmp,imageName);    //save the image locally for next time
                }
                return bmp;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                listener.onResult(result);
            }
        };
        task.execute();
    }

    private void saveImageToFile(Bitmap imageBitmap, String imageFileName){
        FileOutputStream fos;
        OutputStream out = null;
        try {

            //File dir = context.getExternalFilesDir(null);
//            boolean hasPermission = (MyApplication.getAppContext().checkPermission(Manifest.permission.) == PackageManager.PERMISSION_GRANTED);
//            if (!hasPermission) {
//                Log.d(TAG, "Has no permission! Ask!");
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
//            } else {
//                Log.d(TAG, "Permission already given!");
//                write();
//            }


            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File imageFile = new File(dir,imageFileName);
            imageFile.createNewFile();

            out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

            //add the picture to the gallery so we dont need to manage the cache size
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(imageFile);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
            Log.d("tag","add image to cache: " + imageFileName);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap loadImageFromFile(String imageFileName){
        String str = null;
        Bitmap bitmap = null;
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir,imageFileName);

            //File dir = context.getExternalFilesDir(null);
            InputStream inputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(inputStream);
            Log.d("tag","got image from cache: " + imageFileName);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}

package com.example.lior.instakilo.models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.lior.instakilo.MyApplication;
import com.example.lior.instakilo.models.cloudinary.ModelCloudinary;
import com.example.lior.instakilo.models.firebase.ModelFirebase;
import com.example.lior.instakilo.models.sqlite.ModelSql;
import com.facebook.AccessToken;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class Model {

    public enum ModelClass {
        POST,
        COMMENT
    }

    private final static Model instance = new Model();
    Context context;
    ModelFirebase modelFirebase;
    ModelCloudinary modelCloudinary;
    ModelSql modelSql;

    private Model(){
        context = MyApplication.getAppContext();
        modelFirebase = new ModelFirebase(MyApplication.getAppContext());
        modelCloudinary = new ModelCloudinary();
        modelSql = new ModelSql(MyApplication.getAppContext());
    }

    public static Model getInstance(){
        return instance;
    }

    public interface AuthListener{
        void onDone(String userId, Exception e);
    }

    public void signIn(AccessToken token, AuthListener listener){
        modelFirebase.signIn(token,  listener);
    }

    public void signOut(){
        modelFirebase.signOut();
    }

    public String getUserId(){
        return modelFirebase.getUserId();
    }

    public interface GetManyListener {
        void onResult(List<Object> objects);
        void onCancel();
    }

    public void getAll(ModelClass model, final GetManyListener listener){

        // TODO: Change the hole method to use cache
        final String lastUpdateDate = "2016-08-07 08:00:00";// PostSql.getLastUpdateDate(modelSql.getReadbleDB());
        modelFirebase.getAll(model, lastUpdateDate, new GetManyListener() {
            @Override
            public void onResult(List<Object> objects) {
                //if(objects != null && objects.size() > 0) {
                    //update the local DB
                    /*String reacentUpdate = lastUpdateDate;
                    for (Post s : posts) {
                        PostSql.add(modelSql.getWritableDB(), s);
                        if (reacentUpdate == null || s.getLastUpdated().compareTo(reacentUpdate) > 0) {
                            reacentUpdate = s.getLastUpdated();
                        }
                        Log.d("TAG","updating: " + s.toString());
                    }
                    PostSql.setLastUpdateDate(modelSql.getWritableDB(), reacentUpdate);*/
                //}
                //return the complete student list to the caller
                //List<Post> res = PostSql.getAllPosts(modelSql.getReadbleDB());
                listener.onResult(objects);
            }

            @Override
            public void onCancel() {
                listener.onCancel();
            }
        });
    }

    public interface GetOneListener{
        void onResult(Object object);
        void onCancel();
    }

    public void getById(ModelClass model, String id, GetOneListener listener){
        modelFirebase.getById(model, id, listener);
    }

    public interface AddListener {
        void onComplete(DatabaseError databaseError, DatabaseReference databaseReference, String key);
    }

    public void add(Object model, AddListener listener){
        modelFirebase.add(model, listener);
    }

    public interface UpdateListener {
        void onComplete(DatabaseError databaseError, DatabaseReference databaseReference, String key);
    }

    public void update(Object model, UpdateListener listener){
        modelFirebase.update(model, listener);
    }

    public interface DeleteListener {
        void onComplete(DatabaseError databaseError, DatabaseReference databaseReference, String key);
    }

    public void delete(Object model, DeleteListener listener){
        modelFirebase.delete(model, listener);
    }

    public void attachCacheListener(ModelClass model) {
        modelFirebase.attachCacheListener(model);
    }

    public void savePhoto(final Bitmap photoBitmap, final String photoName) {
        //saveImageToFile(imageBitmap, imageName); // synchronously save image locally
        Thread savePhoto = new Thread(new Runnable() {  // asynchronously save image to parse
            @Override
            public void run() {
                modelCloudinary.savePhoto(photoBitmap, photoName);
            }
        });
        savePhoto.start();
    }

    public interface LoadPhotoListener {
        void onResult(Bitmap photo);
    }

    public void loadPhoto(final String photoId, final LoadPhotoListener listener) {
        AsyncTask<String,String,Bitmap> task = new AsyncTask<String, String, Bitmap >() {
            @Override
            protected Bitmap doInBackground(String... params) {
                //Bitmap photo = loadImageFromFile(photoId);              //first try to fin the image on the device
                //if (photo == null) {

                    // Download photo from cloudinary
                    Bitmap photo = modelCloudinary.loadPhoto(photoId);

                    // Save to cache
                    //if (photo != null) {
                    //    saveImageToFile(bmp,photoId);    //save the image locally for next time
                    //}
                //}
                return photo;
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

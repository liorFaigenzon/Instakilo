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
import com.google.firebase.auth.AuthCredential;
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
import java.util.concurrent.ExecutionException;

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

    public void signIn(AuthCredential credential, AuthListener listener){
        modelFirebase.signIn(credential,  listener);
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

    public void getAll(final ModelClass model, final GetManyListener listener){

        final String lastUpdateDate = modelSql.getLastUpdateData(model);

        modelFirebase.getAll(model, lastUpdateDate, new GetManyListener() {
            @Override
            public void onResult(List<Object> objects) {
                if(objects != null && objects.size() > 0) {
                    //update the local DB
                    String reacentUpdate = lastUpdateDate;

                    for (Object m : objects) {
                        modelSql.add(m);

                        switch (model) {
                            case POST:
                                if (reacentUpdate == null || ((Post)m).getLastUpdated().compareTo(reacentUpdate) > 0) {
                                    reacentUpdate = ((Post)m).getLastUpdated();
                                }
                                Log.d("TAG", "updating: " + ((Post)m).toString());
                                break;
                            case COMMENT:
                                if (reacentUpdate == null || ((Comment)m).getLastUpdated().compareTo(reacentUpdate) > 0) {
                                    reacentUpdate = ((Comment)m).getLastUpdated();
                                }
                                Log.d("TAG", "updating: " + ((Comment)m).toString());
                                break;
                        }
                    }

                    modelSql.setLastUpdateData(model, reacentUpdate);
                }

                //return the complete objects list to the caller
                List<Object> res = modelSql.getAll(model);
                listener.onResult(res);
            }

            @Override
            public void onCancel() {
                listener.onCancel();
            }
        });
    }

    public void getCommentsByPostId(final String postId, final GetManyListener listener) {

        final String lastUpdateDate = modelSql.getLastUpdateData(ModelClass.COMMENT);

        modelFirebase.getCommentsByPostId(postId, lastUpdateDate, new GetManyListener() {
            @Override
            public void onResult(List<Object> objects) {
                if(objects != null && objects.size() > 0) {
                    //update the local DB
                    String reacentUpdate = lastUpdateDate;

                    for (Object m : objects) {
                        modelSql.add(m);

                        if (reacentUpdate == null || ((Comment)m).getLastUpdated().compareTo(reacentUpdate) > 0) {
                            reacentUpdate = ((Comment)m).getLastUpdated();
                        }
                        Log.d("TAG", "updating: " + ((Comment)m).toString());
                        break;
                    }

                    modelSql.setLastUpdateData(ModelClass.COMMENT, reacentUpdate);
                }

                //return the complete objects list to the caller
                List<Object> res = modelSql.getCommentsByPostId(postId);
                listener.onResult(res);
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

    public interface AttachCacheListener {
        void onChildAdded(Object model);
        void onChildRemoved(Object model);
        void onChildChanged(Object model);
    }

    public void attachCacheListener(ModelClass model) {
        modelFirebase.attachCacheListener(model, new AttachCacheListener() {
            @Override
            public void onChildAdded(Object model) {
                // Add object to local db
                modelSql.add(model);
            }

            @Override
            public void onChildRemoved(Object model) {
                // Remove object from local db
                modelSql.delete(model);
            }

            @Override
            public void onChildChanged(Object model) {
                // Update object in local db
                modelSql.update(model);
            }
        });
    }

    public interface AttachUpdateUIListener {
        void onDataChange(Object model);
    }

    public void attachUpdateUIListener(ModelClass model, AttachUpdateUIListener listener) {
        modelFirebase.attachUpdateUIListener(model, listener);
    }

    public void savePhoto(final Bitmap photoBitmap, final String photoName) {
        saveImageToFile(photoBitmap,photoName); // synchronously save image locally
        Thread d = new Thread(new Runnable() {  // asynchronously save image to parse
            @Override
            public void run() {
                modelCloudinary.savePhoto(photoBitmap,photoName);
            }
        });
        d.start();
    }

    public interface LoadPhotoListener {
        void onResult(Bitmap photo);
    }

    public void loadPhoto(final String photoId, final LoadPhotoListener listener) throws ExecutionException, InterruptedException {
        AsyncTask<String,String,Bitmap> task = new AsyncTask<String, String, Bitmap >() {
            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap bmp = loadImageFromFile(photoId);              //first try to fin the image on the device
                if (bmp == null) {                                      //if image not found - try downloading it from parse
                    bmp = modelCloudinary.loadPhoto(photoId);
                    if (bmp != null) saveImageToFile(bmp,photoId);    //save the image locally for next time
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

    public void deletePhoto(final String photoName) {
        deleteImageFile(photoName);
        Thread d = new Thread(new Runnable() {  // asynchronously save image to parse
            @Override
            public void run() {
                modelCloudinary.destoryImage(photoName);
            }
        });
        d.start();
    }

    private void saveImageToFile(Bitmap imageBitmap, String imageFileName){
        FileOutputStream fos;
        OutputStream out = null;
        try {
            //File dir = context.getExternalFilesDir(null);
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File imageFile = new File(dir,imageFileName);
            imageFile.createNewFile();

            out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
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

    public File createTempImageFile(String imageFileName) throws IOException {

        // Create an image file name
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    private Bitmap loadImageFromFile(String imageFileName){
        String str = null;
        Bitmap bitmap = null;
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir,imageFileName);

            //File dir = context.getExternalFilesDir(null);
            InputStream inputStream = new FileInputStream(imageFile);

            /**
             * This code section is for utilizing the size of bitmaps on the server side stroage
             * It is needed because large files will throw OutOfMemoryException error on calling this code
             */
            BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
            sizeOptions.inSampleSize = 1;

            bitmap = BitmapFactory.decodeStream(inputStream, null, sizeOptions);
            Log.d("tag","got image from cache: " + imageFileName);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap deleteImageFile(String photoFileName){
        Bitmap bitmap = null;

        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir, photoFileName);

            if (imageFile.exists())
                imageFile.delete();
            else
                Log.d("tag", "File " + imageFile + " doesn't exist");
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}

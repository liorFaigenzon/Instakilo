package com.example.lior.instakilo.models.cloudinary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class ModelCloudinary {
    Cloudinary cloudinary;

    public ModelCloudinary() {
        cloudinary = new Cloudinary("cloudinary://135447234796616:Z8_FODZhU32e_2CkxDTkpXO_j4w@instakilo");
    }

    public void savePhoto(final Bitmap photoBitmap, final String photoName) {
        Thread savePhoto = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] bitmapData = bos.toByteArray();
                    ByteArrayInputStream bs = new ByteArrayInputStream(bitmapData);

                    // Cut off the format of the file
                    String name = photoName.substring(0, photoName.lastIndexOf("."));

                    // Upload the image to cloudinary
                    Map res = cloudinary.uploader().upload(bs , ObjectUtils.asMap("public_id", name));
                    Log.d("Nir","Save image to url: " + res.get("url"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        savePhoto.start();
    }

    public Bitmap loadPhoto(String photoId) {
        URL url;
        Bitmap photo;

        try {

            // Create cloudinary url
            url = new URL(cloudinary.url().generate(photoId));
            Log.d("Nir", "Load image from url: " + url);

            // Decode to bitmap
            photo = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return photo;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            photo = null;
        } catch (IOException e) {
            e.printStackTrace();
            photo = null;
        }

        return photo;
    }

}

package com.kzlabs.popularmovies.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by radsen on 4/10/17.
 */

public class IOUtils {

    // Code taken from: http://stackoverflow.com/questions/4989182/converting-java-bitmap-to-byte-array
    public static byte[] bitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }

    public static Bitmap byteArrayToBitmap(byte[] blob) {
        return BitmapFactory.decodeByteArray(blob, 0, blob.length);
    }
}

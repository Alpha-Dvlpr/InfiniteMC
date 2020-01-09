package com.alphadvlpr.infiniteminds.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageDecoder {
    public static Bitmap decode (String image) throws IllegalArgumentException{
        byte[] decodedBytes = Base64.decode(image.substring(image.indexOf(",") + 1), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static Bitmap getScaledBitmap (Bitmap b, int width, int height){
        int scaleWidth = b.getWidth();
        int scaleHeight = b.getHeight();

        int newWidth = scaleWidth;
        int newHeight = scaleHeight;

        if(newWidth > width){
            int ratio = scaleWidth / width;
            if(ratio > 0){
                newWidth = width;
                newHeight = scaleHeight / ratio;
            }
        }

        if(newHeight > height){
            int ratio = scaleHeight / height;
            if(ratio > 0){
                newWidth = scaleWidth / ratio;
                newHeight = height;
            }
        }

        return Bitmap.createScaledBitmap(b, newWidth, newHeight, true);
    }

    public static String encode (Bitmap image){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
    }
}

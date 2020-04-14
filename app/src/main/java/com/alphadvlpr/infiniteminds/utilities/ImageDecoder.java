package com.alphadvlpr.infiniteminds.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * This class makes the following conversions:
 * 1) String to Bitmap.
 * 2) Bitmap rescale.
 * 3) Bitmap to String.
 *
 * @author AlphaDvlpr.
 */
public class ImageDecoder {

    /**
     * This method converts a valid String into a Bitmap.
     *
     * @param image The String to be converted.
     * @return Returns the converted Bitmap.
     * @throws IllegalArgumentException Throws the exception if the given String cannot be
     *                                  converted to Bitmap.
     * @author AlphaDvlpr.
     */
    public static Bitmap decode(String image) throws IllegalArgumentException {
        byte[] decodedBytes = Base64.decode(image.substring(image.indexOf(",") + 1), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    /**
     * This method rescales a Bitmap to the given width and height.
     *
     * @param b      The Bitmap to be rescaled.
     * @param width  The final width of the Bitmap.
     * @param height The final height of the Bitmap
     * @return Returns the rescaled Bitmap.
     * @author AlphaDvlpr.
     */
    public static Bitmap getScaledBitmap(Bitmap b, int width, int height) {
        int scaleWidth = b.getWidth();
        int scaleHeight = b.getHeight();

        int newWidth = scaleWidth;
        int newHeight = scaleHeight;

        if (newWidth > width) {
            int ratio = scaleWidth / width;
            if (ratio > 0) {
                newWidth = width;
                newHeight = scaleHeight / ratio;
            }
        }

        if (newHeight > height) {
            int ratio = scaleHeight / height;
            if (ratio > 0) {
                newWidth = scaleWidth / ratio;
                newHeight = height;
            }
        }

        return Bitmap.createScaledBitmap(b, newWidth, newHeight, true);
    }

    /**
     * This method converts a Bitmap into a String.
     *
     * @param image The Bitmap to be converted.
     * @return Returns the Bitmap converted into String.
     * @author AlphaDvlpr.
     */
    public static String encode(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
    }
}

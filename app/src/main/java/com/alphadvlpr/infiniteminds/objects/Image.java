package com.alphadvlpr.infiniteminds.objects;

/**
 * Custom object for Images.
 *
 * @author AlphaDvlpr.
 */
public class Image {

    private String stringBitmap;

    /**
     * Empty constructor used when fetching data from Firebase database.
     *
     * @author ALphaDvlpr.
     */
    public Image() {
    }

    /**
     * Full constructor used when creating a new Image for been added to an {@link Article}.
     *
     * @param stringBitmap The Bitmap of the image converted to String using
     *                     {@link com.alphadvlpr.infiniteminds.utilities.ImageDecoder}.
     * @author AlphaDvlpr.
     */
    public Image(String stringBitmap) {
        this.stringBitmap = stringBitmap;
    }

    /**
     * @return Returns the Bitmap as a String.
     * @author AlphaDvlpr.
     */
    public String getStringBitmap() {
        return stringBitmap;
    }
}

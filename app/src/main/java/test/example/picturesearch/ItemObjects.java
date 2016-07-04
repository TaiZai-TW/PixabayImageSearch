package test.example.picturesearch;

import android.graphics.drawable.Drawable;

/**
 * Created by taizai on 2016/07/03.
 */
public class ItemObjects {

    public Drawable mImageDrawable;
    public String mImageDescription;
    public String mImageUrl;

    public ItemObjects(Drawable image, String desc, String url) {
        mImageDrawable = image;
        mImageDescription = desc;
        mImageUrl = url;
    }
}

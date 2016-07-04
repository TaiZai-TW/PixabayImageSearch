package test.example.picturesearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by taizai on 2016/07/03.
 */
public class PixabayParser {

    public int mTotalHits = 0;
    public int mHits = 0;
    public List<String> mImageUrls;
    public List<String> mPreviewImageUrls;
    public List<Integer> mImageWidth;
    public List<Integer> mImageHeight;
    public List<Drawable> mImageDrawableSet;
    public List<String> mAppendPreviewImageUrls;
    private Context mContext;

    public PixabayParser(Context context, String responseString, boolean isMore) {
        mContext = context;
        initialize();
        if (responseString != null && responseString != "")
            parse(responseString, isMore);
    }

    private void parse(String jsonString, boolean isMore) {
        try {
            JSONObject responseData = new JSONObject(jsonString);
            if (!isMore) {
                mTotalHits = Integer.parseInt(responseData.getString("totalHits"));
                mHits = Integer.parseInt(responseData.getString("total"));
            }
            JSONArray imageDataArray = responseData.getJSONArray("hits");
            for (int i = 0; i < imageDataArray.length(); i++) {
                JSONObject imageData = imageDataArray.getJSONObject(i);
                mImageUrls.add(imageData.getString("pageURL"));
                mPreviewImageUrls.add(imageData.getString("previewURL"));
                if (isMore)
                    mAppendPreviewImageUrls.add(imageData.getString("previewURL"));
                mImageWidth.add(imageData.getInt("imageWidth"));
                mImageHeight.add(imageData.getInt("imageHeight"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        mImageUrls = new ArrayList<String>();
        mPreviewImageUrls = new ArrayList<String>();
        mAppendPreviewImageUrls = new ArrayList<String>();
        mImageWidth = new ArrayList<Integer>();
        mImageHeight = new ArrayList<Integer>();
        mImageDrawableSet = new ArrayList<Drawable>();
    }

    public List<ItemObjects> groupToList() {
        List<ItemObjects> itemSet = new ArrayList<ItemObjects>();
        for (int i = 0; i < mImageDrawableSet.size(); i++) {
            itemSet.add(new ItemObjects(
                            mImageDrawableSet.get(i),
                            composeDescription(mImageWidth.get(i), mImageHeight.get(i)),
                            mImageUrls.get(i))
            );
        }
        return itemSet;
    }

    private String composeDescription(int width, int height) {
        return width + " x " + height;
    }

    public void getMoreImages(String responseString) {
        mAppendPreviewImageUrls = new ArrayList<String>();
        if (responseString != null && responseString != "") {
            parse(responseString, true);
        }
    }

    public void appendDrawableSet(List<Drawable> imageDrawables) {
        mImageDrawableSet.addAll(imageDrawables);
    }
}

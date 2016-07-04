package test.example.picturesearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by taizai on 2016/03/22.
 */
public class HttpRequestImgHelper extends AsyncTask<Void, Void, List<Drawable>> {

    //    public static String ParseResult = "";
    private List<String> mUrl;
    private Context mContext = null;
    private ProgressDialog mProgressDialog = null;

    public HttpRequestImgHelper(List<String> url, Context context) {
        mUrl = url;
        mContext = context;
    }

    public interface OnTaskExecFinished {
        public void OnTaskExecFinishedEvent(List<Drawable> result);
    }

    private static OnTaskExecFinished mTaskFinishedEvent;

    public static void setOnTaskExecFinishedEvent(OnTaskExecFinished taskEvent) {
        if (taskEvent != null) {
            mTaskFinishedEvent = taskEvent;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle("now parsing");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
    }

    @Override
    protected List<Drawable> doInBackground(Void... params) {

        List<Drawable> images = new ArrayList<Drawable>();
        for (int i = 0; i < mUrl.size(); i++) {
            Drawable result = httpRequest(mUrl.get(i));
            images.add(result);
        }

        return images;
    }

    @Override
    protected void onPostExecute(List<Drawable> images) {
        super.onPostExecute(images);

        if (mTaskFinishedEvent != null) {
            mTaskFinishedEvent.OnTaskExecFinishedEvent(images);
        }

        mProgressDialog.dismiss();
    }

    private Drawable httpRequest(String url) {
        Drawable result = null;
        HttpURLConnection urlConnection = null;

        try {
            String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
            String encodeUrl = Uri.encode(url, ALLOWED_URI_CHARS);
            URL target = new URL(encodeUrl);
            urlConnection = (HttpURLConnection) target.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            Bitmap imageBitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());
            result = new BitmapDrawable(mContext.getResources(), imageBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}

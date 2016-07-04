package test.example.picturesearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by taizai on 2016/03/22.
 */
public class HttpRequestHelper extends AsyncTask<Void, Void, String> {

    //    public static String ParseResult = "";
    private String mUrl = "";
    private Context mContext = null;
    private ProgressDialog mProgressDialog = null;

    public HttpRequestHelper(String url, Context context) {
        mUrl = url;
        mContext = context;
    }

    public interface OnTaskExecFinished {
        public void OnTaskExecFinishedEvent(String result);
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
    protected String doInBackground(Void... params) {

        String result = httpRequest(mUrl);

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (mTaskFinishedEvent != null) {
            mTaskFinishedEvent.OnTaskExecFinishedEvent(s);
        }

        mProgressDialog.dismiss();
    }

    private String httpRequest(String url) {
        String result = "";
        HttpURLConnection urlConnection = null;

        try {
            String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
            String encodeUrl = Uri.encode(url, ALLOWED_URI_CHARS);
            URL target = new URL(encodeUrl);
            urlConnection = (HttpURLConnection) target.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}

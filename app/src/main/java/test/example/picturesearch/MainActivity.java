package test.example.picturesearch;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.util.Log;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText mEditTextView;
    private boolean mIsGrid = true;
    private Context mContext;
    private String mQueryResult;
    private PixabayParser mPixabayParser = null;
    private RecyclerView mRecycleView;
    private StaggeredGridLayoutManager mGridLayoutManager;
    private LinearLayout mGridOption;
    private LinearLayout mListOption;
    private TextView mGetMoreImageButton;
    private String mQueryStringCache;
    private int mPageNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mEditTextView = (EditText) findViewById(R.id.search_text);
        mEditTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mPageNum = 1;
                    requestQuery(false);
                    return true;
                }
                return false;
            }
        });
        mRecycleView = (RecyclerView) findViewById(R.id.image_recycleview);
        mRecycleView.setHasFixedSize(false);
        mGridOption = (LinearLayout) findViewById(R.id.show_grid);
        mListOption = (LinearLayout) findViewById(R.id.show_list);
        mGetMoreImageButton = (TextView) findViewById(R.id.get_more);
        mGetMoreImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageNum += 1;
                requestQuery(true);
            }
        });

        ImageView searchButton = (ImageView) findViewById(R.id.search_img);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do search and update UI
                requestQuery(false);
                mPageNum = 1;
            }
        });

        LinearLayout showGrid = (LinearLayout) findViewById(R.id.show_grid);
        showGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsGrid) {
                    mIsGrid = !mIsGrid;
                    //do something
                    if (mPixabayParser != null) {
                        mGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                        mRecycleView.setLayoutManager(mGridLayoutManager);
                        List<ItemObjects> imageData = mPixabayParser.groupToList();
                        ImageViewAdapter imageViewAdapter = new ImageViewAdapter(mContext, imageData, mIsGrid);
                        mRecycleView.setAdapter(imageViewAdapter);
                    }
                    mGridOption.setBackgroundResource(R.drawable.rect_background);
                    mListOption.setBackgroundResource(0);
                }
            }
        });

        LinearLayout showList = (LinearLayout) findViewById(R.id.show_list);
        showList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsGrid) {
                    mIsGrid = !mIsGrid;
                    //do something
                    if (mPixabayParser != null) {
                        mGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                        mRecycleView.setLayoutManager(mGridLayoutManager);
                        List<ItemObjects> imageData = mPixabayParser.groupToList();
                        ImageViewAdapter imageViewAdapter = new ImageViewAdapter(mContext, imageData, mIsGrid);
                        mRecycleView.setAdapter(imageViewAdapter);
                    }
                    mGridOption.setBackgroundResource(0);
                    mListOption.setBackgroundResource(R.drawable.rect_background);
                }
            }
        });

    }

    private String composeUrl(String query, boolean isMore) {
        if (!isMore)
            return Constant.BASE_URL + "?key=" + Constant.API_KEY + "&q=" + query;
        else
            return Constant.BASE_URL + "?key=" + Constant.API_KEY + "&q=" + query + "&page=" + mPageNum;
    }

    private void requestQuery(final boolean isMore) {
        String query = mEditTextView.getText().toString();
        if (!isMore)
            mQueryStringCache = query;
        else
            query = mQueryStringCache;
        if (query != null && query != "") {
            HttpRequestHelper requestHelper = new HttpRequestHelper(composeUrl(query, isMore), mContext);
            requestHelper.setOnTaskExecFinishedEvent(new HttpRequestHelper.OnTaskExecFinished() {
                @Override
                public void OnTaskExecFinishedEvent(String result) {
                    mQueryResult = result;
                    if (!isMore)
                        mPixabayParser = new PixabayParser(mContext, mQueryResult, isMore);
                    else
                        mPixabayParser.getMoreImages(mQueryResult);
                    if (mPixabayParser != null) {
                        requestThumbImage(isMore);
                    }
                }
            });
            requestHelper.execute();
        }
    }

    private void requestThumbImage(final boolean isMore) {
        HttpRequestImgHelper requestImgHelper = new HttpRequestImgHelper(mPixabayParser.mPreviewImageUrls, mContext);
        if (isMore)
            requestImgHelper = new HttpRequestImgHelper(mPixabayParser.mAppendPreviewImageUrls, mContext);
        requestImgHelper.setOnTaskExecFinishedEvent(new HttpRequestImgHelper.OnTaskExecFinished() {
            @Override
            public void OnTaskExecFinishedEvent(List<Drawable> result) {
                if (!isMore)
                    mPixabayParser.mImageDrawableSet = result;
                else
                    mPixabayParser.appendDrawableSet(result);

                //update UI
                if (mIsGrid)
                    mGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                else
                    mGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                mRecycleView.setLayoutManager(mGridLayoutManager);
                List<ItemObjects> imageData = mPixabayParser.groupToList();
                ImageViewAdapter imageViewAdapter = new ImageViewAdapter(mContext, imageData, mIsGrid);
                mRecycleView.setAdapter(imageViewAdapter);
                if (mPixabayParser.mTotalHits != 0)
                    mGetMoreImageButton.setVisibility(View.VISIBLE);
                else
                    mGetMoreImageButton.setVisibility(View.INVISIBLE);
            }
        });
        requestImgHelper.execute();
    }
}

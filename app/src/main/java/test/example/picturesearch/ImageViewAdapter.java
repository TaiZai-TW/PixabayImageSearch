package test.example.picturesearch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.util.Log;
import java.util.List;

/**
 * Created by taizai on 2016/07/03.
 */
public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewHolder> {

    private Context mContext;
    private List<ItemObjects> mImageList;
    private boolean mIsGrid = true;

    public ImageViewAdapter(Context context, List<ItemObjects> imageList, boolean isGrid) {
        mContext = context;
        mImageList = imageList;
        mIsGrid = isGrid;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card, null);
        if (!mIsGrid)
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_image_card, null);
        ImageViewHolder imageViewHolder = new ImageViewHolder(layoutView);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, final int position) {
        holder.mImageView.setImageDrawable(mImageList.get(position).mImageDrawable);
        holder.mTextView.setText(mImageList.get(position).mImageDescription);
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mImageList.get(position).mImageUrl));
                mContext.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }
}

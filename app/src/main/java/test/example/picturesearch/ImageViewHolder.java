package test.example.picturesearch;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.util.Log;
/**
 * Created by taizai on 2016/07/03.
 */
public class ImageViewHolder extends RecyclerView.ViewHolder {

    public ImageView mImageView;
    public TextView mTextView;
    public CardView mCardView;

    public ImageViewHolder(View itemView) {
        super(itemView);

        mImageView = (ImageView) itemView.findViewById(R.id.image_res);
        mTextView = (TextView) itemView.findViewById(R.id.image_description);
        mCardView = (CardView) itemView.findViewById(R.id.image_card);
    }
}

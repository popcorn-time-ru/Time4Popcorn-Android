package se.popcorn_time.mobile.ui.adapter;

import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import se.popcorn_time.GrayscaleTransformation;
import se.popcorn_time.mobile.R;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;

public final class VideoPosterAdapter extends BaseAdapter {

    private String[] urls;

    public VideoPosterAdapter(@Nullable String[] urls) {
        this.urls = urls;
    }

    public void setUrls(@Nullable String[] urls) {
        this.urls = urls;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return urls != null ? urls.length : 1;
    }

    @Override
    public Object getItem(int i) {
        return urls[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (view != null) {
            imageView = (ImageView) view;
        } else {
            imageView = new ImageView(viewGroup.getContext());
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        if (urls != null && urls.length > i) {
            final Picasso picasso = Picasso.with(viewGroup.getContext());
            final RequestCreator creator = picasso.load(urls[i]).memoryPolicy(NO_CACHE).placeholder(R.color.v3_bottom_poster_placeholder);
            if (Configuration.ORIENTATION_PORTRAIT == viewGroup.getContext().getResources().getConfiguration().orientation) {
                creator.transform(new GrayscaleTransformation(picasso));
            }
            creator.into(imageView);
            if (i + 1 < urls.length) {
                // preload next image
                Picasso.with(viewGroup.getContext()).load(urls[i + 1]).fetch();
            }
        } else {
            imageView.setImageResource(R.color.v3_bottom_poster_placeholder);
        }
        return imageView;
    }
}

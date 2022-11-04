package se.popcorn_time.mobile.ui.adapter;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.drawable.BitmapDrawable;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.Locale;

import se.popcorn_time.VibrantUtils;
import se.popcorn_time.base.database.tables.Favorites;
import se.popcorn_time.base.model.video.info.VideoInfo;
import se.popcorn_time.mobile.R;
import se.popcorn_time.mobile.ui.DetailsActivity;

public final class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private Cursor mCursor;
    private boolean mDataValid;
    private int mRowIDColumn;
    private DataSetObserver mDataSetObserver;

    private int width = 356;
    private int height = 534;

    public FavoritesAdapter() {
        mCursor = null;
        mDataValid = false;
        mRowIDColumn = -1;
        mDataSetObserver = new NotifyDataSetObserver();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        try {
            holder.onBind(Favorites.create(mCursor), width, height);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                return mCursor.getLong(mRowIDColumn);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        if (oldCursor != null) {
            if (mDataSetObserver != null) oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (newCursor != null) {
            if (mDataSetObserver != null) newCursor.registerDataSetObserver(mDataSetObserver);
            mRowIDColumn = newCursor.getColumnIndexOrThrow(BaseColumns._ID);
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIDColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    public void setItemSize(Display display, int columnCount, int spacingPixels) {
        final DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        width = (metrics.widthPixels - spacingPixels * (columnCount - 1)) / columnCount;
        height = (int) (width * 1.5f);
    }

    static final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        private final ImageView poster;
        private final TextView rating;
        private final CompoundButton favorite;
        private final TextView year;

        private VideoInfo info;

        ViewHolder(@NonNull ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_content, parent, false));
            itemView.setOnClickListener(this);
            poster = (ImageView) itemView.findViewById(R.id.poster);
            rating = (TextView) itemView.findViewById(R.id.rating);
            favorite = (CompoundButton) itemView.findViewById(R.id.favorite);
            year = (TextView) itemView.findViewById(R.id.year);
        }

        void onBind(@NonNull VideoInfo info, int width, int height) {
            this.info = info;
            final ViewGroup.LayoutParams params = itemView.getLayoutParams();
            params.width = width;
            params.height = height;
            itemView.setLayoutParams(params);
            Picasso.with(itemView.getContext()).load(info.getPoster()).placeholder(R.drawable.poster).into(poster);
            rating.setText(String.format(Locale.ENGLISH, "%.1f", info.getRating()));
            favorite.setOnCheckedChangeListener(null);
            final Cursor cursor = Favorites.query(itemView.getContext(), null, Favorites._IMDB + "=\"" + info.getImdb() + "\"", null, null);
            if (cursor != null) {
                favorite.setChecked(cursor.getCount() > 0);
                cursor.close();
            }
            favorite.setOnCheckedChangeListener(this);
            year.setText(String.format(Locale.ENGLISH, "%d", info.getYear()));
        }

        @Override
        public void onClick(View v) {
            VibrantUtils.setAccentColor(((BitmapDrawable) poster.getDrawable()).getBitmap(), ContextCompat.getColor(v.getContext(), R.color.v3_accent));
            DetailsActivity.start(v.getContext(), info);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Favorites.insert(buttonView.getContext(), info);
            } else {
                Favorites.delete(buttonView.getContext(), info);
            }
        }
    }

    private class NotifyDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            mDataValid = false;
            notifyDataSetChanged();
        }
    }
}

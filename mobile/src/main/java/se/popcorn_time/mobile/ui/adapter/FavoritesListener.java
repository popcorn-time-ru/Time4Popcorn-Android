package se.popcorn_time.mobile.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

import se.popcorn_time.base.database.tables.Favorites;
import se.popcorn_time.base.model.video.info.VideoInfo;
import se.popcorn_time.mobile.R;

public class FavoritesListener implements OnLongClickListener {

    private Context context;
    private VideoInfo info;

    public FavoritesListener(Context context, VideoInfo info) {
        this.context = context;
        this.info = info;
    }

    @Override
    public boolean onLongClick(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.favorites_remove:
                        Favorites.delete(context, info);
                        return true;
                    case R.id.favorites_add:
                        Favorites.insert(context, info);
                        return true;
                    default:
                        return false;
                }
            }
        });

        popup.inflate(R.menu.favorites);
        popup.show();
        Cursor cursor = Favorites.query(context, null, Favorites._IMDB + "=\"" + info.getImdb() + "\"", null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                popup.getMenu().findItem(R.id.favorites_add).setVisible(false);
            } else {
                popup.getMenu().findItem(R.id.favorites_remove).setVisible(false);
            }
            cursor.close();
        }
        return true;
    }
}
package se.popcorn_time.mobile.ui;

import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;

import se.popcorn_time.GridSpacingItemDecoration;
import se.popcorn_time.base.database.tables.Favorites;
import se.popcorn_time.mobile.R;
import se.popcorn_time.mobile.ui.adapter.FavoritesAdapter;

public final class FavoritesActivity extends UpdateActivity {

    private Toolbar toolbar;

    private GridLayoutManager gridLayoutManager;
    private FavoritesAdapter favoritesAdapter;

    private int gridSpacingPixels;

    private Cursor cursor;
    private boolean stopped;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_favorites);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.favorites);

        final RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        gridLayoutManager = new GridLayoutManager(this, getGridSpanCount(getResources().getConfiguration()));
        recycler.setLayoutManager(gridLayoutManager);
        gridSpacingPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        recycler.addItemDecoration(new GridSpacingItemDecoration(gridSpacingPixels) {

            @Override
            protected int getTopOffset(int spanCount, int spanIndex, int position) {
                return (position < spanCount ? toolbar.getMeasuredHeight() : 0) + super.getTopOffset(spanCount, spanIndex, position);
            }
        });
        favoritesAdapter = new FavoritesAdapter();
        favoritesAdapter.setItemSize(getWindowManager().getDefaultDisplay(), gridLayoutManager.getSpanCount(), gridSpacingPixels);
        recycler.setAdapter(favoritesAdapter);

        if (cursor == null) {
            cursor = Favorites.query(this, null, null, null, Favorites._ID + " DESC");
        }
        final Cursor oldCursor = favoritesAdapter.swapCursor(cursor);
        if (oldCursor != null) {
            oldCursor.close();
        }
        findViewById(R.id.empty).setVisibility(cursor != null && cursor.getCount() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (stopped) {
            stopped = false;
            favoritesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopped = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        gridLayoutManager.setSpanCount(getGridSpanCount(newConfig));
        favoritesAdapter.setItemSize(getWindowManager().getDefaultDisplay(), gridLayoutManager.getSpanCount(), gridSpacingPixels);
        favoritesAdapter.notifyItemRangeChanged(0, favoritesAdapter.getItemCount());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int getGridSpanCount(Configuration configuration) {
        return configuration.orientation == Configuration.ORIENTATION_PORTRAIT ? 3 : 5;
    }
}

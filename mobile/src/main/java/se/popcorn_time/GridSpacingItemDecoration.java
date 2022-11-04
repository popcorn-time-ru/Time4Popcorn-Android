package se.popcorn_time;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    protected final int spacing;

    public GridSpacingItemDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
        final GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        final int spanIndex = layoutParams.getSpanIndex();
        final int position = layoutParams.getViewAdapterPosition();
        outRect.left = spanIndex * spacing / spanCount;
        outRect.right = spacing - (spanIndex + 1) * spacing / spanCount;
        outRect.top = getTopOffset(spanCount, spanIndex, position);
    }

    protected int getTopOffset(int spanCount, int spanIndex, int position) {
        return position >= spanCount ? spacing : 0;
    }
}
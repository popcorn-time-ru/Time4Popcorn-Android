package se.popcorn_time;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public final class HeaderBehavior extends AppBarLayout.Behavior {

    @Nullable
    private View bottomContent;

    private int minTopAndBottomOffset = -Integer.MAX_VALUE;

    public HeaderBehavior() {
    }

    public HeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, AppBarLayout child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        boolean handled = super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
        final int height = (int) (0.8f * parent.getMeasuredHeight());
        if (child.getMeasuredHeight() != height) {
            final ViewGroup.LayoutParams params = child.getLayoutParams();
            params.height = height;
            child.setLayoutParams(params);
        } else {
            if (bottomContent != null) {
                final int minBottomContentHeight = parent.getMeasuredHeight() - child.getMeasuredHeight();
                if (bottomContent.getMeasuredHeight() < minBottomContentHeight) {
                    ViewGroup.LayoutParams params = bottomContent.getLayoutParams();
                    params.height = minBottomContentHeight;
                    bottomContent.setLayoutParams(params);
                    setMinTopAndBottomOffset(0);
                } else {
                    setMinTopAndBottomOffset(parent.getMeasuredHeight() - child.getMeasuredHeight() - bottomContent.getMeasuredHeight());
                }
            }
        }
        return handled;
    }

    @Override
    public boolean setTopAndBottomOffset(int offset) {
        return super.setTopAndBottomOffset(offset < minTopAndBottomOffset ? minTopAndBottomOffset : offset);
    }

    public void setBottomContent(@Nullable View bottomContent) {
        this.bottomContent = bottomContent;
    }

    public int getMinTopAndBottomOffset() {
        return minTopAndBottomOffset;
    }

    public void setMinTopAndBottomOffset(int minTopAndBottomOffset) {
        this.minTopAndBottomOffset = minTopAndBottomOffset;
    }
}

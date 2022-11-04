package se.popcorn_time.mobile.model.filter;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.util.Collection;

import se.popcorn_time.model.filter.Filter;
import se.popcorn_time.model.filter.IFilter;
import se.popcorn_time.model.filter.IFilterItem;

public final class FilterView implements IFilter {

    private final int viewName;
    private final int viewIcon;
    private final IFilter filter;

    private FilterView(@StringRes int viewName, @DrawableRes int viewIcon, @NonNull IFilter filter) {
        this.viewName = viewName;
        this.viewIcon = viewIcon;
        this.filter = filter;
    }

    @NonNull
    @Override
    public String getName() {
        return filter.getName();
    }

    @NonNull
    @Override
    public Collection<IFilterItem> getItems() {
        return filter.getItems();
    }

    @Override
    public boolean isSingleChoice() {
        return filter.isSingleChoice();
    }

    @Override
    public boolean isChecked(@NonNull IFilterItem item) {
        return filter.isChecked(item);
    }

    @Override
    public void setChecked(IFilterItem... items) {
        filter.setChecked(items);
    }

    @Override
    public void setOnCheckedListener(@Nullable OnCheckedListener onCheckedListener) {
        filter.setOnCheckedListener(onCheckedListener != null ? new FilterViewOnCheckedListener(onCheckedListener) : null);
    }

    @StringRes
    public int getViewName() {
        return viewName;
    }

    @DrawableRes
    public int getViewIcon() {
        return viewIcon;
    }

    public static final class Builder {

        private final int viewName;
        private final int viewIcon;
        private final Filter.Builder builder;

        public Builder(@StringRes int viewName, @DrawableRes int viewIcon, @NonNull String name, boolean singleChoice) {
            this.viewName = viewName;
            this.viewIcon = viewIcon;
            builder = new Filter.Builder(name, singleChoice);
        }

        @NonNull
        public Builder add(@NonNull IFilterItem item, boolean selected) {
            builder.add(item, selected);
            return this;
        }

        @NonNull
        public FilterView create() {
            return new FilterView(viewName, viewIcon, builder.create());
        }
    }

    private final class FilterViewOnCheckedListener implements OnCheckedListener {

        private final OnCheckedListener onCheckedListener;

        private FilterViewOnCheckedListener(@NonNull OnCheckedListener onCheckedListener) {
            this.onCheckedListener = onCheckedListener;
        }

        @Override
        public void onFilterChecked(@NonNull IFilter filter) {
            onCheckedListener.onFilterChecked(FilterView.this);
        }
    }
}

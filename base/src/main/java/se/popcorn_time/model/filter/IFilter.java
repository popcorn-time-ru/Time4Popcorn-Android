package se.popcorn_time.model.filter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;

public interface IFilter {

    @NonNull
    String getName();

    @NonNull
    Collection<IFilterItem> getItems();

    boolean isSingleChoice();

    boolean isChecked(@NonNull IFilterItem item);

    void setChecked(IFilterItem... items);

    void setOnCheckedListener(@Nullable OnCheckedListener onCheckedListener);

    interface OnCheckedListener {

        void onFilterChecked(@NonNull IFilter filter);
    }
}

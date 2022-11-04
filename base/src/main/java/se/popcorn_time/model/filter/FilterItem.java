package se.popcorn_time.model.filter;

import android.support.annotation.NonNull;

public final class FilterItem implements IFilterItem {

    private final String value;

    public FilterItem(@NonNull String value) {
        this.value = value;
    }

    @NonNull
    @Override
    public String getValue() {
        return value;
    }
}

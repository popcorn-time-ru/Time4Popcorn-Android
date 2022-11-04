package se.popcorn_time.model.filter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Filter implements IFilter {

    private final String name;
    private final Map<IFilterItem, Boolean> items;
    private final boolean singleChoice;

    @Nullable
    private OnCheckedListener onCheckedListener;

    private Filter(@NonNull String name, @NonNull Map<IFilterItem, Boolean> items, boolean singleChoice) {
        this.name = name;
        this.items = items;
        this.singleChoice = singleChoice;
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public Collection<IFilterItem> getItems() {
        return items.keySet();
    }

    @Override
    public boolean isSingleChoice() {
        return singleChoice;
    }

    @Override
    public boolean isChecked(@NonNull IFilterItem item) {
        return items.containsKey(item) && items.get(item);
    }

    @Override
    public void setChecked(IFilterItem... items) {
        if (singleChoice) {
            if (items.length > 0) {
                final IFilterItem item = items[items.length - 1];
                for (Map.Entry<IFilterItem, Boolean> entry : this.items.entrySet()) {
                    entry.setValue(item.equals(entry.getKey()));
                }
            }
        } else {
            for (Map.Entry<IFilterItem, Boolean> entry : this.items.entrySet()) {
                entry.setValue(false);
            }
            for (IFilterItem item : items) {
                this.items.put(item, true);
            }
        }
        if (onCheckedListener != null) {
            onCheckedListener.onFilterChecked(Filter.this);
        }
    }

    public void setOnCheckedListener(@Nullable OnCheckedListener onCheckedListener) {
        this.onCheckedListener = onCheckedListener;
    }

    public static final class Builder {

        private final String name;
        private final Map<IFilterItem, Boolean> items = new LinkedHashMap<>();
        private final boolean singleChoice;

        private IFilterItem selected;

        public Builder(@NonNull String name, boolean singleChoice) {
            this.name = name;
            this.singleChoice = singleChoice;
        }

        @NonNull
        public Builder add(@NonNull IFilterItem item, boolean selected) {
            if (singleChoice && selected) {
                if (this.selected == null) {
                    this.selected = item;
                } else {
                    throw new IllegalArgumentException("Already have selected item for single choice filter");
                }
            }
            items.put(item, selected);
            return Builder.this;
        }

        @NonNull
        public Filter create() {
            if (items.isEmpty()) {
                throw new IllegalStateException("Filter items is empty");
            }
            return new Filter(name, items, singleChoice);
        }
    }
}

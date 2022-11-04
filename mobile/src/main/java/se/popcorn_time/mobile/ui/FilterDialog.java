package se.popcorn_time.mobile.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import se.popcorn_time.IUseCaseManager;
import se.popcorn_time.mobile.model.filter.FilterItemView;
import se.popcorn_time.mobile.model.filter.FilterView;
import se.popcorn_time.model.content.IContentUseCase;
import se.popcorn_time.model.filter.IFilter;
import se.popcorn_time.model.filter.IFilterItem;

public final class FilterDialog extends DialogFragment {

    private static final String KEY_FILTER_NAME = "filter_name";

    private IFilter filter;
    private IFilterItem[] items;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        setCancelable(true);
        final String filterName = getArguments().getString(KEY_FILTER_NAME);
        if (TextUtils.isEmpty(filterName)) {
            return;
        }
        final IContentUseCase contentUseCase = ((IUseCaseManager) getActivity().getApplication()).getContentUseCase();
        for (IFilter filter : contentUseCase.getContentProvider().getFilters()) {
            if (filterName.equals(filter.getName())) {
                this.filter = filter;
                this.items = filter.getItems().toArray(new IFilterItem[filter.getItems().size()]);
                break;
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(isCancelable());
        if (filter != null) {
            builder.setTitle(filter instanceof FilterView ? getString(((FilterView) filter).getViewName()) : filter.getName());
            if (filter.isSingleChoice()) {
                onCreateSingleChoice(builder);
            } else {
                onCreateMultiChoice(builder);
            }
            builder.setNegativeButton(android.R.string.cancel, null);
        } else {
            dismiss();
        }
        return builder.create();
    }

    private void onCreateSingleChoice(@NonNull AlertDialog.Builder builder) {
        final int size = items.length;
        final String[] itemNames = new String[size];
        int checkedItem = -1;
        for (int i = 0; i < size; i++) {
            final IFilterItem item = items[i];
            itemNames[i] = getFilterItemName(item);
            if (filter.isChecked(item)) {
                checkedItem = i;
            }
        }
        builder.setSingleChoiceItems(itemNames, checkedItem, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                filter.setChecked(items[which]);
                dismiss();
            }
        });
    }

    private void onCreateMultiChoice(@NonNull AlertDialog.Builder builder) {
        final int size = items.length;
        final String[] itemNames = new String[size];
        final boolean[] checkedItems = new boolean[size];
        for (int i = 0; i < size; i++) {
            final IFilterItem item = items[i];
            itemNames[i] = getFilterItemName(item);
            checkedItems[i] = filter.isChecked(item);
        }
        builder.setMultiChoiceItems(itemNames, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedItems[which] = isChecked;
            }
        });
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                final List<IFilterItem> list = new ArrayList<>();
                for (int i = 0; i < checkedItems.length; i++) {
                    if (checkedItems[i]) {
                        list.add(items[i]);
                    }
                }
                filter.setChecked(list.toArray(new IFilterItem[list.size()]));
            }
        });
    }

    @NonNull
    private String getFilterItemName(@NonNull IFilterItem filterItem) {
        return filterItem instanceof FilterItemView ? getString(((FilterItemView) filterItem).getViewName()) : filterItem.getValue();
    }

    public static void showDialog(@NonNull FragmentManager manager, @NonNull String tag, @NonNull IFilter filter) {
        DialogFragment fragment = (DialogFragment) manager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new FilterDialog();
        }
        if (fragment.isAdded()) {
            return;
        }
        final Bundle args = new Bundle(1);
        args.putString(KEY_FILTER_NAME, filter.getName());
        fragment.setArguments(args);
        fragment.show(manager, tag);
    }
}

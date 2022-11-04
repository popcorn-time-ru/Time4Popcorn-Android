package com.player.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.player.R;

import java.util.List;

public class SingleChoiceDialog extends DialogFragment {

    private class ChoiceAdapter extends BaseAdapter {

        private List<? extends ListItemEntity> items;
        private LayoutInflater inflater;

        public ChoiceAdapter(List<? extends ListItemEntity> items) {
            this.items = items;
            this.inflater = LayoutInflater.from(getActivity());
        }

        @Override
        public int getCount() {
            return items != null ? items.size() : 0;
        }

        @Override
        public ListItemEntity getItem(int position) {
            return items != null ? items.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.single_choice_item_layout, parent, false);
            }
            ((CheckedTextView) convertView).setText(getItem(position).getName());
            return convertView;
        }
    }

    private String title;
    private List<? extends ListItemEntity> items;
    private int checkedItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setSingleChoiceItems(new ChoiceAdapter(items), checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                items.get(which).onItemChosen();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public void show(FragmentManager fm, String title, List<? extends ListItemEntity> items, ListItemEntity checkedItem) {
        if (!isAdded() && !fm.isDestroyed()) {
            this.title = title;
            this.items = items;
            this.checkedItem = checkedItem != null ? checkedItem.getPosition() : -1;
            this.show(fm, "single_choice_dialog_" + hashCode());
        }
    }
}
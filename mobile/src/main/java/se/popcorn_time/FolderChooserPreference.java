package se.popcorn_time;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.player.dialog.ListItemEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import se.popcorn_time.base.storage.StorageMount;
import se.popcorn_time.base.storage.StorageUtil;
import se.popcorn_time.mobile.R;
import se.popcorn_time.mobile.ui.FolderChooserActivity;

public class FolderChooserPreference extends DialogPreference {

    private File folder;

    public FolderChooserPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public FolderChooserPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FolderChooserPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FolderChooserPreference(Context context) {
        super(context);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return getFolder(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setFolder(restorePersistedValue ? getFolder(getPersistedString(folder.getAbsolutePath())) : (File) defaultValue);
    }

    public File getFolder() {
        return folder;
    }

    public void setFolder(File folder) {
        final boolean changed = (folder != null && this.folder == null)
                || (folder == null && this.folder != null)
                || (folder != null && !folder.equals(this.folder));
        if (changed) {
            this.folder = folder;
            persistString(folder != null ? folder.getAbsolutePath() : null);
            notifyChanged();
        }
    }

    @Nullable
    private static File getFolder(@Nullable String path) {
        return path != null ? new File(path) : null;
    }

    public static final class Dialog extends PreferenceDialogFragmentCompat {

        private static final String SAVE_FOLDER = "FolderChooserPreference.Dialog.folder";

        private static final int REQUEST_CODE_CHOOSE_FOLDER = 101;

        private File folder;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (savedInstanceState == null) {
                folder = getFolderChooserPreference().getFolder();
            } else {
                folder = getFolder(savedInstanceState.getString(SAVE_FOLDER));
            }
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            if (folder != null) {
                outState.putString(SAVE_FOLDER, folder.getAbsolutePath());
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (REQUEST_CODE_CHOOSE_FOLDER == requestCode && Activity.RESULT_OK == resultCode) {
                chooseFolder(getFolder(data.getStringExtra(FolderChooserActivity.SELECTED_DIR)));
            } else {
                dismiss();
            }
        }

        @Override
        protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
            super.onPrepareDialogBuilder(builder);
            final List<ListItemEntity<StorageMount>> listItemEntities = new ArrayList<>();
            for (StorageMount storage : StorageUtil.getStorageMounts()) {
                final ListItemEntity<StorageMount> listItemEntity = new ListItemEntity<StorageMount>(storage) {

                    @Override
                    public void onItemChosen() {
                        if (getValue().primary) {
                            FolderChooserActivity.startForResult(Dialog.this, getValue().dir, REQUEST_CODE_CHOOSE_FOLDER);
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                chooseFolder(getValue().dir);
                                // TODO: notify about delete video after remove app
                            } else {
                                FolderChooserActivity.startForResult(Dialog.this, getValue().dir, REQUEST_CODE_CHOOSE_FOLDER);
                            }
                        }
                    }
                };
                ListItemEntity.addItemToList(listItemEntities, listItemEntity);
            }
            builder.setAdapter(new Adapter(getContext(), listItemEntities), null);
            builder.setPositiveButton(null, null);
        }

        @NonNull
        @Override
        public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
            final AlertDialog dialog = (AlertDialog) super.onCreateDialog(savedInstanceState);
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {
                    ((AlertDialog) dialog).getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ((ListItemEntity) parent.getAdapter().getItem(position)).onItemChosen();
                        }
                    });
                }
            });
            return dialog;
        }

        @Override
        public void onDialogClosed(boolean positiveResult) {
            if (positiveResult) {
                if (getFolderChooserPreference().callChangeListener(folder)) {
                    getFolderChooserPreference().setFolder(folder);
                }
            }
        }

        private FolderChooserPreference getFolderChooserPreference() {
            return (FolderChooserPreference) getPreference();
        }

        private void chooseFolder(File folder) {
            this.folder = folder;
            onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
            dismiss();
        }

        public static Dialog newInstance(String key) {
            final Dialog fragment = new Dialog();
            final Bundle b = new Bundle(1);
            b.putString(ARG_KEY, key);
            fragment.setArguments(b);
            return fragment;
        }

        private final class Adapter extends ArrayAdapter<ListItemEntity<StorageMount>> {

            Adapter(Context context, List<ListItemEntity<StorageMount>> list) {
                super(context, R.layout.list_item_two_line_choice, list);
            }

            @NonNull
            @Override
            public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                final Holder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_two_line_choice, parent, false);
                    holder = new Holder();
                    holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
                    holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
                    holder.checkbox = (RadioButton) convertView.findViewById(android.R.id.checkbox);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                final StorageMount storage = getItem(position).getValue();
                if (storage.primary) {
                    holder.text1.setText(R.string.device_storage);
                } else {
                    holder.text1.setText(getString(R.string.sdcard_storage) + ": " + storage.label);
                }
                holder.text2.setText(getString(R.string.size) + ": " + StorageUtil.getSizeText(StorageUtil.getAvailableSpaceInBytes(storage.dir.getAbsolutePath())));
                if (folder != null && folder.getAbsolutePath().startsWith(storage.dir.getAbsolutePath())) {
                    holder.checkbox.setChecked(true);
                } else {
                    holder.checkbox.setChecked(false);
                }
                return convertView;
            }

            private final class Holder {
                TextView text1;
                TextView text2;
                RadioButton checkbox;
            }
        }
    }
}

package com.player.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class FileChooserDialog extends DialogFragment implements FileFilter {

    public interface OnChooserListener {

        void onChooserSelected(File file);

        void onChooserCancel();
    }

    private class ChooserData {

        public ChooserFileType type;
        public File file;

        public ChooserData(ChooserFileType type, File file) {
            this.type = type;
            this.file = file;
        }
    }

    private enum ChooserFileType {
        DIRECTORY, FILE, UP_ACTION
    }

    private class ChooserAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        private ArrayList<ChooserData> data = new ArrayList<>();

        public ChooserAdapter(Context context) {
            this.context = context;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public ChooserData getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ChooserData chooserData = getItem(position);
            if (convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            TextView view = (TextView) convertView;
            view.setText(chooserData.file.getName());
            if (ChooserFileType.DIRECTORY.equals(chooserData.type)) {
                view.setTextColor(context.getResources().getColor(android.R.color.white));
            } else if (ChooserFileType.FILE.equals(chooserData.type)) {
                view.setTextColor(context.getResources().getColor(android.R.color.holo_blue_light));
            } else if (ChooserFileType.UP_ACTION.equals(chooserData.type)) {
                view.setTextColor(context.getResources().getColor(android.R.color.white));
            }

            return view;
        }

        public ArrayList<ChooserData> getData() {
            return data;
        }
    }

    private int title;
    private OnChooserListener listener;
    private String[] acceptExtensions;
    private String[] denyFolderNames;
    private File initFolder;
    private File selectedFolder;

    private ChooserAdapter folderAdapter;
    private TextView vCurrentFolder;
    private ListView vFolderList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        folderAdapter = new ChooserAdapter(getActivity());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setNeutralButton(android.R.string.cancel, cancelListener);
        builder.setView(createView());

        if (initFolder == null || !initFolder.exists()) {
            initFolder = new File("/");
        }
        changeDirectory(initFolder);

        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (listener != null) {
            listener.onChooserCancel();
        }
    }

    @Override
    public boolean accept(File pathname) {
        if (pathname.isHidden()) {
            return false;
        }
        if (pathname.isDirectory()) {
            if (denyFolderNames != null) {
                for (String denyFolderName : denyFolderNames) {
                    if (pathname.getName().equals(denyFolderName)) {
                        return false;
                    }
                }
            }
        } else {
            if (acceptExtensions != null) {
                for (String acceptException : acceptExtensions) {
                    if (!pathname.getName().endsWith(acceptException)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void setTitle(@StringRes int title) {
        this.title = title;
    }

    public void setChooserListener(OnChooserListener listener) {
        this.listener = listener;
    }

    public void setAcceptExtensions(String[] acceptExtensions) {
        this.acceptExtensions = acceptExtensions;
    }

    public void setDenyFolderNames(String[] denyFolderNames) {
        this.denyFolderNames = denyFolderNames;
    }

    public void show(FragmentManager manager, File initFolder) {
        this.initFolder = initFolder;
        show(manager, "file_chooser_dialog_" + hashCode());
    }

    private View createView() {
        LinearLayout parent = new LinearLayout(getActivity());
        LinearLayout.LayoutParams parent_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        parent.setLayoutParams(parent_params);
        parent.setOrientation(LinearLayout.VERTICAL);

        vCurrentFolder = (TextView) LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_1, parent, false);
        vCurrentFolder.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
        parent.addView(vCurrentFolder);

        vFolderList = new ListView(getActivity());
        vFolderList.setOnItemClickListener(folderListener);
        vFolderList.setAdapter(folderAdapter);
        vFolderList.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        parent.addView(vFolderList, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        return parent;
    }

    private void changeDirectory(File dir) {
        if (dir != null && dir.isDirectory()) {
            selectedFolder = dir;
            File[] file_list = selectedFolder.listFiles(FileChooserDialog.this);
            if (file_list != null) {
                Arrays.sort(file_list, fileComparator);
                folderAdapter.getData().clear();
                if (selectedFolder.getParentFile() != null) {
                    folderAdapter.getData().add(new ChooserData(ChooserFileType.UP_ACTION, new File("...")));
                }
                for (File f : file_list) {
                    if (f.isDirectory()) {
                        folderAdapter.getData().add(new ChooserData(ChooserFileType.DIRECTORY, f));
                    } else {
                        folderAdapter.getData().add(new ChooserData(ChooserFileType.FILE, f));
                    }
                }

                folderAdapter.notifyDataSetChanged();
                vCurrentFolder.setText(selectedFolder.getAbsolutePath());
                vFolderList.post(new Runnable() {

                    @Override
                    public void run() {
                        vFolderList.setSelection(0);
                    }
                });
            }
        }
    }

    private void upFolder() {
        File parent;
        if (selectedFolder != null && (parent = selectedFolder.getParentFile()) != null) {
            changeDirectory(parent);
        }
    }

    private Comparator<File> fileComparator = new Comparator<File>() {

        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.isDirectory()) {
                if (rhs.isDirectory()) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                } else {
                    return -1;
                }
            } else {
                if (rhs.isDirectory()) {
                    return 1;
                } else {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
            }
        }
    };

    private OnItemClickListener folderListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            ChooserData chooserData = (ChooserData) adapter.getItemAtPosition(position);
            if (ChooserFileType.DIRECTORY.equals(chooserData.type)) {
                changeDirectory(chooserData.file);
            } else if (ChooserFileType.FILE.equals(chooserData.type)) {
                if (listener != null) {
                    listener.onChooserSelected(chooserData.file);
                    getDialog().dismiss();
                }
            } else if (ChooserFileType.UP_ACTION.equals(chooserData.type)) {
                upFolder();
            }
        }

    };

    private OnClickListener cancelListener = new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (listener != null) {
                listener.onChooserCancel();
            }
        }

    };
}
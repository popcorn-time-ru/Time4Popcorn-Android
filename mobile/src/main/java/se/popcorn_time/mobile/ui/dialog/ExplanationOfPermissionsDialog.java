package se.popcorn_time.mobile.ui.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import se.popcorn_time.mobile.R;

public class ExplanationOfPermissionsDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final boolean CANCELABLE = false;
    public static final String DIALOG_TAG = "explanation_of_permissions_dialog";

    private ExplanationOfPermissionsDialogListener explanationOfPermissionsDialogListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ExplanationOfPermissionsDialogListener) {
            explanationOfPermissionsDialogListener = (ExplanationOfPermissionsDialogListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        explanationOfPermissionsDialogListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(CANCELABLE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(CANCELABLE);
        builder.setTitle(R.string.eop_dialog_title);
        builder.setMessage(R.string.eop_dialog_text);
        builder.setPositiveButton(R.string.eop_dialog_positive, ExplanationOfPermissionsDialog.this);
        //builder.setNegativeButton(R.string.eop_dialog_negative, ExplanationOfPermissionsDialog.this);
        return builder.create();
    }

    public static void show(@NonNull FragmentManager fm) {
        if (fm.isDestroyed()) {
            return;
        }
        DialogFragment fragment = (DialogFragment) fm.findFragmentByTag(DIALOG_TAG);
        if (fragment == null) {
            fragment = new ExplanationOfPermissionsDialog();
        }
        if (!fragment.isAdded()) {
            fragment.show(fm, DIALOG_TAG);
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                if (explanationOfPermissionsDialogListener != null) {
                    explanationOfPermissionsDialogListener.onExplanationOfPermissionsDialogRequestPermission();
                }
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                if (explanationOfPermissionsDialogListener != null) {
                    explanationOfPermissionsDialogListener.onExplanationOfPermissionsDialogExit();
                }
            default:
                break;
        }
    }

    public interface ExplanationOfPermissionsDialogListener {

        void onExplanationOfPermissionsDialogRequestPermission();

        void onExplanationOfPermissionsDialogExit();
    }
}
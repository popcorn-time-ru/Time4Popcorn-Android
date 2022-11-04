package se.popcorn_time.mobile.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class OptionDialog extends DialogFragment {

    public interface OptionListener {

        boolean positiveShow();

        boolean negativeShow();

        boolean neutralShow();

        String positiveButtonText();

        String negativeButtonText();

        String neutralButtonText();

        void positiveAction();

        void negativeAction();

        void neutralAction();
    }

    public static class SimpleOptionListener implements OptionListener {

        public SimpleOptionListener() {

        }

        @Override
        public boolean positiveShow() {
            return false;
        }

        @Override
        public boolean negativeShow() {
            return false;
        }

        @Override
        public boolean neutralShow() {
            return false;
        }

        @Override
        public String positiveButtonText() {
            return "Ok";
        }

        @Override
        public String negativeButtonText() {
            return "No";
        }

        @Override
        public String neutralButtonText() {
            return "Cancel";
        }

        @Override
        public void positiveAction() {

        }

        @Override
        public void negativeAction() {

        }

        @Override
        public void neutralAction() {

        }
    }

    public static final String TITLE_KEY = "title";
    public static final String MESSAGE_KEY = "message";

    private OptionListener listener;

    public OptionDialog() {

    }

    public void setListener(OptionListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (getArguments() != null && getArguments().containsKey(TITLE_KEY)) {
            builder.setTitle(getArguments().getString(TITLE_KEY));
        }
        if (getArguments() != null && getArguments().containsKey(MESSAGE_KEY)) {
            builder.setMessage(getArguments().getString(MESSAGE_KEY));
        }
        if (listener != null) {
            if (listener.positiveShow()) {
                builder.setPositiveButton(listener.positiveButtonText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.positiveAction();
                    }
                });
            }
            if (listener.negativeShow()) {
                builder.setNegativeButton(listener.negativeButtonText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.negativeAction();
                    }
                });
            }
            if (listener.neutralShow()) {
                builder.setNeutralButton(listener.neutralButtonText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.neutralAction();
                    }
                });
            }
        }
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static Bundle createArguments(String title, String message) {
        Bundle args = new Bundle();
        args.putString(TITLE_KEY, title);
        args.putString(MESSAGE_KEY, message);
        return args;
    }
}
package se.popcorn_time.mobile.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import se.popcorn_time.mobile.PopcornApplication;
import se.popcorn_time.model.messaging.IMessagingData;
import se.popcorn_time.model.messaging.IMessagingDialogData;
import se.popcorn_time.model.messaging.IMessagingDialogHtmlData;
import se.popcorn_time.model.messaging.IMessagingUseCase;
import se.popcorn_time.model.messaging.MessagingUtils;

public final class FirebaseMessagingDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final boolean CANCELABLE = true;

    private IMessagingUseCase firebaseMessagingUseCase;

    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(CANCELABLE);
        firebaseMessagingUseCase = ((PopcornApplication) getActivity().getApplication()).getMessagingUseCase();
        if (firebaseMessagingUseCase.getData() == null) {
            dismiss();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(CANCELABLE);
        if (firebaseMessagingUseCase.getData() instanceof IMessagingDialogData) {
            IMessagingDialogData data = (IMessagingDialogData) firebaseMessagingUseCase.getData();
            builder.setTitle(data.getTitle());
            builder.setMessage(data.getMessage());
            if (!TextUtils.isEmpty(data.getPositiveButton())) {
                builder.setPositiveButton(data.getPositiveButton(), FirebaseMessagingDialog.this);
            }
            if (!TextUtils.isEmpty(data.getNegativeButton())) {
                builder.setNegativeButton(data.getNegativeButton(), FirebaseMessagingDialog.this);
            }
        } else if (firebaseMessagingUseCase.getData() instanceof IMessagingDialogHtmlData) {
            IMessagingDialogHtmlData data = (IMessagingDialogHtmlData) firebaseMessagingUseCase.getData();
            webView = new WebView(getContext());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.addJavascriptInterface(new HostAppInterface(), "hostApp");
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(data.getUrl());
            builder.setView(webView);
        }

        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (webView != null) {
            webView.destroy();
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                if (firebaseMessagingUseCase.getData() instanceof IMessagingDialogData) {
                    IMessagingData.Action action = ((IMessagingDialogData) firebaseMessagingUseCase.getData()).getAction();
                    if (action != null) {
                        MessagingUtils.action(getContext(), action);
                    }
                }
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            default:
                break;
        }
    }

    public static void show(@NonNull FragmentManager manager, IMessagingData data, @NonNull String tag) {
        FirebaseMessagingDialog fragment = (FirebaseMessagingDialog) manager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new FirebaseMessagingDialog();
        }
        if (fragment.isAdded()) {
            return;
        }
        fragment.show(manager, tag);
    }

    private final class HostAppInterface {

        @JavascriptInterface
        public void close() {
            dismiss();
        }

        @JavascriptInterface
        public void action(String data) {
            try {
                IMessagingData.Action action = MessagingUtils.parse(new JSONObject(data));
                if (action != null) {
                    MessagingUtils.action(getContext(), action);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

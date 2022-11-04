package se.popcorn_time.mobile.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import se.popcorn_time.base.IPopcornApplication;
import se.popcorn_time.mobile.R;
import se.popcorn_time.model.config.VpnConfig;
import se.popcorn_time.mvp.IViewRouter;
import se.popcorn_time.ui.vpn.IVpnView;

public final class VpnDialog extends AppCompatDialogFragment implements DialogInterface.OnShowListener {

    private VpnDialogListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Theme_Popcorn_VpnDialog);
        builder.setCancelable(isCancelable());
        builder.setView(R.layout.view_vpn_alert);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(this);
        return dialog;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        final AlertDialog dialog = (AlertDialog) getDialog();
        dialog.setContentView(R.layout.view_vpn_alert);
        onShow(dialog);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        final VpnConfig.Alert alert = ((IPopcornApplication) getActivity().getApplication()).getConfigUseCase().getConfig().getVpnConfig().getAlert();
        final AlertDialog alertDialog = (AlertDialog) dialog;
        if (!TextUtils.isEmpty(alert.getTitle())) {
            ((TextView) alertDialog.findViewById(R.id.title)).setText(Html.fromHtml(alert.getTitle()));
        }
        final VpnConfig.Alert.Text[] texts = alert.getTexts();
        if (texts != null && texts.length >= 2) {
            final TextView text1 = (TextView) alertDialog.findViewById(R.id.text1);
            text1.setText(Html.fromHtml(texts[0].text));
            text1.setMaxLines(texts[0].lines);
            final TextView text2 = (TextView) alertDialog.findViewById(R.id.text2);
            text2.setText(Html.fromHtml(texts[1].text));
            text2.setMaxLines(texts[1].lines);
        }
        alertDialog.findViewById(R.id.btn_activate_vpn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((IViewRouter) getActivity().getApplication()).onShowView(IVpnView.class);
                dismiss();
            }
        });
        alertDialog.findViewById(R.id.btn_continue).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onContinue();
                }
                dismiss();
            }
        });
    }

    public static void showDialog(@NonNull FragmentManager manager, @Nullable String tag, @Nullable VpnDialogListener listener) {
        VpnDialog fragment = (VpnDialog) manager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new VpnDialog();
        }
        if (fragment.isAdded()) {
            return;
        }
        fragment.listener = listener;
        fragment.show(manager, tag);
    }

    public interface VpnDialogListener {
        void onContinue();
    }
}
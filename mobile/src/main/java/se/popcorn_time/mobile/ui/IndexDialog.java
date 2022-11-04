package se.popcorn_time.mobile.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import java.util.LinkedHashSet;
import java.util.Set;

import se.popcorn_time.IUseCaseManager;
import se.popcorn_time.mobile.R;
import se.popcorn_time.mobile.model.content.ContentProviderView;
import se.popcorn_time.model.content.IContentProvider;
import se.popcorn_time.model.content.IContentUseCase;

public final class IndexDialog extends DialogFragment {

    private Integer[] items;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(isCancelable());
        builder.setTitle(R.string.index);
        final IContentUseCase contentUseCase = ((IUseCaseManager) getActivity().getApplication()).getContentUseCase();
        final IContentProvider[] providers = contentUseCase.getContentProviders();
        final Set<Integer> set = new LinkedHashSet<>();
        final ContentProviderView provider = contentUseCase.getContentProvider() instanceof ContentProviderView ? (ContentProviderView) contentUseCase.getContentProvider() : null;
        int checkedItem = -1;
        for (IContentProvider p : providers) {
            if (p instanceof ContentProviderView) {
                final ContentProviderView cpv = (ContentProviderView) p;
                if (set.add(cpv.getViewCategoryName()) && provider != null && provider.getViewCategoryName() == cpv.getViewCategoryName()) {
                    checkedItem = set.size() - 1;
                }
            }

        }
        items = set.toArray(new Integer[set.size()]);
        if (items.length > 0) {
            final CharSequence[] categoryNames = new CharSequence[items.length];
            for (int i = 0; i < items.length; i++) {
                categoryNames[i] = getString(items[i]);
            }
            builder.setSingleChoiceItems(categoryNames, checkedItem, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (IContentProvider p : contentUseCase.getContentProviders()) {
                        if (p instanceof ContentProviderView) {
                            if (((ContentProviderView) p).getViewCategoryName() == items[which]) {
                                contentUseCase.setContentProvider(p);
                                dismiss();
                                break;
                            }
                        }
                    }
                }
            });
        }
        builder.setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }

    public static void showDialog(@NonNull FragmentManager manager, @NonNull String tag) {
        DialogFragment fragment = (DialogFragment) manager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new IndexDialog();
        }
        if (fragment.isAdded()) {
            return;
        }
        fragment.show(manager, tag);
    }
}

package se.popcorn_time.ui.settings;

import android.support.annotation.NonNull;

import se.popcorn_time.mvp.Presenter;
import se.popcorn_time.mvp.ViewState;

public final class OnDownloadsUploadSpeedViewState extends ViewState<ISettingsView> {

    private Integer downloadsUploadSpeed;

    public OnDownloadsUploadSpeedViewState(@NonNull Presenter<ISettingsView> presenter, @NonNull Integer downloadsUploadSpeed) {
        super(presenter);
        this.downloadsUploadSpeed = downloadsUploadSpeed;
    }

    public void apply(@NonNull Integer downloadsUploadSpeed) {
        this.downloadsUploadSpeed = downloadsUploadSpeed;
        apply();
    }

    @Override
    public void apply(@NonNull ISettingsView view) {
        view.onDownloadsUploadSpeed(downloadsUploadSpeed);
    }
}

package se.popcorn_time.ui.settings;

import android.support.annotation.NonNull;

import se.popcorn_time.mvp.Presenter;
import se.popcorn_time.mvp.ViewState;

public final class OnPlayerHardwareAccelerationViewState extends ViewState<ISettingsView> {

    private Integer playerHardwareAcceleration;

    public OnPlayerHardwareAccelerationViewState(@NonNull Presenter<ISettingsView> presenter, @NonNull Integer playerHardwareAcceleration) {
        super(presenter);
        this.playerHardwareAcceleration = playerHardwareAcceleration;
    }

    public void apply(@NonNull Integer playerHardwareAcceleration) {
        this.playerHardwareAcceleration = playerHardwareAcceleration;
        apply();
    }

    @Override
    public void apply(@NonNull ISettingsView view) {
        view.onPlayerHardwareAcceleration(playerHardwareAcceleration);
    }
}

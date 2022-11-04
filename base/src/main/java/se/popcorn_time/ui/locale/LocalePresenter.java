package se.popcorn_time.ui.locale;

import android.support.annotation.NonNull;

import io.reactivex.functions.Consumer;
import se.popcorn_time.model.settings.ISettingsUseCase;
import se.popcorn_time.mvp.Presenter;

public final class LocalePresenter extends Presenter<ILocaleView> implements ILocalePresenter {

    private final OnLocaleChangedViewState onLocaleChangedViewState;

    public LocalePresenter(@NonNull ISettingsUseCase settingsUseCase) {
        onLocaleChangedViewState = new OnLocaleChangedViewState(LocalePresenter.this, settingsUseCase.getLanguage());
        settingsUseCase.getLanguageObservable().subscribe(new Consumer<String>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull String language) throws Exception {
                onLocaleChangedViewState.setLanguage(language).apply();
            }
        });
    }

    @Override
    protected void onAttach(@NonNull ILocaleView view) {
        super.onAttach(view);
        onLocaleChangedViewState.apply(view);
    }
}

package se.popcorn_time.mobile.ui;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;

import se.popcorn_time.base.utils.InterfaceUtil;
import se.popcorn_time.mobile.PopcornApplication;
import se.popcorn_time.mvp.IViewRouter;
import se.popcorn_time.ui.locale.ILocaleView;

public abstract class LocaleActivity extends AppCompatActivity implements IViewRouter, ILocaleView {

    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration configuration = newBase.getResources().getConfiguration();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            configuration.setLocales(new LocaleList(InterfaceUtil.getAppLocale()));
            newBase = newBase.createConfigurationContext(configuration);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(InterfaceUtil.getAppLocale());
            newBase = newBase.createConfigurationContext(configuration);
        }
        super.attachBaseContext(new ContextWrapper(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getApplication() instanceof PopcornApplication) {
            ((PopcornApplication) getApplication()).setActiveViewRouter(LocaleActivity.this);
            ((PopcornApplication) getApplication()).getLocalePresenter().attach(LocaleActivity.this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getApplication() instanceof PopcornApplication) {
            ((PopcornApplication) getApplication()).setActiveViewRouter(null);
            ((PopcornApplication) getApplication()).getLocalePresenter().detach(LocaleActivity.this);
        }
    }

    @Override
    public boolean onShowView(@NonNull Class<?> view, Object... args) {
        return getApplication() instanceof IViewRouter && ((IViewRouter) getApplication()).onShowView(view, args);
    }

    @Override
    public final void onLocaleChanged(@Nullable String language) {
        if (language == null) {
            return;
        }
        final Locale locale;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            locale = getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = getResources().getConfiguration().locale;
        }
        final String loc = locale.toString();
        if (!loc.equals(language)) {
            recreate();
        }
    }
}

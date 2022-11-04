package se.popcorn_time.mobile.ui.locale;

import android.content.Context;
import android.content.res.Configuration;

import se.popcorn_time.base.utils.InterfaceUtil;

public class LocaleHelper {

    private Context context;
    private LocaleListener mListener;
    private String lastLang;

    public LocaleHelper(Context context, LocaleListener localeListener) {
        this.context = context;
        mListener = localeListener;
        lastLang = InterfaceUtil.getAppLocale().getLanguage();
        updateLocale();
    }

    public void checkLanguage() {
        if (lastLang.equals(InterfaceUtil.getAppLocale().getLanguage())) {
            updateLocale();
        } else {
            lastLang = InterfaceUtil.getAppLocale().getLanguage();
            updateLocale();
            mListener.updateLocaleText();
        }
    }

    public void updateLocale() {
        //Locale.setDefault(InterfaceUtil.getAppLocale());
        Configuration config = context.getResources().getConfiguration();
        config.locale = InterfaceUtil.getAppLocale();
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
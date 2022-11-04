package se.popcorn_time.mobile.ui.locale;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class LocaleActivity extends AppCompatActivity implements LocaleListener {

    protected LocaleHelper mLocaleHelper;

    @Override
    protected void onCreate(Bundle arg0) {
        mLocaleHelper = new LocaleHelper(getBaseContext(), LocaleActivity.this);
        super.onCreate(arg0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        updateLocaleText();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocaleHelper.checkLanguage();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mLocaleHelper.updateLocale();
    }

    @Override
    public void updateLocaleText() {

    }
}
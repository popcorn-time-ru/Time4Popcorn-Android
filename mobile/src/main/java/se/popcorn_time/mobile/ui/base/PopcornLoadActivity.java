package se.popcorn_time.mobile.ui.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

public abstract class PopcornLoadActivity extends PopcornBaseActivity implements ContentLoadListener {

    private int mContentViewID = -1;

    private LoadingFragment loadingFragment = new LoadingFragment();
    private NoConnectionFragment noConnectionFragment = new NoConnectionFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noConnectionFragment.setLoadListener(PopcornLoadActivity.this);
    }

    @Override
    public void updateLocaleText() {
        super.updateLocaleText();
        if (noConnectionFragment.isAdded()) {
            noConnectionFragment.updateLocaleText();
        }
    }

    @Override
    public void showLoading() {
        replaceFragment(loadingFragment);
    }

    @Override
    public void showError() {
        replaceFragment(noConnectionFragment);
    }

    public void setPopcornContentViewId(int id) {
        this.mContentViewID = id;
    }

    public final void replaceFragment(final Fragment fragment) {
        if (mContentViewID != -1 && fragment != null && !fragment.isAdded()) {
            new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                    tr.replace(mContentViewID, fragment);
                    tr.commit();
                    return true;
                }
            }).sendEmptyMessage(0);
        }
    }
}
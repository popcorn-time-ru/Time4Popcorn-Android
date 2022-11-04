package se.popcorn_time.ui.share;

import android.support.annotation.NonNull;

import se.popcorn_time.model.share.IShareData;

public interface IShareView {

    void onShowShareData(@NonNull IShareData shareData);
}

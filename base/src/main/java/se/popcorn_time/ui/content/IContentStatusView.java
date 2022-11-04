package se.popcorn_time.ui.content;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import se.popcorn_time.model.content.IContentStatus;

public interface IContentStatusView {

    void onKeywords(@Nullable String keywords);

    void onContentStatus(@NonNull IContentStatus contentStatus);
}

package se.popcorn_time.mvp;

import android.support.annotation.NonNull;

public interface IViewRouter {

    boolean onShowView(@NonNull Class<?> view, Object... args);
}

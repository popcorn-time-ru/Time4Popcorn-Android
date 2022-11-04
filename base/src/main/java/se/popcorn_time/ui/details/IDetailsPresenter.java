package se.popcorn_time.ui.details;

import se.popcorn_time.mvp.IPresenter;

import se.popcorn_time.base.model.video.info.VideoInfo;

public interface IDetailsPresenter<T extends VideoInfo, V extends IDetailsView<T>> extends IPresenter<V> {
}

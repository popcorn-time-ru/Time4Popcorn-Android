package se.popcorn_time.model.content;

import java.util.List;

import se.popcorn_time.base.model.video.info.VideoInfo;

public interface IContentStatus {

    boolean isLoading();

    Throwable getError();

    List<VideoInfo> getList();
}

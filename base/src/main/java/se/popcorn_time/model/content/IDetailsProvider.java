package se.popcorn_time.model.content;

import android.support.annotation.NonNull;

import io.reactivex.Observable;
import se.popcorn_time.base.model.video.info.VideoInfo;

public interface IDetailsProvider {

    <T extends VideoInfo> boolean isDetailsExists(T videoInfo);

    @NonNull
    Observable<? extends VideoInfo> getDetails(VideoInfo videoInfo);
}

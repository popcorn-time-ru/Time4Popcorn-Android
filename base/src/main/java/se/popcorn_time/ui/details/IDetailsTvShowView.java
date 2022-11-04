package se.popcorn_time.ui.details;

import android.support.annotation.Nullable;

import se.popcorn_time.base.model.video.info.Episode;
import se.popcorn_time.base.model.video.info.Season;
import se.popcorn_time.base.model.video.info.TvShowsInfo;

public interface IDetailsTvShowView extends IDetailsView<TvShowsInfo> {

    void onSeasons(@Nullable Season[] seasons, int position);

    void onEpisodes(@Nullable Episode[] episodes, int position);
}

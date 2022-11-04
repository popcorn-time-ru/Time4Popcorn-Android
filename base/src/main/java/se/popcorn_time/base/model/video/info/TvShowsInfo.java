package se.popcorn_time.base.model.video.info;

import java.util.List;

public class TvShowsInfo extends VideoInfo {

    private List<Season> seasons;

    public TvShowsInfo(String type) {
        super(type);
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }
}

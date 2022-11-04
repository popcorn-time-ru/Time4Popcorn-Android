package se.popcorn_time.base.model.video.info;

import java.util.List;

public final class Season {

    private int number;
    private List<Episode> episodes;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }
}

package se.popcorn_time.base.model.video.info;

import java.util.List;
import java.util.Map;

public final class Episode {

    private int number;
    private String title;
    private String description;
    private String airDate;
    private Map<String, List<Torrent>> langTorrents;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAirDate() {
        return airDate;
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    public Map<String, List<Torrent>> getLangTorrents() {
        return langTorrents;
    }

    public void setLangTorrents(Map<String, List<Torrent>> torrents) {
        this.langTorrents = torrents;
    }
}

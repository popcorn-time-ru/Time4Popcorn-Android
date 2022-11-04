package se.popcorn_time.base.model.video.info;

import java.util.List;
import java.util.Map;

public class MoviesInfo extends VideoInfo {

    private Map<String, List<Torrent>> langTorrents;

    public MoviesInfo(String type) {
        super(type);
    }

    public Map<String, List<Torrent>> getLangTorrents() {
        return langTorrents;
    }

    public void setLangTorrents(Map<String, List<Torrent>> langTorrents) {
        this.langTorrents = langTorrents;
    }
}

package se.popcorn_time.base.model.video.info;

public class VideoInfo {

    private final String type;

    private String imdb;
    private String title;
    private int year;
    private float rating;
    private int durationMinutes;
    private String description;
    private String actors;
    private String poster;
    private String posterBig;
    private String trailer;
    private String[] genres;
    private String[] backdrops;

    public VideoInfo(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getImdb() {
        return imdb;
    }

    public void setImdb(String imdb) {
        this.imdb = imdb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getPosterBig() {
        return posterBig;
    }

    public void setPosterBig(String posterBig) {
        this.posterBig = posterBig;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public String[] getBackdrops() {
        return backdrops;
    }

    public void setBackdrops(String[] backdrops) {
        this.backdrops = backdrops;
    }

}
package se.popcorn_time.model.share;

public class VideoShareData extends ShareData implements IVideoShareData {

    private String imdb;
    private double rate;

    @Override
    public String getImdb() {
        return imdb;
    }

    @Override
    public double getRate() {
        return rate;
    }

    public void setImdb(String imdb) {
        this.imdb = imdb;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}

package se.popcorn_time.model.config;

public class Config {

    private String[] urls;
    private String[] updaterUrls;
    private String[] shareUrls;
    private String analyticsId;
    private String cinemaUrl;
    private String animeUrl;
    private String posterUrl;
    private String subtitlesUrl;
    private String siteUrl;
    private String forumUrl;
    private String facebookUrl;
    private String twitterUrl;
    private String youtubeUrl;
    private String imdbUrl;
    private String referrerRegex;
    private VpnConfig vpnConfig;

    public String[] getUrls() {
        return urls;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }

    public String[] getUpdaterUrls() {
        return updaterUrls;
    }

    public void setUpdaterUrls(String[] updaterUrls) {
        this.updaterUrls = updaterUrls;
    }

    public String[] getShareUrls() {
        return shareUrls;
    }

    public void setShareUrls(String[] shareUrls) {
        this.shareUrls = shareUrls;
    }

    public String getAnalyticsId() {
        return analyticsId;
    }

    public void setAnalyticsId(String analyticsId) {
        this.analyticsId = analyticsId;
    }

    public String getCinemaUrl() {
        return cinemaUrl;
    }

    public void setCinemaUrl(String cinemaUrl) {
        this.cinemaUrl = cinemaUrl;
    }

    public String getAnimeUrl() {
        return animeUrl;
    }

    public void setAnimeUrl(String animeUrl) {
        this.animeUrl = animeUrl;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getSubtitlesUrl() {
        return subtitlesUrl;
    }

    public void setSubtitlesUrl(String subtitlesUrl) {
        this.subtitlesUrl = subtitlesUrl;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getForumUrl() {
        return forumUrl;
    }

    public void setForumUrl(String forumUrl) {
        this.forumUrl = forumUrl;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getTwitterUrl() {
        return twitterUrl;
    }

    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getImdbUrl() {
        return imdbUrl;
    }

    public void setImdbUrl(String imdbUrl) {
        this.imdbUrl = imdbUrl;
    }

    public String getReferrerRegex() {
        return referrerRegex;
    }

    public void setReferrerRegex(String referrerRegex) {
        this.referrerRegex = referrerRegex;
    }

    public VpnConfig getVpnConfig() {
        return vpnConfig;
    }

    public void setVpnConfig(VpnConfig vpnConfig) {
        this.vpnConfig = vpnConfig;
    }
}

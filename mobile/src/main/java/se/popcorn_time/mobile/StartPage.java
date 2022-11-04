package se.popcorn_time.mobile;

import android.content.res.Resources;
import android.support.annotation.NonNull;

public final class StartPage {

    public static final int PAGE_CINEMA_MOVIES = 0;
    public static final int PAGE_CINEMA_TV_SHOWS = 1;
    public static final int PAGE_ANIME_MOVIES = 2;
    public static final int PAGE_ANIME_TV_SHOWS = 3;
    public static final int DEFAULT_START_PAGE = PAGE_CINEMA_MOVIES;

    private StartPage() {
    }

    @NonNull
    public static String getName(@NonNull Resources resources, @NonNull Integer startPage) {
        switch (startPage) {
            case PAGE_CINEMA_MOVIES:
                return resources.getString(R.string.cinema) + " - " + resources.getString(R.string.movies);
            case PAGE_CINEMA_TV_SHOWS:
                return resources.getString(R.string.cinema) + " - " + resources.getString(R.string.tv_shows);
            case PAGE_ANIME_MOVIES:
                return resources.getString(R.string.anime) + " - " + resources.getString(R.string.movies);
            case PAGE_ANIME_TV_SHOWS:
                return resources.getString(R.string.anime) + " - " + resources.getString(R.string.tv_shows);
        }
        return "Unknown";
    }
}

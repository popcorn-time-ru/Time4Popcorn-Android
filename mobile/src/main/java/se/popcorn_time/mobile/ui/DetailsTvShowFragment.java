package se.popcorn_time.mobile.ui;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.player.dialog.ListItemEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import se.popcorn_time.IUseCaseManager;
import se.popcorn_time.VibrantUtils;
import se.popcorn_time.base.database.tables.History;
import se.popcorn_time.base.model.DownloadInfo;
import se.popcorn_time.base.model.video.info.Episode;
import se.popcorn_time.base.model.video.info.Season;
import se.popcorn_time.base.model.video.info.Torrent;
import se.popcorn_time.base.model.video.info.TvShowsInfo;
import se.popcorn_time.base.model.video.info.VideoInfo;
import se.popcorn_time.mobile.R;
import se.popcorn_time.mobile.ui.adapter.EpisodesAdapter;
import se.popcorn_time.mobile.ui.adapter.VideoPosterAdapter;
import se.popcorn_time.mobile.ui.widget.ItemSelectButton;
import se.popcorn_time.ui.details.DetailsTvShowPresenter;
import se.popcorn_time.ui.details.IDetailsTvShowPresenter;
import se.popcorn_time.ui.details.IDetailsTvShowView;

public final class DetailsTvShowFragment extends DetailsVideoFragment<TvShowsInfo, IDetailsTvShowView, IDetailsTvShowPresenter> implements IDetailsTvShowView {

    private View selector;
    private ItemSelectButton seasonsBtn;
    private RecyclerView episodes;

    @NonNull
    @Override
    protected IDetailsTvShowPresenter onCreateDetailsPresenter(@NonNull IUseCaseManager useCaseManager) {
        return new DetailsTvShowPresenter(useCaseManager.getContentUseCase(), useCaseManager.getDetailsUseCase());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_details_tvshow, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selector = view.findViewById(R.id.selector);
        seasonsBtn = (ItemSelectButton) view.findViewById(R.id.seasons);
        seasonsBtn.setPrompt(R.string.seasons);
        seasonsBtn.setFragmentManager(getActivity().getSupportFragmentManager());
        episodes = (RecyclerView) view.findViewById(R.id.episodes);
        episodes.setAdapter(new EpisodesAdapter(detailsUseCase));
        episodes.addItemDecoration(new RecyclerView.ItemDecoration() {

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                final int offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
                final int position = parent.getLayoutManager().getPosition(view);
                outRect.left = position == 0 ? offset : 0;
                outRect.right = position == parent.getLayoutManager().getItemCount() - 1 ? offset : 0;
            }
        });
        view.findViewById(R.id.seasons_bkg).setBackgroundColor(VibrantUtils.getAccentColor());
    }

    @Override
    public void onSeasons(@Nullable Season[] seasons, int position) {
        if (seasons == null || seasons.length == 0) {
            selector.setVisibility(View.GONE);
            seasonsBtn.setItems(null, -1);
        } else {
            selector.setVisibility(View.VISIBLE);
            final List<ListItemEntity> items = new ArrayList<>();
            for (Season season : seasons) {
                ListItemEntity.addItemToList(items, new ListItemEntity<Season>(season) {

                    @Override
                    public String getName() {
                        return String.format(Locale.ENGLISH, "%s #%s", getString(R.string.season), getValue().getNumber());
                    }

                    @Override
                    public void onItemChosen() {
                        detailsUseCase.getSeasonChoiceProperty().setPosition(getPosition());
                    }
                });
            }
            seasonsBtn.setItems(items, position);
        }
    }

    @Override
    public void onEpisodes(@Nullable Episode[] episodes, int position) {
        final Episode episode = episodes != null ? episodes[position] : null;
        if (episode != null) {
            watchedButton.setOnCheckedChangeListener(null);
            watchedButton.setChecked(
                    History.isWatched(getContext(),
                            detailsUseCase.getVideoInfoProperty().getValue().getImdb(),
                            detailsUseCase.getSeasonChoiceProperty().getItem().getNumber(),
                            episode.getNumber())
            );
            watchedButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    final VideoInfo info = detailsUseCase.getVideoInfoProperty().getValue();
                    final Season season = detailsUseCase.getSeasonChoiceProperty().getItem();
                    if (isChecked) {
                        History.insert(getContext(), info.getImdb(), season.getNumber(), episode.getNumber());
                        Toast.makeText(getContext(), R.string.episode_marked_as_watched, Toast.LENGTH_SHORT).show();
                    } else {
                        History.delete(getContext(), info.getImdb(), season.getNumber(), episode.getNumber());
                        Toast.makeText(getContext(), R.string.episode_marked_as_unwatched, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            final StringBuilder builder = new StringBuilder("<big><b>" + episode.getTitle() + "</b></big>");
            if (!TextUtils.isEmpty(episode.getDescription())) {
                builder.append("<br><br>");
                builder.append(episode.getDescription());
            }
            additionalDescription.setText(Html.fromHtml(builder.toString()));
            additionalDescription.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(episode.getAirDate())) {
                additionalReleaseDate.setVisibility(View.GONE);
            } else {
                additionalReleaseDate.setText(String.format(Locale.ENGLISH, "Air Date: %s", episode.getAirDate()));
                additionalReleaseDate.setVisibility(View.VISIBLE);
            }
        } else {
            additionalDescription.setVisibility(View.GONE);
        }
        ((EpisodesAdapter) this.episodes.getAdapter()).setEpisodes(episodes, position);
        if (Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation && backdrops != null) {
            // Fix backdrops size
            ((VideoPosterAdapter) backdrops.getAdapter()).notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    protected DownloadInfo buildDownloadInfo(@NonNull Torrent torrent) {
        final DownloadInfo info = super.buildDownloadInfo(torrent);
        final Season season = detailsUseCase.getSeasonChoiceProperty().getItem();
        info.season = season != null ? season.getNumber() : -1;
        final Episode episode = detailsUseCase.getEpisodeChoiceProperty().getItem();
        info.episode = episode != null ? episode.getNumber() : -1;
        return info;
    }
}

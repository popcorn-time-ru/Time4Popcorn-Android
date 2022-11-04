package se.popcorn_time.ui.details;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import se.popcorn_time.base.model.video.info.Torrent;
import se.popcorn_time.base.model.video.info.VideoInfo;
import se.popcorn_time.model.ChoiceProperty;
import se.popcorn_time.model.Property;
import se.popcorn_time.model.content.IContentUseCase;
import se.popcorn_time.model.details.IDetailsUseCase;
import se.popcorn_time.model.subtitles.Subtitles;
import se.popcorn_time.mvp.IViewState;
import se.popcorn_time.mvp.Presenter;

public abstract class DetailsPresenter<T extends VideoInfo, V extends IDetailsView<T>> extends Presenter<V> implements IDetailsPresenter<T, V> {

    protected final IContentUseCase contentUseCase;
    protected final IDetailsUseCase detailsUseCase;

    private final VideoInfoViewState videoInfoViewState = new VideoInfoViewState();
    private final DubbingViewState dubbingViewState = new DubbingViewState();
    private final TorrentsViewState torrentsViewState = new TorrentsViewState();
    private final LangSubtitlesViewState langSubtitlesViewState = new LangSubtitlesViewState();

    protected final CompositeDisposable disposables = new CompositeDisposable();

    public DetailsPresenter(@NonNull IContentUseCase contentUseCase, @NonNull IDetailsUseCase detailsUseCase) {
        this.contentUseCase = contentUseCase;
        this.detailsUseCase = detailsUseCase;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        videoInfoViewState.apply((Property<T>) detailsUseCase.getVideoInfoProperty());
        dubbingViewState.apply(detailsUseCase.getDubbingChoiceProperty());
        torrentsViewState.apply(detailsUseCase.getTorrentChoiceProperty());
        langSubtitlesViewState.apply(detailsUseCase.getLangSubtitlesChoiceProperty());
        disposables.add(detailsUseCase.getVideoInfoProperty().getObservable().subscribe(new Consumer<Property<? extends VideoInfo>>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull Property<? extends VideoInfo> property) throws Exception {
                videoInfoViewState.apply((Property<T>) property);
            }
        }));
        disposables.add(detailsUseCase.getDubbingChoiceProperty().getObservable().subscribe(new Consumer<ChoiceProperty<Map.Entry<String, List<Torrent>>>>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull ChoiceProperty<Map.Entry<String, List<Torrent>>> property) throws Exception {
                dubbingViewState.apply(property);
            }
        }));
        disposables.add(detailsUseCase.getTorrentChoiceProperty().getObservable().subscribe(new Consumer<ChoiceProperty<Torrent>>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull ChoiceProperty<Torrent> property) throws Exception {
                torrentsViewState.apply(property);
            }
        }));
        disposables.add(detailsUseCase.getLangSubtitlesChoiceProperty().getObservable().subscribe(new Consumer<ChoiceProperty<Map.Entry<String, List<Subtitles>>>>() {

            @Override
            public void accept(@io.reactivex.annotations.NonNull ChoiceProperty<Map.Entry<String, List<Subtitles>>> property) throws Exception {
                langSubtitlesViewState.apply(property);
            }
        }));
    }

    @Override
    protected void onAttach(@NonNull V view) {
        super.onAttach(view);
        videoInfoViewState.apply(view);
        dubbingViewState.apply(view);
        torrentsViewState.apply(view);
        langSubtitlesViewState.apply(view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    private final class VideoInfoViewState implements IViewState<V> {

        private T videoInfo;

        @Override
        public void apply(@NonNull V view) {
            if (videoInfo != null) {
                view.onVideoInfo(videoInfo);
            }
        }

        public void apply(@NonNull Property<T> property) {
            this.videoInfo = property.getValue();
            DetailsPresenter.this.apply(this);
        }
    }

    abstract class ChoicePropertyViewState<E> implements IViewState<V> {

        protected E[] items;
        protected int position;

        public final void apply(@NonNull ChoiceProperty<E> property) {
            this.items = property.getItems();
            this.position = property.getPosition();
            DetailsPresenter.this.apply(this);
        }
    }

    private final class DubbingViewState extends ChoicePropertyViewState<Map.Entry<String, List<Torrent>>> {

        @Override
        public void apply(@NonNull V view) {
            final String[] languages = items != null ? new String[items.length] : null;
            if (languages != null) {
                for (int i = 0; i < languages.length; i++) {
                    languages[i] = items[i].getKey();
                }
            }
            view.onDubbing(languages, position);
        }
    }

    private final class TorrentsViewState extends ChoicePropertyViewState<Torrent> {

        @Override
        public void apply(@NonNull V view) {
            view.onTorrents(items, position);
        }
    }

    private final class LangSubtitlesViewState extends ChoicePropertyViewState<Map.Entry<String, List<Subtitles>>> {

        @Override
        public void apply(@NonNull V view) {
            final String[] languages = items != null ? new String[items.length] : null;
            if (languages != null) {
                for (int i = 0; i < languages.length; i++) {
                    languages[i] = items[i].getKey();
                }
            }
            view.onLangSubtitles(languages, position);
        }
    }
}

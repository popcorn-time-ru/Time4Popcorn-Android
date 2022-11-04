package se.popcorn_time.ui.details;

import android.support.annotation.NonNull;

import se.popcorn_time.base.model.video.info.MoviesInfo;
import se.popcorn_time.model.content.IContentUseCase;
import se.popcorn_time.model.details.IDetailsUseCase;

public final class DetailsMoviePresenter extends DetailsPresenter<MoviesInfo, IDetailsMovieView> implements IDetailsMoviePresenter {

    public DetailsMoviePresenter(@NonNull IContentUseCase contentUseCase, @NonNull IDetailsUseCase detailsUseCase) {
        super(contentUseCase, detailsUseCase);
    }
}

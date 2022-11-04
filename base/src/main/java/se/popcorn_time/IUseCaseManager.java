package se.popcorn_time;

import android.support.annotation.NonNull;

import se.popcorn_time.model.content.IContentUseCase;
import se.popcorn_time.model.details.IDetailsUseCase;

public interface IUseCaseManager {

    @NonNull
    IContentUseCase getContentUseCase();

    @NonNull
    IDetailsUseCase getDetailsUseCase();
}

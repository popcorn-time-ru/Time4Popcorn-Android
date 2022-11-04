package se.popcorn_time.model;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class ObservableProperty<T> extends Property<T> {

    private final Subject<Property<T>> subject = PublishSubject.create();

    public ObservableProperty() {
    }

    public ObservableProperty(T value) {
        super(value);
    }

    @Override
    public void setValue(T value) {
        setValue(value, true);
    }

    public void setValue(T value, boolean notify) {
        super.setValue(value);
        if (notify) {
            subject.onNext(this);
        }
    }

    public final Observable<Property<T>> getObservable() {
        return subject;
    }
}

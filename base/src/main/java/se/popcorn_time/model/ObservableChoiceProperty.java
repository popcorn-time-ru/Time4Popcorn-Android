package se.popcorn_time.model;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class ObservableChoiceProperty<T> extends ChoiceProperty<T> {

    private final Subject<ChoiceProperty<T>> subject = PublishSubject.create();

    public ObservableChoiceProperty() {
    }

    public ObservableChoiceProperty(T[] items) {
        super(items);
    }

    public ObservableChoiceProperty(T[] items, int position) {
        super(items, position);
    }

    @Override
    public void setItems(T[] items) {
        super.setItems(items);
        subject.onNext(this);
    }

    @Override
    public void setPosition(int position) {
        super.setPosition(position);
        subject.onNext(this);
    }

    @Override
    public void setItems(T[] items, int position) {
        super.setItems(items, position);
        subject.onNext(this);
    }

    public Observable<ChoiceProperty<T>> getObservable() {
        return subject;
    }
}

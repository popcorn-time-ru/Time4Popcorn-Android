package se.popcorn_time.model;

public class ChoiceProperty<T> {

    private T[] items;
    private int position;

    public ChoiceProperty() {
        this.items = null;
        this.position = getDefaultPosition(null);
    }

    public ChoiceProperty(T[] items) {
        this.items = null;
        this.position = getDefaultPosition(items);
    }

    public ChoiceProperty(T[] items, int position) {
        this.items = items;
        this.position = position;
    }

    public T[] getItems() {
        return items;
    }

    public void setItems(T[] items) {
        this.items = items;
        this.position = getDefaultPosition(items);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setItems(T[] items, int position) {
        this.items = items;
        this.position = position;
    }

    public T getItem() {
        return getItem(position);
    }

    public T getItem(int position) {
        return position >= 0 && items != null && items.length > position ? items[position] : null;
    }

    private int getDefaultPosition(T[] items) {
        return items != null && items.length > 0 ? 0 : -1;
    }
}

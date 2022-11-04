package com.player.dialog;

import java.util.List;

public abstract class ListItemEntity<T> {

    private T value;
    private String name;
    private int position;

    public ListItemEntity(T value) {
        this(value, null);
    }

    public ListItemEntity(T value, String name) {
        this.value = value;
        this.name = name;
    }

    public abstract void onItemChosen();

    public T getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public CharSequence getControlText() {
        return getName();
    }

    protected void setPosition(int position) {
        this.position = position;
    }

    public static <T extends ListItemEntity> void addItemToList(List<T> list, T item) {
        if (list == null || item == null) {
            return;
        }
        if (list.add(item)) {
            item.setPosition(list.size() - 1);
        }
    }
}
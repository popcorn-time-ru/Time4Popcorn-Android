package com.player;

public interface PlayerControl {

    long getLength();

    long getPosition();

    boolean isPlaying();

    void play();

    void pause();

    void seek(long position);

    void volumeUp();

    void volumeDown();
}
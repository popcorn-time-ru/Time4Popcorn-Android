package se.popcorn_time.base.torrent;

public class FileEntry {

    public String path;
    public long size;

    public FileEntry() {

    }

    public FileEntry(String path, long size) {
        this.path = path;
        this.size = size;
    }

    @Override
    public String toString() {
        return path + "-->" + size;
    }
}
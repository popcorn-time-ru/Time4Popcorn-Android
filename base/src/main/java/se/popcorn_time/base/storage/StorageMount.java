package se.popcorn_time.base.storage;

import java.io.File;

public class StorageMount {

    public final File dir;
    public final String label;
    public final boolean primary;

    public StorageMount(File dir, String label, boolean primary) {
        this.label = label;
        this.dir = dir;
        this.primary = primary;
    }
}
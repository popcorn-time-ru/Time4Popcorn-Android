package se.popcorn_time.base.storage;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.EnvironmentCompat;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import se.popcorn_time.base.utils.Logger;
import se.popcorn_time.model.settings.ISettingsUseCase;

public class StorageUtil {

    private static final StorageUtil INSTANCE = new StorageUtil();

    public final static boolean DEFAULT_CLEAR = true;

    public final static long SIZE_KB = 1024L;
    public final static long SIZE_MB = SIZE_KB * SIZE_KB;
    public final static long SIZE_GB = SIZE_KB * SIZE_KB * SIZE_KB;

    public static final String ROOT_FOLDER_NAME = "time4popcorn";
    private static final String CACHE_FOLDER_NAME = "cache";
    private static final String DOWNLOADS_FOLDER_NAME = "downloads";

    private final List<StorageMount> storageMounts = new ArrayList<>();
    private File rootDir;
    private File cacheDir;
    private File downloadsDir;

    private StorageUtil() {
    }

    public static void init(@NonNull Context context, @NonNull ISettingsUseCase settingsUseCase) {
        INSTANCE.storageMounts.clear();
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            INSTANCE.storageMounts.add(new StorageMount(Environment.getExternalStorageDirectory(), "device", true));
        }

        File externalFilesDir = context.getExternalFilesDir(null);
        if (externalFilesDir != null) {
            List<File> externalDirs;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                externalDirs = new LinkedList<>();
                for (File file : ContextCompat.getExternalFilesDirs(context, null)) {
                    if (file != null) {
                        externalDirs.add(file);
                    }
                }
            } else {
                externalDirs = new LinkedList<>();
                externalDirs.add(context.getExternalFilesDir(null));
                try {
                    String secondaryStorages = System.getenv("SECONDARY_STORAGE");
                    if (secondaryStorages != null) {
                        String[] storages = secondaryStorages.split(File.pathSeparator);
                        for (String s : storages) {
                            externalDirs.add(new File(s));
                        }
                    }
                } catch (Throwable e) {
                    Logger.error("StorageUtil<init>: Unable to get secondary external storage", e);
                }
            }

            String primaryPath = externalFilesDir.getParent();
            for (int i = 0; i < externalDirs.size(); i++) {
                File file = externalDirs.get(i);
                if (!file.getAbsolutePath().startsWith(primaryPath) && isSecondaryExternalStorageMounted(file)) {
                    INSTANCE.storageMounts.add(new StorageMount(file, Integer.toString(i), false));
                }
            }
        }

        if (settingsUseCase.getDownloadsCacheFolder() == null) {
            for (StorageMount s : INSTANCE.storageMounts) {
                if (s.primary) {
                    final File file = new File(s.dir.getAbsolutePath(), ROOT_FOLDER_NAME);
                    settingsUseCase.setDownloadsCacheFolder(file);
                    setRootDir(file);
                    break;
                }
            }
        } else {
            setRootDir(settingsUseCase.getDownloadsCacheFolder());
        }
    }

    public static List<StorageMount> getStorageMounts() {
        return INSTANCE.storageMounts;
    }

    private static boolean isSecondaryExternalStorageMounted(File path) {
        if (path == null) {
            return false;
        }

        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            result = Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(path));
        } else {
            try {
                String[] l = path.list();
                result = l != null && l.length > 0;
            } catch (Throwable e) {
                Logger.error("StorageUtil<isSecondaryExternalStorageMounted>: Error detecting secondary external storage state", e);
            }
        }

        return result;
    }

	/*
     * Root
	 */

    public static void setRootDir(File dir) {
        if (dir != null && dir != INSTANCE.rootDir) {
            if (!dir.exists() && !dir.mkdirs()) {
                Logger.error("StorageUtil<setRootDir>: Create root dir error");
                return;
            }
            //TODO: what about files from download dir?
            clearCacheDir();
//            clearDir(INSTANCE.rootDir);

            File cache = new File(dir.getAbsolutePath(), CACHE_FOLDER_NAME);
            if (cache.exists()) {
                INSTANCE.cacheDir = cache;
            } else {
                if (cache.mkdirs()) {
                    INSTANCE.cacheDir = cache;
                } else {
                    Logger.error("StorageUtil<setRootDir>: Create cache dir error");
                }
            }

            File downloads = new File(dir.getAbsolutePath(), DOWNLOADS_FOLDER_NAME);
            if (downloads.exists()) {
                INSTANCE.downloadsDir = downloads;
            } else {
                if (downloads.mkdirs()) {
                    INSTANCE.downloadsDir = downloads;
                } else {
                    Logger.error("StorageUtil<setRootDir>: Create downloads dir error");
                }
            }
            INSTANCE.rootDir = dir;
        }
    }

	/*
     * Cache
	 */

    public static File getCacheDir() {
        return INSTANCE.cacheDir;
    }

    public static String getCacheDirPath() {
        return INSTANCE.cacheDir != null ? INSTANCE.cacheDir.getAbsolutePath() : null;
    }

    public static void clearCacheDir() {
        clearDir(INSTANCE.cacheDir);
    }

	/*
     * Downloads
	 */

    public static File getDownloadsDir() {
        return INSTANCE.downloadsDir;
    }

    public static String getDownloadsDirPath() {
        return INSTANCE.downloadsDir != null ? INSTANCE.downloadsDir.getAbsolutePath() : null;
    }

    public static void clearDownloadsDir() {
        clearDir(INSTANCE.downloadsDir);
    }

    /*
     * Static methods
	 */

    public static void deleteDir(File parent) {
        if (parent != null && parent.isDirectory()) {
            try {
                FileUtils.deleteDirectory(parent);
            } catch (IOException e) {
                Logger.error("StorageUtil<deleteDirectory>: Error", e);
            }
        }
    }

    public static void clearDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            try {
                FileUtils.cleanDirectory(dir);
            } catch (IOException e) {
                Logger.error("StorageUtil<clearDirectory>: Error", e);
            }
        }
    }

    public static File getSDCardFolder(Context context) {
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                return Environment.getExternalStorageDirectory();
            } else {
                return context.getExternalCacheDir();
            }
        } catch (Exception ex) {
            Logger.error("StorageUtil<getSDCardFolder>: Error", ex);
        }
        return null;
    }

    public static long getAvailableSpaceInBytes(String path) {
        long availableSpace = -1L;

        try {
            StatFs stat = new StatFs(path);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                availableSpace = stat.getAvailableBytes();
            } else {
                availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return availableSpace;
    }

    public static String getSizeText(long size) {
        String text;
        if (size >= 1000000000) {
            text = String.format("%.2f", ((float) size / SIZE_GB)) + " GB";
        } else if (size >= 1000000) {
            text = String.format("%.2f", ((float) size / SIZE_MB)) + " MB";
        } else if (size >= 1000) {
            text = String.format("%.2f", ((float) size / SIZE_KB)) + " KB";
        } else {
            text = size + " B";
        }
        text = text.replace(',', '.');
        return text;
    }
}
package se.popcorn_time.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public final class PermissionsUtils {

    private PermissionsUtils() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean requestPermissions(@NonNull Activity activity, int requestCode, String... permissions) {
        final List<String> list = new ArrayList<>();
        for (String permission : permissions) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                list.add(permission);
            }
        }
        if (list.isEmpty()) {
            return false;
        }
        activity.requestPermissions(list.toArray(new String[list.size()]), requestCode);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean requestPermissions(@NonNull Fragment fragment, int requestCode, String... permissions) {
        final List<String> list = new ArrayList<>();
        for (String permission : permissions) {
            if (fragment.getContext().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                list.add(permission);
            }
        }
        if (list.isEmpty()) {
            return false;
        }
        fragment.requestPermissions(list.toArray(new String[list.size()]), requestCode);
        return true;
    }

    public static boolean isPermissionsGranted(@NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}

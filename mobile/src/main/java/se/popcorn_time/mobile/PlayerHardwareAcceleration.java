package se.popcorn_time.mobile;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import org.videolan.vlc.util.VLCOptions;

public final class PlayerHardwareAcceleration {

    public static final int AUTOMATIC = VLCOptions.HW_ACCELERATION_AUTOMATIC;
    public static final int DISABLED = VLCOptions.HW_ACCELERATION_DISABLED;
    public static final int DECODING = VLCOptions.HW_ACCELERATION_DECODING;
    public static final int FULL = VLCOptions.HW_ACCELERATION_FULL;

    private PlayerHardwareAcceleration() {
    }

    @NonNull
    public static String getName(@NonNull Resources resources, @NonNull Integer playerHardwareAcceleration) {
        switch (playerHardwareAcceleration) {
            case AUTOMATIC:
                return resources.getString(R.string.hardware_acceleration_automatic);
            case DISABLED:
                return resources.getString(R.string.hardware_acceleration_disabled);
            case DECODING:
                return resources.getString(R.string.hardware_acceleration_decoding);
            case FULL:
                return resources.getString(R.string.hardware_acceleration_full);
        }
        return Integer.toString(playerHardwareAcceleration);
    }
}

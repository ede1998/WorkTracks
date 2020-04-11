package me.erikhennig.worktracks.model;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.pm.PackageInfoCompat;

import me.erikhennig.worktracks.BuildConfig;

public class SystemUtils {

    private static final String TAG = SystemUtils.class.getName();

    private SystemUtils() {
    }

    /**
     * Get the app version from the manifest.
     *
     * @return the version, or an empty string in case of failure.
     */
    public static String getAppVersionName(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_META_DATA);
            return pi.versionName + "/" + BuildConfig.VERSION_NAME_FULL;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Failed to get version info.", e);
            return "";
        }
    }

    public static Long getAppVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_META_DATA);
            return PackageInfoCompat.getLongVersionCode(pi);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Failed to get version info.", e);
            return -1L;
        }
    }
}

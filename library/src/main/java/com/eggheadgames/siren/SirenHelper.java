package com.eggheadgames.siren;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by yuriy on 10.03.2016.
 */
public class SirenHelper {

    protected static String getPackageName(Context context) {
        return context.getPackageName();
    }

    protected static int getDaysSinceLastCheck(Context context) {
        long lastCheckTimestamp = PreferenceManager.getDefaultSharedPreferences(context).getLong(Constants.PREFERENCES_LAST_CHECK_DATE, 0);

        if (lastCheckTimestamp > 0) {
            return (int) (TimeUnit.MILLISECONDS.toDays(Calendar.getInstance().getTimeInMillis()) - TimeUnit.MILLISECONDS.toDays(lastCheckTimestamp));
        } else {
            return 0;
        }
    }

    protected static int getVersionCode(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(getPackageName(context), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    protected static boolean isVersionSkippedByUser(Context context, String minAppVersion) {
        String skippedVersion = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.PREFERENCES_SKIPPED_VERSION, "");
        return skippedVersion.equals(minAppVersion);
    }

    protected static void setLastVerificationDate(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(Constants.PREFERENCES_LAST_CHECK_DATE, Calendar.getInstance().getTimeInMillis())
                .commit();
    }

    public static String getAlertMessage(Context context, String minAppVersion) {
        try {
            if (context.getApplicationInfo().labelRes != 0) {
                return context.getString(R.string.update_alert_message, context.getString(context.getApplicationInfo().labelRes), minAppVersion);
            } else {
                return context.getString(R.string.update_alert_message, context.getString(R.string.fallback_app_name), minAppVersion);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return context.getString(R.string.update_alert_message, context.getString(R.string.fallback_app_name), minAppVersion);
        }
    }

    public static void openGooglePlay(Activity activity) {
        final String appPackageName = getPackageName(activity);
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void setVersionSkippedByUser(Context context, String skippedVersion) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(Constants.PREFERENCES_SKIPPED_VERSION, skippedVersion)
                .commit();
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(getPackageName(context), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean isGreater(String first, String second) {
        if (TextUtils.isDigitsOnly(first) && TextUtils.isDigitsOnly(second)) {
            return Integer.parseInt(first) > Integer.parseInt(second);
        }
        return false;
    }
}

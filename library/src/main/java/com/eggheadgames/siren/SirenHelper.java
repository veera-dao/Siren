package com.eggheadgames.siren;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;

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

    protected static boolean isVersionSkippedByUser(Context context, int minAppVersion) {
        int skippedVersion = PreferenceManager.getDefaultSharedPreferences(context).getInt(Constants.PREFERENCES_SKIPPED_VERSION, 0);
        return minAppVersion == skippedVersion;
    }

    protected static void setLastVerificationDate(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(Constants.PREFERENCES_LAST_CHECK_DATE, Calendar.getInstance().getTimeInMillis())
                .commit();
    }

    public static String getAlertMessage(Context context) {
        try {
            if (context.getApplicationInfo().labelRes != 0) {
                return context.getString(R.string.alert_message_pattern, context.getString(context.getApplicationInfo().labelRes));
            } else {
                return context.getString(R.string.alert_message_pattern, context.getString(R.string.fallback_app_name));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return context.getString(R.string.alert_message_pattern, context.getString(R.string.fallback_app_name));
        }
    }

    public static void openGooglePlay(Activity activity) {
        final String appPackageName = getPackageName(activity); // getPackageName() from Context or Activity object
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void setVersionSkippedByUser(Context context, int skippedVersion) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(Constants.PREFERENCES_SKIPPED_VERSION, skippedVersion)
                .commit();
    }
}

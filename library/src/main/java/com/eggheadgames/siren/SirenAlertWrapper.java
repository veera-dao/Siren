package com.eggheadgames.siren;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public class SirenAlertWrapper {

    private final WeakReference<Activity> mActivityRef;
    private final ISirenListener mSirenListener;
    private final SirenAlertType mSirenAlertType;
    private final String mMinAppVersion;
    @Nullable private final String mUpdateUrl;
    private final SirenSupportedLocales mLocale;
    private final SirenHelper mSirenHelper;

    public SirenAlertWrapper(Activity activity, ISirenListener sirenListener, SirenAlertType sirenAlertType,
                             String minAppVersion, @Nullable String updateUrl, SirenSupportedLocales locale, SirenHelper sirenHelper) {
        this.mSirenListener = sirenListener;
        this.mSirenAlertType = sirenAlertType;
        this.mMinAppVersion = minAppVersion;
        this.mUpdateUrl = updateUrl;
        this.mLocale = locale;
        this.mSirenHelper = sirenHelper;
        this.mActivityRef = new WeakReference<>(activity);
    }


    public void show() {
        Activity activity = mActivityRef.get();
        if (activity == null) {
            if (mSirenListener != null) {
                mSirenListener.onError(new NullPointerException("activity reference is null"));
            }
        } else if (!activity.isDestroyed()) {

            AlertDialog alertDialog = initDialog(activity);
            setupDialog(alertDialog, this.mUpdateUrl);

            if (mSirenListener != null) {
                mSirenListener.onShowUpdateDialog();
            }
        }
    }

    @SuppressLint("InflateParams")
    private AlertDialog initDialog(Activity activity) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder.setTitle(mSirenHelper.getLocalizedString(mActivityRef.get(), R.string.update_available, mLocale));
        alertBuilder.setCancelable(false);

        View dialogView = LayoutInflater.from(activity).inflate(R.layout.siren_dialog, null);
        alertBuilder.setView(dialogView);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

        return alertDialog;
    }

    private void setupDialog(final AlertDialog dialog, @Nullable String updateUrl) {
        TextView message = (TextView) dialog.findViewById(R.id.tvSirenAlertMessage);
        Button update = (Button) dialog.findViewById(R.id.btnSirenUpdate);
        Button nextTime = (Button) dialog.findViewById(R.id.btnSirenNextTime);
        final Button skip = (Button) dialog.findViewById(R.id.btnSirenSkip);

        update.setText(mSirenHelper.getLocalizedString(mActivityRef.get(), R.string.siren_update, mLocale));
        nextTime.setText(mSirenHelper.getLocalizedString(mActivityRef.get(), R.string.next_time, mLocale));
        skip.setText(mSirenHelper.getLocalizedString(mActivityRef.get(), R.string.skip_this_version, mLocale));

        message.setText(mSirenHelper.getAlertMessage(mActivityRef.get(), mMinAppVersion, mLocale));

        if (mSirenAlertType == SirenAlertType.FORCE
                || mSirenAlertType == SirenAlertType.OPTION
                || mSirenAlertType == SirenAlertType.SKIP) {
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSirenListener != null) {
                        mSirenListener.onLaunchGooglePlay();
                    }
                    dialog.dismiss();
                    mSirenHelper.openGooglePlay(mActivityRef.get(), updateUrl);
                }
            });
        }

        if (mSirenAlertType == SirenAlertType.OPTION
                || mSirenAlertType == SirenAlertType.SKIP) {
            nextTime.setVisibility(View.VISIBLE);
            nextTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSirenListener != null) {
                        mSirenListener.onCancel();
                    }
                    dialog.dismiss();
                }
            });
        }
        if (mSirenAlertType == SirenAlertType.SKIP) {
            skip.setVisibility(View.VISIBLE);
            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSirenListener != null) {
                        mSirenListener.onSkipVersion();
                    }

                    mSirenHelper.setVersionSkippedByUser(mActivityRef.get(), mMinAppVersion);
                    dialog.dismiss();
                }
            });
        }
    }
}

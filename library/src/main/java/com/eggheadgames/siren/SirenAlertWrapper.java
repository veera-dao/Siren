package com.eggheadgames.siren;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by yuriy on 10.03.2016.
 */
public class SirenAlertWrapper {

    WeakReference<Activity> activityRef;
    private int theme;
    private ISirenListener sirenListener;
    private SirenAlertType sirenAlertType;
    private String minAppVersion;

    public SirenAlertWrapper(Activity activity, ISirenListener sirenListener, SirenAlertType sirenAlertType, String minAppVersion) {
        this.sirenListener = sirenListener;
        this.sirenAlertType = sirenAlertType;
        this.minAppVersion = minAppVersion;
        activityRef = new WeakReference<Activity>(activity);
    }

    public SirenAlertWrapper(Activity activity, ISirenListener sirenListener, SirenAlertType sirenAlertType, String minAppVersion, int theme) {
        this(activity, sirenListener, sirenAlertType, minAppVersion);
        this.theme = theme;
    }

    public void show() {
        if (activityRef.get() != null) {
            Dialog dialog;
            if (theme > 0) {
                dialog = new Dialog(activityRef.get(), theme);
            } else {
                dialog = new Dialog(activityRef.get());
            }
            setupDialog(dialog);
            dialog.setCancelable(false);
            dialog.show();

            sirenListener.onShowUpdateDialog();


        } else {
            if (sirenListener != null) {
                sirenListener.onError(new NullPointerException("activity reference is null"));
            }
        }
    }

    private void setupDialog(final Dialog dialog) {
        dialog.setTitle(R.string.update_available);
        dialog.setContentView(R.layout.siren_dialog);
        TextView message = (TextView) dialog.findViewById(R.id.tvSirenAlertMessage);
        Button update = (Button) dialog.findViewById(R.id.btnSirenUpdate);
        Button nextTime = (Button) dialog.findViewById(R.id.btnSirenNextTime);
        final Button skip = (Button) dialog.findViewById(R.id.btnSirenSkip);

        message.setText(SirenHelper.getAlertMessage(activityRef.get(), minAppVersion));

        if (sirenAlertType == SirenAlertType.FORCE
                || sirenAlertType == SirenAlertType.OPTION
                || sirenAlertType == SirenAlertType.SKIP) {
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sirenListener != null) {
                        sirenListener.onLaunchGooglePlay();
                    }
                    dialog.dismiss();
                    SirenHelper.openGooglePlay(activityRef.get());
                }
            });
        }

        if (sirenAlertType == SirenAlertType.OPTION
                || sirenAlertType == SirenAlertType.SKIP) {
            nextTime.setVisibility(View.VISIBLE);
            nextTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(sirenListener != null) {
                        sirenListener.onCancel();
                    }
                    dialog.dismiss();
                }
            });
        }
        if (sirenAlertType == SirenAlertType.SKIP) {
            skip.setVisibility(View.VISIBLE);
            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sirenListener != null) {
                        sirenListener.onSkipVersion();
                    }

                    SirenHelper.setVersionSkippedByUser(activityRef.get(), minAppVersion);
                    dialog.dismiss();
                }
            });
        }
    }
}

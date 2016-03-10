package com.eggheadgames.siren;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yuriy on 09.03.2016.
 */
public class Siren {
    /**JSON format should be the following
     * {
     *     "com.example.app": {
     *         "minVersionCode": 7
     *     }
     * }
     */

    private static Siren ourInstance = new Siren();
    private ISirenListener sirenListener;
    private Context context;
    private SirenAlertType alertType = SirenAlertType.OPTION;
    private WeakReference<Activity> activityRef;

    /**
     *
     * @param context - you should use an Application context here in order to not cause memory leaks
     * @return
     */
    public static Siren getInstance(Context context) {
        ourInstance.context = context;
        return ourInstance;
    }

    private Siren() {
    }

    public void setSirenListener(ISirenListener sirenListener) {
        this.sirenListener = sirenListener;
    }

    public void setAlertType(SirenAlertType alertType) {
        this.alertType = alertType;
    }

    public void checkVersion(Activity activity, SirenVersionCheckType versionCheckType, String appDescriptionUrl) {

        activityRef = new WeakReference<Activity>(activity);

        if (TextUtils.isEmpty(appDescriptionUrl)) {
            Log.e(getClass().getSimpleName(), "Please make sure your set correct path to app version description document");
            return;
        }

        if (versionCheckType == SirenVersionCheckType.IMMEDIATELY) {
            performVersionCheck(appDescriptionUrl);
        } else if (versionCheckType.getValue() <= SirenHelper.getDaysSinceLastCheck(context)) {
            performVersionCheck(appDescriptionUrl);
        }
    }

    private void performVersionCheck(String appDescriptionUrl) {
        new LoadJsonTask().execute(appDescriptionUrl);
    }

    protected void handleVerificationResults(String json) {
        try {
            JSONObject rootJson = new JSONObject(json);

            if (!rootJson.isNull(SirenHelper.getPackageName(context))) {
                JSONObject appJson = rootJson.getJSONObject(SirenHelper.getPackageName(context));

                if (!appJson.isNull(Constants.JSON_MIN_VERSION_CODE)) {
                    int minAppVersion = appJson.getInt(Constants.JSON_MIN_VERSION_CODE);

                    //save last successful verification date
                    SirenHelper.setLastVerificationDate(context);

                    if (SirenHelper.getVersionCode(context) < minAppVersion
                            && SirenHelper.isVersionSkippedByUser(context, minAppVersion)) {
                        showAlert(minAppVersion);
                    }
                } else {
                    throw new JSONException("field not found");
                }
            } else {
                throw new JSONException("field not found");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            if (sirenListener != null) {
                sirenListener.onError(e);
            }
        }
    }

    private void showAlert(int minAppVersion) {
        if (alertType == SirenAlertType.NONE) {
            if (sirenListener != null) {
                sirenListener.onDetectNewVersionWithoutAlert(SirenHelper.getAlertMessage(context));
            }
        } else {
            new SirenAlertWrapper(activityRef.get(), sirenListener, alertType, minAppVersion).show();
        }
    }

    public static class LoadJsonTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setUseCaches(false);
                connection.setAllowUserInteraction(false);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.connect();
                int status = connection.getResponseCode();

                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            if (isCancelled()) {
                                br.close();
                                connection.disconnect();
                                return null;
                            }
                            sb.append(line+"\n");
                        }
                        br.close();
                        return sb.toString();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                if (Siren.ourInstance.sirenListener != null) {
                    Siren.ourInstance.sirenListener.onError(ex);
                }

            } finally {
                if (connection != null) {
                    try {
                        connection.disconnect();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        if (Siren.ourInstance.sirenListener != null) {
                            Siren.ourInstance.sirenListener.onError(ex);
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (!TextUtils.isEmpty(result)) {
                Siren.ourInstance.handleVerificationResults(result);
            } else {
                if (Siren.ourInstance.sirenListener != null) {
                    Siren.ourInstance.sirenListener.onError(new NullPointerException());
                }
            }
        }
    }
}

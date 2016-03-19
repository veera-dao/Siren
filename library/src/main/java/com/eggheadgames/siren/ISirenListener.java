package com.eggheadgames.siren;

public interface ISirenListener {

    public void onShowUpdateDialog();                       // User presented with update dialog
    public void onLaunchGooglePlay();                       // User did click on button that launched Google Play
    public void onSkipVersion();                            // User did click on button that skips version update
    public void onCancel();                                 // User did click on button that cancels update dialog
    public void onDetectNewVersionWithoutAlert(String message); // Siren performed version check and did not display alert
    public void onError(Exception e);
}

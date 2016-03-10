package com.eggheadgames.siren;

/**
 * Created by yuriy on 09.03.2016.
 *
 * Determines the frequency in which the the version check is performed
 */
public enum SirenVersionCheckType {

    IMMEDIATELY(0),    // Version check performed every time the app is launched
    DAILY(1),          // Version check performed once a day
    WEEKLY(7);         // Version check performed once a week

    private int value;

    SirenVersionCheckType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}

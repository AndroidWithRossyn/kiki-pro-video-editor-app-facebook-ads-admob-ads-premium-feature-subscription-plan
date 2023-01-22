package com.ridercode.provideo_editor.ads;

import com.ridercode.provideo_editor.BuildConfig;

public class AdsConstants {

    public static int INTERSTITIAL = 0;
    public static int NATIVE = 1;
    public static int BANNER = 2;

    public static String FACEBOOK_ADS_test = "YOUR_PLACEMENT_ID";
    public static String ADMOB_Interstatial_test = "ca-app-pub-3940256099942544/1033173712";
    public static String ADMOB_Banner_test = "ca-app-pub-3940256099942544/6300978111";
    public static String ADMOB_Native_test = "ca-app-pub-3940256099942544/2247696110";

    public static String type = "ad";
    public static String admob_banner_id = "";
    public static String admob_interstitial_id = "";
    public static String admob_native_id = "";
    public static String rider_fb_banner_code_id = "";
    public static String rider_fb_interstitial_code_id = "";
    public static String rider_fb_native_code_id = "";

    static String id;

    public static String getAdId(int adType, boolean isAdmob) {
        if (isAdmob) {
            if (adType == INTERSTITIAL)
                if (BuildConfig.DEBUG) {
                    id = ADMOB_Interstatial_test;
                } else {
                    id = "ADMOB_Interstatial_id";
                }
            else if (adType == NATIVE)
                if (BuildConfig.DEBUG) {
                    id = ADMOB_Native_test;
                } else {
                    id = "ADMOB_Native_id";
                }
            else if (adType == BANNER)
                if (BuildConfig.DEBUG) {
                    id = ADMOB_Banner_test;
                } else {
                    id = "ADMOB_Banner_id";
                }

        } else {

            if (adType == INTERSTITIAL)
                if (BuildConfig.DEBUG) {
                    id = FACEBOOK_ADS_test;
                } else {
                    id = "Facebook_Interstitial_id";
                }
            else if (adType == NATIVE)
                if (BuildConfig.DEBUG) {
                    id = FACEBOOK_ADS_test;
                } else {
                    id = "Facebook_Native_id";
                }
            else if (adType == BANNER)
                if (BuildConfig.DEBUG) {
                    id = FACEBOOK_ADS_test;
                } else {
                    id = "Facebook_Banner_id";
                }
        }

        return id;
    }

}

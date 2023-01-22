package com.ridercode.provideo_editor;

import android.content.Context;
import android.content.SharedPreferences;

public class Saved {
    private static SharedPreferences userDetails;
    private static SharedPreferences.Editor edit;


    public static void s_Int(Context context, String key, int value){
        userDetails = context.getSharedPreferences("videoApp", Context.MODE_PRIVATE);
        edit = userDetails.edit();
        edit.putInt(key,value).apply();
    }
    public static int g_Int(Context context, String key, int defaultValue){
        userDetails = context.getSharedPreferences("videoApp", Context.MODE_PRIVATE);
        return userDetails.getInt(key,defaultValue);
    }

    public static void s_String(Context context, String key, String value){
        userDetails = context.getSharedPreferences("videoApp", Context.MODE_PRIVATE);
        edit = userDetails.edit();
        edit.putString(key,value).apply();
    }
    public static String g_String(Context context, String key, String defaultValue){
        userDetails = context.getSharedPreferences("videoApp", Context.MODE_PRIVATE);
        return userDetails.getString(key,defaultValue);
    }

    public static void s_Boolean(Context context, String key, boolean value){
        userDetails = context.getSharedPreferences("videoApp", Context.MODE_PRIVATE);
        edit = userDetails.edit();
        edit.putBoolean(key,value).apply();
    }
    public static boolean g_Boolean(Context context, String key, boolean defaultValue){
        userDetails = context.getSharedPreferences("videoApp", Context.MODE_PRIVATE);
        return userDetails.getBoolean(key,defaultValue);
    }

}

package com.secretbrowser;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class PreferenceHelper extends Activity {

    SharedPreferences sharedpreferences;
    public static final String Mypreferences = "SettingFile";

    public void setStringArrayPref(Context mContext, String key, ArrayList<String> values) {
        sharedpreferences = mContext.getSharedPreferences(Mypreferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.commit();
    }

    public ArrayList<String> getStringArrayPref(Context mContext, String key) {
        sharedpreferences = mContext.getSharedPreferences(Mypreferences, Context.MODE_PRIVATE);
        String json = sharedpreferences.getString(key, null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    public void setIntPref(Context mContext, String key, int values) {
        sharedpreferences = mContext.getSharedPreferences(Mypreferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(key, values);
        editor.commit();
    }

    public int getIntPref(Context mContext, String key) {
        sharedpreferences = mContext.getSharedPreferences(Mypreferences, Context.MODE_PRIVATE);
        int value = sharedpreferences.getInt(key, 0);
        return value;
    }
}

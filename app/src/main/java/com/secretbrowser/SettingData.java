package com.secretbrowser;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.Arrays;

public class SettingData extends AppCompatActivity {

    public static boolean plz_restart = false;

    public static String[] setting_list = {
            "true", "1", "0", "true", "true"
    };
    public static ArrayList<String> setting_arraylist = new ArrayList<>();

    public void save_setting(Context mContext) {
        //배열을 array 리스트로 변경후 데이터 저장
        setting_arraylist = new ArrayList<>(Arrays.asList(setting_list));
        PreferenceHelper preferenceHelper = new PreferenceHelper();
        preferenceHelper.setStringArrayPref(mContext, "Setting_Data", setting_arraylist);
    }

    public void load_setting(Context mContext) {
        //데이터를 불러와서 array 리스트를 배열로 변환
        PreferenceHelper preferenceHelper = new PreferenceHelper();
        setting_arraylist = preferenceHelper.getStringArrayPref(mContext, "Setting_Data");
        if (setting_arraylist.size() != setting_list.length) {
            save_setting(mContext);
        } else {
            setting_list = setting_arraylist.toArray(new String[setting_arraylist.size()]);
        }
    }

    public String getSetting(int index) {
        return setting_list[index];
    }

    public void setSetting(int index, String value) {
        setting_list[index] = value;
    }

    public boolean getplz_restart() {
        return plz_restart;
    }
    public void setplz_restart(boolean value) {
        plz_restart = value;
    }
}

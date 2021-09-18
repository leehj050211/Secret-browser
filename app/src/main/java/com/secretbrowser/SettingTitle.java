package com.secretbrowser;

public class SettingTitle {

    int index;
    String title, subtitle, type, value;

    public SettingTitle(String title, String subtitle, String type, String value, int index) {
        this.index = index;
        this.title = title;
        this.subtitle = subtitle;
        this.type = type;
        this.value = value;
    }

    public String getSettingTitle() {
        return title;
    }

    public void setSettingTitle(String title) {
        this.title = title;
    }

    public String getSettingSubTitle() {
        return subtitle;
    }

    public void setSettingSubTitle(String title) {
        this.subtitle = subtitle;
    }

    public String getSettingType() {
        return type;
    }

    public void setSettingType(String type) {
        this.type = type;
    }

    public String getSettingValue() {
        return value;
    }

    public void setSettingValue(String value) {
        this.value = value;
    }

    public int getSettingindex() {
        return index;
    }

    public void setSettingindex(int index) {
        this.index = index;
    }
}

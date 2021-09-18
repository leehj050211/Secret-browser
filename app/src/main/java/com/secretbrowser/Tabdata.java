package com.secretbrowser;

public class Tabdata {

    int index;
    String title;

    public Tabdata(String title, int index) {
        this.index = index;
        this.title = title;
    }

    public String getTabTitle() {
        return title;
    }

    public void setTabTitle(String title) {
        this.title = title;
    }

    public int getTabindex() {
        return index;
    }

    public void setTabindex(int index) {
        this.index = index;
    }
}

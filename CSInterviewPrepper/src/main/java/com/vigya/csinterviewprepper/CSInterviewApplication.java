package com.vigya.csinterviewprepper;

import android.app.Application;

/**
 * Created by vigyas on 3/30/14.
 */
public class CSInterviewApplication extends Application {
    private HomeModel mHomeModel;

    public void setHomeModel(HomeModel homeModel) {
        mHomeModel = homeModel;
    }

    public HomeModel homeModel() {
        return mHomeModel;
    }
}

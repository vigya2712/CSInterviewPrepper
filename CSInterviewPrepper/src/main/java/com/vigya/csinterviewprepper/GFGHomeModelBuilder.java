package com.vigya.csinterviewprepper;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by vigyas on 3/16/14.
 */
public class GFGHomeModelBuilder extends HomeModelBuilder {
    public static HomeModel build(String uri) throws MalformedURLException, IOException {
        // Build the GFG home mode;
        HomeModel homeModel = new HomeModel(uri);

        return homeModel;
    }
}

package com.vigya.csinterviewprepper;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by vigyas on 3/16/14.
 */
public class GFGCategoryModelBuilder extends CategoryModelBuilder {
    private static String TAG = "CSInterviewPrepper/GFGCategoryModelBuilder";
    // Groups:
    // 1: the URL
    // 2: the list item text
    private static String CATEGORY_INFO_REGEX = "<a href=.*\"([^\"]+)\" [^>]+>([^<]*)</a>";

    public static List<CategoryModel> build(HomeModel homeModel) throws MalformedURLException, IOException {
        List<CategoryModel> categoryModels = null;
        URL url = new URL(homeModel.uri());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
        categoryModels = categoryModelsFromStream(inputStream);

        return categoryModels;
    }

    private static List<CategoryModel> categoryModelsFromStream(BufferedInputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        ArrayList<CategoryModel> categoryModels = new ArrayList<CategoryModel>();

        // Read lines till the beginning of a cat-item list item is found
        String line;
        Pattern categoryPattern = Pattern.compile(CATEGORY_INFO_REGEX);

        while ((line = reader.readLine()) != null) {
            int catListItemStartOffset = line.indexOf("<li class=\"cat-item");

            if (catListItemStartOffset != -1) {
                // Get the category text and link out
                Matcher m = categoryPattern.matcher(line);

                if (m.find()) {
                    // groups:
                    // 1: URL
                    // 2: label
                    try {
                        URL url = new URL(m.group(1));
                        CategoryModel categoryModel = new CategoryModel(m.group(1), m.group(2));
                        categoryModels.add(categoryModel);
                    } catch (MalformedURLException malformedURLException) {
                        Log.e(TAG, malformedURLException.getMessage());
                    }
                }
            }
        }

        return categoryModels;
    }
}

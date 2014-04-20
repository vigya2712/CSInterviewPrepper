package com.vigya.csinterviewprepper;

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

/**
 * Created by vigyas on 3/23/14.
 */
public class GFGPageModelBuilder {
    private static String TAG = "CSInterviewPrepper/GFGPageModelBuilder";
    private static String PAGE_INFO_REGEX = "<span class='pages'>Page [0-9]+ of ([0-9]+)</span>";

    public static List<PageModel> build(CategoryModel categoryModel) throws MalformedURLException, IOException {
        List<PageModel> pageModels = null;
        URL url = new URL(categoryModel.uri());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
        pageModels = pageModelsFromStream(inputStream, categoryModel);

        return pageModels;
    }

    private static List<PageModel> pageModelsFromStream(BufferedInputStream inputStream,
                                                        CategoryModel categoryModel) throws IOException {
        // The first category page (page 1) is aliased to the category link.
        // We read that to figure out how many pages there are in this category
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<PageModel> pageModels = new ArrayList<PageModel>();

        // Read lines till the beginning of a cat-item list item is found
        String line;
        Pattern pageNumberPattern = Pattern.compile(PAGE_INFO_REGEX);

        while ((line = reader.readLine()) != null) {
            if (line.indexOf("<span class='pages'>Page") != -1) {
                Matcher m = pageNumberPattern.matcher(line);

                if (m.find()) {
                    try {
                        int numPages = Integer.parseInt(m.group(1));

                        // Got a valid page count.
                        for (int i = 0; i < numPages; i++) {
                            String pageURLString  = categoryModel.uri();

                            if (pageURLString.endsWith("/") == false) {
                                pageURLString += "/";
                            }

                            pageURLString += "page/" + (i + 1) + "/";

                            pageModels.add(new PageModel(pageURLString, i + 1));
                        }

                        break;
                    } catch (NumberFormatException numberFormatException) {
                        // Ignore.
                    }
                }
            }
        }

        return pageModels;
    }
}

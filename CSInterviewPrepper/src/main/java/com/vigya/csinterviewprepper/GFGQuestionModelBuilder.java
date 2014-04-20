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

/**
 * Created by vigyas on 3/30/14.
 */
public class GFGQuestionModelBuilder extends QuestionModelBuilder {
    private static String TAG = "CSInterviewPrepper/GFGQuestionModelBuilder";
    private static String QUESTION_INFO_REGEX = "<h2 class=\"post-title\"><a href=\"([^\"]+)\"[^>]+>([^<]+)</a>";
    private static String QUESTION_END_MARKER = "<!-- end post main-->";
    private static String POST_CONTENT_DIV_TAG = "<div class=\"post-content\">";

    public static List<QuestionModel> build(PageModel pageModel) throws MalformedURLException, IOException {
        List<QuestionModel> questionModels = null;
        URL url = new URL(pageModel.uri());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
        questionModels = questionModelsFromStream(inputStream, pageModel);

        return questionModels;
    }

    private static List<QuestionModel> questionModelsFromStream(BufferedInputStream inputStream,
                                                            PageModel pageModel) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<QuestionModel> questionModels = new ArrayList<QuestionModel>();

        String line;
        Pattern questionLinkPattern = Pattern.compile(QUESTION_INFO_REGEX);

        while ((line = reader.readLine()) != null) {
            int questionStartOffset = line.indexOf("class=\"post-title\"");

            if (questionStartOffset != -1) {
                // Valid question post title
                Matcher m = questionLinkPattern.matcher(line);

                if (m.find()) {
                    try {
                        URL url = new URL(m.group(1));
                        QuestionModel questionModel = new QuestionModel(m.group(1), m.group(2), "");

                        // Look for post-content
                        while ((line = reader.readLine()) != null && line.indexOf(QUESTION_END_MARKER) == -1) {
                            if (line.indexOf(POST_CONTENT_DIV_TAG) != -1) {
                                // The next line is the post content
                                if ((line = reader.readLine()) != null) {
                                    questionModel.setmDetailsHTML(line);
                                }
                            }
                        }

                        questionModels.add(questionModel);
                    } catch (MalformedURLException malformedURLException) {
                        Log.e(TAG, malformedURLException.getMessage());
                    }
                }
            }
        }

        return questionModels;
    }
}

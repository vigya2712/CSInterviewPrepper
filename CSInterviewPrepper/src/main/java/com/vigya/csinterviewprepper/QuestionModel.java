package com.vigya.csinterviewprepper;

/**
 * Created by vigyas on 3/16/14.
 */
public class QuestionModel {
    public static String QUESTION_TITLE = "QUESTION_TITLE";
    public static String QUESTION_URI = "QUESTION_URI";
    public static String QUESTION_DETAILS = "QUESTION_DETAILS";

    // Link to the page for this question
    private String mUri;
    // Plain-text question title
    private String mTitle;
    // HTML data corresponding to the details of this question
    private String mDetailsHTML;

    public QuestionModel(String uri, String title, String detailsHTML) {
        mUri = uri;
        mTitle = title;
        mDetailsHTML = detailsHTML;
    }

    public String uri() {
        return mUri;
    }

    public String title() {
        return mTitle;
    }

    public String detailsHMTL() {
        return mDetailsHTML;
    }

    public void setQuestionUri(String uri) {
        mUri = uri;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setmDetailsHTML(String detailsHTML) {
        mDetailsHTML = detailsHTML;
    }
}

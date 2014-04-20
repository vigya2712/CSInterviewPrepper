package com.vigya.csinterviewprepper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vigyas on 3/16/14.
 */
public class PageModel {
    private String mUri;
    private int mNumber; // the page number (1-based)
    private List<QuestionModel> mQuestionModels;
    private boolean mModelPopulatedStatus = false;

    public PageModel(String uri, int number) {
        mUri = uri;
        mNumber = number;
        mQuestionModels = new ArrayList<QuestionModel>();
    }

    public String uri() {
        return mUri;
    }

    public int number() {
        return mNumber;
    }

    public void setModelPopulatedStatus(boolean status) {
        mModelPopulatedStatus = status;
    }

    public boolean modelPopulatedStatus() {
        return mModelPopulatedStatus;
    }

    /*
     * DON'T make changes to this list
     */
    public List<QuestionModel> questionModels() {
        return mQuestionModels;
    }

    /*public void setUri(String uri) {
        mUri = uri;
    }

    public void setNumber(int number) {
        mNumber = number;
    }*/

    public void addQuestionModel(QuestionModel questionModel) {
        mQuestionModels.add(questionModel);
    }

    public void clearModel() {
        mQuestionModels.clear();
    }
}

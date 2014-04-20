package com.vigya.csinterviewprepper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vigyas on 3/23/14.
 * Picks questions, while trying its best to not repeat them.
 */
public class QuestionBank {
    /*
     * How do we organize the questions
     *  - maintain a list of pages for every category
     *  - once a page has been read and parsed, maintain
     *    questions for that page
     */
    private static HomeModel mHomeModel;
    private static List<List<Integer>> mPageTable;

    public static void buildBank(HomeModel homeModel) {
        mHomeModel = homeModel;
        mPageTable = new ArrayList<List<Integer>>();

        // Populate the pagetable such that there's one list
        // per category.
        for (int i = 0; i < mHomeModel.categoryModels().size(); i++) {
            mPageTable.add(new ArrayList<Integer>());
        }
    }

    /*
     * Picks a random page for the category, parses it to make QuestionModels
     * and returns one question model.
     */
    public static QuestionModel pickQuestion(CategoryModel categoryModel) {
        // Pick a random page
        return null;
    }
}

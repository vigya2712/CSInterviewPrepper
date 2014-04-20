package com.vigya.csinterviewprepper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vigyas on 3/16/14.
 */
public class HomeModel {
    private String mUri;
    private List<CategoryModel> mCategoryModels;

    public HomeModel(String uri) {
        mUri = uri;
        mCategoryModels = new ArrayList<CategoryModel>();
    }

    public String uri() {
        return mUri;
    }

    /*public void setUri(String uri) {
        mUri = uri;
    }*/

    public void addCategoryModel(CategoryModel categoryModel) {
        mCategoryModels.add(categoryModel);
    }

    public List<CategoryModel> categoryModels() {
        return mCategoryModels;
    }

    public void clearModel() {
        for (CategoryModel categoryModel : mCategoryModels) {
            categoryModel.clearModel();
        }

        mCategoryModels.clear();
    }
}

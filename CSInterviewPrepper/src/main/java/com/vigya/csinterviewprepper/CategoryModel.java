package com.vigya.csinterviewprepper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vigyas on 3/16/14.
 */
public class CategoryModel {
    private String mUri;
    private String mCategoryName;
    private List<PageModel> mPageModels;

    public CategoryModel(String uri, String categoryName) {
        mUri = uri;
        mCategoryName = categoryName;
        mPageModels = new ArrayList<PageModel>();
    }

    public String uri() {
        return mUri;
    }

    public String categoryName() {
        return mCategoryName;
    }

    public List<PageModel> pageModels() {
        return mPageModels;
    }

    public void addPageModel(PageModel pageModel) {
        mPageModels.add(pageModel);
    }

    public void clearModel() {
        for (PageModel pageModel : mPageModels) {
            pageModel.clearModel();
        }

        mPageModels.clear();
    }
}

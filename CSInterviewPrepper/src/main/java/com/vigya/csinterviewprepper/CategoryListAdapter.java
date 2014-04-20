package com.vigya.csinterviewprepper;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Created by vigyas on 3/30/14.
 */
public class CategoryListAdapter implements ListAdapter {
    private static String TAG = "CSInterviewPrepper/CategoryListAdapter";

    private Context mContext;
    private HomeModel mHomeModel;

    public CategoryListAdapter(Context context, HomeModel homeModel) {
        mContext = context;
        mHomeModel = homeModel;
    }

    @Override
    public int getCount() {
        // Return count of categories
        return mHomeModel.categoryModels().size();
    }

    @Override
    public Object getItem(int i) {
        return mHomeModel.categoryModels().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (i % 2 == 0) {
                view = layoutInflater.inflate(R.layout.category_list_item_even,
                        viewGroup, false);
            } else {
                view = layoutInflater.inflate(R.layout.category_list_item_odd,
                        viewGroup, false);
            }
        }

        TextView txtCategoryName;

        if (i % 2 == 0) {
            txtCategoryName = (TextView)view.findViewById(R.id.txtCategoryNameEven);
        } else {
            txtCategoryName = (TextView)view.findViewById(R.id.txtCategoryNameOdd);
        }

        txtCategoryName.setText(mHomeModel.categoryModels().get(i).categoryName());

        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return i % 2;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        // Ignore
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        // Ignore
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return mHomeModel.categoryModels().isEmpty();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int i) {
        // Everything's enabled
        return true;
    }
}

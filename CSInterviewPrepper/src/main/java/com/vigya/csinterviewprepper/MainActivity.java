package com.vigya.csinterviewprepper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cengalabs.flatui.FlatUI;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends ActionBarActivity {
    private static String TAG = "CSInterviewPrepper/MainActivity";
    private static String LAST_CHOSEN_QUESTION_CATEGORY_KEY = "LAST_CHOSEN_QUESTION";
    private static String QUESTION_PICKER_CANCELLED_KEY = "QUESTION_PICKER_CANCELLED";
    private static String FETCHING_QUESTION_KEY = "FETCHING_QUESTION_KEY";

    private HomeModel mHomeModel;
    private boolean mHomeModelCreationInFlight = false;
    private boolean mPickingQuestion = false;
    private static List<List<Integer>> mPageTable;
    private Object mPageTableSynch = new Object();
    private GFGQuestionPickerTask mQuestionPickerTask;
    private GFGModelBuilderTask mModelBuilderTask;
    private CSInterviewApplication mApplication;
    private AlertDialog mLoadingDialog, mQuestionLoadingDialog;
    private String mLastChosenQuestionCategoryName;
    private boolean mQuestionPickerWasCancelled = false;
    private boolean mFetchQuestion = false;
    private Random mRamdonGenerator = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FlatUI.setDefaultTheme(FlatUI.GRASS);
        FlatUI.setActionBarTheme(this, FlatUI.GRASS, false, false);

        mApplication = (CSInterviewApplication)getApplication();
        mHomeModel = mApplication.homeModel();

        if (savedInstanceState != null) {
            mFetchQuestion = savedInstanceState.getBoolean(FETCHING_QUESTION_KEY, false);
            mLastChosenQuestionCategoryName = savedInstanceState.getString(LAST_CHOSEN_QUESTION_CATEGORY_KEY, "");
        }

        // Wire event-handlers

        // Pick question
        Button btnPickQuestion = (Button)findViewById(R.id.btnPickQuestion);

        btnPickQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mPickingQuestion) {
                    Log.i(TAG, "Picking question");

                    mQuestionPickerTask = new GFGQuestionPickerTask();
                    mQuestionPickerTask.execute("");
                }
            }
        });

        // If a home model is ready, populate the category list with it
        ListView lstCategories = (ListView)findViewById(R.id.lstCategories);

        if (mHomeModel != null) {
            ListAdapter categoryListAdapter = new CategoryListAdapter(MainActivity.this,
                    mHomeModel);

            lstCategories.setAdapter(categoryListAdapter);
        }

        lstCategories.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        HomeModel homeModel = mApplication.homeModel();

                        if (homeModel != null && mPickingQuestion == false) {
                            Log.i(MainActivity.TAG, "Picking question from category \"" +
                                    homeModel.categoryModels().get(i).categoryName() + "\"");

                            // Start the question picker task
                            mQuestionPickerTask = new GFGQuestionPickerTask();
                            mLastChosenQuestionCategoryName = homeModel.categoryModels().get(i).categoryName();

                            mQuestionPickerTask.execute(mLastChosenQuestionCategoryName);

                            // Show a 'working' dialog
                            mQuestionLoadingDialog.show();
                        }
                    }
                }
        );

        // Build the loading message dialog
        AlertDialog.Builder loadingDialogBuilder = new AlertDialog.Builder(this);
        loadingDialogBuilder.setTitle(getResources().getString(R.string.app_name));
        loadingDialogBuilder.setMessage(getResources().getString(R.string.loading_indication));

        mLoadingDialog = loadingDialogBuilder.create();
        mQuestionLoadingDialog = loadingDialogBuilder.create();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mLoadingDialog.cancel();
        mQuestionLoadingDialog.cancel();

        if (mHomeModelCreationInFlight) {
            mModelBuilderTask.cancel(true);
        }

        if (mPickingQuestion) {
            mQuestionPickerTask.cancel(true);
            mQuestionPickerWasCancelled = true;
        }
    }

    protected void onStart() {
        super.onStart();

        mHomeModel = mApplication.homeModel();

        // If home model creation isn't already in progress and we
        // don't have a home model yet
        if (!mHomeModelCreationInFlight && mHomeModel == null) {
            Log.i(TAG, "Building models");

            mModelBuilderTask = new GFGModelBuilderTask();
            mModelBuilderTask.execute("http://www.geeksforgeeks.org/");

            mLoadingDialog.show();
        }

        if (mFetchQuestion && mHomeModel != null) {
            // If the question picker was cancelled, start it again
            mQuestionPickerTask = new GFGQuestionPickerTask();

            if (mLastChosenQuestionCategoryName == null) {
                Log.e(TAG + "/onStart", "mLastChosenQuestionCategoryName = null");
            }

            mQuestionPickerTask.execute(mLastChosenQuestionCategoryName);
            mQuestionPickerWasCancelled = false;
            mFetchQuestion = false;

            // Show a 'working' dialog
            mQuestionLoadingDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mHomeModelCreationInFlight) {
            mModelBuilderTask.cancel(true);
        }

        if (mPickingQuestion) {
            mQuestionPickerTask.cancel(true);
            mQuestionPickerWasCancelled = true;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mQuestionPickerWasCancelled =
                savedInstanceState.getBoolean(QUESTION_PICKER_CANCELLED_KEY);
        mLastChosenQuestionCategoryName =
                savedInstanceState.getString(LAST_CHOSEN_QUESTION_CATEGORY_KEY);

        if (mLastChosenQuestionCategoryName == null) {
            Log.e(TAG + "/onRestoreInstanceState", "mLastChosenQuestionCategoryName = null");
        }

        mFetchQuestion = savedInstanceState.getBoolean(FETCHING_QUESTION_KEY);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mLastChosenQuestionCategoryName == null) {
            Log.e(TAG, "mLastChosenQuestionCategoryName = null");
        }

        outState.putString(LAST_CHOSEN_QUESTION_CATEGORY_KEY, mLastChosenQuestionCategoryName);
        outState.putBoolean(QUESTION_PICKER_CANCELLED_KEY, mPickingQuestion);
        outState.putBoolean(FETCHING_QUESTION_KEY, mQuestionLoadingDialog.isShowing());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * Model-builder task
     */
    class GFGModelBuilderTask extends AsyncTask<String, Void, Boolean> {
        //        private static String TAG = "CSInterviewPrepper/GFGModelBuilderTask";
        private boolean mStatusSuccess;

        @Override
        protected Boolean doInBackground(String... links) {
            String mainPageLink = links[0];
            mStatusSuccess = true;
            mHomeModelCreationInFlight = true;

            try {
                mHomeModel = GFGHomeModelBuilder.build(mainPageLink);

                if (!isCancelled()) {
                    List<CategoryModel> categoryModels = GFGCategoryModelBuilder.build(mHomeModel);

                    for (CategoryModel categoryModel : categoryModels) {
                        mHomeModel.addCategoryModel(categoryModel);

                        Log.i(TAG, String.format("category name: %s; category link: %s",
                                categoryModel.categoryName(),
                                categoryModel.uri()));

                        // For each category model, build the page models
                        if (!isCancelled()) {
                            List<PageModel> pageModels = GFGPageModelBuilder.build(categoryModel);

                            for (PageModel pageModel : pageModels) {
                                categoryModel.addPageModel(pageModel);

                                Log.i(TAG, String.format("\t page %d; page link: %s", pageModel.number(),
                                        pageModel.uri()));
                            }
                        }
                    }
                }
            } catch (MalformedURLException malformedURLException) {
                Log.e(TAG + "/doInBackground", malformedURLException.getMessage());
                mStatusSuccess = false;
            } catch (IOException ioException) {
                Log.e(TAG + "/doInBackground", ioException.getMessage());
                mStatusSuccess = false;
            } finally {
                mHomeModelCreationInFlight = false;
            }

            return mStatusSuccess;
        }

        public boolean successStatus() {
            return mStatusSuccess;
        }

        protected void onPostExecute(Boolean result) {
            if (!mStatusSuccess) {
                if (!isCancelled()) {
                    Toast.makeText(MainActivity.this, "Failed to fetch data",
                            Toast.LENGTH_SHORT).show();
                }

                Log.e(TAG + "/onPostExecute", "Finished with errors");

                mHomeModel = null;
            } else {
                Log.i(TAG, "Model built");

                // Update the model in the application
                mApplication.setHomeModel(mHomeModel);

                // At this point, we can update the list with the new model
                // At this point, we can start a new activity for this question
                if (!isCancelled()) {
                    ListView lstCategories = (ListView)findViewById(R.id.lstCategories);
                    ListAdapter categoryListAdapter = new CategoryListAdapter(MainActivity.this,
                            mHomeModel);

                    lstCategories.setAdapter(categoryListAdapter);
                }
            }

            mLoadingDialog.cancel();
        }
    }

    class GFGQuestionPickerTask extends AsyncTask<String, Void, QuestionModel> {
        private final int MAX_QUESTION_FETCH_ATTEMPTS = 10;

        @Override
        protected QuestionModel doInBackground(String... categories) {
            mPickingQuestion = true;
            String categoryName = "";
            QuestionModel selectedQuestionModel = null;
            mRamdonGenerator.setSeed(System.currentTimeMillis());

            if (categories != null) {
                categoryName = categories[0];
            }

            try {
                if (mPageTable == null) {
                    // This needs to be done just once
                    mPageTable = new ArrayList<List<Integer>>();

                    // Populate the pagetable such that there's one list
                    // per category.
                    for (int i = 0; i < mHomeModel.categoryModels().size(); i++) {
                        List<Integer> pages = new ArrayList<Integer>();
                        CategoryModel categoryModel = mHomeModel.categoryModels().get(i);

                        for (int j = 0; j < categoryModel.pageModels().size(); j++) {
                            pages.add(new Integer(j));
                        }

                        mPageTable.add(pages);
                    }
                }

                boolean questionSelected = false;
                int questionFetchAttempts = 0;

                while (selectedQuestionModel == null && questionFetchAttempts++ < MAX_QUESTION_FETCH_ATTEMPTS) {
                    CategoryModel categoryModel = null;
                    int categoryModelOffset = -1;

                    if (categoryName.length() == 0) {
                        // Pick a random category model
                        categoryModelOffset = (int) (Math.random() * (mHomeModel.categoryModels().size() - 1));

                    } else {
                        for (int i = 0; i < mHomeModel.categoryModels().size(); i++) {
                            if (mHomeModel.categoryModels().get(i).categoryName().equals(categoryName)) {
                                categoryModelOffset = i;

                                break;
                            }
                        }
                    }

                    categoryModel = mHomeModel.categoryModels().get(categoryModelOffset);

                    // From this categoryModel, pick a page model
                    int pageModelOffset = (int) (Math.random() * (categoryModel.pageModels().size() / 2));

                    // Having picked this page model, move it to the last position
                    // in the page table array for this category
                    synchronized (mPageTableSynch) {
                        mPageTable.get(categoryModelOffset).remove(pageModelOffset);
                        mPageTable.get(categoryModelOffset).add(pageModelOffset);
                    }

                    PageModel pageModel = categoryModel.pageModels().get(pageModelOffset);

                    if (pageModel.modelPopulatedStatus() == false) {
                        // Page model not populated with questions yet. Let's do it.
                        try {
                            List<QuestionModel> questionModels =
                                    GFGQuestionModelBuilder.build(pageModel);
                            if (questionModels != null) {
                                for (QuestionModel questionModel : questionModels) {
                                    pageModel.addQuestionModel(questionModel);
                                }

                                pageModel.setModelPopulatedStatus(true);
                            }
                        } catch (Exception exception) {
                            Log.e(TAG + "/doInBackground", exception.getMessage());
                        }
                    }

                    // Now that the page model has been populated, go ahead and select a question.
                    if (pageModel.questionModels().size() > 0) {
//                        int questionModelOffset = (int) (Math.random() * (pageModel.questionModels().size() - 1));
                        int questionModelOffset = mRamdonGenerator.nextInt(pageModel.questionModels().size());
                        selectedQuestionModel = pageModel.questionModels().get(questionModelOffset);
                    }
                }
            } finally {
                mPickingQuestion = false;
            }

            return selectedQuestionModel;
        }

        protected void onPostExecute(QuestionModel questionModel) {
            if (questionModel == null) {
                if (!isCancelled()) {
                    Toast.makeText(MainActivity.this, "Failed to fetch data",
                            Toast.LENGTH_SHORT).show();
                }
                Log.e(TAG + "/onPostExecute", "Unable to fetch questions");
            } else {
                Log.i(TAG, "Question fetched: " + questionModel.title());

                // Launch the QuestionDetailsActivity
                Intent questionDetailsActivityIntent = new Intent(MainActivity.this, QuestionDetailsActivity.class);
                questionDetailsActivityIntent.putExtra(QuestionModel.QUESTION_TITLE, questionModel.title());
                questionDetailsActivityIntent.putExtra(QuestionModel.QUESTION_URI, questionModel.uri());
                questionDetailsActivityIntent.putExtra(QuestionModel.QUESTION_DETAILS, questionModel.detailsHMTL());
                startActivity(questionDetailsActivityIntent);
            }

            // Done picking question
            mQuestionLoadingDialog.cancel();
        }

        @Override
        protected void onCancelled(QuestionModel questionModel) {

        }
    }
}

package com.vigya.csinterviewprepper;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cengalabs.flatui.FlatUI;


public class QuestionDetailsActivity extends ActionBarActivity {
    private static String TAG = "CSInterviewPrepper/QuestionDetailsActivity";

    private String mQuestionUri;
    private String mQuestionTitle;
    private String mQuestionDetailsHTML;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_details);

        FlatUI.setDefaultTheme(FlatUI.GRASS);
        FlatUI.setActionBarTheme(this, FlatUI.GRASS, false, false);

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra(QuestionModel.QUESTION_TITLE) &&
                    intent.hasExtra(QuestionModel.QUESTION_URI)) {
                mQuestionTitle = intent.getStringExtra(QuestionModel.QUESTION_TITLE);
                mQuestionUri = intent.getStringExtra(QuestionModel.QUESTION_URI);
                mQuestionDetailsHTML = "";

                TextView txtQuestionTitle = (TextView)findViewById(R.id.txtQuestionTitle);
                txtQuestionTitle.setText(mQuestionTitle);

                if (intent.hasExtra(QuestionModel.QUESTION_DETAILS)) {
                    mQuestionDetailsHTML = intent.getStringExtra(QuestionModel.QUESTION_DETAILS);

                    TextView txtQuestionDetails = (TextView)findViewById(R.id.txtQuestionDetails);
                    txtQuestionDetails.setText(Html.fromHtml(mQuestionDetailsHTML));
                }

                if (mQuestionUri.length() > 0) {
                    Button btnGoToQuestion = (Button)findViewById(R.id.btnGoToQuestion);

                    btnGoToQuestion.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent goToQuestionIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(mQuestionUri));
                            startActivity(goToQuestionIntent);
                        }
                    });
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.question_details, menu);
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

}

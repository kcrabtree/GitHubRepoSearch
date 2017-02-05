/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.datafrominternet;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.datafrominternet.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText mSearchBoxEditText;
    private TextView mUrlDisplayTextView;
    private TextView mSearchResultsTextView;
    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);
        mUrlDisplayTextView = (TextView) findViewById(R.id.tv_url_display);
        mSearchResultsTextView = (TextView) findViewById(R.id.tv_github_search_results_json);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
    }

    private void makeGithubSearchQuery() {
        // Retrieve the search query
        String searchQuery = mSearchBoxEditText.getText().toString();

        // Build the URL
        URL url = NetworkUtils.buildUrl(searchQuery);

        // Update the UI with our search query
        mUrlDisplayTextView.setText("URL Hit: "+url.toString());

        // Make the http request to fetch the search results
        new GithubQueryTask().execute(url);
    }

    // Helper methods used to show/hide the search results and error message text views
    private void showSearchResults() {
        mSearchResultsTextView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mSearchResultsTextView.setVisibility(View.INVISIBLE);
    }

    public class GithubQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            // Before performing a search, clear any previous results and show the loader
            mSearchResultsTextView.setText("");
            mLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String githubSearchResults = null;

            try {
                githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return githubSearchResults;
        }

        @Override
        protected void onPostExecute(String searchResults) {
            // Hide the loading progress bar
            mLoadingProgressBar.setVisibility(View.INVISIBLE);

            // If we have search results, set the text view and display.
            // Otherwise, hide the search results text view and show the error message.
            if (searchResults != null && !searchResults.equals("")) {
                mSearchResultsTextView.setText(searchResults);
                showSearchResults();
            } else {
                showErrorMessage();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedMenuItem = item.getItemId();
        if (selectedMenuItem == R.id.action_search) {
            this.makeGithubSearchQuery();
        }
        return super.onOptionsItemSelected(item);
    }
}

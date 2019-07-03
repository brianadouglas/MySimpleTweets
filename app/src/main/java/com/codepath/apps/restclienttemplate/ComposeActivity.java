package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    EditText tweetContent;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // get access to the Twitter client
        client = TwitterApp.getRestClient(this);

    }


    // ActivityNamePrompt.java -- launched for a result
    // this is the function called when the Tweet button is clicked
    public void onSubmit(View v) {
        // get the contents of the tweet created by the user
        tweetContent = (EditText) findViewById(R.id.tweetEdit);

        // create a Tweet object here
        client.sendTweet(tweetContent.getText().toString(), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // transform the JSON response into a tweet object
                try {
                    // create the new tweet
                    Tweet tweet = Tweet.fromJSON(response);
                    // Prepare data intent
                    Intent data = new Intent();
                    // Pass relevant data back as a result after being Parcel wrapped
                    data.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    // the code here - 200 - signifies that the activity was successfully called and can therefore be returned with the correct code
                    data.putExtra("code", 200); // ints work too
                    // Activity finished ok, return the data
                    setResult(RESULT_OK, data); // set result code and bundle data for response
                    finish(); // closes the activity, pass data to parent
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Compose Activity", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }

}

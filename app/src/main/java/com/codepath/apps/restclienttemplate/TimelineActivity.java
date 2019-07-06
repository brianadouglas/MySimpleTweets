package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    // Instance of the progress action view
    MenuItem miActionProgressItem;

    // declare a reference to the Twitter client
    private TwitterClient client;
    private Tweet tweet;
    private SwipeRefreshLayout swipeContainer;
    public long max_id;


    // constant for the intent passing and return from Compose Activity
    private final int REQUEST_CODE = 200;

    // declare the tweet adapter
    TweetAdapter tweetAdapter;
    // the array list - data source
    ArrayList<Tweet> tweets;
    RecyclerView rvtweets;
    EndlessRecyclerViewScrollListener scroll;
    LinearLayoutManager linearLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("");
//        getSupportActionBar().setLogo(R.drawable.ic_white_blue);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // get access to the Twitter client
        client = TwitterApp.getRestClient(this);

        //
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        // find the RecyclerView
        rvtweets = (RecyclerView) findViewById(R.id.rvTweet);

        // init the arrayList (data source)

        tweets = new ArrayList<>();
        // construct the adapter from this data source
        tweetAdapter = new TweetAdapter(tweets);
        // RecyclerView setup (layout manager, use adapter)
        rvtweets.setLayoutManager(linearLayoutManager);

        // set the adapter
        rvtweets.setAdapter(tweetAdapter);


        scroll = new EndlessRecyclerViewScrollListener(linearLayoutManager) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateTimeline(false);
            }
        };




        rvtweets.addOnScrollListener(scroll);


        // initializing the refresh function

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                // Remember to CLEAR OUT old items before appending in the new ones
                tweetAdapter.clear();
                // ...the data has come back, add new items to your adapter...
                populateTimeline(true);
                tweetAdapter.addAll(tweets);
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        populateTimeline(true);

    }

//    public void fetchTimelineAsync(int page) {
//        // Send the network request to fetch the updated data
//        // `client` here is an instance of Android Async HTTP
//        // getHomeTimeline is an example endpoint.
//        client.getHomeTimeline(new JsonHttpResponseHandler() {
//            public void onSuccess(JSONArray json) {
//                // Remember to CLEAR OUT old items before appending in the new ones
//                tweetAdapter.clear();
//                // ...the data has come back, add new items to your adapter...
//                tweetAdapter.addAll(tweets);
//                // Now we call setRefreshing(false) to signal refresh has finished
//                swipeContainer.setRefreshing(false);
//            }
//
//            public void onFailure(Throwable e) {
//                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
//            }
//        }, max_id);
//    }


    private void populateTimeline(boolean first) {
        // to set up the max_id for endless scroll
        if (first) {
            max_id = -1;
        } else {
            max_id = tweets.get(tweets.size()-1).uid;
            max_id--;

        }
        showProgressBar();
        // make the network request to get data from the Twitter API
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            // create an anonymous class to handle data from the API call


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Log.d("TwitterClient", response.toString());
                // iterate through the JSON array
                // for each entry, deserialize the JSON object
                // start the progress bar
                for (int i = 0; i < response.length(); i++) {
                    // convert each object to a Tweet model
                    // add the Tweet model to our data source
                    // notify the adapter that we've added an item
                    Tweet tweet = null;
                    try {
                        tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size()-1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // hide the progress bar when process finishes
                hideProgressBar();

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }
        }, max_id);


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);

        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // this is how the compose item in the ActionBar comes up
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
//        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, ComposeActivity.class);
        startActivityForResult(intent, REQUEST_CODE); // the request code 1 dictates the intent call made
        return super.onOptionsItemSelected(item);
    }

    // ActivityOne.java, time to handle the result of the sub-activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract object from result extras
            // Make sure the key here matches the one specified in the result passed from ActivityTwo.java
            // retrieves the tweet from the Compose Activity
            tweet = (Tweet) Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            // adds the tweet to the Array List
            tweets.add(0, tweet);
            // notify the adapter
            tweetAdapter.notifyItemInserted(0);
            // get the Recycler View to scroll to the top
            rvtweets.scrollToPosition(0);
        }
    }

    public void showProgressBar() {
        // Show progress item
        if (miActionProgressItem != null) {
        miActionProgressItem.setVisible(true);
        }
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

}

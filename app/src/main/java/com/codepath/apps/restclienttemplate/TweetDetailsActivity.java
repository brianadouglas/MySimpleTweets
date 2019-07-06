package com.codepath.apps.restclienttemplate;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailsActivity extends AppCompatActivity {

    // details for the tweet to be displayed
    Tweet tweet;
    ImageView profile;
    TextView username;
    TextView screen;
    TextView body;
    ImageView image;
    TextView time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        // declaring the values for the view
        profile = (ImageView) findViewById(R.id.ivProfileImage2);
        username = (TextView) findViewById(R.id.tvUserName2);
        body = (TextView) findViewById(R.id.tvBody2);
        image = (ImageView) findViewById(R.id.ivAttached2);
        time = (TextView) findViewById(R.id.tvTime2);
        screen = (TextView) findViewById(R.id.tvScreen);

        // unwrap from the parcel
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        // set the values for different parts of the view
        Glide.with(this).load(tweet.user.profileImageUrl).asBitmap().centerCrop().into(new BitmapImageViewTarget(profile) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                profile.setImageDrawable(circularBitmapDrawable);
            }
        });
        username.setText(tweet.user.name);
        screen.setText(String.format("@%s", tweet.user.screenName));
        body.setText(tweet.body);
        time.setText(tweet.createdAt);

        if (tweet.image_url != null) {
            Glide.with(this).load(tweet.image_url).bitmapTransform(new RoundedCornersTransformation(this, 25, 0)).into(image);
        } else {
            image.setVisibility(View.INVISIBLE);
        }

    }
}

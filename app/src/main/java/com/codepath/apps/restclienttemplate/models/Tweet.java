package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Tweet {
    // define all the attributes
    public String body;
    public long uid; // database id for the tweet
    public User user;
    public String createdAt;
    public String image_url;

    public Tweet() {}

    // to take in a JSON object and instantiate a Tweet object
    // deserialize the JSON data

    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        // extract the values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

        if(jsonObject.has("extended_entities")) {
            // get the entities within the tweet
            JSONObject entities = jsonObject.getJSONObject("entities");
            // get the media object from the entity
            JSONArray media = entities.getJSONArray("media");
            // get the expanded URL for the single image attached in the entities section
            tweet.image_url = media.getJSONObject(0).getString("media_url_https");
        }
        return tweet;
    }
}

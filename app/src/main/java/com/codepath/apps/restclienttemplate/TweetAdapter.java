package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    private List<Tweet> mTweets;
    Context context;
    // pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }
    RelativeLayout relativeLayout;

    // for each row, inflate the layout and cache the references into ViewHolder

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        relativeLayout = (RelativeLayout) tweetView.findViewById(R.id.singleTweet);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // bind the values based on the position of the element

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // get the data according to position
        Tweet tweet = mTweets.get(position);
        // populate the views according to this data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        String time = getRelativeTimeAgo(tweet.createdAt);
        String [] arrStr = time.split(" ");
        holder.tvTime.setText(String.format(" · %s%s", arrStr[0], arrStr[1].charAt(0)));
//        Glide.with(context).load(tweet.user.profileImageUrl).into(holder.ivProfileImage);
        Glide.with(context).load(tweet.user.profileImageUrl).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.ivProfileImage) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                holder.ivProfileImage.setImageDrawable(circularBitmapDrawable);
            }
        });
        if (tweet.image_url != null) {
//            // add the image into an image view below the tweet body and to the right of the profile image
//            ImageView imageView = new ImageView(context);
//            Glide.with(context).load(tweet.image_url).into(imageView);
//            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.WRAP_CONTENT,
//                    RelativeLayout.LayoutParams.WRAP_CONTENT
//            );
//            layoutParams.addRule(RelativeLayout.BELOW, R.id.tvBody);
//            layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.ivProfileImage);
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            // add image into holder
            Glide.with(context).load(tweet.image_url).bitmapTransform(new RoundedCornersTransformation(context, 25, 0)).into(holder.ivAttached);

        } else {
            holder.ivAttached.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // create ViewHolder class

                public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvTime;
        public ImageView ivAttached;

        public ViewHolder(View itemView) {
            super(itemView);

            // perform findViewById lookups

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            ivAttached = (ImageView) itemView.findViewById(R.id.ivAttached);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // get the item position
            int position = getAdapterPosition();
            // make sure the position is valid - exists in the view
            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = mTweets.get(position);
                Intent intent = new Intent(context, TweetDetailsActivity.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                context.startActivity(intent);
            }
        }
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

}


package com.hazemelsawy.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;



public class DetailsActivity extends AppCompatActivity {

    private ImageView thumb;
    private TextView movieTitle;
    private TextView movieOverview;
    private TextView movieRating;
    private TextView movieDate;
    private String title, poster, date, overView;
    private Float rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        thumb = (ImageView) findViewById(R.id.iv_thumb);
        movieTitle = (TextView) findViewById(R.id.tv_movie_title);
        movieOverview = (TextView) findViewById(R.id.tv_overview);
        movieRating = (TextView) findViewById(R.id.tv_user_rating);
        movieDate = (TextView) findViewById(R.id.tv_release_date);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent() != null) {
            Intent i = getIntent();
            title = i.getStringExtra("title");
            poster = i.getStringExtra("poster_url");
            date = i.getStringExtra("date");
            overView = i.getStringExtra("overview");
            rating = i.getFloatExtra("vote_average", 0);
            Picasso.with(this)
                    .load("http://image.tmdb.org/t/p/w185//" + poster)
                    .into(thumb);
            movieTitle.setText(title);
            movieDate.setText(date);
            movieOverview.setText(overView);
            movieRating.setText(String.valueOf(rating) + "/10");
        }

    }
}
